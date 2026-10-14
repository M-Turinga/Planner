package com.company.planner.service;

import com.company.planner.entity.Task;
import com.company.planner.entity.TaskStatus;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component("plan_TaskStatusService")
public class TaskStatusService {
    @Autowired
    private DataManager dataManager;

    public TaskStatus calculateStatus(Task task) {

        LocalDate deadline = task.getDeadline();
        LocalDate today = LocalDate.now();

        if (deadline == null) {
            return TaskStatus.IN_PROGRESS;
        }

        if (deadline.isBefore(today)) {
            return TaskStatus.EXPIRED;
        } else if (deadline.isEqual(today)) {
            return TaskStatus.DEADLINE_DAY;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

    public boolean updateTaskStatus(Task task) {
        if (task.getStatus() == TaskStatus.COMPLETED) {
            return false;
        }

        TaskStatus newStatus = calculateStatus(task);
        if (newStatus != task.getStatus()) {
            task.setStatus(newStatus);
            dataManager.saveWithoutReload(task);
            return true;
        }
        return false;
    }

    public int updateAllTasksStatuses() {
        List<Task> tasks = dataManager.load(Task.class)
                .query("select t from plan_Task t")
                .list();

        int updatedCount = 0;
        for (Task task : tasks) {
            if (updateTaskStatus(task)) {
                updatedCount++;
            }
        }
        return updatedCount;
    }
}
