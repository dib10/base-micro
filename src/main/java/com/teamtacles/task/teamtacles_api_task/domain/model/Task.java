package com.teamtacles.task.teamtacles_api_task.domain.model;

import com.teamtacles.task.teamtacles_api_task.domain.model.enums.Status;
import com.teamtacles.task.teamtacles_api_task.domain.valueObject.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "title", nullable = false, length = 50))
    private TaskTitle title;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "description", length = 250))
    private Description description;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "due_date", nullable = false))
    private DueDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "owner_user_id", nullable = false))
    private OwnerUserId ownerUserId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "project_id", nullable = false))
    private ProjectId projectId;

    @ElementCollection
    @CollectionTable(name = "task_responsible_users", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "user_id", nullable = false)
    private List<Long> responsibleUserIds;
}