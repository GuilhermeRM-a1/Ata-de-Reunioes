import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AlertaService } from '../services/alerta.service';

/**
 * Corpo de erro que a API devolve. O back usa ProblemDetail (RFC 7807),
 * entao a mensagem pronta e em portugues vem em `detail`.
 */
interface ProblemDetail {
  title?: string;
  detail?: string;
  status?: number;
  campos?: { campo: string; mensagem: string }[];
}

/** Traduz o status HTTP para uma frase que o usuario entenda. */
function tituloPara(status: number): string {
  switch (status) {
    case 0:   return 'Sem conexão com o servidor';
    case 400: return 'Dados inválidos';
    case 401:
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

  // Erro de validação: mostra campo por campo, que é o que o usuário precisa corrigir.
  if (corpo?.campos?.length) {
    return corpo.campos.map((c) => `${c.campo}: ${c.mensagem}`).join('\n');
  }

  return corpo?.detail ?? 'Tente novamente em instantes.';
}

/**
 * Captura toda falha HTTP num lugar so, avisa o usuario e repassa o erro
 * adiante — quem chamou ainda decide o que fazer, mas nenhuma falha passa
 * despercebida.
 */
export const erroInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  const alerta = inject(AlertaService);

  return proximo(requisicao).pipe(
    catchError((erro: HttpErrorResponse) => {
      console.error('Falha HTTP', requisicao.method, requisicao.url, erro.status);

      alerta.erro(tituloPara(erro.status), mensagemPara(erro));

      return throwError(() => erro);
    }),
  );
};
