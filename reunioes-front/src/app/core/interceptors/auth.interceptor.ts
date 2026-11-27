import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // /auth/login e /auth/register são públicas: não levam token.
  if (req.url.includes('/auth/')) {
    return next(req);
  }

  const token = inject(AuthService).token;
  if (!token) {
    return next(req);
  }

  return next(
    req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
  );
};