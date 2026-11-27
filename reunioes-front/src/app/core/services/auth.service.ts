import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { API } from '../config/api.config';

export type Papel = 'ADMIN' | 'USUARIO';

export interface LoginResponse {
  token: string;
  nome: string;
  email: string;
  papel: Papel;
}

const URL_LOGIN = `${API.auth}/login`;
const URL_REGISTRO = `${API.auth}/register`;
const CHAVE_TOKEN = 'token';
const CHAVE_NOME = 'nome';
const CHAVE_PAPEL = 'papel';
const CHAVE_EMAIL = 'email';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly http = inject(HttpClient);

  login(email: string, senha: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(URL_LOGIN, { email, senha })
      .pipe(tap(resposta => this.guardarSessao(resposta)));
  }

  /**
   * Cadastro público. O back sempre cria o colaborador como USUARIO e já
   * devolve o token, então o usuário entra logado logo depois de se cadastrar.
   */
  registrar(nome: string, email: string, senha: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(URL_REGISTRO, { nome, email, senha })
      .pipe(tap(resposta => this.guardarSessao(resposta)));
  }

  guardarSessao(resposta: LoginResponse): void {
    localStorage.setItem(CHAVE_TOKEN, resposta.token);
    localStorage.setItem(CHAVE_NOME, resposta.nome);
    localStorage.setItem(CHAVE_EMAIL, resposta.email);
    localStorage.setItem(CHAVE_PAPEL, resposta.papel);
  }

  get token(): string | null {
    return localStorage.getItem(CHAVE_TOKEN);
  }

  get nome(): string | null {
    return localStorage.getItem(CHAVE_NOME);
  }

  get papel(): Papel | null {
    const valor = localStorage.getItem(CHAVE_PAPEL);
    return valor === 'ADMIN' || valor === 'USUARIO' ? valor : null;
  }

  get email(): string | null {
    return localStorage.getItem(CHAVE_EMAIL);
  }

  get isAdmin(): boolean {
    return this.papel === 'ADMIN';
  }

  get estaLogado(): boolean {
    const token = this.token;
    if (!token) {
      return false;
    }
    const expiracao = this.lerExpiracao(token);
    return expiracao !== null && expiracao * 1000 > Date.now();
  }

  sair(): void {
    localStorage.removeItem(CHAVE_TOKEN);
    localStorage.removeItem(CHAVE_NOME);
    localStorage.removeItem(CHAVE_EMAIL);
    localStorage.removeItem(CHAVE_PAPEL);
  }

  private lerExpiracao(token: string): number | null {
    try {
      const payload = token.split('.')[1];
      const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      const exp = JSON.parse(json).exp;
      return typeof exp === 'number' ? exp : null;
    } catch {
      return null;
    }
  }
}