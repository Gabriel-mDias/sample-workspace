package br.com.gems.sample_project.security.config;

import br.com.gems.sample_project.security.annotation.PublicEndpoint;
import br.com.gems.sample_project.security.dto.PublicRoute;
import br.com.gems.utils.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationContext applicationContext;
    private final SampleKeycloakProperties keycloakProperties;

    /**
     * Origens liberadas para requisições cross-origin (SPA Angular). Parametrizável por ambiente —
     * em produção deve conter apenas o domínio do frontend; nunca use {@code *} com credenciais.
     */
    @Value("${sample.security.cors.allowed-origins:http://localhost:4200}")
    private List<String> corsAllowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        // Habilita CORS na cadeia do Security (senão o preflight é barrado antes dos headers CORS
        // serem escritos). Usa o CorsConfigurationSource declarado abaixo.
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.authorizeHttpRequests(auth -> {
            //Actuator and Preflight endpoints
            auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
            auth.requestMatchers("/actuator/**").permitAll();

            getPublicEndpoints().forEach(route -> {
                if ( ObjectUtil.isNotNullAndNotEmpty( route.method() ) ) {
                    auth.requestMatchers(route.method(), route.pattern()).permitAll();
                    log.warn("Endpoint público identificado: [{}] {}. Ele não exigirá autenticação.", route.method(), route.pattern());
                } else {
                    auth.requestMatchers(route.pattern()).permitAll();
                    log.warn("Endpoint público identificado: [ALL] {}. Ele não exigirá autenticação.", route.pattern());
                }
            });

            auth.anyRequest().authenticated();
        });

        http.sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new org.springframework.security.web.authentication.HttpStatusEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED))
        );

        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        return http.build();
    }

    /**
     * Configuração de CORS para a SPA. Expõe o header de correlação para o cliente e permite os
     * cabeçalhos/métodos usados pelo frontend (incluindo o preflight OPTIONS). As origens vêm de
     * {@code sample.security.cors.allowed-origins} (default: localhost:4200 para dev).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(corsAllowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Correlation-Id"));
        config.setExposedHeaders(List.of("X-Correlation-Id"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Método que busca qualquer endpoint anotado com o @PublicEndpoint e retorna em uma lista.
     * Desta forma, ele será exposto como público em nossa API.
     * @return
     */
    private List<PublicRoute> getPublicEndpoints() {
        var publicEndpoints = new ArrayList<PublicRoute>();

        // Lista de endpoints públicos *default* (neste caso se aplicam a todos os verbos)
        List.of(
                "/api/auth/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
        ).forEach(pattern -> publicEndpoints.add(new PublicRoute(null, pattern)));

        try {
            RequestMappingHandlerMapping requestMappingHandlerMapping =
                    applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

            Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                HandlerMethod handlerMethod = entry.getValue();
                RequestMappingInfo mappingInfo = entry.getKey();

                boolean isPublic = handlerMethod.getMethod().isAnnotationPresent(PublicEndpoint.class) ||
                                   handlerMethod.getBeanType().isAnnotationPresent(PublicEndpoint.class);

                if (isPublic && ObjectUtil.isNotNullAndNotEmpty( mappingInfo.getPatternValues() ) ) {
                    var methods = mappingInfo.getMethodsCondition().getMethods();
                    for (String pattern : mappingInfo.getPatternValues()) {
                        if (methods.isEmpty()) {
                            publicEndpoints.add(new PublicRoute(null, pattern));
                        } else {
                            for (RequestMethod requestMethod : methods) {
                                publicEndpoints.add(new PublicRoute(HttpMethod.valueOf(requestMethod.name()), pattern));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore. O contexto Web MVC pode não estar disponível imediatamente
        }

        return publicEndpoints;
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            var authorities = extractAuthorities(jwt);
            return new JwtAuthenticationToken(jwt, authorities);
        };
    }

    @SuppressWarnings("unchecked")
    private List<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        var authorities = new ArrayList<SimpleGrantedAuthority>();

        // 1. Realm Roles
        if (jwt.hasClaim("realm_access")) {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                var roles = (List<String>) realmAccess.get("roles");
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
            }
        }

        // 2. Resource Roles (Client Roles dinâmicos do Keycloak)
        if (jwt.hasClaim("resource_access")) {
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            String clientId = keycloakProperties.getClientId();
            if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(clientId);
                if (clientAccess != null && clientAccess.containsKey("roles")) {
                    var roles = (List<String>) clientAccess.get("roles");
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
                }
            }
        }

        return authorities;
    }
}
