import { StatusReuniao } from '../reuniao.model';

export interface ReuniaoApiDTO {
  id: number;
  titulo: string;
  data: string;
  status: StatusReuniao;
  participantes: string[];
  areas: string[];
  totalAcoes: number;
}

export interface ReuniaoApiRequest {
  titulo: string;
  data: string;
  status: StatusReuniao;
  areas: string[];
  pontosChaves: string[];
  participantes: number[];
  acoes: number[];
}