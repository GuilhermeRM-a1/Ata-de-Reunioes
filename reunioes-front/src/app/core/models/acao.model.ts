/** Tipos aceitos pelo campo `tipo` da acao. */
export type TipoAcao = 'ACAO' | 'TAREFA';

/**
 * Espelha o AcaoDTO do back (web/controller/dto/Acao/AcaoDTO.java).
 * Atencao: o back serializa o id como String (String.valueOf) e devolve
 * `responsavel` como lista de nomes, nao de ids.
 */
export interface Acao {
  id: string;
  titulo: string;
  tipo: TipoAcao;
  descricao: string;
  responsavel: string[];
  prazo: string | null;
  concluida: boolean;
  reuniaoId: number;
}
