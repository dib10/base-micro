// infrastructure/dto/response/RoleResponseDTO.java

package com.teamtacles.task.teamtacles_api_task.infrastructure.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {
    @Schema(description = "The name of the user's role.", example = "USER")
    private String roleName;
}