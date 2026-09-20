import { Routes } from '@angular/router';

/**
 * Rotas unificadas. Nao existe mais prefixo /admin nem /usuario:
 * a mesma tela atende os dois perfis e o AuthService decide o que aparece.
 */
export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
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
        loadComponent: () =>
          import('./features/reunioes/pages/reuniao-form/reuniao-form.component').then(m => m.ReuniaoFormComponent)
      },
      {
        path: 'acoes',
        loadComponent: () =>
          import('./features/acoes/pages/acoes/acoes.component').then(m => m.AcoesComponent)
      },
      {
        path: 'colaboradores',
        loadComponent: () =>
          import('./features/colaboradores/pages/colaboradores/colaboradores.component').then(m => m.ColaboradoresComponent)
      },

      // Caminhos antigos: mantidos como redirect para nao quebrar link salvo.
      { path: 'admin/reunioes', redirectTo: 'reunioes', pathMatch: 'full' },
      { path: 'admin/reunioes/novo', redirectTo: 'reunioes/novo', pathMatch: 'full' },
      { path: 'admin/reunioes/:id', redirectTo: 'reunioes/:id', pathMatch: 'full' },
      { path: 'admin/reunioes/:id/editar', redirectTo: 'reunioes/:id/editar', pathMatch: 'full' },
      { path: 'admin/acoes', redirectTo: 'acoes', pathMatch: 'full' },
      { path: 'usuario/reunioes', redirectTo: 'reunioes', pathMatch: 'full' },
      { path: 'usuario/reunioes/:id', redirectTo: 'reunioes/:id', pathMatch: 'full' },
      { path: 'usuario/acoes', redirectTo: 'acoes', pathMatch: 'full' },
    ]
  },
  {
    path: '**',
    redirectTo: 'reunioes'
  }
];
