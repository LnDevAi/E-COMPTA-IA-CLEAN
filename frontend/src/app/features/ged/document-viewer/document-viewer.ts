import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, ViewChild, ElementRef, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSliderModule } from '@angular/material/slider';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { Subject, takeUntil } from 'rxjs';

import { DocumentManagementService, GedDocument } from '../../../shared/services/document-management.service';

interface ViewerState {
  currentPage: number;
  totalPages: number;
  zoom: number;
  rotation: number;
  isFullscreen: boolean;
  isLoading: boolean;
  error: string | null;
}

@Component({
  selector: 'app-document-viewer',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule,
    MatToolbarModule,
    MatSliderModule,
    MatMenuModule,
    MatTooltipModule,
    MatChipsModule,
    MatDividerModule
  ],
  templateUrl: './document-viewer.html',
  styleUrls: ['./document-viewer.scss']
})
export class DocumentViewerComponent implements OnInit, OnDestroy {
  @Input() document!: GedDocument;
  @Input() showToolbar = true;
  @Input() showMetadata = true;
  @Output() close = new EventEmitter<void>();
  @Output() download = new EventEmitter<GedDocument>();
  @Output() edit = new EventEmitter<GedDocument>();

  @ViewChild('viewerContainer') viewerContainer!: ElementRef<HTMLDivElement>;
  @ViewChild('imageViewer') imageViewer!: ElementRef<HTMLImageElement>;
  @ViewChild('pdfViewer') pdfViewer!: ElementRef<HTMLObjectElement>;

  // État du visualiseur
  viewerState: ViewerState = {
    currentPage: 1,
    totalPages: 1,
    zoom: 100,
    rotation: 0,
    isFullscreen: false,
    isLoading: true,
    error: null
  };

  // Données du document
  documentContent: Blob | null = null;
  documentUrl: string | null = null;
  documentType: 'pdf' | 'image' | 'text' | 'unsupported' = 'unsupported';

  // Options de zoom
  zoomOptions = [25, 50, 75, 100, 125, 150, 200, 300, 400];
  
  // Subject pour la destruction
  private destroy$ = new Subject<void>();

  constructor(
    private documentService: DocumentManagementService,
    private snackBar: MatSnackBar,
    private dialogRef: MatDialogRef<DocumentViewerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { document: GedDocument }
  ) {
    this.document = data.document;
  }

  ngOnInit(): void {
    this.loadDocument();
    this.detectDocumentType();
  }

