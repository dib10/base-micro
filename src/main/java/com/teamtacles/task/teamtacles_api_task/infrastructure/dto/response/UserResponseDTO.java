// infrastructure/dto/response/UserResponseDTO.java

package com.teamtacles.task.teamtacles_api_task.infrastructure.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    @Schema(description = "The unique identifier of the user.", example = "1")
    private Long id; // <-- CAMPO ADICIONADO

    @Schema(description = "The unique username of the user.", example = "jane.doe")
    private String userName;

    @Schema(description = "The unique email address of the user.", example = "jane.doe@example.com")
    private String email;

    @Schema(description = "A set of roles assigned to the user.", type = "array")
    private Set<RoleResponseDTO> roles; // <-- CAMPO ADICIONADO

    /**
     * Método de ajuda para verificar se o usuário é ADMIN.
     * A anotação @JsonIgnore evita que este método apareça no JSON de resposta.
     */
    @JsonIgnore
    public boolean isAdmin() {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream().anyMatch(role -> "ADMIN".equals(role.getRoleName()));
    }
}