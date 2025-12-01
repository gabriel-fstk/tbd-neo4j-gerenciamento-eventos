# Sistema de Gerenciamento de Eventos

Este projeto é um exemplo completo de integração entre Java (Spring Boot), MongoDB e Neo4j, com API REST para gerenciamento de eventos e participantes.

## Funcionalidades

- Cadastro de eventos e pessoas (MongoDB)
- Registro de participação e organização de eventos (Neo4j)
- Consulta de rede de participação/organização
- Filtros dinâmicos de eventos (por local, data, palavra-chave)
- Exportação de dados em JSON e SQL
- Testes de API via arquivo `.http`

## Arquitetura

- **Java (Spring Boot):** API REST, lógica de negócio, integração com bancos
- **MongoDB:** Armazena documentos de eventos e pessoas
- **Neo4j:** Armazena relações entre pessoas e eventos (participação, organização)

## Endpoints Principais

- `POST /api/eventos` — Criar evento
- `POST /api/eventos/pessoas` — Criar pessoa
- `POST /api/eventos/{id}/participacao` — Vincular pessoa a evento (participante/organizador)
- `PUT /api/eventos/{id}/promover?email=...` — Promover participante para organizador
- `GET /api/eventos/{id}/rede` — Consultar rede de participação/organização
- `GET /api/eventos/buscar?criterio=...&valor=...` — Filtros dinâmicos (MongoDB)
- `GET /api/eventos/exportar/json` — Exportar eventos em JSON
- `GET /api/eventos/exportar/sql` — Exportar eventos em SQL

## Como rodar

1. Inicie o MongoDB e o Neo4j localmente (usuário/senha conforme `application.properties`)
2. Execute o projeto:
   ```
   mvn spring-boot:run
   ```
3. Use o arquivo `requests-test.http` para testar todos os fluxos da API

## Testando a API

Abra o arquivo `requests-test.http` no VS Code e execute as requisições para:
- Criar eventos e pessoas
- Vincular participações/organizações
- Consultar rede
- Buscar eventos
- Exportar dados

## Consultando dados no Neo4j

No Neo4j Browser, execute:
- `MATCH (e:Evento) RETURN e;` — Ver eventos
- `MATCH (p:Pessoa) RETURN p;` — Ver pessoas
- `MATCH (p:Pessoa)-[r]->(e:Evento) RETURN p, r, e;` — Ver vínculos

## Observações
- O sistema pode ser expandido facilmente para outros domínios.
- Para usar outro banco no Neo4j, ajuste o código para especificar o banco desejado.
- O projeto segue boas práticas de separação de camadas e uso de DTOs.

---

