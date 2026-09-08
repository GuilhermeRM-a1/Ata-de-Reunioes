# Manual de testes da API — back-reunioes (Insomnia)

Tudo neste manual foi executado contra a aplicação rodando de verdade em 17/08/2026.
Os status HTTP e os corpos de resposta são os que a API devolveu, não o que a
documentação promete.

---

## Parte 1 — Subir a API

### 1.1 O Java do seu PATH não serve

O `java` que responde no seu terminal é o **JRE 1.8**. O projeto exige **Java 17+**
(`<java.version>17</java.version>` no `pom.xml`, Spring Boot 4.1.0). Com o Java 8 o
build nem chega a compilar — quebra no carregamento dos plugins do Maven.

Você tem dois JDKs instalados que servem:

- `C:\Users\guilh\.jdks\openjdk-25.0.2` (usei este)
- `C:\Users\guilh\.jdks\ms-17.0.18`

No PowerShell, antes de qualquer comando Maven:

```bash
$env:JAVA_HOME='C:\Users\guilh\.jdks\openjdk-25.0.2'
```

Isso vale só para a janela atual. No IntelliJ, o SDK do projeto já deve estar
apontado para um desses — o problema é só na linha de comando.

### 1.2 A porta 8080 está ocupada

Existe um **Apache httpd (PID 5740)** escutando na 8080 na sua máquina — provavelmente
XAMPP. Se você subir a API sem resolver isso, ela morre com:

```
APPLICATION FAILED TO START
Web server failed to start. Port 8080 was already in use.
```

Duas saídas:

**A) Parar o Apache** (se for XAMPP, pelo painel de controle) e usar a 8080 normal.

**B) Subir a API em outra porta** — foi o que fiz nos testes:

```bash
cd Ata-de-Reunioes/back-reunioes && ./mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Se escolher a B, troque `base_url` para `http://localhost:8081` no Insomnia.

### 1.3 Subir

```bash
cd Ata-de-Reunioes/back-reunioes && ./mvnw.cmd spring-boot:run
```

Ou gerar o jar e rodar:

```bash
cd Ata-de-Reunioes/back-reunioes && ./mvnw.cmd -DskipTests package
```

```bash
java -jar Ata-de-Reunioes/back-reunioes/target/reunioes-0.0.1-SNAPSHOT.jar
```

Espere a linha `Started ReunioesApplication in X seconds`. Subiu em ~6s aqui.

### 1.4 O banco é H2 em memória e apaga a cada restart

`application.yml` usa `jdbc:h2:mem:reunioes` com `ddl-auto: create` e Flyway desligado.
Consequências práticas para o teste:

- **Não existe nenhum dado ao subir.** O primeiro `GET` sempre vem `content: []`.
- **Os ids sempre reiniciam do 1** a cada restart. Isso é bom: os testes deste manual
  são reproduzíveis.
- **Reiniciou, perdeu tudo.** Se algo parou de bater com o manual, reinicie a API e
  refaça na ordem.

