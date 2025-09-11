import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatStepperModule } from '@angular/material/stepper';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { RegisterRequest } from '../../../shared/models/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatStepperModule
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  hidePassword = true;
  hideConfirmPassword = true;
  isLoading = false;
  currentStep = 0;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      // Étape 1: Informations personnelles
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      phoneNumber: ['', [Validators.pattern(/^[+]?[0-9\s\-\(\)]{10,}$/)]],
      
      // Étape 2: Mot de passe
      password: ['', [Validators.required, Validators.minLength(8), this.passwordStrengthValidator]],
      confirmPassword: ['', [Validators.required]],
      
      // Étape 3: Entreprise (optionnel)
      companyName: [''],
      
      // Conditions
      acceptTerms: [false, [Validators.requiredTrue]]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    // Vérifier si l'utilisateur est déjà connecté
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.loadingService.show();

      const registerRequest: RegisterRequest = {
        username: this.registerForm.value.username,
        email: this.registerForm.value.email,
        password: this.registerForm.value.password,
        firstName: this.registerForm.value.firstName,
        lastName: this.registerForm.value.lastName,
        phoneNumber: this.registerForm.value.phoneNumber,
        companyName: this.registerForm.value.companyName,
        acceptTerms: this.registerForm.value.acceptTerms
      };

      this.authService.register(registerRequest).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.loadingService.hide();
          
          if (response.success) {
            this.notificationService.showSuccess('Inscription réussie ! Vérifiez votre email.');
            this.router.navigate(['/login']);
          } else {
            this.notificationService.showError(response.message || 'Erreur lors de l\'inscription');
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.notifyApiError(error);
        }
      });
    } else {
      this.markFormGroupTouched();
      this.notificationService.showError('Veuillez corriger les erreurs du formulaire');
    }
  }

  nextStep(): void {
    if (this.isCurrentStepValid()) {
      this.currentStep++;
    } else {
      this.markCurrentStepTouched();
      this.notificationService.showError('Veuillez corriger les erreurs avant de continuer');
    }
  }

  previousStep(): void {
    this.currentStep--;
  }

  isCurrentStepValid(): boolean {
    switch (this.currentStep) {
      case 0:
        return !!(this.registerForm.get('username')?.valid &&
               this.registerForm.get('email')?.valid &&
               this.registerForm.get('firstName')?.valid &&
               this.registerForm.get('lastName')?.valid);
      case 1:
        return !!(this.registerForm.get('password')?.valid &&
               this.registerForm.get('confirmPassword')?.valid);
      case 2:
        return !!this.registerForm.get('acceptTerms')?.valid;
      default:
        return false;
    }
  }

  private markCurrentStepTouched(): void {
    switch (this.currentStep) {
      case 0:
        ['username', 'email', 'firstName', 'lastName'].forEach(field => {
          this.registerForm.get(field)?.markAsTouched();
        });
        break;
      case 1:
        ['password', 'confirmPassword'].forEach(field => {
          this.registerForm.get(field)?.markAsTouched();
        });
        break;
      case 2:
        this.registerForm.get('acceptTerms')?.markAsTouched();
        break;
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registerForm.controls).forEach(key => {
      const control = this.registerForm.get(key);
      control?.markAsTouched();
    });
  }

  private passwordStrengthValidator(control: AbstractControl): {[key: string]: any} | null {
    const value = control.value;
    if (!value) return null;

    const hasUpperCase = /[A-Z]/.test(value);
    const hasLowerCase = /[a-z]/.test(value);
    const hasNumeric = /[0-9]/.test(value);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);

    const strength = [hasUpperCase, hasLowerCase, hasNumeric, hasSpecialChar].filter(Boolean).length;

    return strength >= 3 ? null : { passwordStrength: true };
  }

  private passwordMatchValidator(control: AbstractControl): {[key: string]: any} | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }

    return null;
  }

  getErrorMessage(fieldName: string): string {
    const control = this.registerForm.get(fieldName);
    if (control?.hasError('required')) {
      return `${fieldName} est requis`;
    }
    if (control?.hasError('email')) {
      return 'Email invalide';
    }
    if (control?.hasError('minlength')) {
      return `${fieldName} doit contenir au moins ${control.errors?.['minlength'].requiredLength} caractères`;
    }
    if (control?.hasError('maxlength')) {
      return `${fieldName} ne peut pas dépasser ${control.errors?.['maxlength'].requiredLength} caractères`;
    }
    if (control?.hasError('pattern')) {
      return 'Format invalide';
    }
    if (control?.hasError('passwordStrength')) {
      return 'Le mot de passe doit contenir au moins 3 des éléments suivants: majuscules, minuscules, chiffres, caractères spéciaux';
    }
    if (this.registerForm.hasError('passwordMismatch') && fieldName === 'confirmPassword') {
      return 'Les mots de passe ne correspondent pas';
    }
    return '';
  }

  getPasswordStrength(): string {
    const password = this.registerForm.get('password')?.value;
    if (!password) return '';

    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumeric = /[0-9]/.test(password);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);

    const strength = [hasUpperCase, hasLowerCase, hasNumeric, hasSpecialChar].filter(Boolean).length;

    switch (strength) {
      case 0:
      case 1:
        return 'Très faible';
      case 2:
        return 'Faible';
      case 3:
        return 'Moyen';
      case 4:
        return 'Fort';
      default:
        return '';
    }
  }
}