  ngOnDestroy(): void {
    this.cleanup();
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ==================== CHARGEMENT DU DOCUMENT ====================

  loadDocument(): void {
    this.viewerState.isLoading = true;
    this.viewerState.error = null;

    this.documentService.previewDocument(this.document.id!)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          this.documentContent = blob;
          this.documentUrl = URL.createObjectURL(blob);
          this.viewerState.isLoading = false;
        },
        error: (error) => {
          this.viewerState.error = 'Erreur lors du chargement du document';
          this.viewerState.isLoading = false;
          this.snackBar.open(this.viewerState.error, 'Fermer', { duration: 5000 });
        }
      });
  }

  private detectDocumentType(): void {
    if (!this.document.mimeType) {
      this.documentType = 'unsupported';
      return;
    }

    const mimeType = this.document.mimeType.toLowerCase();
    
    if (mimeType.includes('pdf')) {
      this.documentType = 'pdf';
    } else if (mimeType.includes('image')) {
      this.documentType = 'image';
    } else if (mimeType.includes('text')) {
      this.documentType = 'text';
    } else {
      this.documentType = 'unsupported';
    }
  }

  // ==================== NAVIGATION ====================

  nextPage(): void {
    if (this.viewerState.currentPage < this.viewerState.totalPages) {
      this.viewerState.currentPage++;
    }
  }

  previousPage(): void {
    if (this.viewerState.currentPage > 1) {
      this.viewerState.currentPage--;
    }
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.viewerState.totalPages) {
      this.viewerState.currentPage = page;
    }
  }

  // ==================== ZOOM ET ROTATION ====================

  setZoom(zoom: number): void {
    this.viewerState.zoom = Math.max(25, Math.min(400, zoom));
  }

  zoomIn(): void {
    const currentIndex = this.zoomOptions.indexOf(this.viewerState.zoom);
    if (currentIndex < this.zoomOptions.length - 1) {
      this.setZoom(this.zoomOptions[currentIndex + 1]);
    } else {
      this.setZoom(this.viewerState.zoom * 1.25);
    }
  }

  zoomOut(): void {
    const currentIndex = this.zoomOptions.indexOf(this.viewerState.zoom);
    if (currentIndex > 0) {
      this.setZoom(this.zoomOptions[currentIndex - 1]);
    } else {
      this.setZoom(this.viewerState.zoom / 1.25);
    }
  }

  resetZoom(): void {
    this.setZoom(100);
  }

  rotateLeft(): void {
    this.viewerState.rotation = (this.viewerState.rotation - 90) % 360;
  }

  rotateRight(): void {
    this.viewerState.rotation = (this.viewerState.rotation + 90) % 360;
  }

  resetRotation(): void {
    this.viewerState.rotation = 0;
  }

  // ==================== PLEIN ÉCRAN ====================

  toggleFullscreen(): void {
    this.viewerState.isFullscreen = !this.viewerState.isFullscreen;
    
    if (this.viewerState.isFullscreen) {
      this.enterFullscreen();
    } else {
      this.exitFullscreen();
    }
  }

  private enterFullscreen(): void {
    const element = this.viewerContainer.nativeElement;
    if (element.requestFullscreen) {
      element.requestFullscreen();
    } else if ((element as any).webkitRequestFullscreen) {
      (element as any).webkitRequestFullscreen();
    } else if ((element as any).msRequestFullscreen) {
      (element as any).msRequestFullscreen();
    }
  }

  private exitFullscreen(): void {
    if (document.exitFullscreen) {
      document.exitFullscreen();
    } else if ((document as any).webkitExitFullscreen) {
      (document as any).webkitExitFullscreen();
    } else if ((document as any).msExitFullscreen) {
      (document as any).msExitFullscreen();
    }
  }

  // ==================== ACTIONS ====================

  onDownload(): void {
    this.download.emit(this.document);
    this.documentService.downloadDocument(this.document.id!)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = this.document.fileName;
          link.click();
          window.URL.revokeObjectURL(url);
        },
        error: (error) => {
          this.snackBar.open('Erreur lors du téléchargement', 'Fermer', { duration: 5000 });
        }
      });
  }

  onEdit(): void {
    this.edit.emit(this.document);
  }

  onClose(): void {
    this.close.emit();
    this.dialogRef.close();
  }

  onPrint(): void {
    if (this.documentType === 'pdf' && this.documentUrl) {
      const printWindow = window.open(this.documentUrl, '_blank');
      if (printWindow) {
        printWindow.onload = () => {
          printWindow.print();
        };
      }
    } else {
      window.print();
    }
  }

  // ==================== UTILITAIRES ====================

  private cleanup(): void {
    if (this.documentUrl) {
      URL.revokeObjectURL(this.documentUrl);
    }
  }

  formatFileSize(bytes: number | undefined): string {
    if (!bytes) return '0 Bytes';
    return this.documentService.formatFileSize(bytes);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('fr-FR');
  }

  getDocumentTypeIcon(): string {
    return this.documentService.getDocumentTypeIcon(this.document.documentType);
  }

  getStatusColor(): string {
    return this.documentService.getStatusColor(this.document.status);
  }

  getSecurityLevelColor(): string {
    return this.documentService.getSecurityLevelColor(this.document.securityLevel);
  }

  // ==================== GETTERS ====================

  get canNavigate(): boolean {
    return this.documentType === 'pdf' && this.viewerState.totalPages > 1;
  }

  get canZoom(): boolean {
    return this.documentType === 'pdf' || this.documentType === 'image';
  }

  get canRotate(): boolean {
    return this.documentType === 'image';
  }

  get canPrint(): boolean {
    return this.documentType === 'pdf' || this.documentType === 'text';
  }

  get pageInfo(): string {
    return `${this.viewerState.currentPage} / ${this.viewerState.totalPages}`;
  }

  get zoomInfo(): string {
    return `${this.viewerState.zoom}%`;
  }

  get rotationInfo(): string {
    return `${this.viewerState.rotation}°`;
  }
}
