import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
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
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { LoadingService } from '../../../shared/services/loading';
import { InventoryService, InventoryItem } from '../../../shared/services/inventory.service';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-inventory-list',
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
    MatProgressBarModule
  ],
  templateUrl: './inventory-list.html',
  styleUrl: './inventory-list.scss'
})
export class InventoryList implements OnInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  currentUser: User | null = null;
  isLoading = false;
  private destroy$ = new Subject<void>();

  // Données
  inventoryItems: InventoryItem[] = [];
  filteredInventoryItems: MatTableDataSource<InventoryItem> = new MatTableDataSource<InventoryItem>();
  
  // Colonnes du tableau
  displayedColumns: string[] = [
    'select',
    'productCode',
    'productName',
    'category',
    'quantityOnHand',
    'unitPrice',
    'totalValue',
    'status',
    'actions'
  ];

  // Filtres
  filterForm: FormGroup;
  filterExpanded = false;
  
  // Sélection
  selectedItems: Set<number> = new Set();
  selectAll = false;

  // Pagination et tri
  pageSize = 25;
  pageSizeOptions = [10, 25, 50, 100];

  // Statuts disponibles
  statuses = [
    { value: 'ACTIVE', label: 'Actif', color: 'green' },
    { value: 'INACTIVE', label: 'Inactif', color: 'gray' },
    { value: 'DISCONTINUED', label: 'Discontinué', color: 'red' },
    { value: 'OUT_OF_STOCK', label: 'Rupture', color: 'orange' }
  ];

  // Catégories disponibles
  categories = [
    'Matériel informatique', 'Mobilier', 'Fournitures de bureau',
    'Équipement technique', 'Produits de nettoyage', 'Autre'
  ];

  // Unités disponibles
  units = ['pièce', 'kg', 'm', 'm²', 'L', 'paquet', 'lot'];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private loadingService: LoadingService,
    private inventoryService: InventoryService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      category: [''],
      status: [''],
      minQuantity: [''],
      maxQuantity: ['']
    });
  }

  ngOnInit(): void {
    this.loadInventoryItems();
    this.setupFilterSubscription();
    this.setupUserSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadInventoryItems(): void {
    this.isLoading = true;
    this.loadingService.show();

    this.inventoryService.getAllInventoryItems()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (items: InventoryItem[]) => {
          this.inventoryItems = items;
          this.filteredInventoryItems.data = this.inventoryItems;
          this.filteredInventoryItems.paginator = this.paginator;
          this.filteredInventoryItems.sort = this.sort;
          
          this.isLoading = false;
          this.loadingService.hide();
          
          this.notificationService.showSuccess(`${this.inventoryItems.length} articles d'inventaire chargés`);
        },
        error: (error: any) => {
          console.error('Erreur lors du chargement des articles d\'inventaire:', error);
          this.isLoading = false;
          this.loadingService.hide();
          this.notificationService.showError('Erreur lors du chargement des articles d\'inventaire');
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
    let filtered = [...this.inventoryItems];

    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      filtered = filtered.filter(item =>
        item.productCode?.toLowerCase().includes(searchTerm) ||
        item.productName?.toLowerCase().includes(searchTerm) ||
        item.category?.toLowerCase().includes(searchTerm) ||
        item.description?.toLowerCase().includes(searchTerm)
      );
    }

    if (filters.category) {
      filtered = filtered.filter(item => item.category === filters.category);
    }

    if (filters.status) {
      filtered = filtered.filter(item => item.status === filters.status);
    }

    if (filters.minQuantity) {
      filtered = filtered.filter(item => item.quantityOnHand >= filters.minQuantity);
    }

    if (filters.maxQuantity) {
      filtered = filtered.filter(item => item.quantityOnHand <= filters.maxQuantity);
    }

    this.filteredInventoryItems.data = filtered;
  }

  // Sélection
  toggleSelectAll(): void {
    if (this.selectAll) {
      this.selectedItems.clear();
    } else {
      this.filteredInventoryItems.data.forEach(item => {
        if (item.id) {
          this.selectedItems.add(item.id);
        }
      });
    }
    this.selectAll = !this.selectAll;
  }

  toggleItemSelection(itemId: number | undefined): void {
    if (!itemId) return;
    
    if (this.selectedItems.has(itemId)) {
      this.selectedItems.delete(itemId);
    } else {
      this.selectedItems.add(itemId);
    }
    this.updateSelectAllState();
  }

  private updateSelectAllState(): void {
    const totalItems = this.filteredInventoryItems.data.length;
    const selectedCount = this.selectedItems.size;
    this.selectAll = totalItems > 0 && selectedCount === totalItems;
  }

  // Actions
  addInventoryItem(): void {
    this.router.navigate(['/inventory/items/new']);
  }

  editInventoryItem(item: InventoryItem): void {
    this.router.navigate(['/inventory/items/edit', item.id]);
  }

  viewInventoryItem(item: InventoryItem): void {
    this.router.navigate(['/inventory/items/view', item.id]);
  }

  deleteInventoryItem(item: InventoryItem): void {
    this.notificationService.showInfo(`Suppression de ${item.productName}`);
  }

  deleteSelectedItems(): void {
    if (this.selectedItems.size === 0) {
      this.notificationService.showWarning('Aucun article sélectionné');
      return;
    }
    this.notificationService.showInfo(`${this.selectedItems.size} articles sélectionnés pour suppression`);
  }

  exportInventoryItems(): void {
    this.notificationService.showInfo('Export des articles d\'inventaire en cours...');
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.applyFilters();
  }

  toggleFilterPanel(): void {
    this.filterExpanded = !this.filterExpanded;
  }

  // Méthodes utilitaires
  getStatusColor(status: string): string {
    const statusObj = this.statuses.find(s => s.value === status);
    return statusObj ? statusObj.color : 'gray';
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'check_circle';
      case 'INACTIVE': return 'pause_circle';
      case 'DISCONTINUED': return 'cancel';
      case 'OUT_OF_STOCK': return 'warning';
      default: return 'help';
    }
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }

  formatDate(date: string): string {
    return new Intl.DateTimeFormat('fr-FR').format(new Date(date));
  }

  getStockLevel(item: InventoryItem): 'low' | 'medium' | 'high' {
    if (item.quantityOnHand <= (item.minimumStock || 0)) return 'low';
    if (item.quantityOnHand <= (item.maximumStock || 0) * 0.5) return 'medium';
    return 'high';
  }

  getStockLevelColor(level: string): string {
    switch (level) {
      case 'low': return 'red';
      case 'medium': return 'orange';
      case 'high': return 'green';
      default: return 'gray';
    }
  }
}
