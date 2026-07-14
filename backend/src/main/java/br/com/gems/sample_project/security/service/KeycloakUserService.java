package br.com.gems.sample_project.security.service;

import br.com.gems.sample_project.security.config.SampleKeycloakProperties;
import br.com.gems.sample_project.security.dto.CreateUserDTO;
import br.com.gems.exception.exception.BusinessException;
import br.com.gems.exception.exception.SecurityException;
import br.com.gems.utils.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private final Keycloak keycloak;
    private final SampleKeycloakProperties keycloakProperties;

    private static final List<Integer> EXPECTED_SUCCESS_STATUS_FOR_CREATED_USER = List.of( 200,201,204 );

    private static final Integer CONFLICT_STATUS = 409;

    /**
     * Método responsável por criar um usuário no Keycloak.
     * @param dto Dados mínimos para a criação de um usuário no Keycloak
     * @return O identificador único gerado para este usuário no Keycloak. Com este identificador, será possível desabilitá-lo.
     */
    public String createUser( CreateUserDTO dto ) {
        if( ObjectUtil.isNullOrEmpty( dto.password() ) ){
            throw new BusinessException("A senha não foi informada durante a criação de um novo usuário!");
        }

        var user = buildUserRepresentation(dto);
        var usersResource = keycloak.realm(keycloakProperties.getRealm()).users();

        String userId = null;
        try (var response = usersResource.create(user)) {
            if( CONFLICT_STATUS.equals( response.getStatus() ) ){
                throw new BusinessException("Falha ao criar o usuário junto a API de Autenticação. Nome de usuário ou email já existente.");
            }

            if ( EXPECTED_SUCCESS_STATUS_FOR_CREATED_USER.stream().noneMatch(status -> status.equals( response.getStatus() ) ) ) {
                throw new SecurityException("Falha ao criar o usuário junto a API de Autenticação. Status: " + response.getStatus());
            }

            userId = CreatedResponseUtil.getCreatedId(response);
        }

        try{
            setUsersPassword(userId, dto.password());
        } catch ( Exception e ){
            deleteUser( userId );
            throw new SecurityException( "Falha na definição da senha do usuário junto a API de Autenticação!" );
        }

        return userId;
    }

    /**
     * Método que define a senha de um usuário pelo seu ID
     * @param userId Identificador único do usuário no Keycloak
     * @param newPassword nova senha que será definida para o usuário
     */
    private void setUsersPassword( String userId, String newPassword ) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue( newPassword );

        keycloak.realm(keycloakProperties.getRealm()).users().get(userId).resetPassword(credential);
    }

    /**
     * Método responsável por desabilitar um usuário no Keycloak (impede novos logins).
     * @param keycloakUserId identificador único do usuário no Keycloak.
     */
    public void disableUser(String keycloakUserId) {
        setUserEnabled(keycloakUserId, false);
    }

    /**
     * Habilita ou desabilita um usuário no Keycloak. Desabilitar impede novos logins; habilitar restaura o
     * acesso.
     * @param keycloakUserId identificador único do usuário no Keycloak.
     * @param enabled {@code true} para habilitar, {@code false} para desabilitar.
     */
    public void setUserEnabled(String keycloakUserId, boolean enabled) {
        var usersResource = keycloak.realm(keycloakProperties.getRealm()).users();
        var user = usersResource.get(keycloakUserId).toRepresentation();

        user.setEnabled(enabled);
        usersResource.get(keycloakUserId).update(user);
    }

    public void deleteUser(String keycloakUserId){
        var usersResource = keycloak.realm(keycloakProperties.getRealm()).users();
        usersResource.get(keycloakUserId).remove();
    }

    private UserRepresentation buildUserRepresentation(CreateUserDTO dto ) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername( dto.username() );
        user.setEmail( dto.email() );
        user.setFirstName( dto.firstName() );
        user.setLastName(dto.lastName() );
        user.setAttributes( dto.attributes() );

        return user;
    }

}
