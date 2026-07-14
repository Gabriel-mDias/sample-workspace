package br.com.gems.sample_project.security;

/**
 * Taxonomia única de roles do Sample (single-tenant).
 *
 * <p>Nomes exatamente como cadastrados no realm do Keycloak (case-sensitive). O
 * {@code SecurityConfig} prefixa cada realm role com {@code ROLE_} e a converte para
 * MAIÚSCULAS ao montar as authorities do Spring Security. Portanto:
 * <ul>
 *   <li>{@code hasRole('X')} → authority {@code ROLE_X};</li>
 *   <li>as constantes {@code AUTHORITY_*} carregam o prefixo {@code ROLE_} já aplicado,
 *       para uso direto em {@code @PreAuthorize("hasAuthority(...)")}.</li>
 * </ul>
 */
public final class SecurityRoles {

    private SecurityRoles() {
    }

    /** Administrador da aplicação: acesso completo. */
    public static final String ADMIN = "ADMIN";

    /** Usuário padrão da aplicação. */
    public static final String USER = "USER";

    // Authorities já com o prefixo ROLE_ (como o SecurityConfig as expõe).
    public static final String AUTHORITY_ADMIN = "ROLE_" + ADMIN;
    public static final String AUTHORITY_USER = "ROLE_" + USER;
}
