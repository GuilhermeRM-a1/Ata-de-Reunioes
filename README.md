# Ata de Reuniões — URBIA Cataratas

Sistema de gestão de atas de reunião. O back expõe uma API REST em Spring Boot e
o front é uma SPA em Angular. O repositório guarda os dois, lado a lado:

```
Ata-de-Reunioes/
├── back-reunioes/    API REST — Spring Boot 4.1.0, Java 17
└── reunioes-front/   SPA — Angular 19
```

---

## Subindo o projeto

São três peças e elas sobem nesta ordem: banco, back, front.

### 1. Banco (Docker)

```bash
cd back-reunioes && docker compose up -d postgres
```

O `docker-compose.yml` publica o Postgres na porta **5433** do host, porque a
5432 costuma estar ocupada por um Postgres instalado na máquina. Confira em qual
porta o seu contêiner subiu antes de seguir:

```bash
docker ps --filter name=reunioes_postgres
```

Contêineres criados antes desta versão do compose podem estar em outra porta
(55432, por exemplo). Nesse caso use a porta que o `docker ps` mostrar.

### 2. Back

O projeto compila para **Java 17**. O `JAVA_HOME` precisa apontar para um **JDK**,
não para um JRE — um JRE não traz compilador e o Maven falha com
`No compiler is provided in this environment`.

```bash
cd back-reunioes
export JAVA_HOME=/caminho/para/o/jdk-17
export DB_URL=jdbc:postgresql://localhost:5433/reunioes_dev
./mvnw spring-boot:run
```

No PowerShell do Windows (onde `&&` não funciona e cada comando vai numa linha):

```powershell
$env:JAVA_HOME = "C:\caminho\para\o\jdk-17"
```

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5433/reunioes_dev"
```

```powershell
./mvnw spring-boot:run
```

O padrão do `application.yml` aponta para `localhost:5432`. Se você não definir
`DB_URL`, o Spring tenta conectar no Postgres instalado na máquina em vez do
contêiner, e o erro que aparece é `senha falhou para o usuário postgres` — que
engana, porque o problema é o banco errado, não a senha.

O schema é criado pelo **Flyway**, nunca pelo Hibernate (`ddl-auto: validate`).
As migrations ficam em `back-reunioes/src/main/resources/db/migration/`. Uma
migration já aplicada **não se edita**: isso quebra o checksum e o back não sobe.
Crie uma nova.

A API fica em `http://localhost:8080`.

### 3. Front

```bash
cd reunioes-front && npm install && npm start
```

Abre em `http://localhost:4200` e consome a API real em `localhost:8080`.

---

## Entrando no sistema

Não há autenticação de verdade: **a senha é pedida no formulário mas não é
verificada**, porque o back não tem endpoint de autenticação. O login apenas
confere se o e-mail existe e lê o papel do colaborador.

A massa de desenvolvimento (migration `V2__seed_data.sql`) traz dois
administradores:

| E-mail | Papel |
|---|---|
| `ana.fontes@exemplo.com.br` | ADMIN |
| `gustavo.sato@exemplo.com.br` | ADMIN |

Os demais colaboradores da seed são `USUARIO`. A senha pode ser qualquer coisa.

---

## Telas do front

Há **uma tela por assunto**, servindo os dois perfis. O papel é lido pelo
`AuthService` e decide o que aparece: os botões de criar, editar e excluir só são
renderizados para `ADMIN`, e os métodos correspondentes também barram quem não é
admin — esconder o botão no template não impede a chamada pelo console.

| Rota | Tela | Acesso |
|---|---|---|
| `/login` | Entrada | público |
| `/reunioes` | Listagem, agrupada por status | todos |
| `/reunioes/novo` | Cadastro de reunião | admin |
| `/reunioes/:id` | Detalhe | todos |
| `/reunioes/:id/editar` | Edição | admin |
| `/acoes` | Listagem de ações | todos |
| `/colaboradores` | CRUD de colaboradores | admin |

As rotas antigas `/admin/**` e `/usuario/**` continuam funcionando como
redirecionamento, para não quebrar link salvo.

> Isso organiza a interface, **não protege nada**. Quem abrir o DevTools e
> escrever `localStorage.setItem('papel','ADMIN')` vê a tela de admin. A
> autorização de verdade precisa ser feita no back, que hoje aceita qualquer
> requisição.

---

## Documentação da API

Com o back no ar:

- Swagger UI — http://localhost:8080/swagger-ui/index.html
- OpenAPI — http://localhost:8080/v3/api-docs

Manual de testes e coleção do Insomnia prontos para importar:

- `back-reunioes/docs/MANUAL-TESTES-API.md`
- `back-reunioes/docs/insomnia-reunioes.json`

No Insomnia: **Create → Import → From File**, e ajuste a variável `base_url` do
ambiente se estiver usando outra porta.

---

## Endpoints

Tudo abaixo da raiz `/api/reunioes`.

### Reuniões — `/api/reunioes`

