-- V1__create_schema.sql
-- Esquema inicial do Ata de Reunioes.
-- O Hibernate roda com ddl-auto: validate, entao os nomes aqui precisam bater
-- exatamente com o que as entidades JPA esperam (snake_case por padrao).

-- ---------------------------------------------------------------- colaborador
create table colaborador (
    id                 bigserial     primary key,
    nome               varchar(255),
    email              varchar(255)  not null,
    senha              varchar(255),
    papel              varchar(255)  not null default 'USUARIO',
    monitorar_reunioes boolean,
    data_cadastro      varchar(255),
    constraint uk_colaborador_email unique (email),
    constraint ck_colaborador_papel check (papel in ('ADMIN', 'USUARIO'))
);

-- -------------------------------------------------------------------- reuniao
create table reuniao (
    id          bigserial    primary key,
    titulo      varchar(255),
    data        varchar(255),
    resumo      text,
    status      varchar(255),
    total_acoes integer
);

-- ----------------------------------------------------------------------- acao
-- A ligacao com a reuniao mora aqui: uma reuniao tem N acoes.
create table acao (
    id         bigserial    primary key,
    titulo     varchar(255),
    descricao  text,
    tipo       varchar(255),
    prazo      varchar(255),
    concluida  boolean,
    reuniao_id bigint,
    constraint fk_acao_reuniao foreign key (reuniao_id) references reuniao (id)
);

create index idx_acao_reuniao on acao (reuniao_id);

-- --------------------------------------------------- quem participa da reuniao
create table reuniao_participantes (
    reuniao_id       bigint not null,
    participantes_id bigint not null,
    constraint fk_rp_reuniao     foreign key (reuniao_id)       references reuniao (id),
    constraint fk_rp_colaborador foreign key (participantes_id) references colaborador (id)
);

create index idx_rp_reuniao     on reuniao_participantes (reuniao_id);
create index idx_rp_colaborador on reuniao_participantes (participantes_id);

-- ------------------------------------------------ quem responde por cada acao
create table acao_responsavel (
    acao_id        bigint not null,
    responsavel_id bigint not null,
    constraint fk_ar_acao        foreign key (acao_id)        references acao (id),
    constraint fk_ar_colaborador foreign key (responsavel_id) references colaborador (id)
);

create index idx_ar_acao        on acao_responsavel (acao_id);
create index idx_ar_colaborador on acao_responsavel (responsavel_id);

-- ------------------------------------------- listas simples ligadas a reuniao
-- @ElementCollection: texto solto, sem entidade propria.
create table reuniao_areas (
    reuniao_id bigint not null,
    areas      varchar(255),
    constraint fk_areas_reuniao foreign key (reuniao_id) references reuniao (id)
);

create index idx_areas_reuniao on reuniao_areas (reuniao_id);

create table reuniao_pontos_chaves (
    reuniao_id    bigint not null,
    pontos_chaves varchar(255),
    constraint fk_pontos_reuniao foreign key (reuniao_id) references reuniao (id)
);

create index idx_pontos_reuniao on reuniao_pontos_chaves (reuniao_id);
