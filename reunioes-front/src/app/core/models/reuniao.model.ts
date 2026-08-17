export enum StatusReuniao {
  RECEBIDA = 'RECEBIDA',
  TRANSCREVENDO = 'TRANSCREVENDO',
  ANALISANDO = 'ANALISANDO',
  CONCLUIDA = 'CONCLUIDA',
  ERRO = 'ERRO'
}

export interface AcaoReuniao {
  descricao: string;
  tipo: string;
  prazo: string | null;
  responsavel: string | null;
}

export interface Reuniao {
  id: number;
  titulo: string;
  data: string; // ISO 8601
  status: StatusReuniao;
  areas: string[];
  resumoExecutivo: string;
  pontosChave: string; // linhas separadas por \n
  acoes: AcaoReuniao[];
  transcricao: string;
  participantes: string[];
}