export type StatusTranscricao =
  | 'RECEBIDA'
  | 'TRANSCREVENDO'
  | 'ANALISANDO'
  | 'CONCLUIDA'
  | 'ERRO';

export type StatusReuniao =
  | 'EM_ANDAMENTO'
  | 'FINALIZADA'
  | 'PENDENTE';

/** Usado pelo teste do status-badge para cobrir todos os status. */
export const STATUS_REUNIAO: StatusReuniao[] = [
  'EM_ANDAMENTO',
  'FINALIZADA',
  'PENDENTE',
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
