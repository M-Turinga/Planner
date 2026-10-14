package com.company.planner.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "PLAN_TASK")
@Entity(name = "plan_Task")
public class Task {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "USER_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @OneToMany(mappedBy = "task")
    private List<TaskSchedule> schedules;

    @InstanceName
    @Column(name = "NAME", nullable = false)
    @NotNull
    private String name;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DEADLINE")
    private LocalDate deadline;

    @Column(name = "STATUS", nullable = false)
    @NotNull
    private String status;

    @JoinColumn(name = "CATEGORY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TaskCategory category;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setStatus(TaskStatus status) {
        this.status = status == null ? null : status.getId();
    }

    @Transient
    public String getStatusDisplay() {
        TaskStatus status = getStatus();
        if (status == null) return "—";
        return switch (status) {
            case IN_PROGRESS -> "In Progress";
            case COMPLETED -> "Completed";
            case EXPIRED -> "Expired";
            case DEADLINE_DAY -> "Deadline Day";
            default -> status.name();
        };
    }
    public List<TaskSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<TaskSchedule> schedules) {
        this.schedules = schedules;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public TaskStatus getStatus() {
        return status == null ? null : TaskStatus.fromId(status);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}