import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface InventoryItem {
  id?: number;
  productCode: string;
  productName: string;
  category?: string;
  description?: string;
  unit: string;
  unitPrice: number;
  quantityOnHand: number;
  minimumStock?: number;
  maximumStock?: number;
  reorderPoint?: number;
  supplier?: string;
  supplierCode?: string;
  location?: string;
  warehouse?: string;
  shelf?: string;
  expiryDate?: Date;
  batchNumber?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED' | 'OUT_OF_STOCK' | 'EXPIRED' | 'DAMAGED' | 'RESERVED';
  valuationMethod: 'FIFO' | 'LIFO' | 'AVERAGE_COST' | 'STANDARD_COST' | 'SPECIFIC_IDENTIFICATION';
  lastPurchasePrice?: number;
  averageCost?: number;
  totalValue: number;
  lastMovementDate?: Date;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
  createdAt?: Date;
  updatedAt?: Date;
  createdBy?: number;
  updatedBy?: number;
}

export interface InventoryMovement {
  id?: number;
  movementCode: string;
  productId: number;
  movementType: 'IN' | 'OUT' | 'TRANSFER' | 'ADJUSTMENT' | 'RETURN' | 'DAMAGE' | 'LOSS' | 'EXPIRY';
  quantity: number;
  unitPrice?: number;
  totalAmount?: number;
  movementDate: Date;
  referenceNumber?: string;
  referenceType?: string;
  warehouseFrom?: string;
  warehouseTo?: string;
  locationFrom?: string;
  locationTo?: string;
  batchNumber?: string;
  expiryDate?: Date;
  reason?: string;
  notes?: string;
  approvedBy?: number;
  approvedAt?: Date;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED' | 'COMPLETED';
  companyId: number;
  countryCode: string;
  accountingStandard: string;
  createdAt?: Date;
  updatedAt?: Date;
  createdBy?: number;
  updatedBy?: number;
}

export interface CreateInventoryItemRequest {
  productCode: string;
  productName: string;
  category?: string;
  description?: string;
  unit: string;
  unitPrice: number;
  quantityOnHand: number;
  minimumStock?: number;
  maximumStock?: number;
  reorderPoint?: number;
  supplier?: string;
  supplierCode?: string;
  location?: string;
  warehouse?: string;
  shelf?: string;
  expiryDate?: Date;
  batchNumber?: string;
  status: string;
  valuationMethod: string;
  lastPurchasePrice?: number;
  averageCost?: number;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
}

export interface UpdateInventoryItemRequest extends Partial<CreateInventoryItemRequest> {
  id: number;
}

