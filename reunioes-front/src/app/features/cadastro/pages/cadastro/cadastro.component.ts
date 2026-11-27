import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { MdbRippleModule } from 'mdb-angular-ui-kit/ripple';
import { AuthService } from '../../../../core/services/auth.service';

/** Mesmos limites do back (RegistroRequest): bcrypt só considera 72 caracteres. */
const SENHA_MIN = 8;
const SENHA_MAX = 72;

function senhasIguais(grupo: AbstractControl): ValidationErrors | null {
  const senha = grupo.get('senha')?.value;
  const confirmacao = grupo.get('confirmarSenha')?.value;
  return senha === confirmacao ? null : { senhasDiferentes: true };
}

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, MdbFormsModule, MdbRippleModule],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.scss'
})
export class CadastroComponent {
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);

  readonly senhaMin = SENHA_MIN;
  enviando = false;

  form: FormGroup = this.fb.group(
    {
      nome: ['', [Validators.required, Validators.pattern(/.*\S.*/)]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(SENHA_MIN), Validators.maxLength(SENHA_MAX)]],
      confirmarSenha: ['', [Validators.required]]
    },
    { validators: senhasIguais }
  );

  get nome() { return this.form.get('nome'); }
  get email() { return this.form.get('email'); }
  get senha() { return this.form.get('senha'); }
  get confirmarSenha() { return this.form.get('confirmarSenha'); }

  get senhasDiferem(): boolean {
    return !!this.form.errors?.['senhasDiferentes'] && !!this.confirmarSenha?.touched;
  }

  cadastrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { nome, email, senha } = this.form.value;
    this.enviando = true;

    // Erros HTTP (409 e-mail repetido, 400 validação, sem conexão) já viram
    // alerta no erroInterceptor; aqui só libera o botão de novo.
    this.auth.registrar(nome.trim(), email.trim(), senha).subscribe({
      next: () => this.router.navigate(['/reunioes']),
      error: () => (this.enviando = false)
    });
  }
}
