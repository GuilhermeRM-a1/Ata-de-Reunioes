import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';
import { authGuard } from './core/guards/auth.guard';
import { loginGuard } from './core/guards/login.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    canActivate: [loginGuard],
    loadComponent: () =>
      import('./features/login/pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./layout/shell/shell.component').then(m => m.ShellComponent),
    children: [
      {
        path: 'reunioes',
        loadComponent: () =>
          import('./features/reunioes/pages/reunioes/reunioes.component').then(m => m.ReunioesComponent)
      },
      {
        path: 'reunioes/novo',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/reunioes/pages/reuniao-form/reuniao-form.component').then(m => m.ReuniaoFormComponent)
      },
      {
        path: 'reunioes/:id',
        loadComponent: () =>
          import('./features/reunioes/pages/reuniao-detalhe/reuniao-detalhe.component').then(m => m.ReuniaoDetalheComponent)
      },
      {
        path: 'reunioes/:id/editar',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/reunioes/pages/reuniao-form/reuniao-form.component').then(m => m.ReuniaoFormComponent)
      },
      {
        path: 'acoes',
        redirectTo: 'acoes/pendentes',
        pathMatch: 'full'
      },
      {
        path: 'acoes/pendentes',
        data: { status: 'pendentes' },
        loadComponent: () =>
          import('./features/acoes/pages/acoes/acoes.component').then(m => m.AcoesComponent)
      },
      {
        path: 'acoes/concluidas',
        data: { status: 'concluidas' },
        loadComponent: () =>
          import('./features/acoes/pages/acoes/acoes.component').then(m => m.AcoesComponent)
      },
      {
        path: 'colaboradores',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/colaboradores/pages/colaboradores/colaboradores.component').then(m => m.ColaboradoresComponent)
      },
      { path: 'admin/reunioes', redirectTo: 'reunioes', pathMatch: 'full' },
      { path: 'admin/reunioes/novo', redirectTo: 'reunioes/novo', pathMatch: 'full' },
      { path: 'admin/reunioes/:id', redirectTo: 'reunioes/:id', pathMatch: 'full' },
      { path: 'admin/reunioes/:id/editar', redirectTo: 'reunioes/:id/editar', pathMatch: 'full' },
      { path: 'admin/acoes', redirectTo: 'acoes/pendentes', pathMatch: 'full' },
      { path: 'usuario/reunioes', redirectTo: 'reunioes', pathMatch: 'full' },
      { path: 'usuario/reunioes/:id', redirectTo: 'reunioes/:id', pathMatch: 'full' },
      { path: 'usuario/acoes', redirectTo: 'acoes/pendentes', pathMatch: 'full' },
    ]
  },
  {
    path: '**',
    redirectTo: 'reunioes'
  }
];