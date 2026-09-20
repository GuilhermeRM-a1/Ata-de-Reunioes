export type StatusTranscricao =
  | 'RECEBIDA'
  | 'TRANSCREVENDO'
  | 'ANALISANDO'
  | 'CONCLUIDA'
  | 'ERRO';

  export type StatusReuniao =
  | 'EM_ANDAMENTO'
  | 'FINALIZADA'
  | 'PENDENTE'

export const STATUS_REUNIAO: StatusReuniao[] = [
  'EM_ANDAMENTO',
  'FINALIZADA',
  'PENDENTE',
];

export const STATUS_TRANSCRICAO: StatusTranscricao[] = [
  'RECEBIDA',
  'TRANSCREVENDO',
  'ANALISANDO',
  'CONCLUIDA',
  'ERRO'
];

/** Rotulo legivel para exibicao — nunca mostrar a constante crua na tela. */
export const STATUS_TRANSCRICAO_LABEL: Record<StatusTranscricao, string> = {
  RECEBIDA: 'Recebida',
  TRANSCREVENDO: 'Transcrevendo',
  ANALISANDO: 'Analisando',
  CONCLUIDA: 'Concluída',
  ERRO: 'Erro',
};

export const STATUS_REUNIAO_LABEL: Record<StatusReuniao, string> = {
  EM_ANDAMENTO: 'Em Andamento',
  PENDENTE: 'Pendente',
  FINALIZADA: 'Finalizada',
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
  statusReuniao: StatusReuniao;
  statusTranscricao: StatusTranscricao;
  participantes: string[];
  areas: string[];
  totalAcoes: number;
}

export interface ReuniaoDetalhe extends Reuniao {
  transcricaoPura: string;
  pontosChave: string;
  acoes: Acao[];
}
