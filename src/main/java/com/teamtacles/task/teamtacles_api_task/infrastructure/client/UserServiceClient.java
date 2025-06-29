// infrastructure/client/UserServiceClient.java

package com.teamtacles.task.teamtacles_api_task.infrastructure.client;

import com.teamtacles.task.teamtacles_api_task.infrastructure.dto.response.UserResponseDTO;
import com.teamtacles.task.teamtacles_api_task.infrastructure.exception.ResourceNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "${application.client.urls.user-service}")
public interface UserServiceClient {

    @GetMapping("/api/user/{id}") // Endpoint para buscar usuário por ID no monólito
    UserResponseDTO getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/user/me") // Endpoint para buscar usuário pelo token no monólito
    UserResponseDTO getUserFromToken(@RequestHeader("Authorization") String authorizationToken);

    default void validateUserExists(Long id) {
        try {
            getUserById(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User with ID " + id + " not found.");
        }
    }
}