import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { Roles } from './core/auth/roles';

/**
 * Rotas de topo (a casca/side-menu vive no AppComponent, por fora).
 *
 * - `dashboard` : qualquer usuário autenticado (home de boas-vindas).
 * - `produtos`  : apenas `ADMIN`/`USER` (CRUD de produtos — componentes na próxima wave).
 * - `acesso-negado` : sem guard (destino da negação de role; casca oculta nesta rota).
 *
 * Guards: `authGuard` (só sessão) e `roleGuard` (sessão + `data.roles`; negação → `/acesso-negado`).
 */
export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then((c) => c.DashboardComponent)
  },
  {
    path: 'produtos',
    canActivate: [roleGuard],
    data: { roles: [Roles.ADMIN, Roles.USER] },
    loadComponent: () =>
      import('./features/produtos/list/produto-list.component').then((c) => c.ProdutoListComponent)
  },
  {
    path: 'acesso-negado',
    loadComponent: () =>
      import('./features/acesso-negado/acesso-negado.component').then(
        (c) => c.AcessoNegadoComponent
      )
  },
  { path: '**', redirectTo: 'dashboard' }
];
