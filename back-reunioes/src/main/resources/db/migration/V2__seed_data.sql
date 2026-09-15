-- V2__seed_data.sql
-- Massa exclusivamente ficticia. Nenhum conteudo real de reuniao, nenhum dado
-- pessoal de pessoa real. Serve para o CRUD e as listagens terem o que mostrar.
--
-- Os 8 nomes e as 4 areas sao os mesmos do mock do front
-- (reunioes-front/src/app/core/mock/), para que os dois lados contem a mesma
-- historia quando a integracao chegar.
--
-- Ids explicitos de proposito: as tabelas de ligacao precisam deles. As
-- sequences sao reposicionadas no fim do arquivo.
--
-- Casos de borda plantados:
--   reuniao 4      -> nenhuma acao
--   reuniao 7      -> cinco acoes
--   acao 5         -> sem prazo e sem responsavel
--   acoes 15 e 17  -> sem responsavel
--   acoes 9 e 24   -> sem prazo

-- ---------------------------------------------------------------- colaborador
insert into colaborador (id, nome, email, senha, papel, monitorar_reunioes, data_cadastro) values
  (1, 'Ana Beatriz Fontes',    'ana.fontes@exemplo.com.br',     'senha-ficticia-trocar', 'ADMIN',   true,  '2026-05-04'),
  (2, 'Carlos Eduardo Prado',  'carlos.prado@exemplo.com.br',   'senha-ficticia-trocar', 'USUARIO', true,  '2026-05-04'),
  (3, 'Daniela Ribeiro Alves', 'daniela.alves@exemplo.com.br',  'senha-ficticia-trocar', 'USUARIO', false, '2026-05-11'),
  (4, 'Eduardo Nunes Vieira',  'eduardo.vieira@exemplo.com.br', 'senha-ficticia-trocar', 'USUARIO', true,  '2026-05-11'),
  (5, 'Fernanda Lopes Braga',  'fernanda.braga@exemplo.com.br', 'senha-ficticia-trocar', 'USUARIO', false, '2026-05-18'),
  (6, 'Gustavo Henrique Sato', 'gustavo.sato@exemplo.com.br',   'senha-ficticia-trocar', 'ADMIN',   true,  '2026-05-18'),
  (7, 'Helena Martins Rocha',  'helena.rocha@exemplo.com.br',   'senha-ficticia-trocar', 'USUARIO', false, '2026-05-25'),
  (8, 'Igor Salgado Teixeira', 'igor.teixeira@exemplo.com.br',  'senha-ficticia-trocar', 'USUARIO', true,  '2026-05-25');

-- -------------------------------------------------------------------- reuniao
-- Datas espalhadas por tres meses: junho, julho e agosto de 2026.
-- Os 5 status aparecem, para exercitar todas as cores do badge no front.
insert into reuniao (id, titulo, data, resumo, status, total_acoes) values
  (1,  'Alinhamento semanal de Operações',            '2026-06-02T09:00:00', 'Revisão dos indicadores da semana e redistribuição de escalas para o período de alta demanda.',   'CONCLUIDA',     2),
  (2,  'Comitê de Tecnologia — roadmap do trimestre', '2026-06-08T14:30:00', 'Definição das prioridades técnicas do trimestre e discussão da dívida acumulada no legado.',      'CONCLUIDA',     3),
  (3,  'Fechamento financeiro de maio',               '2026-06-15T10:15:00', 'Consolidação do resultado de maio, com desvio relevante na linha de manutenção predial.',         'CONCLUIDA',     2),
  (4,  'Café com RH — escuta de clima',               '2026-06-23T16:00:00', 'Conversa aberta sobre clima organizacional, sem encaminhamentos formais registrados.',            'CONCLUIDA',     0),
  (5,  'Revisão de contratos de fornecedores',        '2026-07-01T11:00:00', 'Análise dos contratos que vencem no semestre e definição da estratégia de renegociação.',         'CONCLUIDA',     2),
  (6,  'Planejamento de treinamento operacional',     '2026-07-07T08:45:00', 'Desenho da trilha de treinamento para novas contratações do time de operações.',                  'ANALISANDO',    1),
  (7,  'Retrospectiva trimestral integrada',          '2026-07-14T15:00:00', 'Retrospectiva com as quatro áreas, consolidando aprendizados e compromissos do próximo ciclo.',   'CONCLUIDA',     5),
  (8,  'Incidente de indisponibilidade — pós-morte',  '2026-07-21T18:20:00', 'Análise da indisponibilidade do ambiente de atendimento e das ações de contenção adotadas.',      'ERRO',          2),
  (9,  'Revisão de metas do segundo semestre',        '2026-07-29T13:30:00', 'Ajuste das metas do semestre com base no resultado parcial e na capacidade das equipes.',         'ANALISANDO',    1),
  (10, 'Kickoff do projeto de automação de atas',     '2026-08-03T09:30:00', 'Abertura do projeto de automação de atas, com escopo, papéis e primeiros marcos definidos.',      'TRANSCREVENDO', 2),
  (11, 'Auditoria interna de processos',              '2026-08-06T14:00:00', 'Apresentação dos achados preliminares da auditoria interna sobre processos de compras.',          'TRANSCREVENDO', 1),
  (12, 'Comitê de segurança da informação',           '2026-08-11T10:00:00', 'Revisão das políticas de acesso e do plano de resposta a incidentes de segurança.',               'RECEBIDA',      1),
  (13, 'Alinhamento de contratações do trimestre',    '2026-08-14T17:00:00', 'Definição das vagas prioritárias do trimestre e do fluxo de aprovação das contratações.',         'RECEBIDA',      2);

