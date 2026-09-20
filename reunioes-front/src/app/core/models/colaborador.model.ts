import { Papel } from '../services/auth.service';

/**
 * Espelha o ColaboradorDTO do back. O `id` vem do banco e so existe em
 * registro ja salvo — por isso opcional.
 */
export interface Colaborador {
  id?: number;
  email: string;
  nome: string;
  monitorarReunioes: boolean;
  papel: Papel;
}
