import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { ContextService } from './context.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService, private context: ContextService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.auth.getCurrentToken();
    const ctx = this.context.getContext();

    let headers: Record<string, string> = {
      'X-Company-Id': String(ctx.companyId),
      'X-Country-Code': ctx.countryCode,
      'X-Accounting-Standard': ctx.accountingStandard,
      'X-Currency': ctx.currency
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const cloned = req.clone({ setHeaders: headers, withCredentials: false });
    return next.handle(cloned);
  }
}

