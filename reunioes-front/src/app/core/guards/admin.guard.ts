import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AlertaService } from '../services/alerta.service';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const alerta = inject(AlertaService);
  const router = inject(Router);

  if (auth.isAdmin) {
    return true;
  }

  alerta.erro('Acesso negado', 'Você não tem permissão');
  return router.createUrlTree(['/reunioes']);
};