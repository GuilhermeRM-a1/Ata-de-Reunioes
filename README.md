# Ata de Reuniões — URBIA Cataratas

Sistema de gestão de atas de reunião: cadastro de reuniões, ações e colaboradores,
indicadores, verificação de feriado nacional e relatório consolidado. Autenticação
por **JWT**, com perfis `ADMIN` e `USUARIO`.

```
Ata-de-Reunioes/
├── back-reunioes/    API REST — Spring Boot 4.1.0, Java 17, PostgreSQL
├── reunioes-front/   SPA — Angular 19
└── scripts/
    └── deploy.sh     Provisionamento e deploy na EC2 (Ubuntu)
```

| Camada | Tecnologia |
|---|---|
| Front-end | Angular 19, MDB Angular UI Kit, SweetAlert2 |
| Back-end | Spring Boot 4.1.0, Spring Security, Spring Data JPA, Flyway, OpenFeign, springdoc |
| Banco | PostgreSQL |
| Produção | AWS EC2 (Ubuntu), Nginx, systemd |

---

## Sumário

1. [Funcionalidades](#funcionalidades)
2. [Arquitetura](#arquitetura)
3. [Segurança](#segurança)
4. [Deploy na AWS (EC2)](#deploy-na-aws-ec2)
5. [Rodando localmente](#rodando-localmente)
6. [Telas do front](#telas-do-front)
7. [API](#api)
8. [Testes](#testes)
9. [Contribuindo](#contribuindo)

---

## Funcionalidades

**CRUD** de reuniões, ações e colaboradores, persistido em PostgreSQL.

**Além do CRUD:**

| Funcionalidade | Onde está |
|---|---|
| Cadastro público e login com **JWT** | Telas `/cadastro` e `/login`; `POST /auth/register` e `POST /auth/login` |
| **Dashboard**: total de reuniões, reuniões finalizadas, finalizadas nos últimos 3 meses, ações concluídas e pendentes | Cards nas telas de reuniões e ações; `GET /api/dashboard` |
| **Verificação de feriado nacional** da data da reunião, via **Spring Cloud OpenFeign** na BrasilAPI | Formulário de reunião (`GET /api/reunioes/feriados?data=aaaa-MM-dd`); `GET /api/reunioes/{id}/feriado` |
| **Ingestão da análise da IA** (resumo executivo e transcrição) | Somente API: `PATCH /api/reunioes/{id}/ingestao` |
| **Relatório consolidado** da reunião | Somente API: `GET /api/reunioes/{id}/relatorio` |

No relatório, participantes, ações, responsáveis, áreas e pontos-chave vêm do que o
administrador registrou; da IA entra apenas o resumo executivo. Sem o resumo, o
relatório é recusado com **409**.

---

## Arquitetura

O back-end é organizado em camadas, dentro de `br.com.empresa.reunioes`:

| Pacote | Responsabilidade |
|---|---|
| `web` | Controllers, DTOs de entrada e saída, mappers web e `RestExceptionHandler` |
| `application` | Services com as regras de negócio e mappers de aplicação |
| `domain` | Entidades JPA, enums, repositories e exceções de domínio |
| `infra` | Segurança (JWT, filtro, CORS), cliente Feign e OpenAPI |

O schema é criado e versionado pelo **Flyway**. O Hibernate roda com
`ddl-auto: validate` e só confere se as entidades batem com o banco.

```
back-reunioes/src/main/resources/db/
├── migration/   V1, V3...   só schema — roda em todos os perfis
├── seed-dev/    V1000       massa fictícia de desenvolvimento (perfil dev)
└── seed-prod/   V1000       7 reuniões de demonstração, sem usuários (perfil prod)
```

Os perfis Spring são escolhidos **por fora do código** (`SPRING_PROFILES_ACTIVE`).
Não existe perfil padrão de propósito: esquecer de definir o perfil em produção
não carrega a massa fictícia de desenvolvimento.

---

## Segurança

- **JWT** assinado com HMAC256, emitido em `/auth/login` e `/auth/register`, com
  **expiração de 8 horas**. O token leva `id` e `papel`.
- **Senhas** gravadas com **BCrypt**.
- **Spring Security** stateless: só `/auth/login`, `/auth/register` e a
  documentação OpenAPI são públicos; qualquer outra rota exige token. Sem token, com
  token inválido ou expirado, ou com credenciais erradas, a resposta é **401**.
  Token válido sem o papel necessário recebe **403**. O papel é verificado com
  `@PreAuthorize` em cada endpoint.
- **CORS** liberado apenas para as origens de `app.cors.allowed-origins` (lista
  separada por vírgula).
- **Angular**: `authInterceptor` anexa o `Bearer` token em toda requisição fora de
  `/auth/`; `authGuard`, `adminGuard` e `loginGuard` protegem as rotas;
  `erroInterceptor` traduz os erros da API em mensagens para o usuário e, em caso de
  401, encerra a sessão e volta ao login. O `authGuard` também considera a
  expiração do token.
- **Nenhum segredo no repositório.** Senha do banco e `JWT_SECRET` vêm de variáveis
  de ambiente. Em produção o `deploy.sh` gera os dois de forma aleatória na própria
  instância, em `/etc/reunioes.env` (permissão `600`). O `.env` local está no
  `.gitignore`; o repositório versiona apenas o `.env.example`.

> O tráfego é HTTP puro (porta 80). Para uso real, coloque um domínio e HTTPS
> (por exemplo, Certbot) na frente do Nginx.

---

## Deploy na AWS (EC2)

O deploy é feito por **um único script**, `scripts/deploy.sh`, que provisiona a
instância do zero. Ele pode ser executado de novo a qualquer momento para atualizar
a aplicação.

### 1. Crie a instância

| Item | Valor |
|---|---|
| AMI | Ubuntu Server 22.04 ou 24.04 LTS |
| Tipo | `t3.small` (2 GB) recomendado; em instâncias menores o script cria swap, mas o build fica mais lento |
| Disco | 20 GB |
| Par de chaves | O seu, para acessar por SSH |

### 2. Configure o Security Group

Libere **somente** estas portas de entrada:

| Porta | Protocolo | Origem | Para quê |
|---|---|---|---|
| 22 | TCP | **Seu IP** (não use `0.0.0.0/0`) | SSH |
| 80 | TCP | `0.0.0.0/0` | Aplicação (Nginx) |

Não abra a **5432** (PostgreSQL) nem a **8080** (Spring Boot). O Nginx é a única
porta pública: ele serve o Angular e repassa `/api` e `/auth` ao back-end, que
conversa com o banco dentro da própria instância.

### 3. Execute o script

Acesse a instância por SSH e rode:

### 3. Execute o script

Acesse a instância por SSH e rode:

```bash
curl -fsSL https://raw.githubusercontent.com/GuilhermeRM-a1/Ata-de-Reunioes/develop/scripts/deploy.sh -o deploy.sh
sudo ADMIN_EMAIL=seu@email.com ADMIN_SENHA=Senha1234 bash deploy.sh
```

A senha do admin precisa ter entre **8 e 20 caracteres**, que é a regra de validação do cadastro.

Ao final o script imprime `Pronto: http://<IP-PÚBLICO>/`. Abra o endereço no
navegador e entre com o e-mail e a senha informados.

Variáveis opcionais: `BRANCH` (padrão `develop`) e `HOST` (padrão: IP público
detectado). Exemplo: `sudo BRANCH=main ADMIN_EMAIL=... ADMIN_SENHA=... bash deploy.sh`.

O `read -s` evita que a senha apareça na tela e no histórico do shell. Ela precisa
ter entre **8 e 20 caracteres**, que é a regra de validação do cadastro.

Ao final o script imprime `Pronto: http://<IP-PÚBLICO>/`. Abra o endereço no
navegador e entre com o e-mail e a senha informados.

Variáveis opcionais: `BRANCH` (padrão `develop`) e `HOST` (padrão: IP público
detectado). Exemplo: `sudo BRANCH=main ADMIN_EMAIL=... ADMIN_SENHA=... bash deploy.sh`.

### O que o script faz

| # | Etapa | Detalhe |
|---|---|---|
| 1 | Swap | Cria 2 GB de swap se não houver, porque o build estoura a memória de instâncias pequenas |
| 2 | Pacotes | Git, Nginx, PostgreSQL, OpenJDK 17 e Node.js 20 |
| 3 | Banco e segredos | Cria usuário e banco `reunioes`, gera a senha do banco e o `JWT_SECRET` e grava em `/etc/reunioes.env` (só na primeira execução) |
| 4 | Código | Clona o repositório em `/opt/reunioes-src` (ou faz `git pull`) |
| 5 | Back-end | Compila com `./mvnw`, copia o jar para `/opt/reunioes.jar` e registra o serviço **systemd** `reunioes`, com usuário sem privilégios |
| 6 | Front-end | `npm ci` + `ng build` (produção) e copia para `/var/www/reunioes` |
| 7 | Nginx | Serve o Angular com **fallback para rotas SPA** (`try_files ... /index.html`) e repassa `/api/` e `/auth/` para `127.0.0.1:8080` |
| 8 | Admin inicial | Cadastra o admin pela API (`/auth/register`) e o promove a `ADMIN` no banco |

O back sobe com o perfil `prod`: o Flyway aplica o schema e a `seed-prod`
(7 reuniões de demonstração, sem usuários). O Swagger fica desligado e o CORS usa o
IP público da instância. O script **não usa credenciais da AWS**: ele roda dentro
da instância.

### 4. Confira

```bash
systemctl status reunioes nginx postgresql        # os três devem estar "active (running)"
curl -i http://localhost:8080/api/reunioes        # 401 = a API está de pé e protegida
sudo ss -tlnp | grep 5432                         # deve aparecer só 127.0.0.1:5432
```

De fora da instância, a porta 5432 deve estar fechada.

### 5. Logs

```bash
sudo journalctl -u reunioes -f                    # back-end ao vivo (Ctrl+C para sair)
sudo journalctl -u reunioes -n 100 --no-pager     # últimas 100 linhas
sudo journalctl -u reunioes --since "10 min ago"  # últimos 10 minutos
sudo tail -f /var/log/nginx/error.log             # erros do Nginx
sudo tail -f /var/log/nginx/access.log            # requisições recebidas
```

### 6. Atualizar a aplicação

Rode o mesmo script de novo. Ele faz `git pull`, recompila back e front, reinicia
o serviço e recarrega o Nginx. Os segredos existentes são preservados e novas
migrations do Flyway são aplicadas na subida.

Se o IP público mudar (parar e iniciar a EC2 gera um novo IP), rode o script de
novo para atualizar o CORS, ou associe um **Elastic IP** à instância.

### Problemas comuns

| Sintoma | Causa provável | O que fazer |
|---|---|---|
| Página não abre | Porta 80 fechada no Security Group | Libere a 80 de entrada |
| `502 Bad Gateway` | Back-end não subiu | `sudo journalctl -u reunioes -n 100` |
| Login do admin falha | Senha fora de 8–20 caracteres: o cadastro foi recusado | Rode o script de novo com uma senha válida |
| Erro de CORS no navegador | IP da instância mudou | Rode o script de novo |
| Build morre com `Killed` | Pouca memória | Use `t3.small` ou maior |

---

## Rodando localmente

Pré-requisitos: **JDK 17** (não um JRE), Node.js 20+ e Docker.

### 1. Banco

```bash
cd back-reunioes
cp .env.example .env     # preencha os valores
docker compose up -d postgres
```

O contêiner publica o Postgres em `localhost:5433` (a 5432 costuma estar ocupada
por um Postgres instalado na máquina). O `docker-compose.yml` lê `DB_NAME`,
`DB_USER` e `DB_PASSWORD` do `.env`, e a aplicação lê `DB_URL`, `DB_USER`,
`DB_PASSWORD` e `JWT_SECRET`:

```
DB_URL=jdbc:postgresql://localhost:5433/reunioes
DB_NAME=reunioes
DB_USER=postgres
DB_PASSWORD=sua_senha
JWT_SECRET=<mínimo 32 caracteres; gere com: openssl rand -base64 48>
```

Use valores simples, sem aspas, espaços ou `$`.

### 2. Back-end

O perfil é obrigatório (não há padrão). No IntelliJ, preencha **Active profiles**
com `dev`. Pelo terminal:

```bash
cd back-reunioes
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

No PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
./mvnw spring-boot:run
```

Confira no log: `The following 1 profile is active: "dev"`. A API sobe em
`http://localhost:8080` e o perfil `dev` carrega a massa fictícia (`seed-dev`).

### 3. Front-end

```bash
cd reunioes-front && npm install && npm start
```

Abre em `http://localhost:4200`. O `proxy.conf.json` repassa `/api` e `/auth` para
`localhost:8080`; o front só usa caminhos relativos (`core/config/api.config.ts`).

### Usuários de desenvolvimento

Senha de todos: `senha123`.

| E-mail | Papel |
|---|---|
| `ana.fontes@exemplo.com.br` | ADMIN |
| `gustavo.sato@exemplo.com.br` | ADMIN |
| `carlos.prado@exemplo.com.br` | USUARIO |

Os demais colaboradores da seed são `USUARIO`.

### Documentação da API (somente dev)

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs

Em `back-reunioes/docs/` há uma coleção do Insomnia e um manual de testes, mas eles
**não incluem autenticação**. Para usá-los, faça `POST /auth/login` e envie o token
no cabeçalho `Authorization: Bearer <token>` em todas as chamadas.

---

## Telas do front

| Rota | Tela | Acesso |
|---|---|---|
| `/login` | Entrada | público |
| `/cadastro` | Cadastro de novo usuário (entra logado em seguida) | público |
| `/reunioes` | Cards do dashboard e listagem agrupada por status | autenticado |
| `/reunioes/novo` | Cadastro de reunião, com verificação de feriado da data | admin |
| `/reunioes/:id` | Detalhe: resumo, pontos-chave, ações e participantes | autenticado |
| `/reunioes/:id/editar` | Edição | admin |
| `/acoes` | Ações pendentes e concluídas, paginadas | autenticado |
| `/colaboradores` | CRUD de colaboradores | admin |

As rotas antigas `/admin/**` e `/usuario/**` redirecionam para as atuais. Os botões
de criar, editar e excluir só aparecem para `ADMIN`, mas quem decide é o back-end:
cada endpoint valida o papel do token e responde 403 quando não é permitido.

---

## API

Todas as rotas, exceto `/auth/login` e `/auth/register`, exigem o cabeçalho
`Authorization: Bearer <token>`.

### Autenticação — `/auth`

| Método | Rota | Sucesso | Acesso |
|---|---|---|---|
| POST | `/auth/login` | 200, devolve `token`, `nome`, `email` e `papel` | público |
| POST | `/auth/register` | 201, cria sempre como `USUARIO` e devolve o token | público |

### Reuniões — `/api/reunioes`

| Método | Rota | Sucesso | Acesso |
|---|---|---|---|
| GET | `/api/reunioes` | 200, paginado | autenticado |
| GET | `/api/reunioes/{id}` | 200 | autenticado |
| POST | `/api/reunioes` | 201, com cabeçalho `Location` | admin |
| PUT | `/api/reunioes/{id}` | 200 | admin |
| PATCH | `/api/reunioes/{id}` | 200 | admin |
| PATCH | `/api/reunioes/{id}/ingestao` | 202 | admin |
| GET | `/api/reunioes/{id}/relatorio` | 200; 409 se não há resumo da IA | autenticado |
| GET | `/api/reunioes/feriados?data=aaaa-MM-dd` | 200; 404 se a data é inválida; 503 se a BrasilAPI falha | autenticado |
| GET | `/api/reunioes/{id}/feriado` | 200; 404; 503 | autenticado |
| DELETE | `/api/reunioes/{id}` | 204 | admin |

Corpo de criação/substituição da reunião: `titulo` (mínimo 5 caracteres), `data`,
`statusTranscricao` e `statusReuniao` são obrigatórios; `areas`, `pontosChaves`,
`resumo` são opcionais; `participantes` e `acoes` são **listas de ids** de registros
que já existem. No PATCH, campo ausente ou nulo não altera nada.

Ingestão: `resumo` é obrigatório e `transcricao` é opcional (reenviar só o resumo
não apaga a transcrição já gravada).

Valores aceitos: `statusReuniao` = `PENDENTE`, `EM_ANDAMENTO`, `FINALIZADA`;
`statusTranscricao` = `RECEBIDA`, `TRANSCREVENDO`, `ANALISANDO`, `CONCLUIDA`, `ERRO`.

### Ações — `/api/reunioes/acoes`

| Método | Rota | Sucesso | Acesso |
|---|---|---|---|
| POST | `/api/reunioes/acoes/{reuniaoId}` | 201 | admin |
| GET | `/api/reunioes/acoes?concluida=true\|false` | 200, paginado; `concluida` é obrigatório | autenticado |
| GET | `/api/reunioes/acoes/reuniao?reuniaoId={id}` | 200, lista | autenticado |
| GET | `/api/reunioes/acoes/{id}` | 200 | autenticado |
| PUT | `/api/reunioes/acoes/{id}` | 200 | admin |
| PATCH | `/api/reunioes/acoes/{id}` | 200 | admin |
| DELETE | `/api/reunioes/acoes/{reuniaoId}/{id}` | 204 | admin |

Corpo da ação: `titulo` obrigatório; `descricao`, `tipo`, `prazo`, `concluida` e
`responsavel` (lista de ids de colaboradores) opcionais.

### Colaboradores — `/api/reunioes/colaboradores`

| Método | Rota | Sucesso | Acesso |
|---|---|---|---|
| POST | `/api/reunioes/colaboradores` | 201 | admin |
| GET | `/api/reunioes/colaboradores` | 200, paginado | autenticado |
| GET | `/api/reunioes/colaboradores/{id}` | 200 | autenticado |
| GET | `/api/reunioes/colaboradores/email/{email}` | 200 | autenticado |
| PUT | `/api/reunioes/colaboradores/{id}` | 200 | admin |
| PATCH | `/api/reunioes/colaboradores/{id}` | 200 | admin |
| DELETE | `/api/reunioes/colaboradores/{id}` | 204 | admin |

Corpo do colaborador: `nome`, `email` válido, `senha` de **8 a 20 caracteres** e
`papel` (`ADMIN` ou `USUARIO`) são obrigatórios. E-mail repetido devolve 409.

### Dashboard — `/api/dashboard`

| Método | Rota | Resposta | Acesso |
|---|---|---|---|
| GET | `/api/dashboard` | `totalReunioes`, `reunioesConcluidas`, `reunioesConcluidasUltimos3Meses`, `acoesConcluidas`, `acoesPendentes` | autenticado |

"Concluída" aqui significa reunião com status `FINALIZADA`.

### Contrato de erros

Listagens paginadas aceitam `page`, `size` e `sort`. Erros seguem
**ProblemDetail (RFC 7807)**, sem stack trace; quando a validação reprova campos, a
resposta traz o array `campos` com `campo` e `mensagem`.

| Status | Quando |
|---|---|
| 400 | Campo inválido ou ausente, JSON malformado, id ou parâmetro com tipo errado, parâmetro obrigatório ausente |
| 401 | Sem token, token inválido ou expirado, e-mail ou senha incorretos |
| 403 | Token válido, mas sem o papel necessário |
| 404 | Recurso ou rota inexistente |
| 405 | Método HTTP não suportado na rota |
| 409 | E-mail já cadastrado, relatório sem resumo da IA, ou violação de integridade (registro ainda vinculado a outros) |
| 503 | A API externa de feriados (BrasilAPI) está indisponível |
| 500 | Erro inesperado; o detalhe fica só no log |

### Limitações conhecidas

- `data`, `prazo` e `dataCadastro` são texto livre, sem validação de formato; `Acao.tipo` também.
- O front lista reuniões e colaboradores sem paginar: aparece só a primeira página
  da API (20 itens, padrão do Spring).
- Relatório e ingestão existem só na API; não há tela para eles.
- Na documentação OpenAPI, PUT e PATCH anunciam 202, mas o código responde 200.
- A `seed-dev` marca todas as ações como concluídas, então a aba de pendentes
  aparece vazia em desenvolvimento.
- A comunicação em produção é HTTP; HTTPS depende de domínio e certificado.

---

## Testes

```bash
cd back-reunioes && ./mvnw test
```

```bash
cd reunioes-front && npx ng test --watch=false
```

---

## Contribuindo

Branches saem da `develop` e voltam por Pull Request — nada direto na `develop`
ou na `main`.

```bash
git checkout develop && git pull origin develop
git checkout -b feature/nome-da-feature
```

Commits seguem Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`,
`test:`), com mensagens claras. Antes de abrir o PR:

```bash
cd reunioes-front && npx ng build && npx ng test --watch=false
```

```bash
cd back-reunioes && ./mvnw test
```

Uma migration já aplicada **não se edita**: isso quebra o checksum e o back não
sobe. Crie uma nova.
