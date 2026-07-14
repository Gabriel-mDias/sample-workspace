import { inject } from '@angular/core';
import { CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../auth/auth.service';

/**
 * Guard de autorização por role. Lê as roles exigidas de `route.data.roles`.
 *
 * Substitui o `gemsRoleGuard` do SDK porque precisamos **controlar o destino da negação**:
 *   - não logado          ⇒ login no Keycloak, volta à URL pedida;
 *   - logado sem a role    ⇒ redireciona para `/acesso-negado` (página dedicada);
 *   - logado com a role    ⇒ libera.
 */
export const roleGuard: CanActivateFn = (route, state: RouterStateSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    auth.login(window.location.origin + state.url);
    return false;
  }

  const required = (route.data?.['roles'] as string[] | undefined) ?? [];
  if (required.length === 0 || auth.hasAnyRole(required)) {
    return true;
  }

  return router.createUrlTree(['/acesso-negado']);
};
