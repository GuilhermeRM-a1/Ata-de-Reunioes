import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { AlertaService } from '../services/alerta.service';
import { AuthService } from '../services/auth.service';
import { erroInterceptor } from './erro.interceptor';

describe('erroInterceptor', () => {
  let http: HttpClient;
  let controle: HttpTestingController;
  let alerta: jasmine.SpyObj<AlertaService>;
  let auth: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    alerta = jasmine.createSpyObj('AlertaService', ['erro', 'sucesso', 'confirmar']);
    auth = jasmine.createSpyObj('AuthService', ['sair']);
    router = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([erroInterceptor])),
        provideHttpClientTesting(),
        { provide: AlertaService, useValue: alerta },
        { provide: AuthService, useValue: auth },
        { provide: Router, useValue: router },
      ],
    });

    http = TestBed.inject(HttpClient);
    controle = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controle.verify());

  it('mostra a mensagem do ProblemDetail que o back devolve', () => {
    http.get('/api/reunioes/999').subscribe({ error: () => {} });

    controle.expectOne('/api/reunioes/999').flush(
      { title: 'Recurso não encontrado', detail: 'Reunião não encontrada(o) para o id 999' },
      { status: 404, statusText: 'Not Found' },
    );

    expect(alerta.erro).toHaveBeenCalledWith(
      'Não encontrado',
      'Reunião não encontrada(o) para o id 999',
    );
  });

  it('lista campo a campo quando a validação reprova o corpo', () => {
    http.post('/api/reunioes', {}).subscribe({ error: () => {} });

    controle.expectOne('/api/reunioes').flush(
      {
        title: 'Campos inválidos',
        detail: 'Um ou mais campos foram rejeitados na validação.',
        campos: [
          { campo: 'titulo', mensagem: 'O título é obrigatório' },
          { campo: 'data', mensagem: 'A data é obrigatória' },
        ],
      },
      { status: 400, statusText: 'Bad Request' },
    );

    expect(alerta.erro).toHaveBeenCalledWith(
      'Dados inválidos',
      'titulo: O título é obrigatório\ndata: A data é obrigatória',
    );
  });

  it('avisa quando o servidor não responde', () => {
    http.get('/api/reunioes').subscribe({ error: () => {} });

    controle.expectOne('/api/reunioes').error(new ProgressEvent('erro de rede'), { status: 0 });

    expect(alerta.erro).toHaveBeenCalledWith(
      'Sem conexão com o servidor',
      'O servidor não respondeu. Verifique se a API está no ar e tente de novo.',
    );
  });

  it('repassa o erro adiante, para quem chamou ainda poder reagir', () => {
    let recebido: unknown = null;

    http.get('/api/reunioes').subscribe({ error: (e) => (recebido = e) });

    controle.expectOne('/api/reunioes').flush(
      { detail: 'Falhou' },
      { status: 503, statusText: 'Service Unavailable' },
    );

    expect(recebido).toBeTruthy();
  });

  it('não incomoda o usuário quando a requisição dá certo', () => {
    http.get('/api/reunioes').subscribe();

    controle.expectOne('/api/reunioes').flush([]);

    expect(alerta.erro).not.toHaveBeenCalled();
  });

  it('encerra a sessão e vai para o login no 401 fora do login', () => {
    http.get('/api/reunioes').subscribe({ error: () => {} });

    controle.expectOne('/api/reunioes').flush(
      { detail: 'Token expirado' },
      { status: 401, statusText: 'Unauthorized' },
    );

    expect(auth.sair).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
    expect(alerta.erro).toHaveBeenCalledOnceWith('Sessão expirada', 'Faça login novamente.');
  });

  it('não duplica o alerta nem derruba a sessão no 401 do próprio login', () => {
    let recebido: unknown = null;

    http
      .post('http://localhost:8080/auth/login', {})
      .subscribe({ error: (e) => (recebido = e) });

    controle.expectOne('http://localhost:8080/auth/login').flush(
      { detail: 'Credenciais inválidas' },
      { status: 401, statusText: 'Unauthorized' },
    );

    expect(alerta.erro).not.toHaveBeenCalled();
    expect(auth.sair).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(recebido).toBeTruthy();
  });

  it('avisa que não há permissão no 403', () => {
    http.get('/api/colaboradores').subscribe({ error: () => {} });

    controle.expectOne('/api/colaboradores').flush(
      { detail: 'Acesso negado' },
      { status: 403, statusText: 'Forbidden' },
    );

    expect(alerta.erro).toHaveBeenCalledOnceWith('Acesso negado', 'Você não tem permissão');
    expect(auth.sair).not.toHaveBeenCalled();
  });
});