-- Massa exclusivamente ficticia para demonstracao inicial em producao.
-- Sem dados de usuarios (colaborador), sem acoes e sem participantes.
-- Ids explicitos de proposito: as tabelas de ligacao precisam deles.
-- A sequence e reposicionada no fim do arquivo.

-- -------------------------------------------------------------------- reuniao
insert into reuniao (id, titulo, data, resumo, status_transcricao, status_reuniao, total_acoes) values
                                                                                                    (1, 'Alinhamento semanal de Operações',            '2026-06-02T09:00:00', 'Revisão dos indicadores da semana e redistribuição de escalas para o período de alta demanda.', 'CONCLUIDA',     'FINALIZADA',   0),
                                                                                                    (2, 'Comitê de Tecnologia — roadmap do trimestre', '2026-06-08T14:30:00', 'Definição das prioridades técnicas do trimestre e discussão da dívida acumulada no legado.',    'CONCLUIDA',     'FINALIZADA',   0),
                                                                                                    (3, 'Fechamento financeiro de maio',               '2026-06-15T10:15:00', 'Consolidação do resultado de maio, com desvio relevante na linha de manutenção predial.',       'CONCLUIDA',     'FINALIZADA',   0),
                                                                                                    (4, 'Revisão de contratos de fornecedores',        '2026-07-01T11:00:00', 'Análise dos contratos que vencem no semestre e definição da estratégia de renegociação.',       'CONCLUIDA',     'FINALIZADA',   0),
                                                                                                    (5, 'Planejamento de treinamento operacional',     '2026-07-07T08:45:00', 'Desenho da trilha de treinamento para novas contratações do time de operações.',                'ANALISANDO',    'EM_ANDAMENTO', 0),
                                                                                                    (6, 'Kickoff do projeto de automação de atas',     '2026-08-03T09:30:00', 'Abertura do projeto de automação de atas, com escopo, papéis e primeiros marcos definidos.',    'TRANSCREVENDO', 'EM_ANDAMENTO', 0),
                                                                                                    (7, 'Comitê de segurança da informação',           '2026-08-11T10:00:00', 'Revisão das políticas de acesso e do plano de resposta a incidentes de segurança.',             'RECEBIDA',      'PENDENTE',     0);

-- -------------------------------------------------------------- reuniao_areas
insert into reuniao_areas (reuniao_id, areas) values
                                                  (1, 'Operações'),
                                                  (2, 'Tecnologia'),
                                                  (3, 'Financeiro'),
                                                  (4, 'Financeiro'), (4, 'Operações'),
                                                  (5, 'Recursos Humanos'), (5, 'Operações'),
                                                  (6, 'Tecnologia'), (6, 'Recursos Humanos'),
                                                  (7, 'Tecnologia');

-- ------------------------------------------------------ reuniao_pontos_chaves
insert into reuniao_pontos_chaves (reuniao_id, pontos_chaves) values
                                                                  (1, 'Fila de atendimento acima da meta em dois dias da semana.'),
                                                                  (1, 'Novo checklist de abertura aprovado.'),
                                                                  (2, 'Migração do ambiente legado é a prioridade um.'),
                                                                  (2, 'Monitoramento atual não cobre os serviços novos.'),
                                                                  (3, 'Manutenção predial estourou o orçado em uma margem relevante.'),
                                                                  (4, 'Três contratos vencem dentro do semestre.'),
                                                                  (5, 'Trilha atual não cobre os procedimentos revisados.'),
                                                                  (6, 'Escopo da primeira entrega fechado.'),
                                                                  (7, 'Plano de resposta nunca foi testado em simulado.');

-- ------------------------------------------------------ reposiciona sequence
select setval('reuniao_id_seq', (select max(id) from reuniao));