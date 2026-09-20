import { Injectable } from '@angular/core';

/** Papeis aceitos pelo back (enum Papel). */
export type Papel = 'ADMIN' | 'USUARIO';

const CHAVE_PAPEL = 'papel';
const CHAVE_EMAIL = 'email';

/**
 * Unico ponto do front que le e escreve o papel do usuario logado.
 * Nenhum componente deve tocar o localStorage direto.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  guardarSessao(email: string, papel: string): void {
    localStorage.setItem(CHAVE_PAPEL, papel);
    localStorage.setItem(CHAVE_EMAIL, email);
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
    return this.papel !== null;
  }

  sair(): void {
    localStorage.removeItem(CHAVE_PAPEL);
    localStorage.removeItem(CHAVE_EMAIL);
  }
}
