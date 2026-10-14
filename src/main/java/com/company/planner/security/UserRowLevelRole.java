package com.company.planner.security;

import com.company.planner.entity.Task;
import com.company.planner.entity.TaskCategory;
import com.company.planner.entity.TaskSchedule;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

@RowLevelRole(name = "UserRowLevelRole", code = UserRowLevelRole.CODE)
public interface UserRowLevelRole {
    String CODE = "user-row-level-role";

    @JpqlRowLevelPolicy(entityClass = Task.class, where = "{E}.createdBy = :current_user_username")
    void task();

    @JpqlRowLevelPolicy(entityClass = TaskCategory.class, where = "{E}.createdBy = :current_user_username")
    void taskCategory();

    @JpqlRowLevelPolicy(entityClass = TaskSchedule.class, where = "{E}.createdBy = :current_user_username")
    void taskSchedule();
}