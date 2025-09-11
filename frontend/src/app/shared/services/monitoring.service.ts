import { Injectable } from '@angular/core';
import { ErrorHandler } from '@angular/core';
import { Router } from '@angular/router';

export interface PerformanceMetric {
  name: string;
  value: number;
  timestamp: Date;
  metadata?: any;
}

export interface ErrorReport {
  message: string;
  stack?: string;
  url: string;
  timestamp: Date;
  userAgent: string;
  userId?: string;
  severity: 'low' | 'medium' | 'high' | 'critical';
}

export interface UserActivity {
  action: string;
  component: string;
  timestamp: Date;
  duration?: number;
  metadata?: any;
}

@Injectable({
  providedIn: 'root'
})
export class MonitoringService {
  private performanceMetrics: PerformanceMetric[] = [];
  private errorReports: ErrorReport[] = [];
  private userActivities: UserActivity[] = [];
  private isEnabled = true;

  constructor(private router: Router) {
    this.initializeMonitoring();
  }

  private initializeMonitoring(): void {
    if (!this.isEnabled) return;

    // Monitor page load performance
    this.monitorPageLoad();
    
    // Monitor route changes
    this.monitorRouteChanges();
    
    // Monitor memory usage
    this.monitorMemoryUsage();
    
    // Monitor network requests
    this.monitorNetworkRequests();
  }

  // Performance Monitoring
  private monitorPageLoad(): void {
    window.addEventListener('load', () => {
      setTimeout(() => {
        const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
        
        this.recordMetric('page_load_time', navigation.loadEventEnd - navigation.fetchStart);
        this.recordMetric('dom_content_loaded', navigation.domContentLoadedEventEnd - navigation.fetchStart);
        this.recordMetric('first_paint', this.getFirstPaint());
        this.recordMetric('first_contentful_paint', this.getFirstContentfulPaint());
      }, 0);
    });
  }

  private getFirstPaint(): number {
    const paintEntries = performance.getEntriesByType('paint');
    const firstPaint = paintEntries.find(entry => entry.name === 'first-paint');
    return firstPaint ? firstPaint.startTime : 0;
  }

  private getFirstContentfulPaint(): number {
    const paintEntries = performance.getEntriesByType('paint');
    const firstContentfulPaint = paintEntries.find(entry => entry.name === 'first-contentful-paint');
    return firstContentfulPaint ? firstContentfulPaint.startTime : 0;
  }

  private monitorMemoryUsage(): void {
    if ('memory' in performance) {
      setInterval(() => {
        const memory = (performance as any).memory;
        this.recordMetric('memory_used', memory.usedJSHeapSize);
        this.recordMetric('memory_total', memory.totalJSHeapSize);
        this.recordMetric('memory_limit', memory.jsHeapSizeLimit);
      }, 30000); // Every 30 seconds
    }
  }

  private monitorNetworkRequests(): void {
    const originalFetch = window.fetch;
    window.fetch = async (...args) => {
      const startTime = performance.now();
      try {
        const response = await originalFetch(...args);
        const endTime = performance.now();
        this.recordMetric('network_request_time', endTime - startTime, {
          url: args[0],
          status: response.status,
          method: 'fetch'
        });
        return response;
      } catch (error) {
        const endTime = performance.now();
        this.recordMetric('network_request_error', endTime - startTime, {
          url: args[0],
          error: error instanceof Error ? error.message : String(error)
        });
        throw error;
      }
    };
  }

  private monitorRouteChanges(): void {
    this.router.events.subscribe(event => {
      if (event.constructor.name === 'NavigationEnd') {
        this.recordActivity('route_change', 'router', {
          url: (event as any).url,
          timestamp: new Date()
        });
      }
    });
  }

  // Public Methods
  recordMetric(name: string, value: number, metadata?: any): void {
    if (!this.isEnabled) return;

    const metric: PerformanceMetric = {
      name,
      value,
      timestamp: new Date(),
      metadata
    };

    this.performanceMetrics.push(metric);
    
    // Keep only last 1000 metrics
    if (this.performanceMetrics.length > 1000) {
      this.performanceMetrics = this.performanceMetrics.slice(-1000);
    }

    // Send to monitoring service
    this.sendMetricToService(metric);
  }

