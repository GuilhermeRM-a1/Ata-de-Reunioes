import { StatusReuniao, StatusTranscricao } from '../reuniao.model';
import { Acao } from '../acao.model';

export interface ReuniaoApiDTO {
  id: number;
  titulo: string;
  data: string;
  statusReuniao: StatusReuniao;
  statusTranscricao: StatusTranscricao;
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
  statusTranscricao: StatusTranscricao;
  statusReuniao: StatusReuniao;
  areas: string[];
  pontosChaves: string[];
  participantes: number[];
  acoes: number[];
}
