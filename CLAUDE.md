# CLAUDE.md — MyApp

> Pointer para AGENTS.md (SSOT de convenções técnicas).
> Managed by GEMS-AI-SDK 1.0.0.

@AGENTS.md

---

## Regras de Desenvolvimento

Siga as convenções técnicas definidas em `AGENTS.md` e nos arquivos em `.gems-ai/rules/` e `.gems-ai/recipes/`.

Antes de implementar qualquer funcionalidade:
1. Leia a rule relevante em `.gems-ai/rules/`.
2. Se houver uma recipe ponta-a-ponta disponível, siga-a.
3. Use os templates em `.gems-ai/templates/` como ponto de partida.

## Notas deste Projeto (Template Single-Tenant)

### Modelo Single-Tenant

Este é um **template single-tenant**: uma única aplicação, um único schema PostgreSQL (`public`), sem necessidade de resolução de tenant em runtime. Autenticação e autorização via **Keycloak** com roles `ADMIN` (full CRUD) e `USER` (sem exclusão). **Não há suporte a multi-organizações ou tenant switching** — o JWT não inclui claims de `organization`/`tenant_id`.

### Estrutura Backend

**Módulo único** Maven: `br.com.gems.sample_project` com Spring Modulith:
- **`produto`** — CRUD de referência com validação e testes
- **`notificacao`** — Listener de eventos de domínio (ex.: `ProdutoExcluidoEvent`)
- **`security`** — Resource server Keycloak (JWT, validação por role)

Cada domínio é privado (sem `api` package). Cross-domain: via eventos + listeners. Repositories, services, controllers em sua própria pasta; DTOs separadas; validação agregada em `List<String>`.

### Estrutura Frontend

- **`core/auth`** — `AuthService` (fachada Keycloak), constantes de role, `AuthGuard` / `RoleGuard`
- **`core/menu`** — Menu dinâmico (função pura `buildMenu(auth)`)
- **`features/produtos`** — CRUD Produto (list, form, view) + store com signals
- **Casca autenticada** — AppComponent → GemsSideMenuComponent (GEMS SDK) + router-outlet

Standalone-first. Sem inline templates/styles (sempre .ts/.html/.css separados). Stores estendem `GemsBaseStore`.

### Como Adicionar um Novo CRUD

1. **Leia a recipe** `.gems-ai/recipes/crud-end-to-end.md` (backend + frontend ponta-a-ponta)
2. **Leia a recipe** `.gems-ai/recipes/search-paginated-end-to-end.md` (listagem com filtro)
3. **Use templates** em `.gems-ai/templates/backend/` e `.gems-ai/templates/frontend/`
4. **Respeite**:
   - Backend: test-first (escreva teste antes da lógica)
   - Frontend: feature folder organization, componentes standalone, signals para estado
   - Ambos: nenhuma customização CSS desnecessária (usar GEMS SDK e CSS Variables `--gems-*`)
