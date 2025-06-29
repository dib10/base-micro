package com.teamtacles.task.teamtacles_api_task.infrastructure.controller;

import com.teamtacles.task.teamtacles_api_task.application.service.TaskService;
import com.teamtacles.task.teamtacles_api_task.infrastructure.dto.request.TaskRequestDTO;
import com.teamtacles.task.teamtacles_api_task.infrastructure.dto.request.TaskRequestPatchDTO;
import com.teamtacles.task.teamtacles_api_task.infrastructure.dto.response.PagedResponse;
import com.teamtacles.task.teamtacles_api_task.infrastructure.dto.response.TaskResponseDTO;
import com.teamtacles.task.teamtacles_api_task.infrastructure.dto.response.TaskResponseFilteredDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tasks") // ROTA BASE ATUALIZADA!
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Create a new task for a project")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Task created successfully") })
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @RequestParam @Parameter(description = "ID of the project this task belongs to") Long projectId,
            @Valid @RequestBody TaskRequestDTO taskRequestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt authenticationToken) {
        
        Long userIdFromToken = authenticationToken.getClaim("userId");
        TaskResponseDTO taskResponseDTO = taskService.createTask(projectId, taskRequestDTO, userIdFromToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponseDTO);
    }

    @Operation(summary = "Get a task by its ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved the task") })
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable @Parameter(description = "Task ID") Long taskId,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt authenticationToken) {
        
        String userToken = authenticationToken.getTokenValue();
        return ResponseEntity.ok(taskService.getTasksById(taskId, userToken));
    }

    @Operation(summary = "Get all tasks with filters")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of tasks") })
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<TaskResponseFilteredDTO>> getAllTasksFiltered(
            @RequestParam(required = false) @Parameter(description = "Filter by status") String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "Filter by due date") LocalDateTime dueDate,
            @RequestParam(required = false) @Parameter(description = "Filter by Project ID") Long projectId,
            Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt authenticationToken) {
        
        String userToken = authenticationToken.getTokenValue();
        PagedResponse<TaskResponseFilteredDTO> tasksPage = taskService.getAllTasksFiltered(status, dueDate, projectId, pageable, userToken);
        return ResponseEntity.ok(tasksPage);
    }

    @Operation(summary = "Update an existing task completely")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Task updated successfully") })
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable @Parameter(description = "Task ID") Long taskId,
            @Valid @RequestBody TaskRequestDTO taskRequestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt authenticationToken) {
        
        String userToken = authenticationToken.getTokenValue();
        TaskResponseDTO taskResponseDTO = taskService.updateTask(taskId, taskRequestDTO, userToken);
        return ResponseEntity.ok(taskResponseDTO);
    }

    @Operation(summary = "Partially update a task (e.g., status)")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Task partially updated successfully") })
    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> partialUpdateTask(
            @PathVariable @Parameter(description = "Task ID") Long taskId,
            @Valid @RequestBody TaskRequestPatchDTO patchDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt authenticationToken) {
        
        String userToken = authenticationToken.getTokenValue();
        TaskResponseDTO taskResponseDTO = taskService.partialUpdateTask(taskId, patchDTO, userToken);
        return ResponseEntity.ok(taskResponseDTO);
    }

    @Operation(summary = "Delete a task by its ID")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Task deleted successfully") })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable @Parameter(description = "Task ID") Long taskId,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt authenticationToken) {
        
        String userToken = authenticationToken.getTokenValue();
        taskService.deleteTask(taskId, userToken);
        return ResponseEntity.noContent().build();
    }
}