import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatStepperModule } from '@angular/material/stepper';
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { JournalEntryService, JournalEntry, AccountEntry } from '../../../shared/services/journal-entry.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-journal-entries',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatChipsModule,
    MatTooltipModule,
    MatMenuModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDividerModule,
    MatBadgeModule,
    MatExpansionModule,
    MatStepperModule
  ],
  templateUrl: './journal-entries.html',
  styleUrl: './journal-entries.scss'
})
export class JournalEntriesComponent implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  journalEntries: JournalEntry[] = [];
  filteredJournalEntries: MatTableDataSource<JournalEntry> = new MatTableDataSource<JournalEntry>();
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'entryNumber',
    'entryDate',
    'description',
    'journalType',
    'status',
    'totalDebit',
    'totalCredit',
    'isBalanced',
    'createdBy',
    'actions'
  ];

  // Filtres
  filterForm: FormGroup;
  filterExpanded = false;
  
  // Sélection
  selectedEntries: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Types de journaux
  journalTypes = [
    { value: 'ACHATS', label: 'Achats', color: 'blue' },
    { value: 'VENTES', label: 'Ventes', color: 'green' },
    { value: 'BANQUE', label: 'Banque', color: 'purple' },
    { value: 'CAISSE', label: 'Caisse', color: 'orange' },
    { value: 'OD', label: 'Opérations Diverses', color: 'gray' },
    { value: 'CLOTURE', label: 'Clôture', color: 'red' }
  ];

  // Statuts
  statuses = [
    { value: 'BROUILLON', label: 'Brouillon', color: 'gray' },
    { value: 'VALIDÉ', label: 'Validé', color: 'blue' },
    { value: 'COMPTABILISÉ', label: 'Comptabilisé', color: 'green' },
    { value: 'ANNULE', label: 'Annulé', color: 'red' }
  ];

  // Devises
  currencies = ['EUR', 'USD', 'CHF', 'GBP'];

  // Standards comptables
  accountingStandards = ['OHADA', 'IFRS', 'GAAP', 'FRENCH_GAAP'];

  // Pays
  countries = ['LU', 'FR', 'BE', 'DE', 'CH', 'US', 'GB'];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private journalEntryService: JournalEntryService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      journalType: [''],
      status: [''],
      currency: [''],
      dateRange: this.fb.group({
        start: [null],
        end: [null]
      })
    });
  }

  ngOnInit(): void {
    this.loadJournalEntries();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadJournalEntries(): void {
    this.isLoading = true;
    this.loadingService.show();

    // Récupérer les écritures depuis l'API
    this.journalEntryService.getAllJournalEntries()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (entries) => {
          this.journalEntries = entries;
          this.filteredJournalEntries.data = this.journalEntries;
          this.filteredJournalEntries.paginator = this.paginator;
          this.filteredJournalEntries.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${entries.length} écritures chargées`);
        },
        error: (error) => {
          console.error('Erreur lors du chargement des écritures:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des écritures');
        }
      });
  }

  private setupFilterSubscription(): void {
    this.filterForm.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.applyFilters();
      });
  }

  private setupUserSubscription(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });
  }

  private applyFilters(): void {
    const filters = this.filterForm.value;
    let filtered = [...this.journalEntries];

    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(entry =>
        entry.entryNumber.toLowerCase().includes(searchTerm) ||
        entry.description.toLowerCase().includes(searchTerm) ||
        entry.reference?.toLowerCase().includes(searchTerm) ||
        entry.documentNumber?.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.journalType) {
      filtered = filtered.filter(entry => entry.journalType === filters.journalType);
    }

    if (filters.status) {
      filtered = filtered.filter(entry => entry.status === filters.status);
    }

    if (filters.currency) {
      filtered = filtered.filter(entry => entry.currency === filters.currency);
    }

    if (filters.dateRange.start) {
      filtered = filtered.filter(entry => new Date(entry.entryDate) >= filters.dateRange.start);
    }
    if (filters.dateRange.end) {
      filtered = filtered.filter(entry => new Date(entry.entryDate) <= filters.dateRange.end);
    }

    this.filteredJournalEntries.data = filtered;
  }

  // Gestion de la sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedEntries.clear();
    } else {
      this.filteredJournalEntries.data.forEach(entry => {
        this.selectedEntries.add(entry.id);
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleEntrySelection(entryId: number): void {
    if (this.selectedEntries.has(entryId)) {
      this.selectedEntries.delete(entryId);
    } else {
      this.selectedEntries.add(entryId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalEntries = this.filteredJournalEntries.data.length;
    const selectedCount = this.selectedEntries.size;
    this.selectAll = totalEntries > 0 && selectedCount === totalEntries;
  }

  // Actions
  addJournalEntry(): void {
    this.router.navigate(['/accounting/journal-entries/new']);
  }

  editJournalEntry(entry: JournalEntry): void {
    this.router.navigate(['/accounting/journal-entries/edit', entry.id]);
  }

  viewJournalEntry(entry: JournalEntry): void {
    this.router.navigate(['/accounting/journal-entries/view', entry.id]);
  }

  postJournalEntry(entry: JournalEntry): void {
    if (!this.currentUser) {
      this.notificationService.showError('Utilisateur non connecté');
      return;
    }

    this.journalEntryService.validateJournalEntry(entry.id, this.currentUser.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedEntry) => {
          this.notificationService.showSuccess(`Écriture ${entry.entryNumber} comptabilisée avec succès`);
          // Mettre à jour la liste locale
          const index = this.journalEntries.findIndex(e => e.id === entry.id);
          if (index !== -1) {
            this.journalEntries[index] = updatedEntry;
            this.filteredJournalEntries.data = [...this.journalEntries];
          }
        },
        error: (error) => {
          console.error('Erreur lors de la comptabilisation:', error);
          this.notificationService.showError('Erreur lors de la comptabilisation de l\'écriture');
        }
      });
  }

  approveJournalEntry(entry: JournalEntry): void {
    if (!this.currentUser) {
      this.notificationService.showError('Utilisateur non connecté');
      return;
    }

    this.journalEntryService.validateJournalEntry(entry.id, this.currentUser.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedEntry) => {
          this.notificationService.showSuccess(`Écriture ${entry.entryNumber} approuvée avec succès`);
          // Mettre à jour la liste locale
          const index = this.journalEntries.findIndex(e => e.id === entry.id);
          if (index !== -1) {
            this.journalEntries[index] = updatedEntry;
            this.filteredJournalEntries.data = [...this.journalEntries];
          }
        },
        error: (error) => {
          console.error('Erreur lors de l\'approbation:', error);
          this.notificationService.showError('Erreur lors de l\'approbation de l\'écriture');
        }
      });
  }

  rejectJournalEntry(entry: JournalEntry): void {
    this.journalEntryService.cancelJournalEntry(entry.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedEntry) => {
          this.notificationService.showSuccess(`Écriture ${entry.entryNumber} rejetée avec succès`);
          // Mettre à jour la liste locale
          const index = this.journalEntries.findIndex(e => e.id === entry.id);
          if (index !== -1) {
            this.journalEntries[index] = updatedEntry;
            this.filteredJournalEntries.data = [...this.journalEntries];
          }
        },
        error: (error) => {
          console.error('Erreur lors du rejet:', error);
          this.notificationService.showError('Erreur lors du rejet de l\'écriture');
        }
      });
  }

  deleteJournalEntry(entry: JournalEntry): void {
    this.journalEntryService.cancelJournalEntry(entry.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedEntry) => {
          this.notificationService.showSuccess(`Écriture ${entry.entryNumber} supprimée avec succès`);
          // Mettre à jour la liste locale
          const index = this.journalEntries.findIndex(e => e.id === entry.id);
          if (index !== -1) {
            this.journalEntries[index] = updatedEntry;
            this.filteredJournalEntries.data = [...this.journalEntries];
          }
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          this.notificationService.showError('Erreur lors de la suppression de l\'écriture');
        }
      });
  }

  deleteSelectedEntries(): void {
    if (this.selectedEntries.size === 0) {
      this.notificationService.showWarning('Aucune écriture sélectionnée');
      return;
    }

    const selectedIds = Array.from(this.selectedEntries);
    let completed = 0;
    let errors = 0;

    selectedIds.forEach(entryId => {
      this.journalEntryService.cancelJournalEntry(entryId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            completed++;
            if (completed + errors === selectedIds.length) {
              if (errors === 0) {
                this.notificationService.showSuccess(`${completed} écritures supprimées avec succès`);
                this.loadJournalEntries(); // Recharger la liste
              } else {
                this.notificationService.showWarning(`${completed} écritures supprimées, ${errors} erreurs`);
              }
            }
          },
          error: (error) => {
            console.error('Erreur lors de la suppression:', error);
            errors++;
            if (completed + errors === selectedIds.length) {
              this.notificationService.showWarning(`${completed} écritures supprimées, ${errors} erreurs`);
            }
          }
        });
    });
  }

  exportJournalEntries(): void {
    this.notificationService.showInfo('Export des écritures en cours...');
    // TODO: Implémenter l'export
  }

  importJournalEntries(): void {
    this.notificationService.showInfo('Import des écritures en cours...');
    // TODO: Implémenter l'import
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  getJournalTypeColor(type: string): string {
    const typeObj = this.journalTypes.find(t => t.value === type);
    return typeObj ? typeObj.color : 'gray';
  }

  getJournalTypeIcon(type: string): string {
    switch (type) {
      case 'ACHATS': return 'shopping_cart';
      case 'VENTES': return 'sell';
      case 'BANQUE': return 'account_balance';
      case 'CAISSE': return 'payments';
      case 'OD': return 'receipt';
      case 'CLOTURE': return 'lock';
      default: return 'help';
    }
  }

  getStatusColor(status: string): string {
    return this.journalEntryService.getStatusColor(status);
  }

  getStatusIcon(status: string): string {
    return this.journalEntryService.getStatusIcon(status);
  }

  formatCurrency(amount: number, currency: string = 'EUR'): string {
    return this.journalEntryService.formatCurrency(amount, currency);
  }

  formatDate(date: string): string {
    return this.journalEntryService.formatDate(date);
  }

  isJournalEntryBalanced(entry: JournalEntry): boolean {
    return this.journalEntryService.isJournalEntryBalanced(entry);
  }

  getBalanceStatus(entry: JournalEntry): string {
    return this.isJournalEntryBalanced(entry) ? 'Équilibrée' : 'Non équilibrée';
  }

  getBalanceColor(entry: JournalEntry): string {
    return this.isJournalEntryBalanced(entry) ? 'green' : 'red';
  }

  getBalanceIcon(entry: JournalEntry): string {
    return this.isJournalEntryBalanced(entry) ? 'check_circle' : 'error';
  }
}
