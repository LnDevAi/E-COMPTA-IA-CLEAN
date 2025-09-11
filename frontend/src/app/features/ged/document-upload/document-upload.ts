import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Subject, takeUntil } from 'rxjs';

import { DocumentManagementService, DocumentType, SecurityLevel } from '../../../shared/services/document-management.service';

interface UploadFile {
  file: File;
  progress: number;
  status: 'pending' | 'uploading' | 'success' | 'error';
  error?: string;
  documentData?: any;
}

@Component({
  selector: 'app-document-upload',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatSnackBarModule,
    MatDialogModule,
    MatChipsModule,
    MatAutocompleteModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTooltipModule
  ],
  template: `
    <div class="document-upload">
      <!-- Template temporaire -->
      <h2>Upload de Documents</h2>
      <p>Composant d'upload en cours de développement...</p>
    </div>
  `,
  styles: [`
    .document-upload {
      padding: 24px;
      text-align: center;
    }
  `]
})
export class DocumentUploadComponent implements OnInit, OnDestroy {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  // Formulaire
  uploadForm: FormGroup;
  
  // Fichiers sélectionnés
  selectedFiles: UploadFile[] = [];
  
  // États
  uploading = false;
  uploadProgress = 0;
  
  // Options
  documentTypes = Object.values(DocumentType);
  securityLevels = Object.values(SecurityLevel);
  
  // Catégories suggérées
  suggestedCategories = [
    'Administratif',
    'Comptable',
    'Juridique',
    'RH',
    'Technique',
    'Commercial',
    'Marketing',
    'Finance',
    'Audit',
    'Conformité'
  ];
  
  // Subject pour la destruction
  private destroy$ = new Subject<void>();
  
  constructor(
    private fb: FormBuilder,
    private documentService: DocumentManagementService,
    private snackBar: MatSnackBar,
    private dialogRef: MatDialogRef<DocumentUploadComponent>
  ) {
    this.uploadForm = this.createForm();
  }
  
