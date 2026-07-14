package br.com.gems.sample_project.security;

import br.com.gems.utils.ObjectUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PasswordUtil {

    private static final String PASSWORD_PATTERN =
                    "(?=.*[a-z])" +         // Pelo menos uma letra minúscula
                    "(?=.*[A-Z])" +         // Pelo menos uma letra maiúscula
                    "(?=.*[@#$%^&+=!])" +   // Pelo menos um caractere especial
                    "(?=\\S+$)" +           // Sem espaços em branco
                    ".{8,}$";               // No mínimo 8 caracteres

    /**
     * É validado se a string informada é uma senha válida. Para isso, é necessário:
     * <ul>
     *     <li>Pelo menos uma letra minúscula;</li>
     *     <li>Pelo menos uma letra maiúscula;</li>
     *     <li>Pelo menos um caractere especial;</li>
     *     <li>Sem espaços em branco;</li>
     *     <li>No <strong>mínimo</strong> 8 caracteres.</li>
     * </ul>
     * @param password a senha de texto limpo que será submetida à validação.
     * @return {@code true} se a senha atender a todos os critérios de segurança descritos;<br/>
     *         {@code false} se a string for nula, vazia ou falhe em alguma regra.
     */
    public static boolean isValid(String password) {
        if (ObjectUtil.isNullOrEmpty(password)) {
            return false;
        }

        return password.matches(PASSWORD_PATTERN);
    }

}
