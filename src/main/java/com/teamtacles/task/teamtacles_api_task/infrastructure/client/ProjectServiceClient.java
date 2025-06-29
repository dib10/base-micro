// infrastructure/client/ProjectServiceClient.java

package com.teamtacles.task.teamtacles_api_task.infrastructure.client;

import com.teamtacles.task.teamtacles_api_task.infrastructure.dto.response.ProjectResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "project-service", url = "${application.client.urls.project-service}")
public interface ProjectServiceClient {

    @GetMapping("/api/project/{id}")
    ProjectResponseDTO getProjectById(@PathVariable("id") Long id);

    // Endpoint para o monólito validar se um usuário pode acessar um projeto
    @GetMapping("/api/project/{id}/validate-member")
    void validateProjectMembership(@PathVariable("id") Long projectId, @RequestParam("userId") Long userId);
}