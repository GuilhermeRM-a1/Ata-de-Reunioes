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

/**
 * Corpo aceito por POST/PATCH /api/reunioes/acoes.
 * Diferente do AcaoDTO: `titulo` e obrigatorio e `responsavel` vai como
 * lista de ids de colaborador, nao de nomes.
 */
export interface AcaoRequestPayload {
  titulo: string;
  descricao: string;
  tipo: TipoAcao;
  prazo: string | null;
  concluida: boolean;
  responsavel: number[];
}
