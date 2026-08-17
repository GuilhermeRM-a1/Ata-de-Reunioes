import { Colaborador } from '../models';

/**
 * Massa exclusivamente ficticia — nenhum dado real de colaborador.
 * Os mesmos 8 nomes aparecem como participantes em reunioes.mock.ts.
 */
export const COLABORADORES_MOCK: Colaborador[] = [
  { email: 'ana.fontes@exemplo.com.br', nome: 'Ana Beatriz Fontes', monitorarReunioes: true },
  { email: 'carlos.prado@exemplo.com.br', nome: 'Carlos Eduardo Prado', monitorarReunioes: true },
  { email: 'daniela.alves@exemplo.com.br', nome: 'Daniela Ribeiro Alves', monitorarReunioes: false },
  { email: 'eduardo.vieira@exemplo.com.br', nome: 'Eduardo Nunes Vieira', monitorarReunioes: true },
  { email: 'fernanda.braga@exemplo.com.br', nome: 'Fernanda Lopes Braga', monitorarReunioes: false },
  { email: 'gustavo.sato@exemplo.com.br', nome: 'Gustavo Henrique Sato', monitorarReunioes: true },
  { email: 'helena.rocha@exemplo.com.br', nome: 'Helena Martins Rocha', monitorarReunioes: false },
  { email: 'igor.teixeira@exemplo.com.br', nome: 'Igor Salgado Teixeira', monitorarReunioes: true },
];

export const NOMES_COLABORADORES = COLABORADORES_MOCK.map((c) => c.nome);

export const AREAS = ['Operações', 'Tecnologia', 'Financeiro', 'Recursos Humanos'];