O console do H2 está habilitado no yml, mas **não abre**: `/h2-console` devolve 400 e
`/h2-console/` devolve 500 (o motivo está na Parte 4, achado #1). Use a API e o
Swagger para inspecionar os dados.

### 1.5 Não há autenticação

Não existe Spring Security no `pom.xml`. Nenhum endpoint pede token ou header de auth.
Deixe a aba **Auth** do Insomnia em `No Auth` em tudo.

---

## Parte 2 — Configurar o Insomnia

### 2.1 Importar a coleção pronta

Deixei uma coleção com os 29 requests deste manual, já organizada em pastas:

`back-reunioes/docs/insomnia-reunioes.json`

No Insomnia: **Create → Import → From File** → escolha esse arquivo. Vai aparecer a
coleção *API Atas de Reuniao* com as pastas `00 - Sanidade`, `01 - Colaboradores`,
`02 - Reunioes` e `03 - Erros e casos de borda`.

### 2.2 O ambiente

A coleção já vem com um ambiente **Local** com três variáveis:

| Variável | Valor padrão | Para que serve |
|---|---|---|
| `base_url` | `http://localhost:8080` | troque para `:8081` se usou outra porta |
| `id_colaborador` | `1` | id usado nos requests de colaborador |
| `id_reuniao` | `1` | id usado nos requests de reunião |

Editar: canto superior esquerdo, no seletor de ambiente → **Manage Environments**.

### 2.3 Se preferir montar na mão

Só duas coisas importam em todo request com corpo:

- Método e URL corretos (atenção à Parte 3 — as rotas de reunião não são o que você espera).
- Body em **JSON** (no Insomnia: `Body` → `JSON`). Ele já manda o
  `Content-Type: application/json` sozinho, não precisa criar o header à mão.

### 2.4 Sanidade antes de começar

Antes do primeiro teste, rode o request **`00 - Sanidade / OpenAPI`**:

```
GET {{ base_url }}/v3/api-docs
```

Se vier 200 com um JSON grande, a API está no ar e o `base_url` está certo. Esse
endpoint também é a fonte da verdade sobre quais rotas existem — foi assim que
confirmei a lista da Parte 3.

O Swagger UI fica em `http://localhost:8080/swagger-ui.html` (redireciona para
`/swagger-ui/index.html`). Serve para conferir contratos, mas para testar de verdade
o Insomnia é melhor: guarda histórico e você controla o corpo exato.

---

## Parte 3 — As rotas reais

**Leia isto antes de montar qualquer request.** Os dois controllers seguem padrões
diferentes de URL, e não é por opção de design — é um bug (achado #1 da Parte 4).

### Colaboradores — `/api/reunioes/colaboradores`

| Método | URL | Sucesso |
|---|---|---|
| POST | `/api/reunioes/colaboradores` | 201 |
| GET | `/api/reunioes/colaboradores` | 200 |
| GET | `/api/reunioes/colaboradores/{id}` | 200 |
| PUT | `/api/reunioes/colaboradores/{id}` | 202 |
| PATCH | `/api/reunioes/colaboradores/{id}` | 202 |
| DELETE | `/api/reunioes/colaboradores/{id}` | 204 |

### Reuniões — na **raiz** do servidor

| Método | URL | Sucesso |
|---|---|---|
| POST | `/` | 201 |
| GET | `/` | 200 |
| GET | `/{id}` | 200 |
| PUT | `/{id}` | 202 |
| PATCH | `/{id}` | 202 |
| DELETE | `/{id}` | 204 |

Ou seja: a reunião de id 1 é `http://localhost:8080/1`, não
`http://localhost:8080/api/reunioes/1`. O `ReuniaoController` está **sem**
`@RequestMapping` na classe.

### Ações — não existem

Há `AcaoService`, `AcaoRepository`, `AcaoDTO` e `AcaoRequest` no código, mas
**nenhum `AcaoController`**. Não existe rota para criar ação. Isso significa que o
campo `acoes` do corpo da reunião **nunca pode ser preenchido com um id válido** —
qualquer id que você mandar ali vai dar 404, porque a tabela de ações está sempre vazia.
Nos testes, mande `"acoes": []` ou omita o campo.

### Corpos aceitos

**`ColaboradorRequest`**

```json
{
  "nome": "Ana Beatriz Fontes",
  "senha": "123456",
  "monitorarReunioes": true,
  "dataCadastro": "2026-08-17"
}
```

`nome` e `senha` são obrigatórios no POST e no PUT. `dataCadastro` é **String livre**,
não `LocalDate` — a API aceita `"qualquer coisa"` ali.

**`ReuniaoRequest`**

```json
{
  "titulo": "Alinhamento semanal de Operacoes",
  "data": "2026-08-17T09:00:00",
  "status": "CONCLUIDA",
  "areas": ["Operacoes", "Financeiro"],
  "pontosChaves": ["Fila acima da meta"],
  "participantes": [1],
  "acoes": [],
  "totalAcoes": 2
}
```

Regras que valem a pena saber antes de testar:

- `titulo`: obrigatório, **mínimo 5 caracteres**.
- `data`: obrigatório, mas é **String** — não há validação de formato. `"amanhã"` passa.
- `status`: obrigatório, mas é **String** — não é validado contra o enum. Ver achado #3.
- `participantes` e `acoes`: listas de **ids** (números), não de objetos.
- `totalAcoes`: só é respeitado **quando `acoes` vem vazio**. Se você mandar ações, o
  serviço sobrescreve com a quantidade real (`calcularTotalAcoes`).
- `resumo` existe na entidade `Reuniao` mas **não está no request nem no DTO** — não há
  como preencher nem ler.

### Corpos devolvidos

**`ColaboradorDTO`** — a senha nunca volta, isso está correto:

```json
{ "id": 1, "nome": "Ana Beatriz Fontes", "monitorarReunioes": true, "dataCadastro": "2026-08-17" }
```

**`ReuniaoDTO`** — repare que **não tem `id`**:

```json
{
  "titulo": "Alinhamento semanal de Operacoes",
  "data": "2026-08-17T09:00:00",
  "status": "CONCLUIDA",
  "participantes": ["Ana Beatriz Fontes"],
  "areas": ["Operacoes"],
  "totalAcoes": 2
}
```

`participantes` volta como **lista de nomes**, não de ids — você manda `[1]` e recebe
`["Ana Beatriz Fontes"]`. E como não há `id` na resposta, depois do POST você não sabe
qual id foi criado; tem que contar na mão (1, 2, 3...). Ver achado #2.

**Envelope de paginação** (`PaginaResponse`), igual nos dois recursos:

```json
{ "content": [ ... ], "page": 0, "size": 10, "totalElements": 1, "totalPages": 1 }
```

Query params de paginação: `page`, `size` e `sort` (ex.: `sort=titulo,asc`). São
resolvidos pelo `Pageable` do Spring. `page` fora do range devolve **200 com `content`
vazio**, não 404.

**Erros** seguem o padrão ProblemDetail (RFC 7807), com `campos` extra na validação:

```json
{
  "detail": "Um ou mais campos foram rejeitados na validação.",
  "instance": "/",
  "status": 400,
  "title": "Campos inválidos",
  "type": "https://api.reunioes/erros/campos-invalidos",
  "timestamp": "2026-08-17T19:56:19.13-03:00",
  "campos": [
    { "campo": "titulo", "mensagem": "O título deve ter ao menos 5 caracteres" }
  ]
}
```

---

## Parte 4 — Roteiro de testes

Rode **na ordem**, com a API recém-iniciada (banco vazio, ids do 1). A coluna
"resultado" é o que eu obtive de verdade rodando cada um.

### Bloco 1 — Colaboradores, caminho felizes

| # | Request | Corpo | Esperado | Resultado |
|---|---|---|---|---|
| C1 | `POST /api/reunioes/colaboradores` | Ana Beatriz, senha 123456 | 201 + DTO com `id: 1`, **sem senha** | ✅ 201 |
| C2 | `POST /api/reunioes/colaboradores` | Carlos Prado, senha abc123 | 201 + `id: 2` | ✅ 201 |
| C3 | `GET /api/reunioes/colaboradores?page=0&size=10` | — | 200, `totalElements: 2` | ✅ 200 |
| C4 | `GET /api/reunioes/colaboradores/1` | — | 200 + Ana Beatriz | ✅ 200 |
| C5 | `PUT /api/reunioes/colaboradores/1` | nome alterado + senha | **202** (não 200!) | ✅ 202 |
| C6 | `PATCH /api/reunioes/colaboradores/1` | só `{"monitorarReunioes": true}` | 202, só esse campo muda | ✅ 202 |
| C7 | `DELETE /api/reunioes/colaboradores/2` | — | 204 sem corpo | ✅ 204 |

**O que conferir em C1**: o campo `senha` **não pode** aparecer na resposta. É o
requisito de segurança mais óbvio da API e ele está atendido — `ColaboradorDTO` não
carrega senha.

**O que conferir em C5**: o `ColaboradorService.atualizar` **não copia a senha**. Você
manda `"senha": "novaSenha"` no PUT, recebe 202, e a senha no banco continua a antiga.
O campo é obrigatório na validação mas ignorado na gravação. Comportamento estranho,
mas provavelmente intencional (PUT não deveria trocar senha) — vale confirmar com a
equipe se é isso mesmo.

**O que conferir em C6**: rode um `GET /api/reunioes/colaboradores/1` antes e depois.
Só `monitorarReunioes` pode ter mudado; `nome` e `dataCadastro` intactos. É isso que
diferencia PATCH de PUT aqui.

### Bloco 2 — Reuniões, caminho felizes

Precisa do colaborador de id 1 criado (C1).

| # | Request | Corpo | Esperado | Resultado |
|---|---|---|---|---|
| R1 | `POST /` | reunião completa, `participantes: [1]`, `totalAcoes: 2` | 201, `participantes` volta como `["Ana Beatriz Fontes"]` | ✅ 201 |
| R2 | `POST /` | sem participantes, listas vazias | 201, `participantes: []` | ✅ 201 |
| R3 | `GET /?page=0&size=10` | — | 200, `totalElements: 2` | ✅ 200 |
| R4 | `GET /1` | — | 200 + a reunião do R1 | ✅ 200 |
| R5 | `PUT /1` | tudo alterado | 202 | ✅ 202 |
| R6 | `PATCH /1` | só `{"status": "ERRO"}` | 202, só o status muda | ✅ 202 |
| R7 | `PATCH /1` | `{}` corpo vazio | 202, **nada** muda | ✅ 202 |
| R8 | `DELETE /1` | — | 204; depois `GET /1` → 404 | ✅ 204 → 404 |

**O que conferir em R1**: a tradução de ids para nomes. Você mandou `[1]` e recebeu o
nome. É o `ReuniaoDTO.de()` fazendo o mapeamento.

**O que conferir em R5 vs R6**: no PUT, campo que você **omitir** vira `null` na
entidade — o serviço faz `set` de tudo, sem checar. No PATCH, campo omitido é ignorado.
Teste concreto: rode `PUT /1` sem o campo `areas` e depois `GET /1` — `areas` vem `[]`.
Isso é PUT funcionando como manda o figurino, mas é fácil perder dado sem perceber.

**O que conferir em R7**: `PATCH` com `{}` é o teste que prova que a semântica de
"campo nulo = não mexer" está implementada. Se algo mudar aqui, é bug.

### Bloco 3 — Erros e casos de borda

| # | Request | Esperado | Resultado |
|---|---|---|---|
| E1 | `POST /` com `titulo:"abc"`, `data:""`, `status:""`, `totalAcoes:-1` | 400 com **4 itens** em `campos` | ✅ 400, 4 campos |
| E2 | `POST /api/reunioes/colaboradores` com nome e senha `""` | 400 com 2 itens em `campos` | ✅ 400, 2 campos |
| E3 | `GET /999` | 404 `"Reunião não encontrada(o) para o id 999"` | ✅ 404 |
| E4 | `GET /api/reunioes/colaboradores/999` | 404 `"Colaborador não encontrada(o)..."` | ✅ 404 |
| E5 | `GET /abc` | 400 `"O parâmetro id recebeu um valor de tipo incompatível."` | ✅ 400 |
| E6 | `POST /` com `participantes: [999]` | 404 `"Colaborador não encontrado entre os participantes informados"` | ✅ 404 |
| E7 | `POST /` com `acoes: [99]` | 404 `"Ação não encontrada entre as ações informadas"` | ✅ 404 |
| E8 | `POST /` com JSON truncado | 400 `"O corpo enviado não pôde ser lido."` | ✅ 400 |
| E9 | `POST /` com `status: "BANANA"` | deveria dar 400 | ⚠️ **201 criado** — achado #3 |
| E10 | `DELETE /api/reunioes/colaboradores/1` com reunião vinculada | deveria dar 409 | ⚠️ **500** — achado #4 |
| E11 | `PUT /api/reunioes/colaboradores/999` | 404 | ✅ 404 |
| E12 | `GET /?page=50&size=10` | 200 com `content: []` | ✅ 200 |

**Sobre E1** — é o teste mais completo de validação. Confira que os quatro campos
aparecem: `titulo` (mínimo 5 caracteres), `data` (obrigatório), `status` (obrigatório),
`totalAcoes` (não pode ser negativo). E confira o **acento**: a mensagem tem que vir
`"O título deve ter ao menos 5 caracteres"` legível. Veio correta nos testes — se
aparecer `Ã­` na sua tela, é problema de encoding do cliente, não da API.

**Sobre E6 e E7** — é o cuidado bom do `ReuniaoService`: o `findAllById` do Spring Data
descarta id inexistente em silêncio, e o serviço compara a contagem para devolver 404
em vez de salvar uma reunião com participante faltando. Vale testar também
`participantes: [1, 1]` (id repetido) — o código usa `distinct()` na contagem, então
deve passar com 201.

**Sobre E8** — nenhum erro devolve stack trace. Confirme: a resposta de erro só tem
`detail`, `instance`, `status`, `title`, `type` e `timestamp`. Nada de `trace` ou
nome de classe Java. Isso é requisito de segurança e está atendido nos 5 handlers.

---

## Parte 5 — Bugs que os testes encontraram

Quatro coisas que apareceram rodando o roteiro. Nenhuma foi corrigida — só reportada.

### #1 — `ReuniaoController` sem `@RequestMapping` (o mais grave)

[ReuniaoController.java:21](../src/main/java/br/com/empresa/reunioes/web/controller/ReuniaoController.java:21)
não tem `@RequestMapping` na classe, enquanto o `ColaboradorController` tem
`@RequestMapping("/api/reunioes/colaboradores")`. Resultado confirmado pelo
`/v3/api-docs`:

```
/                                      -> GET, POST
/{id}                                  -> GET, PUT, DELETE, PATCH
/api/reunioes/colaboradores            -> GET, POST
/api/reunioes/colaboradores/{id}       -> GET, PUT, DELETE, PATCH
```

Três consequências reais:

1. As rotas de reunião estão fora do padrão do resto da API.
2. O `GET /{id}` **engole qualquer caminho de um nível**. É por isso que o console do
   H2 não abre: `/h2-console` cai no `/{id}`, tenta converter `"h2-console"` para
   `Long` e devolve 400. Qualquer rota nova de um segmento vai colidir também.
3. Quando o front for integrar, vai bater em `/api/reunioes` e tomar 404 (ou pior,
   `/api/reunioes/1` vai dar 404 enquanto `/1` funciona).

Correção: adicionar `@RequestMapping("/api/reunioes")` na classe e ajustar
`/api/reunioes/colaboradores` para ser coerente com isso.

### #2 — `ReuniaoDTO` não devolvia `id` — **CORRIGIDO**

O record não tinha o campo `id`. Depois de um `POST /`, o cliente não tinha como saber
o id da reunião criada — nem no corpo, nem em header `Location`. Na listagem, o mesmo:
uma lista de reuniões sem id nenhum, impossível montar o link para o detalhe. Isso
travaria a integração do front, cujo `ReuniaoDetalhe` tem `id: number`.

Corrigido: `id` adicionado como primeiro campo do record e do `ReuniaoDTO.de()`.
Agora o POST, o GET por id e a listagem devolvem o id. O fluxo da Parte 6 depende
disso — sem o id na resposta, não dá para injetar participante sem adivinhar.

O header `Location` no POST continua ausente. Não é bloqueante agora que o corpo
carrega o id, mas seria o mais correto para um 201.

### #3 — `status` não é validado contra o enum

O enum `StatusReuniao` existe com `RECEBIDA`, `TRANSCREVENDO`, `ANALISANDO`, `CONCLUIDA`
e `ERRO`, mas `Reuniao.status` é `String` e o `ReuniaoRequest.status` também. A única
validação é `@NotBlank`. Teste E9 comprova: `{"status": "BANANA"}` foi **criado com 201**.

O comentário no `RestExceptionHandler` fala de "enum fora dos valores aceitos" no
handler de `HttpMessageNotReadableException` — mas esse caminho nunca é acionado,
porque o campo não é enum.

Correção: trocar o tipo para `StatusReuniao` no request e na entidade (com
`@Enumerated(EnumType.STRING)`), ou validar com `@Pattern`.

### #4 — Excluir colaborador vinculado a reunião devolve 500

Teste E10: com o colaborador 1 participando de uma reunião, `DELETE
/api/reunioes/colaboradores/1` devolve **500 "Erro interno"**. O `ColaboradorService.deletar`
chama `delete` direto, sem limpar o vínculo `reuniao_participantes`, e a violação de
chave estrangeira estoura na rede de segurança `@ExceptionHandler(Exception.class)`.

Do ponto de vista do cliente é indistinguível de a API ter quebrado. O certo é 409
Conflict com mensagem explicando, ou remover o colaborador das reuniões antes de excluir.

**Cuidado nos testes**: por causa disso, sempre exclua colaborador que **não** participa
de nenhuma reunião (foi por isso que o C7 usa o id 2, não o 1).

---

## Parte 6 — Fluxo: injetar colaborador na reunião

Este é o único teste que cobre o relacionamento ManyToMany entre `Reuniao` e
`Colaborador`, e o único que exercita o ramo dos participantes no
`ReuniaoService.atualizarParcial`. Os blocos anteriores não tocam nisso.

A ideia é criar a reunião **vazia** e injetar o participante depois, em vez de já
criar com `participantes: [1]`. Dá mais trabalho, mas testa o `salvar` e o
`atualizarParcial` no mesmo roteiro, em vez de só o `salvar`.

Rode na ordem, com a API recém-iniciada. Está na pasta `04 - Vinculo colaborador x
reuniao` da coleção.

| # | Request | Esperado | Resultado |
|---|---|---|---|
| V1 | `POST /api/reunioes/colaboradores` | 201, **anote o `id`** da resposta | ✅ 201, `id: 1` |
| V2 | `POST /` reunião vazia, `totalAcoes: 0` | 201, `participantes: []`, **anote o `id`** | ✅ 201, `id: 1` |
| V3 | `PATCH /{idReuniao}` com `{"participantes": [1]}` | 202, `participantes` volta com o **nome** | ✅ 202 |
| V4 | `GET /{idReuniao}` | 200, vínculo persistido | ✅ 200 |
| V5 | `PATCH /{idReuniao}` com `{"participantes": [999]}` | 404 | ✅ 404 |
| V6 | `GET /{idReuniao}` | 200, vínculo do V3 **intacto** | ✅ 200 |
| V7 | `PATCH /{idReuniao}` com `{"participantes": []}` | 202, desvincula | ✅ 202 |

### O que cada passo prova

**V1 e V2** — os dois ids saem da resposta, sem adivinhação. Antes da correção do
achado #2 o V2 era impossível: a reunião era criada e você não sabia o id dela.

**V3** — o coração do teste. Você manda `[1]` (id) e recebe
`["Ana Beatriz Fontes"]` (nome). Resposta real:

```json
{
  "id": 1,
  "titulo": "Alinhamento semanal de Operacoes",
  "data": "2026-08-18T09:00:00",
  "status": "RECEBIDA",
  "participantes": ["Ana Beatriz Fontes"],
  "areas": ["Operacoes"],
  "totalAcoes": 0
}
```

Repare que `totalAcoes` continua `0`. O `atualizarParcial` só recalcula o total quando
o campo `acoes` vem no corpo — mexer em participante não mexe no total, o que está certo.

**V4** — separa "o service respondeu bonito" de "gravou no banco". O V3 mostra o
retorno do `save`; o V4 é uma leitura nova, com o Hibernate indo na tabela
`reuniao_participantes`. Se o V3 desse certo e o V4 viesse vazio, o vínculo não teria
persistido.

**V5** — o caso negativo que não pode faltar. O `findAllById` do Spring Data **descarta
id inexistente em silêncio**: sem o cuidado do service, `[999]` viraria uma lista vazia e
a API responderia 202 tendo apagado os participantes. O `buscarColaboradores` compara a
contagem e devolve 404. Mensagem exata:

```
Colaborador não encontrado entre os participantes informados
```

**V6** — o complemento do V5, e o passo que mais gente esquece. Não basta o 404: é
preciso confirmar que a operação rejeitada **não deixou rastro**. O vínculo do V3 tem
que continuar lá. Se o `participantes` voltar vazio aqui, o 404 aconteceu depois de já
ter sujado o estado.

**V7** — lista vazia desvincula de propósito. É diferente de omitir o campo: `[]` limpa,
ausente não mexe. Confirme com um `GET` depois.

### Variação que também vale

Injetar dois colaboradores de uma vez (`{"participantes": [1, 2]}`) e testar id repetido
(`[1, 1]`) — o `buscarColaboradores` usa `distinct()` na contagem, então o repetido deve
passar com 202, não dar 404.

---

## Parte 7 — Cola rápida

```
# subir (PowerShell, uma vez por janela)
$env:JAVA_HOME='C:\Users\guilh\.jdks\openjdk-25.0.2'
cd Ata-de-Reunioes/back-reunioes
./mvnw.cmd spring-boot:run

# se a 8080 estiver ocupada pelo Apache
./mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

| O que | Onde |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Reuniões | `/` e `/{id}` |
| Colaboradores | `/api/reunioes/colaboradores` e `/{id}` |
| Ações | não existe endpoint |
| Console H2 | habilitado no yml, mas não abre (achado #1) |

Status de sucesso, para não estranhar: **POST → 201**, **PUT e PATCH → 202**,
**DELETE → 204**, **GET → 200**.
