import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AlertaService } from '../services/alerta.service';
import { AuthService } from '../services/auth.service';

interface ProblemDetail {
  title?: string;
  detail?: string;
  status?: number;
  campos?: { campo: string; mensagem: string }[];
}

function tituloPara(status: number): string {
  switch (status) {
    case 0:   return 'Sem conexão com o servidor';
    case 400: return 'Dados inválidos';
    case 403: return 'Acesso negado';
    case 404: return 'Não encontrado';
    case 409: return 'Operação não permitida';
    case 503: return 'Serviço indisponível';
    default:  return status >= 500 ? 'Erro no servidor' : 'Não foi possível concluir';
  }
}

function mensagemPara(erro: HttpErrorResponse): string {
  if (erro.status === 0) {
    return 'O servidor não respondeu. Verifique se a API está no ar e tente de novo.';
  }

  const corpo = erro.error as ProblemDetail | null;

  if (corpo?.campos?.length) {
    return corpo.campos.map((c) => `${c.campo}: ${c.mensagem}`).join('\n');
  }

  return corpo?.detail ?? 'Tente novamente em instantes.';
}

export const erroInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  const alerta = inject(AlertaService);
  const auth = inject(AuthService);
  const router = inject(Router);

  return proximo(requisicao).pipe(
    catchError((erro: HttpErrorResponse) => {
      console.error('Falha HTTP', requisicao.method, requisicao.url, erro.status);

      const ehLogin = requisicao.url.includes('/auth/login');

      if (erro.status === 401 && ehLogin) {
        return throwError(() => erro);
      }

      if (erro.status === 401) {
        auth.sair();
        alerta.erro('Sessão expirada', 'Faça login novamente.');
        router.navigate(['/login']);
        return throwError(() => erro);
      }

      if (erro.status === 403) {
        alerta.erro(tituloPara(403), 'Você não tem permissão');
        return throwError(() => erro);
      }

      alerta.erro(tituloPara(erro.status), mensagemPara(erro));
      return throwError(() => erro);
    }),
  );
};