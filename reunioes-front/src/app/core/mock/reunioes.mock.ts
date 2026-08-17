import { ReuniaoDetalhe, Acao } from '../models';
import { StatusReuniao } from '../models';

/**
 * Massa exclusivamente ficticia — nenhum conteudo real de reuniao.
 * Cobre os 5 status, 4 areas recorrentes, 8 nomes que se repetem e datas
 * espalhadas por 3 meses (junho a agosto de 2026).
 *
 * Casos de borda intencionais:
 *  - id 4  : reuniao sem nenhuma acao
 *  - id 7  : reuniao com 5 acoes
 *  - id 2  : acao com prazo e responsavel nulos
 */
export const REUNIOES_MOCK: ReuniaoDetalhe[] = [
  {
    id: 1,
    tituloReuniao: 'Alinhamento semanal de Operações',
    dataProcessamento: '2026-06-02T09:00:00',
    resumoExecutivo:
      'Revisão dos indicadores da semana e redistribuição de escalas para o período de alta demanda.',
    status: 'CONCLUIDA',
    participantes: ['Ana Beatriz Fontes', 'Carlos Eduardo Prado', 'Helena Martins Rocha'],
    areas: ['Operações'],
    totalAcoes: 2,
    pontosChave:
      'Fila de atendimento acima da meta em dois dias da semana.\nEscala de fim de semana precisa de reforço.\nNovo checklist de abertura aprovado.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. O time revisou os números da semana, discutiu a fila de atendimento e fechou o novo checklist de abertura. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Publicar a escala revisada do fim de semana',
        tipo: 'TAREFA',
        prazo: '2026-06-06',
        responsavel: 'Helena Martins Rocha',
      },
      {
        descricao: 'Distribuir o novo checklist de abertura às equipes',
        tipo: 'ACAO',
        prazo: '2026-06-09',
        responsavel: 'Carlos Eduardo Prado',
      },
    ],
  },
  {
    id: 2,
    tituloReuniao: 'Comitê de Tecnologia — roadmap do trimestre',
    dataProcessamento: '2026-06-08T14:30:00',
    resumoExecutivo:
      'Definição das prioridades técnicas do trimestre e discussão da dívida acumulada no ambiente legado.',
    status: 'CONCLUIDA',
    participantes: [
      'Eduardo Nunes Vieira',
      'Gustavo Henrique Sato',
      'Igor Salgado Teixeira',
      'Daniela Ribeiro Alves',
    ],
    areas: ['Tecnologia'],
    totalAcoes: 3,
    pontosChave:
      'Migração do ambiente legado é a prioridade um.\nMonitoramento atual não cobre os serviços novos.\nFalta dono definido para o backlog de segurança.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Discussão sobre prioridades de engenharia, cobertura de monitoramento e responsabilidades do backlog de segurança. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Levantar o esforço de migração do ambiente legado',
        tipo: 'ACAO',
        prazo: '2026-06-20',
        responsavel: 'Gustavo Henrique Sato',
      },
      {
        descricao: 'Estender o monitoramento aos serviços novos',
        tipo: 'TAREFA',
        prazo: '2026-06-30',
        responsavel: 'Igor Salgado Teixeira',
      },
      {
        descricao: 'Definir responsável pelo backlog de segurança',
        tipo: 'ACAO',
        prazo: null,
        responsavel: null,
      },
    ],
  },
  {
    id: 3,
    tituloReuniao: 'Fechamento financeiro de maio',
    dataProcessamento: '2026-06-15T10:15:00',
    resumoExecutivo:
      'Consolidação do resultado de maio, com desvio relevante na linha de manutenção predial.',
    status: 'CONCLUIDA',
    participantes: ['Fernanda Lopes Braga', 'Carlos Eduardo Prado'],
    areas: ['Financeiro'],
    totalAcoes: 2,
    pontosChave:
      'Manutenção predial estourou o orçado em uma margem relevante.\nReceita ficou dentro do previsto.\nProvisões do próximo trimestre precisam de revisão.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Revisão de linhas orçamentárias, desvios e provisões. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Detalhar o desvio da manutenção predial por centro de custo',
        tipo: 'ACAO',
        prazo: '2026-06-22',
        responsavel: 'Fernanda Lopes Braga',
      },
      {
        descricao: 'Revisar as provisões do próximo trimestre',
        tipo: 'TAREFA',
        prazo: '2026-07-05',
        responsavel: 'Fernanda Lopes Braga',
      },
    ],
  },
  {
    id: 4,
    tituloReuniao: 'Café com RH — escuta de clima',
    dataProcessamento: '2026-06-23T16:00:00',
    resumoExecutivo:
      'Conversa aberta sobre clima organizacional, sem encaminhamentos formais registrados.',
    status: 'CONCLUIDA',
    participantes: ['Daniela Ribeiro Alves', 'Helena Martins Rocha', 'Ana Beatriz Fontes'],
    areas: ['Recursos Humanos'],
    totalAcoes: 0,
    pontosChave:
      'Percepção geral de clima positiva.\nPedido recorrente por mais previsibilidade de escala.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Conversa aberta de escuta, sem deliberações. Conteúdo meramente ilustrativo.',
    acoes: [],
  },
  {
    id: 5,
    tituloReuniao: 'Revisão de contratos de fornecedores',
    dataProcessamento: '2026-07-01T11:00:00',
    resumoExecutivo:
      'Análise dos contratos que vencem no semestre e definição da estratégia de renegociação.',
    status: 'CONCLUIDA',
    participantes: ['Fernanda Lopes Braga', 'Eduardo Nunes Vieira', 'Carlos Eduardo Prado'],
    areas: ['Financeiro', 'Operações'],
    totalAcoes: 2,
    pontosChave:
      'Três contratos vencem dentro do semestre.\nDois fornecedores têm histórico de atraso.\nCláusula de reajuste precisa ser padronizada.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Análise de vencimentos contratuais e critérios de renegociação. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Montar comparativo de propostas dos fornecedores críticos',
        tipo: 'ACAO',
        prazo: '2026-07-18',
        responsavel: 'Fernanda Lopes Braga',
      },
      {
        descricao: 'Padronizar a cláusula de reajuste nos novos contratos',
        tipo: 'TAREFA',
        prazo: null,
        responsavel: 'Eduardo Nunes Vieira',
      },
    ],
  },
  {
    id: 6,
    tituloReuniao: 'Planejamento de treinamento operacional',
    dataProcessamento: '2026-07-07T08:45:00',
    resumoExecutivo:
      'Desenho da trilha de treinamento para novas contratações do time de operações.',
    status: 'ANALISANDO',
    participantes: ['Helena Martins Rocha', 'Daniela Ribeiro Alves'],
    areas: ['Recursos Humanos', 'Operações'],
    totalAcoes: 1,
    pontosChave:
      'Trilha atual não cobre os procedimentos revisados.\nAvaliação prática deve entrar ao fim do módulo.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Discussão sobre módulos de treinamento e forma de avaliação. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Reescrever o módulo de procedimentos da trilha',
        tipo: 'TAREFA',
        prazo: '2026-07-25',
        responsavel: 'Daniela Ribeiro Alves',
      },
    ],
  },
  {
    id: 7,
    tituloReuniao: 'Retrospectiva trimestral integrada',
    dataProcessamento: '2026-07-14T15:00:00',
    resumoExecutivo:
      'Retrospectiva com as quatro áreas, consolidando aprendizados e compromissos do próximo ciclo.',
    status: 'CONCLUIDA',
    participantes: [
      'Ana Beatriz Fontes',
      'Carlos Eduardo Prado',
      'Fernanda Lopes Braga',
      'Gustavo Henrique Sato',
      'Helena Martins Rocha',
      'Igor Salgado Teixeira',
    ],
    areas: ['Operações', 'Tecnologia', 'Financeiro', 'Recursos Humanos'],
    totalAcoes: 5,
    pontosChave:
      'Comunicação entre áreas melhorou, mas segue dependente de reuniões.\nIndicadores não são compartilhados em formato comum.\nCompromissos do ciclo anterior ficaram sem acompanhamento.\nDemanda por um painel único é consenso.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Retrospectiva ampla com as quatro áreas, revisando compromissos e propondo um painel comum de indicadores. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Consolidar indicadores das quatro áreas em formato único',
        tipo: 'ACAO',
        prazo: '2026-08-01',
        responsavel: 'Gustavo Henrique Sato',
      },
      {
        descricao: 'Criar rotina de acompanhamento dos compromissos do ciclo',
        tipo: 'TAREFA',
        prazo: '2026-07-28',
        responsavel: 'Ana Beatriz Fontes',
      },
      {
        descricao: 'Levantar requisitos do painel único de indicadores',
        tipo: 'ACAO',
        prazo: '2026-08-10',
        responsavel: 'Igor Salgado Teixeira',
      },
      {
        descricao: 'Definir cadência de comunicação entre áreas',
        tipo: 'TAREFA',
        prazo: null,
        responsavel: 'Helena Martins Rocha',
      },
      {
        descricao: 'Revisar o formato da retrospectiva para o próximo trimestre',
        tipo: 'ACAO',
        prazo: '2026-09-30',
        responsavel: null,
      },
    ],
  },
  {
    id: 8,
    tituloReuniao: 'Incidente de indisponibilidade — pós-morte',
    dataProcessamento: '2026-07-21T18:20:00',
    resumoExecutivo:
      'Análise da indisponibilidade do ambiente de atendimento e das ações de contenção adotadas.',
    status: 'ERRO',
    participantes: ['Gustavo Henrique Sato', 'Igor Salgado Teixeira'],
    areas: ['Tecnologia'],
    totalAcoes: 2,
    pontosChave:
      'Causa raiz ainda não confirmada.\nAlerta disparou depois do impacto ao usuário.\nProcedimento de rollback não estava documentado.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. O áudio original apresentou falha no processamento e a análise ficou incompleta. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Documentar o procedimento de rollback do ambiente',
        tipo: 'TAREFA',
        prazo: '2026-07-31',
        responsavel: 'Igor Salgado Teixeira',
      },
      {
        descricao: 'Antecipar o disparo do alerta de indisponibilidade',
        tipo: 'ACAO',
        prazo: null,
        responsavel: null,
      },
    ],
  },
  {
    id: 9,
    tituloReuniao: 'Revisão de metas do segundo semestre',
    dataProcessamento: '2026-07-29T13:30:00',
    resumoExecutivo:
      'Ajuste das metas do semestre com base no resultado parcial e na capacidade das equipes.',
    status: 'ANALISANDO',
    participantes: ['Carlos Eduardo Prado', 'Fernanda Lopes Braga', 'Ana Beatriz Fontes'],
    areas: ['Financeiro', 'Operações'],
    totalAcoes: 1,
    pontosChave:
      'Meta de custo segue viável.\nMeta de volume precisa de revisão por área.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Ajuste de metas com base em resultado parcial. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Recalcular a meta de volume por área',
        tipo: 'ACAO',
        prazo: '2026-08-08',
        responsavel: 'Carlos Eduardo Prado',
      },
    ],
  },
  {
    id: 10,
    tituloReuniao: 'Kickoff do projeto de automação de atas',
    dataProcessamento: '2026-08-03T09:30:00',
    resumoExecutivo:
      'Abertura do projeto de automação de atas, com escopo, papéis e primeiros marcos definidos.',
    status: 'TRANSCREVENDO',
    participantes: [
      'Eduardo Nunes Vieira',
      'Gustavo Henrique Sato',
      'Daniela Ribeiro Alves',
      'Ana Beatriz Fontes',
    ],
    areas: ['Tecnologia', 'Recursos Humanos'],
    totalAcoes: 2,
    pontosChave:
      'Escopo da primeira entrega fechado.\nPapéis definidos entre as áreas.\nIntegração com o back fica para a fase dois.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Abertura de projeto, definição de escopo e marcos. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Publicar o escopo fechado da primeira entrega',
        tipo: 'TAREFA',
        prazo: '2026-08-07',
        responsavel: 'Eduardo Nunes Vieira',
      },
      {
        descricao: 'Mapear as integrações previstas para a fase dois',
        tipo: 'ACAO',
        prazo: '2026-08-21',
        responsavel: 'Gustavo Henrique Sato',
      },
    ],
  },
  {
    id: 11,
    tituloReuniao: 'Auditoria interna de processos',
    dataProcessamento: '2026-08-06T14:00:00',
    resumoExecutivo:
      'Apresentação dos achados preliminares da auditoria interna sobre processos de compras.',
    status: 'TRANSCREVENDO',
    participantes: ['Fernanda Lopes Braga', 'Helena Martins Rocha', 'Igor Salgado Teixeira'],
    areas: ['Financeiro'],
    totalAcoes: 1,
    pontosChave:
      'Aprovações fora do fluxo padrão foram identificadas.\nRegistro de fornecedores está desatualizado.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Apresentação de achados preliminares de auditoria. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Atualizar o registro de fornecedores ativos',
        tipo: 'TAREFA',
        prazo: '2026-08-29',
        responsavel: 'Fernanda Lopes Braga',
      },
    ],
  },
  {
    id: 12,
    tituloReuniao: 'Comitê de segurança da informação',
    dataProcessamento: '2026-08-11T10:00:00',
    resumoExecutivo:
      'Revisão das políticas de acesso e do plano de resposta a incidentes de segurança.',
    status: 'RECEBIDA',
    participantes: ['Igor Salgado Teixeira', 'Gustavo Henrique Sato', 'Eduardo Nunes Vieira'],
    areas: ['Tecnologia'],
    totalAcoes: 1,
    pontosChave:
      'Política de acesso precisa de revisão anual formal.\nPlano de resposta nunca foi testado em simulado.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Revisão de políticas de acesso e plano de resposta. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Agendar simulado do plano de resposta a incidentes',
        tipo: 'ACAO',
        prazo: '2026-09-15',
        responsavel: 'Igor Salgado Teixeira',
      },
    ],
  },
  {
    id: 13,
    tituloReuniao: 'Alinhamento de contratações do trimestre',
    dataProcessamento: '2026-08-14T17:00:00',
    resumoExecutivo:
      'Definição das vagas prioritárias do trimestre e do fluxo de aprovação das contratações.',
    status: 'RECEBIDA',
    participantes: ['Daniela Ribeiro Alves', 'Helena Martins Rocha', 'Carlos Eduardo Prado'],
    areas: ['Recursos Humanos', 'Operações'],
    totalAcoes: 2,
    pontosChave:
      'Duas vagas de operações são prioridade.\nFluxo de aprovação atual tem etapa redundante.',
    transcricaoPura:
      'Transcrição fictícia gerada para desenvolvimento. Priorização de vagas e revisão do fluxo de aprovação. Conteúdo meramente ilustrativo.',
    acoes: [
      {
        descricao: 'Abrir as duas vagas prioritárias de operações',
        tipo: 'TAREFA',
        prazo: '2026-08-25',
        responsavel: 'Daniela Ribeiro Alves',
      },
      {
        descricao: 'Remover a etapa redundante do fluxo de aprovação',
        tipo: 'ACAO',
        prazo: null,
        responsavel: 'Helena Martins Rocha',
      },
    ],
  },
];
