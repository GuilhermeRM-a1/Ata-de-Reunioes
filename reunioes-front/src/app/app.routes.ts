import { Routes } from '@angular/router';
import { ShellComponent } from './shell/shell.component';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', redirectTo: 'reunioes', pathMatch: 'full' },
      // Troque os componentes abaixo quando criar as telas de verdade:
      // { path: 'reunioes', component: ReunioesComponent },
      // { path: 'colaboradores', component: ColaboradoresComponent },
    ]
  }
];