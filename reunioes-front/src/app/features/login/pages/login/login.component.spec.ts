import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../../../core/services/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let auth: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    auth = jasmine.createSpyObj('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.resolveTo(true);

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  function preencher(): void {
    component.form.setValue({ email: 'ana@teste.com', senha: '123456' });
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('não chama o login quando o formulário é inválido', () => {
    component.login();

    expect(auth.login).not.toHaveBeenCalled();
    expect(component.form.touched).toBeTrue();
  });

  it('navega para /reunioes quando o login dá certo', () => {
    auth.login.and.returnValue(
      of({ token: 'token', nome: 'Ana', email: 'ana@teste.com', papel: 'ADMIN' })
    );
    preencher();

    component.login();

    expect(auth.login).toHaveBeenCalledWith('ana@teste.com', '123456');
    expect(router.navigate).toHaveBeenCalledWith(['/reunioes']);
    expect(component.erroLogin).toBe('');
  });

  it('mostra mensagem de credenciais incorretas no 401', () => {
    auth.login.and.returnValue(throwError(() => new HttpErrorResponse({ status: 401 })));
    preencher();

    component.login();

    expect(component.erroLogin).toBe('E-mail ou senha incorretos');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('avisa quando o servidor não responde', () => {
    auth.login.and.returnValue(throwError(() => new HttpErrorResponse({ status: 0 })));
    preencher();

    component.login();

    expect(component.erroLogin).toBe('Sem conexão com o servidor. Verifique se a API está no ar.');
  });

  it('mostra mensagem genérica para outros erros', () => {
    auth.login.and.returnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    preencher();

    component.login();

    expect(component.erroLogin).toBe('Não foi possível entrar agora. Tente novamente em instantes.');
  });
});