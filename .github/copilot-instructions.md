# GitHub Copilot Instructions — MyApp

> Pointer para AGENTS.md (SSOT de convenções técnicas).
> Managed by GEMS-AI-SDK 1.0.0.

## Contexto do Projeto

Este projeto segue o **GEMS SDK** — Java-GEMS-SDK (backend Spring Boot) e Angular-GEMS-SDK (frontend Angular 20+).

Leia `AGENTS.md` e os arquivos referenciados em `.gems-ai/` antes de sugerir código.

## Regras Críticas

**Backend:**
- Pacote: `br.com.gems.myapp`
- Controller extende `BaseController`, chama `setBodyToExceptionLog(dto)` em POST/PUT
- Service: ordem `findById > search > delete > insert > save > validate`
- Repository: trinca `search > countQuery > searchQuery > appendFilters` com `WHERE 1=1`
- `ObjectUtil.isNullOrEmpty()` — nunca `!= null` ou `.isEmpty()`
- `BusinessException(List<String>)` para validação acumulada

**Frontend:**
- `standalone: true`, `inject()`, `signal()`, `input()`, `@if`/`@for`
- `GemsBaseStore` com `super('api/{recurso}')` sem barra inicial
- `alertService.errorFromApi(err)` em todo bloco `error:` de chamadas HTTP
- `var(--gems-*)` CSS — nunca hex hardcoded

Consulte `.gems-ai/recipes/` para implementações completas ponta-a-ponta.
