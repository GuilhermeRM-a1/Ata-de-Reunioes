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
