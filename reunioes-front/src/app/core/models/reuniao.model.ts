export interface Acao {
  descricao: string;
  responsavel: string;
}

export interface Reuniao {
  id: string;
  tituloReuniao: string;
  dataProcessamento: string;
  resumoExecutivo: string;
  status: string;
  areas: string;
  participantes: string;
  acoes: Acao[];
}