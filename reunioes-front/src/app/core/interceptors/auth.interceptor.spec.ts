import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from '../services/auth.service';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let controle: HttpTestingController;
  let tokenAtual: string | null;

  beforeEach(() => {
    tokenAtual = 'aaa.bbb.ccc';

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: {
            get token() {
              return tokenAtual;
            }
          }
        }
      ]
    });

    http = TestBed.inject(HttpClient);
    controle = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controle.verify());

  it('adiciona o header Authorization nas chamadas à API', () => {
    http.get('http://localhost:8080/api/reunioes').subscribe();

    const requisicao = controle.expectOne('http://localhost:8080/api/reunioes');
    expect(requisicao.request.headers.get('Authorization')).toBe('Bearer aaa.bbb.ccc');
    requisicao.flush([]);
  });

  it('não adiciona o header no /auth/login', () => {
    http.post('http://localhost:8080/auth/login', {}).subscribe();

    const requisicao = controle.expectOne('http://localhost:8080/auth/login');
    expect(requisicao.request.headers.has('Authorization')).toBeFalse();
    requisicao.flush({});
  });

  it('não adiciona o header quando não há token', () => {
    tokenAtual = null;

    http.get('http://localhost:8080/api/reunioes').subscribe();

    const requisicao = controle.expectOne('http://localhost:8080/api/reunioes');
    expect(requisicao.request.headers.has('Authorization')).toBeFalse();
    requisicao.flush([]);
  });
});