  recordError(error: Error, severity: 'low' | 'medium' | 'high' | 'critical' = 'medium'): void {
    if (!this.isEnabled) return;

    const errorReport: ErrorReport = {
      message: error.message,
      stack: error.stack,
      url: window.location.href,
      timestamp: new Date(),
      userAgent: navigator.userAgent,
      severity
    };

    this.errorReports.push(errorReport);
    
    // Keep only last 100 error reports
    if (this.errorReports.length > 100) {
      this.errorReports = this.errorReports.slice(-100);
    }

    // Send to monitoring service
    this.sendErrorToService(errorReport);
  }

  recordActivity(action: string, component: string, metadata?: any): void {
    if (!this.isEnabled) return;

    const activity: UserActivity = {
      action,
      component,
      timestamp: new Date(),
      metadata
    };

    this.userActivities.push(activity);
    
    // Keep only last 500 activities
    if (this.userActivities.length > 500) {
      this.userActivities = this.userActivities.slice(-500);
    }

    // Send to monitoring service
    this.sendActivityToService(activity);
  }

  // Service Integration
  private sendMetricToService(metric: PerformanceMetric): void {
    // Send to your monitoring service (e.g., DataDog, New Relic, custom API)
    console.log('Performance Metric:', metric);
    
    // Example: Send to custom API
    // fetch('/api/monitoring/metrics', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(metric)
    // }).catch(err => console.error('Failed to send metric:', err));
  }

  private sendErrorToService(errorReport: ErrorReport): void {
    // Send to your error tracking service (e.g., Sentry, Bugsnag, custom API)
    console.error('Error Report:', errorReport);
    
    // Example: Send to custom API
    // fetch('/api/monitoring/errors', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(errorReport)
    // }).catch(err => console.error('Failed to send error report:', err));
  }

  private sendActivityToService(activity: UserActivity): void {
    // Send to your analytics service (e.g., Google Analytics, Mixpanel, custom API)
    console.log('User Activity:', activity);
    
    // Example: Send to custom API
    // fetch('/api/monitoring/activities', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(activity)
    // }).catch(err => console.error('Failed to send activity:', err));
  }

  // Utility Methods
  getPerformanceMetrics(): PerformanceMetric[] {
    return [...this.performanceMetrics];
  }

  getErrorReports(): ErrorReport[] {
    return [...this.errorReports];
  }

  getUserActivities(): UserActivity[] {
    return [...this.userActivities];
  }

  clearData(): void {
    this.performanceMetrics = [];
    this.errorReports = [];
    this.userActivities = [];
  }

  enable(): void {
    this.isEnabled = true;
  }

  disable(): void {
    this.isEnabled = false;
  }

  isMonitoringEnabled(): boolean {
    return this.isEnabled;
  }

  // Health Check
  getHealthStatus(): any {
    return {
      isEnabled: this.isEnabled,
      metricsCount: this.performanceMetrics.length,
      errorsCount: this.errorReports.length,
      activitiesCount: this.userActivities.length,
      memoryUsage: this.getMemoryUsage(),
      uptime: this.getUptime()
    };
  }

  private getMemoryUsage(): any {
    if ('memory' in performance) {
      const memory = (performance as any).memory;
      return {
        used: memory.usedJSHeapSize,
        total: memory.totalJSHeapSize,
        limit: memory.jsHeapSizeLimit
      };
    }
    return null;
  }

  private getUptime(): number {
    return performance.now();
  }
}

// Custom Error Handler
@Injectable()
export class MonitoringErrorHandler implements ErrorHandler {
  constructor(private monitoringService: MonitoringService) {}

  handleError(error: any): void {
    console.error('Global error caught:', error);
    
    // Determine severity based on error type
    let severity: 'low' | 'medium' | 'high' | 'critical' = 'medium';
    
    if (error.name === 'ChunkLoadError') {
      severity = 'high';
    } else if (error.message?.includes('Network')) {
      severity = 'medium';
    } else if (error.message?.includes('TypeError')) {
      severity = 'high';
    }

    this.monitoringService.recordError(error, severity);
  }
}
