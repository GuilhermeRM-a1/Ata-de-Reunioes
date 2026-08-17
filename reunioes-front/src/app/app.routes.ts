import { Routes } from '@angular/router';
import { ShellComponent } from './shell/shell.component';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/login/pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', redirectTo: 'reunioes', pathMatch: 'full' },
      { path: 'reunioes', loadComponent: () => import('./features/reunioes/pages/reunioes/reunioes.component').then(m => m.ReunioesComponent) },
      { path: 'reunioes/novo', loadComponent: () => import('./features/reunioes/pages/reuniao-novo/reuniao-novo.component').then(m => m.ReuniaoNovoComponent) },
      { path: 'reunioes/:id', loadComponent: () => import('./features/reunioes/pages/reuniao-detalhe/reuniao-detalhe.component').then(m => m.ReuniaoDetalheComponent) },
      { path: 'reunioes/:id/editar', loadComponent: () => import('./features/reunioes/pages/reuniao-editar/reuniao-editar.component').then(m => m.ReuniaoEditarComponent) },
      { path: 'colaboradores', loadComponent: () => import('./features/colaboradores/pages/colaboradores/colaboradores.component').then(m => m.ColaboradoresComponent) },
    ]
  }
];