-- -------------------------------------------------------------- reuniao_areas
-- Quatro areas recorrentes, para o filtro por area ter resultado.
insert into reuniao_areas (reuniao_id, areas) values
  (1, 'Operações'),
  (2, 'Tecnologia'),
  (3, 'Financeiro'),
  (4, 'Recursos Humanos'),
  (5, 'Financeiro'), (5, 'Operações'),
  (6, 'Recursos Humanos'), (6, 'Operações'),
  (7, 'Operações'), (7, 'Tecnologia'), (7, 'Financeiro'), (7, 'Recursos Humanos'),
  (8, 'Tecnologia'),
  (9, 'Financeiro'), (9, 'Operações'),
  (10, 'Tecnologia'), (10, 'Recursos Humanos'),
  (11, 'Financeiro'),
  (12, 'Tecnologia'),
  (13, 'Recursos Humanos'), (13, 'Operações');

-- ------------------------------------------------------ reuniao_pontos_chaves
insert into reuniao_pontos_chaves (reuniao_id, pontos_chaves) values
  (1, 'Fila de atendimento acima da meta em dois dias da semana.'),
  (1, 'Escala de fim de semana precisa de reforço.'),
  (1, 'Novo checklist de abertura aprovado.'),
  (2, 'Migração do ambiente legado é a prioridade um.'),
  (2, 'Monitoramento atual não cobre os serviços novos.'),
  (2, 'Falta dono definido para o backlog de segurança.'),
  (3, 'Manutenção predial estourou o orçado em uma margem relevante.'),
  (3, 'Receita ficou dentro do previsto.'),
  (4, 'Percepção geral de clima positiva.'),
  (4, 'Pedido recorrente por mais previsibilidade de escala.'),
  (5, 'Três contratos vencem dentro do semestre.'),
  (5, 'Cláusula de reajuste precisa ser padronizada.'),
  (6, 'Trilha atual não cobre os procedimentos revisados.'),
  (7, 'Comunicação entre áreas melhorou, mas segue dependente de reuniões.'),
  (7, 'Indicadores não são compartilhados em formato comum.'),
  (7, 'Demanda por um painel único é consenso.'),
  (8, 'Causa raiz ainda não confirmada.'),
  (8, 'Procedimento de rollback não estava documentado.'),
  (9, 'Meta de volume precisa de revisão por área.'),
  (10, 'Escopo da primeira entrega fechado.'),
  (11, 'Registro de fornecedores está desatualizado.'),
  (12, 'Plano de resposta nunca foi testado em simulado.'),
  (13, 'Fluxo de aprovação atual tem etapa redundante.');

-- ------------------------------------------------------ reuniao_participantes
-- Os mesmos 8 nomes se repetem ao longo das reunioes, como num time real.
insert into reuniao_participantes (reuniao_id, participantes_id) values
  (1, 1), (1, 2), (1, 7),
  (2, 3), (2, 4), (2, 6), (2, 8),
  (3, 2), (3, 5),
  (4, 1), (4, 3), (4, 7),
  (5, 2), (5, 4), (5, 5),
  (6, 3), (6, 7),
  (7, 1), (7, 2), (7, 5), (7, 6), (7, 7), (7, 8),
  (8, 6), (8, 8),
  (9, 1), (9, 2), (9, 5),
  (10, 1), (10, 3), (10, 4), (10, 6),
  (11, 5), (11, 7), (11, 8),
  (12, 4), (12, 6), (12, 8),
  (13, 2), (13, 3), (13, 7);

