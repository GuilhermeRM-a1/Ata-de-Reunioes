# repository4Semestre

## Documentação da API (Swagger)

Com o back rodando, a interface fica em:

- Swagger UI: http://localhost:8080/swagger-ui.html
- Especificação OpenAPI: http://localhost:8080/v3/api-docs

Para subir o back:

```bash
cd back-reunioes && ./mvnw spring-boot:run
```

O banco é H2 em memória — sobe vazio a cada execução, sem precisar de
Postgres. O console do H2 fica em http://localhost:8080/h2-console
(JDBC URL `jdbc:h2:mem:reunioes`, usuário `sa`, senha em branco).

Se a porta 8080 estiver ocupada, suba em outra:

```bash
cd back-reunioes && ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8099
```
## Rodando o front

```bash
cd reunioes-front && npm install
```

```bash
cd reunioes-front && npm start
```

Abre em `http://localhost:4200`. O front ainda consome mock
(`src/app/core/mock/`), não a API.

## Requisito de Java

O back exige **Java 17 ou superior** (Spring Boot 4.1.0). Se o `java -version`
do seu terminal mostrar 1.8, aponte o `JAVA_HOME` para um JDK compatível antes
de rodar o Maven — o build falha no carregamento dos plugins, com um erro que
não deixa claro que a causa é a versão do Java.

## Endpoints

**Colaboradores** — `/api/reunioes/colaboradores`

| Método | Rota | Sucesso |
|---|---|---|
| POST | `/api/reunioes/colaboradores` | 201 |
| GET | `/api/reunioes/colaboradores` | 200 |
| GET | `/api/reunioes/colaboradores/{id}` | 200 |
| PUT | `/api/reunioes/colaboradores/{id}` | 202 |
| PATCH | `/api/reunioes/colaboradores/{id}` | 202 |
| DELETE | `/api/reunioes/colaboradores/{id}` | 204 |

**Reuniões** — respondem na **raiz** do servidor

| Método | Rota | Sucesso |
|---|---|---|
| POST | `/` | 201 |
| GET | `/` | 200 |
| GET | `/{id}` | 200 |
| PUT | `/{id}` | 202 |
| PATCH | `/{id}` | 202 |
| DELETE | `/{id}` | 204 |

> A reunião de id 1 é `/1`, não `/api/reunioes/1`. As rotas estão na raiz porque
> o `ReuniaoController` está sem `@RequestMapping` na classe — bug conhecido.

Listagens devolvem um envelope com `content`, `page`, `size`, `totalElements` e
`totalPages`, e aceitam `page`, `size` e `sort` como query params. Erros seguem
o formato ProblemDetail (RFC 7807), sem stack trace.

Não há autenticação: nenhum endpoint exige token.

## Testando a API

Manual com roteiro passo a passo e coleção do Insomnia pronta para importar:

- `back-reunioes/docs/MANUAL-TESTES-API.md`
- `back-reunioes/docs/insomnia-reunioes.json`

No Insomnia: **Create → Import → From File**. Ajuste a variável `base_url` do
ambiente se estiver usando outra porta.

## Limitações conhecidas

- **Não existe endpoint de ações.** Há `AcaoService`, `AcaoRepository` e os DTOs,
  mas nenhum controller — o campo `acoes` da reunião não aceita id válido.
- **`status` da reunião não é validado** contra o enum `StatusReuniao`; o campo é
  String e aceita qualquer valor.
- **`DELETE` de colaborador vinculado a reunião devolve 500** (violação de FK);
  deveria ser 409.
- **`data` e `dataCadastro` são String livre**, sem validação de formato.

## Contribuindo

Branches saem da `develop` e voltam por Pull Request — nada direto na `develop`
ou na `main`.

```bash
git checkout develop && git pull origin develop
git checkout -b feature/nome-da-feature
```

Commits seguem Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`).
