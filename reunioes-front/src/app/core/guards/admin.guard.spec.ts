import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AlertaService } from '../services/alerta.service';
import { AuthService } from '../services/auth.service';
import { adminGuard } from './admin.guard';

describe('adminGuard', () => {
  const authFalso = { isAdmin: false };
  let alerta: jasmine.SpyObj<AlertaService>;

  beforeEach(() => {
    alerta = jasmine.createSpyObj('AlertaService', ['erro', 'sucesso', 'confirmar']);

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authFalso },
        { provide: AlertaService, useValue: alerta }
      ]
    });
  });

  function executar() {
    return TestBed.runInInjectionContext(() =>
      adminGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );
  }

  it('libera a rota para ADMIN', () => {
    authFalso.isAdmin = true;

    expect(executar()).toBeTrue();
    expect(alerta.erro).not.toHaveBeenCalled();
  });

  it('avisa e redireciona para /reunioes quando não é ADMIN', () => {
    authFalso.isAdmin = false;

    const resultado = executar();

    expect(resultado instanceof UrlTree).toBeTrue();
    expect(TestBed.inject(Router).serializeUrl(resultado as UrlTree)).toBe('/reunioes');
    expect(alerta.erro).toHaveBeenCalledOnceWith('Acesso negado', 'Você não tem permissão');
  });
});