import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Inventory {
  id: number;
  name: string;
  description: string;
  companyId: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface InventoryItem {
  id: number;
  inventoryId: number;
  itemName: string;
  description: string;
  quantity: number;
  unitPrice: number;
  unit: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface InventoryMovement {
  id: number;
  inventoryId: number;
  itemId: number;
  movementType: string;
  quantity: number;
  reason: string;
  userId: number;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private apiUrl = `${environment.apiUrl}/api/inventory`;

  constructor(private http: HttpClient) { }

  /**
   * Créer un nouvel inventaire
   */
  createInventory(name: string, description: string, companyId: number): Observable<Inventory> {
    return this.http.post<Inventory>(`${this.apiUrl}/inventories`, {
      name,
      description,
      companyId
    });
  }

  /**
   * Ajouter un article à l'inventaire
   */
  addInventoryItem(inventoryId: number, itemName: string, description: string, 
                  quantity: number, unitPrice: number, unit: string): Observable<InventoryItem> {
    return this.http.post<InventoryItem>(`${this.apiUrl}/items`, {
      inventoryId,
      itemName,
      description,
      quantity,
      unitPrice,
      unit
    });
  }

  /**
   * Enregistrer un mouvement d'inventaire
   */
  recordMovement(inventoryId: number, itemId: number, movementType: string, 
                quantity: number, reason: string, userId: number): Observable<InventoryMovement> {
    return this.http.post<InventoryMovement>(`${this.apiUrl}/movements`, {
      inventoryId,
      itemId,
      movementType,
      quantity,
      reason,
      userId
    });
  }

  /**
   * Obtenir tous les inventaires d'une entreprise
   */
  getInventoriesByCompany(companyId: number): Observable<Inventory[]> {
    return this.http.get<Inventory[]>(`${this.apiUrl}/inventories/company/${companyId}`);
  }

  /**
   * Obtenir les articles d'un inventaire
   */
  getInventoryItems(inventoryId: number): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.apiUrl}/items/inventory/${inventoryId}`);
  }

  /**
   * Obtenir les mouvements d'un inventaire
   */
  getInventoryMovements(inventoryId: number): Observable<InventoryMovement[]> {
    return this.http.get<InventoryMovement[]>(`${this.apiUrl}/movements/inventory/${inventoryId}`);
  }

  /**
   * Calculer le stock actuel d'un article
   */
  calculateCurrentStock(itemId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/stock/item/${itemId}`);
  }

  /**
   * Mettre à jour la quantité d'un article
   */
  updateItemQuantity(itemId: number, newQuantity: number): Observable<InventoryItem> {
    return this.http.put<InventoryItem>(`${this.apiUrl}/items/${itemId}/quantity`, {
      quantity: newQuantity
    });
  }

  /**
   * Désactiver un inventaire
   */
  deactivateInventory(inventoryId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/inventories/${inventoryId}`);
  }

  /**
   * Obtenir les statistiques d'inventaire
   */
  getInventoryStatistics(companyId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/statistics/company/${companyId}`);
  }

  /**
   * Rechercher des inventaires par nom
   */
  searchInventoriesByName(name: string, companyId: number): Observable<Inventory[]> {
    return this.http.get<Inventory[]>(`${this.apiUrl}/inventories/search?name=${name}&companyId=${companyId}`);
  }

  /**
   * Rechercher des articles par nom
   */
  searchItemsByName(itemName: string, inventoryId: number): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.apiUrl}/items/search?itemName=${itemName}&inventoryId=${inventoryId}`);
  }
}

