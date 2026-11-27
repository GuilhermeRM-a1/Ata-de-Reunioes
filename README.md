# Ata de Reuniões — URBIA Cataratas

Sistema de gestão de atas de reunião: cadastro de reuniões, ações e colaboradores,
relatório consolidado, dashboard e consulta de feriados nacionais. Autenticação
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

**CRUD** de reuniões, ações e colaboradores, com persistência em PostgreSQL.

**Além do CRUD:**

- **Cadastro público e login com JWT** — o usuário se cadastra em `/cadastro` e já
  entra logado.
- **Dashboard** com indicadores consolidados (`GET /api/dashboard`).
- **Relatório consolidado da reunião** (`GET /api/reunioes/{id}/relatorio`).
- **Ingestão de resumo e transcrição** vindos da IA (`PATCH /api/reunioes/{id}/ingestao`).
- **Consulta de feriados nacionais** na BrasilAPI via **Spring Cloud OpenFeign**,
  indicando se a data da reunião cai em feriado.

---

## Arquitetura

O back-end é organizado em camadas, dentro de `br.com.empresa.reunioes`:

| Pacote | Responsabilidade |
|---|---|
| `web` | Controllers, DTOs de entrada e saída, mappers web e `RestExceptionHandler` |
| `application` | Services com as regras de negócio e mappers de aplicação |
| `domain` | Entidades JPA, enums, repositories e exceções de domínio |
| `infra` | Configuração de segurança (JWT, filtro, CORS), cliente Feign e OpenAPI |

O schema é criado e versionado pelo **Flyway**. O Hibernate roda com
`ddl-auto: validate` e só confere se as entidades batem com o banco.

```
back-reunioes/src/main/resources/db/
├── migration/   V1, V3...   só schema — roda em todos os perfis
├── seed-dev/    V1000       massa fictícia de desenvolvimento (perfil dev)
└── seed-prod/   V1000       reuniões de demonstração, sem usuários (perfil prod)
```

Os perfis Spring são escolhidos **por fora do código** (`SPRING_PROFILES_ACTIVE`).
Não existe perfil padrão de propósito: esquecer de definir o perfil em produção
não carrega dados fictícios de desenvolvimento.

---

## Segurança

- **JWT** assinado com HMAC256, emitido em `/auth/login` e `/auth/register`, com
  **expiração de 8 horas**.
- **Senhas** gravadas com **BCrypt**.
- **Spring Security** stateless: qualquer rota fora de `/auth/login` e
  `/auth/register` exige token. Requisição sem token ou com token inválido recebe
  **401**; usuário sem o papel necessário recebe **403**. A autorização por papel
  é feita com `@PreAuthorize` em cada endpoint.
- **CORS** liberado apenas para as origens definidas em `app.cors.allowed-origins`.
- **Angular**: `authInterceptor` anexa o `Bearer` token em toda requisição;
  `authGuard`, `adminGuard` e `loginGuard` protegem as rotas;
  `erroInterceptor` traduz os erros da API em mensagens para o usuário e
  redireciona ao login quando a sessão expira.
- **Nenhum segredo no repositório.** Senha do banco e `JWT_SECRET` são lidos de
  variáveis de ambiente. Em produção o `deploy.sh` gera os dois de forma aleatória
  na própria instância, em `/etc/reunioes.env` (permissão `600`). O arquivo `.env`
  local está no `.gitignore`; o repositório versiona apenas o `.env.example`.

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
| Tipo | `t3.small` (2 GB) recomendado; `t3.micro` funciona, mais devagar, porque o script cria swap |
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

```bash
curl -fsSL https://raw.githubusercontent.com/GuilhermeRM-a1/Ata-de-Reunioes/develop/scripts/deploy.sh -o deploy.sh
read -rsp "Senha do admin (8 a 20 caracteres): " ADMIN_SENHA; echo
sudo ADMIN_EMAIL=seu@email.com ADMIN_SENHA="$ADMIN_SENHA" bash deploy.sh
```

O `read -s` evita que a senha apareça na tela e no histórico do shell. Ela precisa
ter entre **8 e 20 caracteres**, que é a regra do cadastro.

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

O banco é criado com o perfil `prod`: o Flyway aplica o schema e a `seed-prod`
(7 reuniões de demonstração, sem usuários). O Swagger fica desligado em produção
e o CORS usa o IP público da instância.

O script **não usa credenciais da AWS**: ele roda dentro da instância.

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
o serviço e recarrega o Nginx. Os segredos já existentes são preservados e novas
migrations do Flyway são aplicadas na subida.

Se o IP público da instância mudar (parar e iniciar a EC2 gera um novo IP), rode o
script de novo para atualizar o CORS, ou associe um **Elastic IP** à instância.

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
cp .env.example .env     # preencha DB_PASSWORD e JWT_SECRET
docker compose up -d postgres
```

O contêiner publica o Postgres em `localhost:5433` (a 5432 costuma estar ocupada
por um Postgres instalado na máquina). No `.env`, aponte a aplicação para ela:

```
DB_URL=jdbc:postgresql://localhost:5433/reunioes
DB_USER=postgres
DB_PASSWORD=sua_senha
DB_NAME=reunioes
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
`http://localhost:8080`. O perfil `dev` carrega a massa fictícia (`seed-dev`).

### 3. Front-end

