import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  const authFalso = { estaLogado: false };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: AuthService, useValue: authFalso }]
    });
  });

  function executar() {
    return TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
  }

  it('libera a rota quando está logado', () => {
    authFalso.estaLogado = true;

    expect(executar()).toBeTrue();
  });

  it('redireciona para /login quando não está logado', () => {
    authFalso.estaLogado = false;

    const resultado = executar();

    expect(resultado instanceof UrlTree).toBeTrue();
    expect(TestBed.inject(Router).serializeUrl(resultado as UrlTree)).toBe('/login');
  });
});