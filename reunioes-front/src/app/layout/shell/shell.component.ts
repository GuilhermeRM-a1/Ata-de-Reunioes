import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss'
})
export class ShellComponent {
  /** Publico de proposito: o template le `auth.isAdmin` e `auth.papel`. */
  protected readonly auth = inject(AuthService);

  private readonly router = inject(Router);

  sair(): void {
    this.auth.sair();
    this.router.navigate(['/login']);
  }
}