```bash
cd reunioes-front && npm install && npm start
```

Abre em `http://localhost:4200`. O `proxy.conf.json` repassa `/api` e `/auth` para
`localhost:8080`, então o front não tem endereço de API fixo no código.

### Usuários de desenvolvimento

Senha de todos: `senha123`.

| E-mail | Papel |
|---|---|
| `ana.fontes@exemplo.com.br` | ADMIN |
| `gustavo.sato@exemplo.com.br` | ADMIN |
| `carlos.prado@exemplo.com.br` | USUARIO |

### Documentação da API (somente dev)

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs
- Coleção do Insomnia: `back-reunioes/docs/insomnia-reunioes.json`
- Manual de testes: `back-reunioes/docs/MANUAL-TESTES-API.md`

---

## Telas do front

| Rota | Tela | Acesso |
|---|---|---|
| `/login` | Entrada | público |
| `/cadastro` | Cadastro de novo usuário | público |
| `/reunioes` | Listagem, agrupada por status | autenticado |
| `/reunioes/novo` | Cadastro de reunião | admin |
| `/reunioes/:id` | Detalhe, relatório e feriado | autenticado |
| `/reunioes/:id/editar` | Edição | admin |
| `/acoes` | Listagem de ações | autenticado |
| `/colaboradores` | CRUD de colaboradores | admin |

Os botões de criar, editar e excluir só aparecem para `ADMIN`, mas quem decide é o
back-end: cada endpoint valida o papel do token e responde 403 quando não é permitido.

---

## API

Todas as rotas, exceto `/auth/login` e `/auth/register`, exigem o cabeçalho
`Authorization: Bearer <token>`.

### Autenticação — `/auth`

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/auth/login` | Login; devolve o token JWT | público |
| POST | `/auth/register` | Cadastro (sempre `USUARIO`); devolve o token | público |

O cadastro valida nome, e-mail e senha de 8 a 20 caracteres. E-mail repetido
devolve **409**.

### Reuniões — `/api/reunioes`

| Método | Rota | Acesso |
|---|---|---|
| GET | `/api/reunioes` | autenticado |
| GET | `/api/reunioes/{id}` | autenticado |
| POST | `/api/reunioes` | admin |
| PUT | `/api/reunioes/{id}` | admin |
| PATCH | `/api/reunioes/{id}` | admin |
| PATCH | `/api/reunioes/{id}/ingestao` | admin |
| GET | `/api/reunioes/{id}/relatorio` | autenticado |
| GET | `/api/reunioes/feriados` | autenticado |
| GET | `/api/reunioes/{id}/feriado` | autenticado |
| DELETE | `/api/reunioes/{id}` | admin |

### Ações — `/api/reunioes/acoes`

| Método | Rota | Acesso |
|---|---|---|
| POST | `/api/reunioes/acoes/{reuniaoId}` | admin |
| GET | `/api/reunioes/acoes` | autenticado |
| GET | `/api/reunioes/acoes/reuniao?reuniaoId={id}` | autenticado |
| GET | `/api/reunioes/acoes/{id}` | autenticado |
| PUT | `/api/reunioes/acoes/{id}` | admin |
| PATCH | `/api/reunioes/acoes/{id}` | admin |
| DELETE | `/api/reunioes/acoes/{reuniaoId}/{id}` | admin |

### Colaboradores — `/api/reunioes/colaboradores`

| Método | Rota | Acesso |
|---|---|---|
| POST | `/api/reunioes/colaboradores` | admin |
| GET | `/api/reunioes/colaboradores` | autenticado |
| GET | `/api/reunioes/colaboradores/{id}` | autenticado |
| GET | `/api/reunioes/colaboradores/email/{email}` | autenticado |
| PUT | `/api/reunioes/colaboradores/{id}` | admin |
| PATCH | `/api/reunioes/colaboradores/{id}` | admin |
| DELETE | `/api/reunioes/colaboradores/{id}` | admin |

### Dashboard — `/api/dashboard`

| Método | Rota | Acesso |
|---|---|---|
| GET | `/api/dashboard` | autenticado |

### Contrato

- Listagens devolvem um envelope com `content`, `page`, `size`, `totalElements` e
  `totalPages`, e aceitam `page`, `size` e `sort`.
- Erros seguem **ProblemDetail (RFC 7807)**, sem stack trace. Quando a validação
  reprova campos, a resposta traz o array `campos` com `campo` e `mensagem`.
- Ao criar ou substituir uma reunião, `acoes` e `participantes` são **listas de
  ids** de registros já existentes.

| Status | Quando |
|---|---|
| 400 | Campo inválido ou ausente, JSON malformado, parâmetro obrigatório ausente |
| 401 | Sem token, token inválido ou expirado, credenciais incorretas |
| 403 | Token válido, mas sem o papel necessário |
| 404 | Recurso ou rota inexistente |
| 405 | Método HTTP não suportado |
| 409 | E-mail já cadastrado ou violação de integridade (registro vinculado) |
| 500 | Erro inesperado (o detalhe fica só no log) |

### Limitações conhecidas

- `data` e `prazo` são texto livre, sem validação de formato.
- `Acao.tipo` é texto livre, sem enum.
- A `seed-dev` marca todas as ações como concluídas, então a aba de ações
  pendentes aparece vazia em desenvolvimento.
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
