import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss'
})
export class ShellComponent {
  
  get isAdmin(): boolean {
    const papel = localStorage.getItem('papel') === 'ADMIN';
    return papel;
  }

  get reunioesRoute(): string {
    return this.isAdmin ? '/admin/reunioes' : '/usuario/reunioes';
  }

  get acoesRoute(): string {
    return this.isAdmin ? '/admin/acoes' : '/usuario/acoes';
  }

}