-- ----------------------------------------------------------------------- acao
insert into acao (id, titulo, descricao, tipo, prazo, concluida, reuniao_id) values
  (1,  'Escala do fim de semana',     'Publicar a escala revisada do fim de semana',                 'TAREFA', '2026-06-06', true, 1),
  (2,  'Checklist de abertura',       'Distribuir o novo checklist de abertura às equipes',          'ACAO',   '2026-06-09',true, 1),
  (3,  'Esforço da migração',         'Levantar o esforço de migração do ambiente legado',           'ACAO',   '2026-06-20',true, 2),
  (4,  'Monitoramento dos serviços',  'Estender o monitoramento aos serviços novos',                 'TAREFA', '2026-06-30',true, 2),
  (5,  'Dono do backlog',             'Definir responsável pelo backlog de segurança',               'ACAO',   null,true,         2),
  (6,  'Desvio da manutenção',        'Detalhar o desvio da manutenção predial por centro de custo', 'ACAO',   '2026-06-22',true, 3),
  (7,  'Provisões do trimestre',      'Revisar as provisões do próximo trimestre',                   'TAREFA', '2026-07-05',true, 3),
  (8,  'Comparativo de propostas',    'Montar comparativo de propostas dos fornecedores críticos',   'ACAO',   '2026-07-18',true, 5),
  (9,  'Cláusula de reajuste',        'Padronizar a cláusula de reajuste nos novos contratos',       'TAREFA', null,    true,     5),
  (10, 'Módulo de procedimentos',     'Reescrever o módulo de procedimentos da trilha',              'TAREFA', '2026-07-25',true, 6),
  (11, 'Indicadores consolidados',    'Consolidar indicadores das quatro áreas em formato único',    'ACAO',   '2026-08-01',true, 7),
  (12, 'Acompanhamento do ciclo',     'Criar rotina de acompanhamento dos compromissos do ciclo',    'TAREFA', '2026-07-28',true, 7),
  (13, 'Requisitos do painel',        'Levantar requisitos do painel único de indicadores',          'ACAO',   '2026-08-10',true, 7),
  (14, 'Cadência de comunicação',     'Definir cadência de comunicação entre áreas',                 'TAREFA', null,true,         7),
  (15, 'Formato da retrospectiva',    'Revisar o formato da retrospectiva para o próximo trimestre', 'ACAO',   '2026-09-30',true, 7),
  (16, 'Procedimento de rollback',    'Documentar o procedimento de rollback do ambiente',           'TAREFA', '2026-07-31',true, 8),
  (17, 'Alerta de indisponibilidade', 'Antecipar o disparo do alerta de indisponibilidade',          'ACAO',   null,true,         8),
  (18, 'Meta de volume',              'Recalcular a meta de volume por área',                        'ACAO',   '2026-08-08',true, 9),
  (19, 'Escopo da primeira entrega',  'Publicar o escopo fechado da primeira entrega',               'TAREFA', '2026-08-07',true, 10),
  (20, 'Integrações da fase dois',    'Mapear as integrações previstas para a fase dois',            'ACAO',   '2026-08-21',true, 10),
  (21, 'Registro de fornecedores',    'Atualizar o registro de fornecedores ativos',                 'TAREFA', '2026-08-29',true, 11),
  (22, 'Simulado de resposta',        'Agendar simulado do plano de resposta a incidentes',          'ACAO',   '2026-09-15',true, 12),
  (23, 'Vagas prioritárias',          'Abrir as duas vagas prioritárias de operações',               'TAREFA', '2026-08-25',true, 13),
  (24, 'Etapa redundante',            'Remover a etapa redundante do fluxo de aprovação',            'ACAO',   null,true,         13);

-- ------------------------------------------------------------ acao_responsavel
-- As acoes 5, 15 e 17 ficam de fora de proposito: sao os casos sem responsavel.
insert into acao_responsavel (acao_id, responsavel_id) values
  (1, 7),
  (2, 2),
  (3, 6),
  (4, 8),
  (6, 5),
  (7, 5),
  (8, 5),
  (9, 4),
  (10, 3),
  (11, 6),
  (12, 1),
  (13, 8),
  (14, 7),
  (16, 8),
  (18, 2),
  (19, 4),
  (20, 6),
  (21, 5),
  (22, 8),
  (23, 3),
  (24, 7);

-- ------------------------------------------------------ reposiciona sequences
-- Sem isto o proximo insert pela API tentaria o id 1 e bateria em chave
-- duplicada, porque a sequence nao sabe dos ids inseridos na mao.
select setval('colaborador_id_seq', (select max(id) from colaborador));
select setval('reuniao_id_seq',     (select max(id) from reuniao));
select setval('acao_id_seq',        (select max(id) from acao));
