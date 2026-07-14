# Sample Project — GEMS SDK Single-Tenant Template

Starter template **single-tenant** em Java Spring Boot 4.1, Angular 22 e Keycloak, com persistência PostgreSQL em schema único (`public`). Inclui referência completa de CRUD (`Produto`) com busca paginada, validação de negócio e testes.

## Stack

- **Backend**: Java 21 + Spring Boot 4.1 (Maven multi-módulo, Spring Modulith)
- **Frontend**: Angular 22 (standalone)
- **Identidade**: Keycloak 26 (PKCE + session check-sso)
- **Persistência**: PostgreSQL 16 (schema único `public`, Liquibase)
- **Infraestrutura local**: Docker Compose + LocalStack (S3 mock)
- **Dependências privadas**: `br.com.gems:gems-*` (Maven) e `@gabriel-mdias/angular-gems-sdk` (npm) — requer acesso ao registry privado

## Como Rodar

### 1. Infraestrutura (Postgres + Keycloak + LocalStack)

```bash
cd environments/local
docker compose -f docker-compose.yml up -d
```

Aguarde que Keycloak e Postgres estejam prontos (15-20s). Acesse Keycloak em `http://localhost:8443` com `admin:admin@gems.env`.

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

A API fica em `http://localhost:8080`. O schema é validado/criado automaticamente via Liquibase. Requisições precisam de JWT válido emitido pelo Keycloak (Bearer token).

### 3. Frontend

```bash
cd frontend
npm install
npm start
```

SPA fica em `http://localhost:4200`. Login com `admin.sample:admin@gems.env`.

## Estrutura

### Backend (`backend/`)

**Módulo único**: `br.com.gems.sample_project` com Spring Modulith:
- **`produto`** — CRUD de referência (controller, service, repository, entity, DTO, validação, tests)
- **`notificacao`** — Listener de eventos de domínio (exemplo: `ProdutoExcluidoEvent`)
- **`security`** — Keycloak resource server (JWT validation, autorização por role)

Todos os módulos em `public` API (`api` package) ou `private` (sem `api`). Domínios não se chamam diretamente; usam eventos.

### Frontend (`frontend/`)

- **`core/auth`** — `AuthService` (fachada Keycloak), roles const, `TenantFilter` (não usado em single-tenant)
- **`core/guards`** — `authGuard`, `roleGuard`
- **`core/menu`** — Menu dinâmico por role
- **`features/produtos`** — CRUD de Produto (list, form, view) + store (signals)
- **Casca autenticada** — `AppComponent` com `GemsSideMenuComponent` (GEMS SDK)

## Como Estender

### Adicionar um novo CRUD

1. **Leia as recipes** (`.gems-ai/recipes/`):
   - `crud-end-to-end.md` (fluxo completo backend + frontend)
   - `search-paginated-end-to-end.md` (listagem com filtro + paginação)
   - `error-and-validation-end-to-end.md` (validações + erros de negócio)

2. **Use templates** (`.gems-ai/templates/`):
   - Backend: entity, service, controller, dto, tests
   - Frontend: component.ts, service (store), component.html

3. **Respeite as fronteiras**:
   - Backend: cada domínio em seu package; listeners para cross-domain; ports para privacidade
   - Frontend: feature folder organization; componentes standalone; sinal de estado

4. **Test-first** (backend): escreva teste antes da lógica de negócio

## Configuração de Acesso ao Registry Privado

Se as dependências GEMS não instalarem, configure credenciais privadas:

**Maven** (`backend/.m2/settings.xml`):
```xml
<server>
  <id>gems-registry</id>
  <username>seu_usuario</username>
  <password>seu_token</password>
</server>
```

**NPM** (`frontend/.npmrc`):
```
@gabriel-mdias:registry=https://seu-registry.com
//seu-registry.com/:_authToken=seu_token
```

## Documentação

- `CLAUDE.md` — instruções para Claude Code (convenções, regras)
- `AGENTS.md` — técnicas GEMS SDK (backends, frontends, recipes, templates)
- `.gems-ai/rules/` — regras por camada (controllers, services, signals, forms, etc.)
- `backend/src/main/resources/db/changelog/` — migrações Liquibase
