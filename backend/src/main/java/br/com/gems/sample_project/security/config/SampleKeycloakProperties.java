package br.com.gems.sample_project.security.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Propriedades de integração com o Keycloak (Admin API + Resource Server).
 * Namespace: {@code sample.security.keycloak.*}. Fail-fast no startup via {@link Validated}.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "sample.security.keycloak")
public class SampleKeycloakProperties {

    /** URL base do servidor Keycloak (ex.: http://localhost:8443). */
    @NotBlank
    private String serverUrl;

    /** Realm da aplicação (ex.: sample-realm). */
    @NotBlank
    private String realm;

    /** Client confidencial usado para as operações administrativas (ex.: sample-backend). */
    @NotBlank
    private String clientId;

    /** Secret do client confidencial. Em produção deve vir de variável de ambiente/secret manager. */
    @NotBlank
    private String clientSecret;
}
