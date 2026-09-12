export type TipoAcao = 'ACAO' | 'TAREFA';

export interface Acao {
  id: number;
  descricao: string;
  tipo: string;
  titulo: string;
  concluida: boolean; 
  prazo: string | null;
  responsavel: string | null;
  reuniaoId: number;
}