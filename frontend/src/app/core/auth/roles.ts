/**
 * Taxonomia de roles do Sample — espelha `SecurityRoles.java` do backend.
 * Os nomes DEVEM ser idênticos aos realm roles do Keycloak (case-sensitive).
 */
export const Roles = {
  /** Administrador da aplicação: acesso completo. */
  ADMIN: 'ADMIN',
  /** Usuário padrão da aplicação. */
  USER: 'USER'
} as const;

export type Role = (typeof Roles)[keyof typeof Roles];
