import { StatusReuniao, StatusTranscrição } from '../reuniao.model';

export interface ReuniaoApiDTO {
  id: number;
  titulo: string;
  data: string;
  statusReuniao: StatusReuniao;
  statusTranscricao: StatusTranscrição;
  participantes: string[];
  pontosChaves: string[];
  acoes: Acao[];
  areas: string[];
  resumo: string;
  totalAcoes: number;
}

export interface ReuniaoApiRequest {
  titulo: string;
  data: string;
  statusReuniao: StatusReuniao;
  statusTranscricao: StatusTranscrição;
  areas: string[];
  pontosChaves: string[];
  participantes: number[];
  acoes: number[];
}

export type TipoAcao = 'ACAO' | 'TAREFA';

export interface Acao {
  id: number;
  descricao: string;
  tipo: String;
  titulo: string;
  concluida: boolean; 
  prazo: string | null;
  responsavel: string | null;
  reuniaoId: number;
}