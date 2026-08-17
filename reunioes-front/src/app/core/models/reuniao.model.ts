export type StatusReuniao =
  | 'RECEBIDA'
  | 'TRANSCREVENDO'
  | 'ANALISANDO'
  | 'CONCLUIDA'
  | 'ERRO';

export const STATUS_REUNIAO: StatusReuniao[] = [
  'RECEBIDA',
  'TRANSCREVENDO',
  'ANALISANDO',
  'CONCLUIDA',
  'ERRO',
];

/** Rotulo legivel para exibicao — nunca mostrar a constante crua na tela. */
export const STATUS_LABEL: Record<StatusReuniao, string> = {
  RECEBIDA: 'Recebida',
  TRANSCREVENDO: 'Transcrevendo',
  ANALISANDO: 'Analisando',
  CONCLUIDA: 'Concluída',
  ERRO: 'Erro',
};

export type TipoAcao = 'ACAO' | 'TAREFA';

export interface Acao {
  descricao: string;
  tipo: TipoAcao;
  prazo: string | null;
  responsavel: string | null;
}

export interface Reuniao {
  id: number;
  tituloReuniao: string;
  dataProcessamento: string;
  resumoExecutivo: string;
  status: StatusReuniao;
  participantes: string[];
  areas: string[];
  totalAcoes: number;
}

export interface ReuniaoDetalhe extends Reuniao {
  transcricaoPura: string;
  pontosChave: string;
  acoes: Acao[];
}