| Método | Rota | Sucesso |
|---|---|---|
| GET | `/api/reunioes` | 200 |
| GET | `/api/reunioes/{id}` | 200 |
| POST | `/api/reunioes` | 201 |
| PUT | `/api/reunioes/{id}` | 202 (hoje 500, ver limitações) |
| PATCH | `/api/reunioes/{id}` | 200 |
| PATCH | `/api/reunioes/{id}/ingestao` | 200 |
| GET | `/api/reunioes/{id}/relatorio` | 200 |
| GET | `/api/reunioes/{id}/feriado` | 200 |
| DELETE | `/api/reunioes/{id}` | 204 |

`/{id}/feriado` consulta a **BrasilAPI** por meio de um cliente **Spring Cloud
OpenFeign** e informa se a data da reunião caiu em feriado nacional.

### Ações — `/api/reunioes/acoes`

| Método | Rota | Sucesso |
|---|---|---|
| POST | `/api/reunioes/acoes/{reuniaoId}` | 201 |
| GET | `/api/reunioes/acoes` | 200 |
| GET | `/api/reunioes/acoes/reuniao?reuniaoId={id}` | 200 |
| GET | `/api/reunioes/acoes/{id}` | 200 |
| PUT | `/api/reunioes/acoes/{id}` | 200 |
| PATCH | `/api/reunioes/acoes/{id}` | 200 |
| DELETE | `/api/reunioes/acoes/{reuniaoId}/{id}` | 204 |

### Colaboradores — `/api/reunioes/colaboradores`

| Método | Rota | Sucesso |
|---|---|---|
| POST | `/api/reunioes/colaboradores` | 201 |
| GET | `/api/reunioes/colaboradores` | 200 |
| GET | `/api/reunioes/colaboradores/{id}` | 200 |
| GET | `/api/reunioes/colaboradores/email/{email}` | 200 |
| PUT | `/api/reunioes/colaboradores/{id}` | 200 |
| PATCH | `/api/reunioes/colaboradores/{id}` | 200 |
| DELETE | `/api/reunioes/colaboradores/{id}` | 204 |

### Detalhes do contrato

Listagens devolvem um envelope com `content`, `page`, `size`, `totalElements` e
`totalPages`, e aceitam `page`, `size` e `sort` como query params.

Erros seguem **ProblemDetail (RFC 7807)**, sem stack trace. Quando a validação
reprova campos, a resposta traz um array `campos` com `campo` e `mensagem`.

Ao criar ou substituir uma reunião, os campos `acoes` e `participantes` são
**listas de ids** de registros que já existem — não objetos e não nomes. Como a
ação nasce vinculada a uma reunião, criar reunião com ações novas são dois
passos: grava a reunião e depois faz `POST /api/reunioes/acoes/{reuniaoId}` para
cada ação. É o que o formulário do front faz.

Nenhum endpoint exige autenticação.

---

## Limitações conhecidas

Verificadas contra a aplicação rodando em 20/09/2026.

- **`PUT /api/reunioes/{id}` devolve 500** em qualquer cenário, então editar
  reunião não funciona. `buscarAcoes` e `buscarColaboradores` retornam
  `List.of()`, que é imutável, e o Hibernate estoura ao salvar.
- **`ReuniaoRequest` não tem o campo `resumo`.** O resumo enviado é descartado
  em silêncio: a resposta volta 201 com `resumo: null`.
- **Rota inexistente devolve 500** em vez de 404, **método não suportado
  devolve 500** em vez de 405, e **query param obrigatório ausente devolve 500**
  em vez de 400 (tente `GET /api/reunioes/acoes/reuniao` sem `reuniaoId`). O
  `@RestControllerAdvice` não trata `NoResourceFoundException`,
  `HttpRequestMethodNotSupportedException` nem
  `MissingServletRequestParameterException`.
- **`DELETE` de colaborador vinculado a reunião devolve 500** (violação de chave
  estrangeira); deveria ser 409.
- **`data` e `dataCadastro` são String livre**, sem validação de formato.
- **`Acao.tipo` é String livre** no banco, sem enum nem check constraint.
- **`AcaoDTO.id` é serializado como String** (`String.valueOf`), enquanto os
  demais ids são numéricos.
- **PUT e PATCH respondem 200**, embora a documentação OpenAPI dos controllers
  anuncie 202.
- **A seed marca as 24 ações como concluídas**, então a aba de ações pendentes
  aparece vazia numa demonstração.
- **No `docker-compose.yml`, o serviço `api` aponta para `postgres:55432`.**
  Dentro da rede do Docker o Postgres escuta na 5432 — 5433 é apenas a porta
  publicada no host. Subir a stack inteira pelo compose não conecta.

---

## Contribuindo

Branches saem da `develop` e voltam por Pull Request — nada direto na `develop`
ou na `main`.

```bash
git checkout develop && git pull origin develop
git checkout -b feature/nome-da-feature
```

Commits seguem Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`,
`test:`).

Antes de abrir o PR:

```bash
cd reunioes-front && npx ng build && npx ng test --watch=false
```

```bash
cd back-reunioes && ./mvnw test
```
