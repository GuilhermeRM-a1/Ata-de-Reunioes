import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ColaboradorService } from '../../../../core/services/colaborador.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly auth = inject(AuthService);

  form: FormGroup;
  /** Mensagem exibida no formulario. Vazia quando nao ha erro. */
  erroLogin = '';

  constructor(private fb: FormBuilder, private router: Router, private colaboradorService: ColaboradorService) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      // A senha e obrigatoria no formulario, mas ainda NAO e verificada:
      // o back nao tem endpoint de autenticacao. O login so confere o email.
      senha: ['', [Validators.required]]
    });
  }

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

    const emailDigitado = this.form.value.email;

    this.colaboradorService.buscarPorEmail(emailDigitado).subscribe({
      next: (colaborador) => {
        if (colaborador.papel === 'ADMIN' || colaborador.papel === 'USUARIO') {
          this.auth.guardarSessao(emailDigitado, colaborador.papel);
          this.router.navigate(['/reunioes']);
        } else {
          this.erroLogin = 'Seu cadastro está sem papel definido. Procure um administrador.';
        }
      },
      error: (err: HttpErrorResponse) => {
        // Separar os casos importa: servidor fora e email errado sao
        // problemas diferentes e o usuario precisa saber qual e o dele.
        if (err.status === 0) {
          this.erroLogin = 'Sem conexão com o servidor. Verifique se a API está no ar.';
        } else if (err.status === 404) {
          this.erroLogin = 'E-mail não encontrado. Verifique e tente novamente.';
        } else {
          this.erroLogin = 'Não foi possível entrar agora. Tente novamente em instantes.';
        }
      }
    });
  }
}
