import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../../../core/services/auth.service';
import { CadastroComponent } from './cadastro.component';

describe('CadastroComponent', () => {
  let component: CadastroComponent;
  let fixture: ComponentFixture<CadastroComponent>;
  let auth: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    auth = jasmine.createSpyObj('AuthService', ['registrar']);

    await TestBed.configureTestingModule({
      imports: [CadastroComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.resolveTo(true);

    fixture = TestBed.createComponent(CadastroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  function preencher(sobrescrever: object = {}): void {
    component.form.setValue({
      nome: 'Maria Souza',
      email: 'maria@teste.com',
      senha: 'senha1234',
      confirmarSenha: 'senha1234',
      ...sobrescrever
    });
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('não envia quando o formulário é inválido', () => {
    component.cadastrar();

    expect(auth.registrar).not.toHaveBeenCalled();
    expect(component.form.touched).toBeTrue();
  });

  it('barra senha curta', () => {
    preencher({ senha: '123', confirmarSenha: '123' });

    expect(component.senha?.errors?.['minlength']).toBeTruthy();
    component.cadastrar();
    expect(auth.registrar).not.toHaveBeenCalled();
  });

  it('barra confirmação diferente da senha', () => {
    preencher({ confirmarSenha: 'outra-senha' });

    expect(component.form.errors?.['senhasDiferentes']).toBeTrue();
    component.cadastrar();
    expect(auth.registrar).not.toHaveBeenCalled();
  });

  it('cadastra e navega para /reunioes quando dá certo', () => {
    auth.registrar.and.returnValue(
      of({ token: 't', nome: 'Maria Souza', email: 'maria@teste.com', papel: 'USUARIO' })
    );
    preencher();

    component.cadastrar();

    expect(auth.registrar).toHaveBeenCalledOnceWith('Maria Souza', 'maria@teste.com', 'senha1234');
    expect(router.navigate).toHaveBeenCalledWith(['/reunioes']);
  });

  it('libera o botão de novo quando o cadastro falha', () => {
    auth.registrar.and.returnValue(throwError(() => ({ status: 409 })));
    preencher();

    component.cadastrar();

    expect(component.enviando).toBeFalse();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
