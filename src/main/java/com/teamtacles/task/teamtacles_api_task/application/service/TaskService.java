package com.teamtacles.task.teamtacles_api_task.application.service;

import com.teamtacles.task.teamtacles_api_task.domain.model.Task;
import com.teamtacles.task.teamtacles_api_task.domain.model.enums.Status;
import com.teamtacles.task.teamtacles_api_task.domain.valueObject.*;
import com.teamtacles.task.teamtacles_api_task.infrastructure.client.ProjectServiceClient;
import com.teamtacles.task.teamtacles_api_task.infrastructure.client.UserServiceClient;
import com.teamtacles.task.teamtacles_api_task.infrastructure.dto.request.TaskRequestDTO;
import com.teamtacles.task.teamtacles_api_task.infrastructure.dto.request.TaskRequestPatchDTO;
import com.teamtacles.task.teamtacles_api_task.infrastructure.dto.response.*;
import com.teamtacles.task.teamtacles_api_task.infrastructure.exception.ResourceNotFoundException;
import com.teamtacles.task.teamtacles_api_task.infrastructure.mapper.PagedResponseMapper;
import com.teamtacles.task.teamtacles_api_task.infrastructure.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final ModelMapper modelMapper;
    private final PagedResponseMapper pagedResponseMapper;

    public TaskResponseDTO createTask(Long projectId, TaskRequestDTO taskRequestDTO, Long userIdFromToken) {
        projectServiceClient.validateProjectMembership(projectId, userIdFromToken);
        taskRequestDTO.getUsersResponsability().forEach(userServiceClient::validateUserExists);

        Task task = new Task();
        task.setProjectId(new ProjectId(projectId));
        task.setOwnerUserId(new OwnerUserId(userIdFromToken));
        task.setTitle(new TaskTitle(taskRequestDTO.getTitle()));
        task.setDescription(new Description(taskRequestDTO.getDescription()));
        task.setDueDate(new DueDate(taskRequestDTO.getDueDate()));
        task.setResponsibleUserIds(taskRequestDTO.getUsersResponsability());
        task.setStatus(Status.TODO);

        Task createdTask = taskRepository.save(task);
        return enrichTaskResponse(createdTask);
    }

    public TaskResponseDTO getTasksById(Long taskId, String userToken) {
        Task task = findTaskOrThrow(taskId);
        ensureUserCanAccessTask(task, userToken);
        return enrichTaskResponse(task);
    }
    
    public PagedResponse<TaskResponseFilteredDTO> getAllTasksFiltered(String status, LocalDateTime dueDate, Long projectId, Pageable pageable, String userToken) {
        UserResponseDTO user = getAuthenticatedUserFromToken(userToken);
        if (projectId != null) {
            projectServiceClient.validateProjectMembership(projectId, user.getId());
        }

        Status statusEnum = transformStatusToEnum(status);
        Page<Task> tasksPage;
        if (user.isAdmin()) {
            tasksPage = taskRepository.findTasksFiltered(statusEnum, dueDate, projectId, pageable);
        } else {
            tasksPage = taskRepository.findTasksFilteredByUser(statusEnum, dueDate, projectId, user.getId(), pageable);
        }

        List<TaskResponseFilteredDTO> dtoList = tasksPage.getContent().stream()
                .map(this::enrichTaskResponseFiltered)
                .collect(Collectors.toList());

        return new PagedResponse<>(dtoList, tasksPage.getNumber(), tasksPage.getSize(), tasksPage.getTotalElements(), tasksPage.getTotalPages(), tasksPage.isLast());
    }

    public TaskResponseDTO updateTask(Long taskId, TaskRequestDTO taskRequestDTO, String userToken) {
        Task task = findTaskOrThrow(taskId);
        ensureUserCanAccessTask(task, userToken);
        
        taskRequestDTO.getUsersResponsability().forEach(userServiceClient::validateUserExists);

        task.setTitle(new TaskTitle(taskRequestDTO.getTitle()));
        task.setDescription(new Description(taskRequestDTO.getDescription()));
        task.setDueDate(new DueDate(taskRequestDTO.getDueDate()));
        task.setResponsibleUserIds(taskRequestDTO.getUsersResponsability());
        
        Task updatedTask = taskRepository.save(task);
        return enrichTaskResponse(updatedTask);
    }

    public TaskResponseDTO partialUpdateTask(Long taskId, TaskRequestPatchDTO patchDTO, String userToken) {
        Task task = findTaskOrThrow(taskId);
        ensureUserCanAccessTask(task, userToken);

        patchDTO.getStatus().ifPresent(task::setStatus);
        patchDTO.getTitle().ifPresent(title -> task.setTitle(new TaskTitle(title)));
        patchDTO.getDescription().ifPresent(desc -> task.setDescription(new Description(desc)));
        patchDTO.getDueDate().ifPresent(date -> task.setDueDate(new DueDate(date)));
        patchDTO.getUserId().ifPresent(responsibleIds -> {
            responsibleIds.forEach(userServiceClient::validateUserExists);
            task.setResponsibleUserIds(responsibleIds);
        });

        Task updatedTask = taskRepository.save(task);
        return enrichTaskResponse(updatedTask);
    }

    public void deleteTask(Long taskId, String userToken) {
        Task task = findTaskOrThrow(taskId);
        ensureUserCanAccessTask(task, userToken);
        taskRepository.delete(task);
    }

    private Task findTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " not found."));
    }

    private void ensureUserCanAccessTask(Task task, String userToken) {
        UserResponseDTO user = getAuthenticatedUserFromToken(userToken);
        // CORRIGIDO: Usando .value() para acessar o ID do record
        projectServiceClient.validateProjectMembership(task.getProjectId().value(), user.getId());

        boolean isOwner = task.getOwnerUserId().value().equals(user.getId());
        boolean isResponsible = task.getResponsibleUserIds().contains(user.getId());
        
        if (user.isAdmin() || isOwner || isResponsible) {
            return;
        }
        throw new AccessDeniedException("Forbidden: You do not have permission to modify this task.");
    }
    
    private UserResponseDTO getAuthenticatedUserFromToken(String token) {
        return userServiceClient.getUserFromToken("Bearer " + token);
    }

    private TaskResponseDTO enrichTaskResponse(Task task) {
        // CORRIGIDO: Usando .value() para acessar o ID do record
        UserResponseDTO owner = userServiceClient.getUserById(task.getOwnerUserId().value());
        List<UserResponseDTO> responsibles = task.getResponsibleUserIds().stream()
                .map(userServiceClient::getUserById)
                .collect(Collectors.toList());

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        // CORRIGIDO: Usando .value() para acessar o valor primitivo do record
        dto.setTitle(task.getTitle().value());
        dto.setDescription(task.getDescription().value());
        dto.setDueDate(task.getDueDate().value());
        dto.setStatus(task.getStatus());
        dto.setOwner(owner);
        dto.setUsersResponsability(responsibles);
        return dto;
    }

    private TaskResponseFilteredDTO enrichTaskResponseFiltered(Task task) {
        TaskResponseDTO enrichedTask = enrichTaskResponse(task);
        TaskResponseFilteredDTO filteredDto = modelMapper.map(enrichedTask, TaskResponseFilteredDTO.class);
        
        // CORRIGIDO: Usando .value() para acessar o ID do record
        ProjectResponseDTO project = projectServiceClient.getProjectById(task.getProjectId().value());
        filteredDto.setProject(modelMapper.map(project, TaskProjectResponseFilteredDTO.class));
        return filteredDto;
    }

    private Status transformStatusToEnum(String status) {
        if (status == null || status.isEmpty()) return null;
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }
}