export interface CreateInventoryMovementRequest {
  movementCode: string;
  productId: number;
  movementType: string;
  quantity: number;
  unitPrice?: number;
  totalAmount?: number;
  movementDate: Date;
  referenceNumber?: string;
  referenceType?: string;
  warehouseFrom?: string;
  warehouseTo?: string;
  locationFrom?: string;
  locationTo?: string;
  batchNumber?: string;
  expiryDate?: Date;
  reason?: string;
  notes?: string;
  companyId: number;
  countryCode: string;
  accountingStandard: string;
}

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private apiUrl = `${environment.apiUrl}/inventory`;

  constructor(private http: HttpClient) {}

  /**
   * Obtenir tous les articles d'inventaire
   */
  getAllInventoryItems(): Observable<InventoryItem[]> {
    return this.http.get<{data: InventoryItem[], status: string}>(`${this.apiUrl}/items`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des articles');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des articles:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir un article par ID
   */
  getInventoryItemById(id: number): Observable<InventoryItem> {
    return this.http.get<{data: InventoryItem, status: string}>(`${this.apiUrl}/items/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération de l\'article');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération de l\'article:', error);
          throw error;
        })
      );
  }

  /**
   * Créer un nouvel article
   */
  createInventoryItem(item: CreateInventoryItemRequest): Observable<InventoryItem> {
    return this.http.post<{data: InventoryItem, status: string}>(`${this.apiUrl}/items`, item)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la création de l\'article');
        }),
        catchError(error => {
          console.error('Erreur lors de la création de l\'article:', error);
          throw error;
        })
      );
  }

  /**
   * Mettre à jour un article
   */
  updateInventoryItem(item: UpdateInventoryItemRequest): Observable<InventoryItem> {
    return this.http.put<{data: InventoryItem, status: string}>(`${this.apiUrl}/items/${item.id}`, item)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la mise à jour de l\'article');
        }),
        catchError(error => {
          console.error('Erreur lors de la mise à jour de l\'article:', error);
          throw error;
        })
      );
  }

  /**
   * Supprimer un article
   */
  deleteInventoryItem(id: number): Observable<boolean> {
    return this.http.delete<{message: string, status: string}>(`${this.apiUrl}/items/${id}`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return true;
          }
          throw new Error('Erreur lors de la suppression de l\'article');
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression de l\'article:', error);
          return of(false);
        })
      );
  }

  /**
   * Obtenir les mouvements d'inventaire
   */
  getAllInventoryMovements(): Observable<InventoryMovement[]> {
    return this.http.get<{data: InventoryMovement[], status: string}>(`${this.apiUrl}/movements`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des mouvements');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des mouvements:', error);
          return of([]);
        })
      );
  }

  /**
   * Créer un mouvement d'inventaire
   */
  createInventoryMovement(movement: CreateInventoryMovementRequest): Observable<InventoryMovement> {
    return this.http.post<{data: InventoryMovement, status: string}>(`${this.apiUrl}/movements`, movement)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la création du mouvement');
        }),
        catchError(error => {
          console.error('Erreur lors de la création du mouvement:', error);
          throw error;
        })
      );
  }

  /**
   * Obtenir les articles en rupture de stock
   */
  getOutOfStockItems(): Observable<InventoryItem[]> {
    return this.http.get<{data: InventoryItem[], status: string}>(`${this.apiUrl}/items/out-of-stock`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des articles en rupture');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des articles en rupture:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les articles à réapprovisionner
   */
  getReorderItems(): Observable<InventoryItem[]> {
    return this.http.get<{data: InventoryItem[], status: string}>(`${this.apiUrl}/items/reorder`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des articles à réapprovisionner');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des articles à réapprovisionner:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les articles expirés
   */
  getExpiredItems(): Observable<InventoryItem[]> {
    return this.http.get<{data: InventoryItem[], status: string}>(`${this.apiUrl}/items/expired`)
      .pipe(
        map(response => {
          if (response.status === 'SUCCESS') {
            return response.data;
          }
          throw new Error('Erreur lors de la récupération des articles expirés');
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération des articles expirés:', error);
          return of([]);
        })
      );
  }

  /**
   * Obtenir les options pour les statuts d'articles
   */
  getItemStatuses(): Array<{value: string, label: string}> {
    return [
      { value: 'ACTIVE', label: 'Actif' },
      { value: 'INACTIVE', label: 'Inactif' },
      { value: 'DISCONTINUED', label: 'Arrêté' },
      { value: 'OUT_OF_STOCK', label: 'Rupture de stock' },
      { value: 'EXPIRED', label: 'Expiré' },
      { value: 'DAMAGED', label: 'Endommagé' },
      { value: 'RESERVED', label: 'Réservé' }
    ];
  }

  /**
   * Obtenir les options pour les méthodes d'évaluation
   */
  getValuationMethods(): Array<{value: string, label: string}> {
    return [
      { value: 'FIFO', label: 'Premier entré, premier sorti' },
      { value: 'LIFO', label: 'Dernier entré, premier sorti' },
      { value: 'AVERAGE_COST', label: 'Coût moyen' },
      { value: 'STANDARD_COST', label: 'Coût standard' },
      { value: 'SPECIFIC_IDENTIFICATION', label: 'Identification spécifique' }
    ];
  }

  /**
   * Obtenir les options pour les types de mouvements
   */
  getMovementTypes(): Array<{value: string, label: string}> {
    return [
      { value: 'IN', label: 'Entrée' },
      { value: 'OUT', label: 'Sortie' },
      { value: 'TRANSFER', label: 'Transfert' },
      { value: 'ADJUSTMENT', label: 'Ajustement' },
      { value: 'RETURN', label: 'Retour' },
      { value: 'DAMAGE', label: 'Dégâts' },
      { value: 'LOSS', label: 'Perte' },
      { value: 'EXPIRY', label: 'Expiration' }
    ];
  }

  /**
   * Obtenir les options pour les statuts de mouvements
   */
  getMovementStatuses(): Array<{value: string, label: string}> {
    return [
      { value: 'PENDING', label: 'En attente' },
      { value: 'APPROVED', label: 'Approuvé' },
      { value: 'REJECTED', label: 'Rejeté' },
      { value: 'CANCELLED', label: 'Annulé' },
      { value: 'COMPLETED', label: 'Terminé' }
    ];
  }

  /**
   * Obtenir l'icône du statut d'article
   */
  getItemStatusIcon(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'check_circle';
      case 'INACTIVE': return 'pause_circle';
      case 'DISCONTINUED': return 'stop_circle';
      case 'OUT_OF_STOCK': return 'warning';
      case 'EXPIRED': return 'schedule';
      case 'DAMAGED': return 'error';
      case 'RESERVED': return 'lock';
      default: return 'help';
    }
  }

  /**
   * Obtenir la couleur du statut d'article
   */
  getItemStatusColor(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'green';
      case 'INACTIVE': return 'gray';
      case 'DISCONTINUED': return 'red';
      case 'OUT_OF_STOCK': return 'orange';
      case 'EXPIRED': return 'red';
      case 'DAMAGED': return 'red';
      case 'RESERVED': return 'blue';
      default: return 'gray';
    }
  }

  /**
   * Obtenir l'icône du type de mouvement
   */
  getMovementTypeIcon(type: string): string {
    switch (type) {
      case 'IN': return 'add_circle';
      case 'OUT': return 'remove_circle';
      case 'TRANSFER': return 'swap_horiz';
      case 'ADJUSTMENT': return 'tune';
      case 'RETURN': return 'undo';
      case 'DAMAGE': return 'error';
      case 'LOSS': return 'warning';
      case 'EXPIRY': return 'schedule';
      default: return 'help';
    }
  }

  /**
   * Obtenir la couleur du type de mouvement
   */
  getMovementTypeColor(type: string): string {
    switch (type) {
      case 'IN': return 'green';
      case 'OUT': return 'red';
      case 'TRANSFER': return 'blue';
      case 'ADJUSTMENT': return 'orange';
      case 'RETURN': return 'purple';
      case 'DAMAGE': return 'red';
      case 'LOSS': return 'red';
      case 'EXPIRY': return 'orange';
      default: return 'gray';
    }
  }

  /**
   * Formater la devise
   */
  formatCurrency(amount: number, currency: string = 'XOF'): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  /**
   * Formater la date
   */
  formatDate(date: string): string {
    return new Intl.DateTimeFormat('fr-FR').format(new Date(date));
  }
}








