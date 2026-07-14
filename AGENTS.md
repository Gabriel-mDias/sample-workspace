# AGENTS.md — MyApp

> Managed by GEMS-AI-SDK 1.0.0 · Stack: all
> Run `npx @gabriel-mdias/gems-ai-sdk sync` to update the block below.

<!-- GEMS-AI:BEGIN -->
## Convenções Técnicas (GEMS SDK)

Este projeto segue o **GEMS-AI-SDK** — regras e receitas para código uniforme entre modelos de IA.

<!-- BEGIN:java -->
### Backend Java (Spring Boot)
| Camada | Regra |
| :--- | :--- |
| Arquitetura | `.gems-ai/rules/backend-java/architecture.md` |
| Controllers | `.gems-ai/rules/backend-java/controllers.md` |
| Services | `.gems-ai/rules/backend-java/services.md` |
| Repositories | `.gems-ai/rules/backend-java/repositories.md` |
| Persistência | `.gems-ai/rules/backend-java/persistence-db.md` |
| Utils | `.gems-ai/rules/backend-java/utils.md` |
| Mapping | `.gems-ai/rules/backend-java/mapping.md` |
| REST | `.gems-ai/rules/backend-java/rest-common.md` |
| Validação | `.gems-ai/rules/backend-java/validation.md` |
| S3 | `.gems-ai/rules/backend-java/storage-s3.md` |
| Events | `.gems-ai/rules/backend-java/events.md` |
| Multi-tenant | `.gems-ai/rules/backend-java/multi-tenant.md` |
| OpenAPI | `.gems-ai/rules/backend-java/openapi.md` |
| Observability | `.gems-ai/rules/backend-java/observability.md` |
| Security | `.gems-ai/rules/backend-java/security.md` |
| Testing | `.gems-ai/rules/backend-java/testing.md` |
<!-- END:java -->

<!-- BEGIN:angular -->
### Frontend Angular (Angular 20+)
| Camada | Regra |
| :--- | :--- |
| Organização | `.gems-ai/rules/frontend-angular/organization.md` |
| Signals | `.gems-ai/rules/frontend-angular/signals-standards.md` |
| Stores | `.gems-ai/rules/frontend-angular/stores.md` |
| Forms | `.gems-ai/rules/frontend-angular/forms.md` |
| Lists | `.gems-ai/rules/frontend-angular/lists.md` |
| Stepper | `.gems-ai/rules/frontend-angular/stepper.md` |
| Componentes | `.gems-ai/rules/frontend-angular/gems-components.md` |
| Visual | `.gems-ai/rules/frontend-angular/visual-standards.md` |
| Alertas | `.gems-ai/rules/frontend-angular/feedback-alerts.md` |
| Interceptors | `.gems-ai/rules/frontend-angular/http-interceptors.md` |
| Auth | `.gems-ai/rules/frontend-angular/auth.md` |
| Testing | `.gems-ai/rules/frontend-angular/testing.md` |
<!-- END:angular -->

### Receitas Ponta-a-Ponta
- CRUD completo: `.gems-ai/recipes/crud-end-to-end.md`
- Search paginado: `.gems-ai/recipes/search-paginated-end-to-end.md`
- Wizard/Stepper: `.gems-ai/recipes/wizard-stepper-end-to-end.md`
- Upload S3: `.gems-ai/recipes/file-upload-s3-end-to-end.md`
- Domain Events: `.gems-ai/recipes/domain-event-end-to-end.md`
- Erros/Validação: `.gems-ai/recipes/error-and-validation-end-to-end.md`
- Correlation ID: `.gems-ai/recipes/correlation-id-end-to-end.md`
- Recurso seguro: `.gems-ai/recipes/secured-resource-end-to-end.md`
- Setup Backend: `.gems-ai/recipes/setup-backend-sdk.md`
- Setup Frontend: `.gems-ai/recipes/setup-frontend-sdk.md`
- Input CVA: `.gems-ai/recipes/custom-input-cva-end-to-end.md`

### Templates
- Backend: `.gems-ai/templates/backend/`
- Frontend: `.gems-ai/templates/frontend/`

### Relação com spec-kit
O spec-kit (`specify` CLI) define **o quê** implementar. Este SDK define **como** implementar.
Se o projeto usa spec-kit, o `constitution.md` deve referenciar este `AGENTS.md`.
<!-- GEMS-AI:END -->

---

## Notas deste Projeto

> Edite livremente esta seção — o `sync` preserva apenas o bloco acima.

### Template Single-Tenant

Este projeto é um **starter template single-tenant**: aplicação única, schema PostgreSQL único (`public`), sem resolução dinâmica de tenant. Autenticação via Keycloak com roles `ADMIN` (full CRUD) e `USER` (sem exclusão). **Não há suporte a múltiplas organizações** — JWT não inclui `organization` ou `tenant_id`.

### Backend (`br.com.gems.sample_project`)

**Spring Modulith** com domínios:
- **`produto`** — CRUD de referência (entity, service, repository, controller, DTO, validação, testes)
- **`notificacao`** — Listener de eventos (ex.: `ProdutoExcluidoEvent`)
- **`security`** — Resource server Keycloak (JWT validation, `@PreAuthorize`)

Domínios são privados. Cross-domain: via eventos + listeners (nenhuma chamada direta). Test-first no core.

### Frontend (Angular 22 + GEMS SDK)

- **`core/auth`** — `AuthService`, roles const, guards (`authGuard`, `roleGuard`)
- **`core/menu`** — Menu dinâmico (função pura)
- **`features/produtos`** — CRUD Produto (list, form, view, store com signals)
- **Casca** — AppComponent + GemsSideMenuComponent (GEMS SDK)

Standalone-first. Sem inline templates/styles. Stores via `GemsBaseStore` + signals. Nenhum CSS customizado desnecessário — usar GEMS SDK e CSS Variables.

### Estender com Novo CRUD

1. Leia `.gems-ai/recipes/crud-end-to-end.md` e `search-paginated-end-to-end.md`
2. Use templates em `.gems-ai/templates/{backend,frontend}/`
3. Backend: test-first, validação agregada em `List<String>`, eventos para cross-domain
4. Frontend: feature folder, componentes standalone, signals para estado
