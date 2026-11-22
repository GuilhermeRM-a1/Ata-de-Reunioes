import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService, LoginResponse } from './auth.service';

const URL_LOGIN = 'http://localhost:8080/auth/login';

function agora(): number {
  return Math.floor(Date.now() / 1000);
}

function criarToken(payload: object): string {
  return `cabecalho.${btoa(JSON.stringify(payload))}.assinatura`;
}

function criarResposta(papel: 'ADMIN' | 'USUARIO' = 'ADMIN'): LoginResponse {
  return {
    token: criarToken({ exp: agora() + 3600 }),
    nome: 'Ana',
    email: 'ana@teste.com',
    papel
  };
}

describe('AuthService', () => {
  let service: AuthService;
  let controle: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(AuthService);
    controle = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    controle.verify();
    localStorage.clear();
  });

  it('faz POST em /auth/login com e-mail e senha e guarda a sessão', () => {
    const resposta = criarResposta();
    let recebida: LoginResponse | undefined;

    service.login('ana@teste.com', '123456').subscribe(r => (recebida = r));

    const requisicao = controle.expectOne(URL_LOGIN);
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual({ email: 'ana@teste.com', senha: '123456' });
    requisicao.flush(resposta);

    expect(recebida).toEqual(resposta);
    expect(service.token).toBe(resposta.token);
    expect(service.nome).toBe('Ana');
    expect(service.email).toBe('ana@teste.com');
    expect(service.papel).toBe('ADMIN');
    expect(service.estaLogado).toBeTrue();
  });

  it('não guarda nada quando o login falha', () => {
    service.login('ana@teste.com', 'errada').subscribe({ error: () => {} });

    controle.expectOne(URL_LOGIN).flush(
      { detail: 'Credenciais inválidas' },
      { status: 401, statusText: 'Unauthorized' }
    );

    expect(service.token).toBeNull();
    expect(service.estaLogado).toBeFalse();
  });

  it('está logado com token ainda válido', () => {
    service.guardarSessao(criarResposta());

    expect(service.estaLogado).toBeTrue();
  });

  it('não está logado sem token', () => {
    expect(service.estaLogado).toBeFalse();
  });

  it('não está logado com token expirado', () => {
    service.guardarSessao({ ...criarResposta(), token: criarToken({ exp: agora() - 60 }) });

    expect(service.estaLogado).toBeFalse();
  });

  it('não está logado com token sem exp', () => {
    service.guardarSessao({ ...criarResposta(), token: criarToken({ sub: 'ana@teste.com' }) });

    expect(service.estaLogado).toBeFalse();
  });

  it('não está logado com token malformado', () => {
    service.guardarSessao({ ...criarResposta(), token: 'abc' });

    expect(service.estaLogado).toBeFalse();
  });

  it('isAdmin é verdadeiro só para o papel ADMIN', () => {
    service.guardarSessao(criarResposta('ADMIN'));
    expect(service.isAdmin).toBeTrue();

    service.guardarSessao(criarResposta('USUARIO'));
    expect(service.isAdmin).toBeFalse();
  });

  it('ignora papel inválido guardado no navegador', () => {
    localStorage.setItem('papel', 'OUTRO');

    expect(service.papel).toBeNull();
    expect(service.isAdmin).toBeFalse();
  });

  it('sair limpa token, nome, e-mail e papel', () => {
    service.guardarSessao(criarResposta());

    service.sair();

    expect(service.token).toBeNull();
    expect(service.nome).toBeNull();
    expect(service.email).toBeNull();
    expect(service.papel).toBeNull();
    expect(service.estaLogado).toBeFalse();
  });
});