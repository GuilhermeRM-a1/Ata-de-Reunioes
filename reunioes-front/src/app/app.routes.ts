import { Routes } from '@angular/router';
import { ShellComponent } from './shell/shell.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/login/pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: 'admin/reunioes', loadComponent: () => import('./features/reunioes/pages-admin/reunioes/reunioes.component').then(m => m.ReunioesComponent) },
      { path: 'admin/reunioes/novo', loadComponent: () => import('./features/reunioes/pages-admin/reuniao-form/reuniao-form.component').then(m => m.ReuniaoFormComponent) },
      { path: 'admin/reunioes/:id', loadComponent: () => import('./features/reunioes/pages-admin/reuniao-detalhe/reuniao-detalhe.component').then(m => m.ReuniaoDetalheComponent) },
      { path: 'admin/reunioes/:id/editar', loadComponent: () => import('./features/reunioes/pages-admin/reuniao-form/reuniao-form.component').then(m => m.ReuniaoFormComponent) },
      { path: 'admin/acoes', loadComponent: () => import('./features/acao/pages-admin/acao/acao.component').then(m => m.AcaoComponent) },
      { path: 'usuario/reunioes', loadComponent: () => import('./features/reunioes/pages-usuario/reunioes/reunioes.component').then(m => m.ReunioesComponent) },
      { path: 'usuario/reunioes/:id', loadComponent: () => import('./features/reunioes/pages-usuario/reuniao-detalhe/reuniao-detalhe.component').then(m => m.ReuniaoDetalheComponent) },
      { path: 'usuario/acoes', loadComponent: () => import('./features/acao/pages-usuario/acao/acao.component').then(m => m.AcaoComponent) },
    ]
  }
];