import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { MdbRippleModule } from 'mdb-angular-ui-kit/ripple';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MdbFormsModule, MdbRippleModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);

  form: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]]
  });

  erroLogin = '';

  get email() {
    return this.form.get('email');
  }

  get senha() {
    return this.form.get('senha');
  }

  login(): void {
    this.erroLogin = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, senha } = this.form.value;

    this.auth.login(email, senha).subscribe({
      next: () => this.router.navigate(['/reunioes']),
      error: (err: HttpErrorResponse) => {
        if (err.status === 0) {
          this.erroLogin = 'Sem conexão com o servidor. Verifique se a API está no ar.';
        } else if (err.status === 401) {
          this.erroLogin = 'E-mail ou senha incorretos';
        } else {
          this.erroLogin = 'Não foi possível entrar agora. Tente novamente em instantes.';
        }
      }
    });
  }
}