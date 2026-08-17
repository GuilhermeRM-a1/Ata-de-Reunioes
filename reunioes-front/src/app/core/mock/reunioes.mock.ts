import { Reuniao, StatusReuniao } from '../models/reuniao.model';

export const REUNIOES_MOCK: Reuniao[] = [
  {
    id: 1,
    titulo: 'Alinhamento de Sprint - Time Backend',
    data: '2026-08-10T14:30:00',
    status: StatusReuniao.CONCLUIDA,
    areas: ['Backend', 'Produto'],
    resumoExecutivo: 'A equipe revisou o progresso da sprint atual e definiu prioridades para a próxima entrega, com foco na estabilização do módulo de autenticação.',
    pontosChave: 'Módulo de autenticação com 80% de cobertura de testes\nIntegração com o serviço de e-mail apresentou instabilidade\nDecidido adiar a feature de exportação em PDF para a próxima sprint',
    acoes: [
      { descricao: 'Corrigir instabilidade no serviço de e-mail', tipo: 'Bug', prazo: '2026-08-15', responsavel: 'Bruno Costa' },
      { descricao: 'Documentar endpoints de autenticação', tipo: 'Documentação', prazo: null, responsavel: 'Ana Silva' },
    ],
    transcricao: 'Ana: Bom dia a todos, vamos começar revisando o board...\nBruno: O módulo de autenticação está quase pronto...\n(transcrição completa omitida para o mock)',
    participantes: ['Ana Silva', 'Bruno Costa', 'Carla Souza']
  },
  {
    id: 2,
    titulo: 'Reunião com Cliente - Proposta Comercial',
    data: '2026-08-12T09:00:00',
    status: StatusReuniao.ANALISANDO,
    areas: ['Comercial'],
    resumoExecutivo: 'Apresentação da proposta comercial ao cliente, com discussão sobre prazos de implementação e modelo de precificação.',
    pontosChave: 'Cliente demonstrou interesse no plano premium\nPrazo de implementação estimado em 6 semanas\nPróxima reunião marcada para apresentar contrato',
    acoes: [
      { descricao: 'Enviar contrato revisado', tipo: 'Comercial', prazo: '2026-08-20', responsavel: null },
    ],
    transcricao: 'Diego: Obrigado por nos receber hoje...\n(transcrição completa omitida para o mock)',
    participantes: ['Diego Ferreira', 'Cliente - João Mendes']
  },
  {
    id: 3,
    titulo: 'Daily - Time Frontend',
    data: '2026-08-14T10:00:00',
    status: StatusReuniao.TRANSCREVENDO,
    areas: ['Frontend'],
    resumoExecutivo: 'Reunião diária rápida de alinhamento do time de frontend sobre o andamento das tarefas.',
    pontosChave: 'Sem bloqueios reportados\nTela de login finalizada e em revisão',
    acoes: [],
    transcricao: 'Carla: Bom dia, alguém tem bloqueio hoje?...\n(transcrição completa omitida para o mock)',
    participantes: ['Carla Souza', 'Ana Silva']
  },
  {
    id: 4,
    titulo: 'Reunião de Retrospectiva',
    data: '2026-08-08T16:00:00',
    status: StatusReuniao.ERRO,
    areas: ['Backend', 'Frontend', 'Produto'],
    resumoExecutivo: 'Falha no processamento automático desta reunião. Necessário reprocessar o áudio.',
    pontosChave: '',
    acoes: [],
    transcricao: '',
    participantes: []
  }
];