  ngOnInit(): void {
    // Initialisation
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  // ==================== CRÉATION DU FORMULAIRE ====================
  
  private createForm(): FormGroup {
    return this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      documentType: ['', Validators.required],
      category: [''],
      tags: [''],
      securityLevel: [SecurityLevel.INTERNAL, Validators.required],
      expiryDate: [''],
      retentionPeriodYears: [7, [Validators.min(1), Validators.max(50)]],
      moduleReference: [''],
      entityReferenceId: [''],
      entityReferenceType: ['']
    });
  }
  
  // ==================== GESTION DES FICHIERS ====================
  
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.addFiles(Array.from(input.files));
    }
  }
  
  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    
    if (event.dataTransfer?.files) {
      this.addFiles(Array.from(event.dataTransfer.files));
    }
  }
  
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
  }
  
  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
  }
  
  private addFiles(files: File[]): void {
    const validFiles = files.filter(file => this.validateFile(file));
    
    validFiles.forEach(file => {
      const uploadFile: UploadFile = {
        file,
        progress: 0,
        status: 'pending'
      };
      
      this.selectedFiles.push(uploadFile);
    });
    
    if (validFiles.length !== files.length) {
      this.snackBar.open('Certains fichiers ont été ignorés (format ou taille non supportés)', 'Fermer', { duration: 5000 });
    }
  }
  
  private validateFile(file: File): boolean {
    // Vérifier la taille (max 50MB)
    const maxSize = 50 * 1024 * 1024; // 50MB
    if (file.size > maxSize) {
      this.snackBar.open(`Le fichier ${file.name} est trop volumineux (max 50MB)`, 'Fermer', { duration: 5000 });
      return false;
    }
    
    // Vérifier le type de fichier
    const allowedTypes = [
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'application/vnd.ms-excel',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'application/vnd.ms-powerpoint',
      'application/vnd.openxmlformats-officedocument.presentationml.presentation',
      'text/plain',
      'image/jpeg',
      'image/png',
      'image/gif',
      'image/tiff'
    ];
    
    if (!allowedTypes.includes(file.type)) {
      this.snackBar.open(`Le format de fichier ${file.type} n'est pas supporté`, 'Fermer', { duration: 5000 });
      return false;
    }
    
    return true;
  }
  
  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }
  
  clearFiles(): void {
    this.selectedFiles = [];
    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
  }
  
  // ==================== UPLOAD ====================
  
  uploadFiles(): void {
    if (this.selectedFiles.length === 0) {
      this.snackBar.open('Aucun fichier sélectionné', 'Fermer', { duration: 3000 });
      return;
    }
    
    if (!this.uploadForm.valid) {
      this.snackBar.open('Veuillez remplir tous les champs obligatoires', 'Fermer', { duration: 3000 });
      return;
    }
    
    this.uploading = true;
    this.uploadProgress = 0;
    
    const formData = this.uploadForm.value;
    let completedUploads = 0;
    
    this.selectedFiles.forEach((uploadFile, index) => {
      uploadFile.status = 'uploading';
      
      const documentData = {
        ...formData,
        fileName: uploadFile.file.name,
        fileType: this.getFileType(uploadFile.file),
        mimeType: uploadFile.file.type,
        fileSize: uploadFile.file.size,
        companyId: 1, // TODO: Récupérer depuis le service d'authentification
        countryCode: 'SN', // TODO: Récupérer depuis la configuration
        accountingStandard: 'SYCEBNL' // TODO: Récupérer depuis la configuration
      };
      
      this.documentService.uploadDocument(uploadFile.file, documentData)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            uploadFile.status = 'success';
            uploadFile.progress = 100;
            completedUploads++;
            this.updateOverallProgress(completedUploads);
            
            if (completedUploads === this.selectedFiles.length) {
              this.onUploadComplete();
            }
          },
          error: (error) => {
            uploadFile.status = 'error';
            uploadFile.error = error.message || 'Erreur lors de l\'upload';
            completedUploads++;
            this.updateOverallProgress(completedUploads);
            
            if (completedUploads === this.selectedFiles.length) {
              this.onUploadComplete();
            }
          }
        });
    });
  }
  
  private updateOverallProgress(completed: number): void {
    this.uploadProgress = (completed / this.selectedFiles.length) * 100;
  }
  
  private onUploadComplete(): void {
    this.uploading = false;
    
    const successCount = this.selectedFiles.filter(f => f.status === 'success').length;
    const errorCount = this.selectedFiles.filter(f => f.status === 'error').length;
    
    if (successCount > 0) {
      this.snackBar.open(`${successCount} fichier(s) uploadé(s) avec succès`, 'Fermer', { duration: 5000 });
    }
    
    if (errorCount > 0) {
      this.snackBar.open(`${errorCount} fichier(s) ont échoué`, 'Fermer', { duration: 5000 });
    }
    
    // Fermer le dialog après un délai
    setTimeout(() => {
      this.dialogRef.close(successCount > 0);
    }, 2000);
  }
  
  // ==================== UTILITAIRES ====================
  
  private getFileType(file: File): string {
    const extension = file.name.split('.').pop()?.toLowerCase();
    return extension || 'unknown';
  }
  
  formatFileSize(bytes: number): string {
    return this.documentService.formatFileSize(bytes);
  }
  
  getFileIcon(file: File): string {
    const type = file.type;
    
    if (type.includes('pdf')) return 'picture_as_pdf';
    if (type.includes('word')) return 'description';
    if (type.includes('excel') || type.includes('spreadsheet')) return 'table_chart';
    if (type.includes('powerpoint') || type.includes('presentation')) return 'slideshow';
    if (type.includes('image')) return 'image';
    if (type.includes('text')) return 'text_snippet';
    
    return 'insert_drive_file';
  }
  
  getStatusIcon(status: string): string {
    switch (status) {
      case 'pending': return 'schedule';
      case 'uploading': return 'upload';
      case 'success': return 'check_circle';
      case 'error': return 'error';
      default: return 'help';
    }
  }
  
  getStatusColor(status: string): string {
    switch (status) {
      case 'pending': return 'grey';
      case 'uploading': return 'primary';
      case 'success': return 'green';
      case 'error': return 'warn';
      default: return 'grey';
    }
  }
  
  // ==================== ACTIONS ====================
  
  cancel(): void {
    this.dialogRef.close(false);
  }
  
  // ==================== GETTERS ====================
  
  get canUpload(): boolean {
    return this.selectedFiles.length > 0 && this.uploadForm.valid && !this.uploading;
  }
  
  get hasErrors(): boolean {
    return this.selectedFiles.some(f => f.status === 'error');
  }
  
  get allUploaded(): boolean {
    return this.selectedFiles.length > 0 && this.selectedFiles.every(f => f.status === 'success');
  }
}
