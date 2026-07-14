import { describe, it, expect } from 'vitest';
import { buildMenu } from './menu.builder';
import { MenuAuthContext } from '../auth/auth.service';
import { Roles } from '../auth/roles';

/**
 * Stub de {@link MenuAuthContext}: fixa o conjunto de roles, sem tocar no Keycloak.
 */
function auth(roles: string[]): MenuAuthContext {
  return {
    hasRole: (r) => roles.includes(r),
    hasAnyRole: (rs) => rs.some((r) => roles.includes(r))
  };
}

describe('buildMenu', () => {
  it('ADMIN → vê Dashboard e Produtos', () => {
    const menu = buildMenu(auth([Roles.ADMIN]));

    expect(menu.map((i) => i.route)).toEqual(['/dashboard', '/produtos']);
  });

  it('USER → vê Dashboard e Produtos', () => {
    const menu = buildMenu(auth([Roles.USER]));

    expect(menu.map((i) => i.route)).toEqual(['/dashboard', '/produtos']);
  });

  it('sem ADMIN nem USER → vê apenas Dashboard', () => {
    const menu = buildMenu(auth([]));

    expect(menu.map((i) => i.route)).toEqual(['/dashboard']);
  });
});
