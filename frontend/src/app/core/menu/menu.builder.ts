import { GemsSideMenuItem } from '@gabriel-mdias/angular-gems-sdk';
import { Roles } from '../auth/roles';
import { MenuAuthContext } from '../auth/auth.service';

/**
 * Monta os itens do side-menu a partir das roles do token.
 *
 * Aplicação single-tenant: não há grupos por organização — todo usuário autenticado vê o
 * Dashboard, e ADMIN/USER também veem Produtos.
 *
 * Função pura (sem injeção): recebe um {@link MenuAuthContext} para ser testável sem Keycloak.
 */
export function buildMenu(auth: MenuAuthContext): GemsSideMenuItem[] {
  const items: GemsSideMenuItem[] = [
    { label: 'Dashboard', icon: 'fa-solid fa-house', route: '/dashboard' }
  ];

  if (auth.hasAnyRole([Roles.ADMIN, Roles.USER])) {
    items.push({ label: 'Produtos', icon: 'fa-solid fa-box', route: '/produtos' });
  }

  return items;
}
