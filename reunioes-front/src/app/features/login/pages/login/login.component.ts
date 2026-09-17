import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { COLABORADORES_MOCK } from '../../../colaboradores/data/colaboradores.mock';
import { ColaboradorService } from '../../../../core/services/colaborador.service';
import { AlertaService } from '../../../../core/services/alerta.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  form: FormGroup;
  erroLogin = false;

  constructor(private fb: FormBuilder, private router: Router, private colaboradorService: ColaboradorService, private alerta: AlertaService) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
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
    this.erroLogin = false;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const emailDigitado = this.form.value.email;

    this.colaboradorService.buscarPorEmail(emailDigitado).subscribe({
      next: (colaborador) => {
        localStorage.setItem('papel', colaborador.papel);

        if (colaborador.papel === 'ADMIN') {
          this.router.navigate(['/admin/reunioes']);
        } else if (colaborador.papel === 'USUARIO') {
          this.router.navigate(['/usuario/reunioes']);
        } else {
          this.erroLogin = true;
          this.alerta.erro(
            'Não foi possível entrar',
            'Este colaborador está sem papel definido. Procure o administrador.',
          );
        }
      },
      // E-mail que nao existe volta 404. Mensagem propria: o interceptor diria
      // "nao encontrado", que num login nao ajuda ninguem.
      error: () => {
        this.erroLogin = true;
      },
    });
  }
}
