import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { COLABORADORES_MOCK } from '../../../colaboradores/data/colaboradores.mock';

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

  constructor(private fb: FormBuilder, private router: Router) {
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
    const colaboradorEncontrado = COLABORADORES_MOCK.some(
      c => c.email.toLowerCase() === emailDigitado.toLowerCase()
    );

    if (colaboradorEncontrado) {
      this.router.navigate(['/reunioes']);
    } else {
      this.erroLogin = true;
    }
  }
}