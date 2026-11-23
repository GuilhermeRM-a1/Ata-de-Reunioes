import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { loginGuard } from './login.guard';

describe('loginGuard', () => {
  const authFalso = { estaLogado: false };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: AuthService, useValue: authFalso }]
    });
  });

  function executar() {
    return TestBed.runInInjectionContext(() =>
      loginGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
  }

  it('libera o /login quando não está logado', () => {
    authFalso.estaLogado = false;

    expect(executar()).toBeTrue();
  });

  it('redireciona para /reunioes quando já está logado', () => {
    authFalso.estaLogado = true;

    const resultado = executar();

    expect(resultado instanceof UrlTree).toBeTrue();
    expect(TestBed.inject(Router).serializeUrl(resultado as UrlTree)).toBe('/reunioes');
  });
});