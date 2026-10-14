package com.company.planner.security;

import com.company.planner.entity.Task;
import com.company.planner.entity.TaskCategory;
import com.company.planner.entity.TaskSchedule;
import com.company.planner.entity.User;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.model.SecurityScope;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "UserRole", code = UserRole.CODE, scope = SecurityScope.UI)
public interface UserRole {
    String CODE = "user-role";

    @EntityPolicy(entityClass = Task.class, actions = EntityPolicyAction.ALL)
    @EntityAttributePolicy(entityClass = Task.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    void task();

    @EntityAttributePolicy(entityClass = TaskCategory.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = TaskCategory.class, actions = EntityPolicyAction.ALL)
    void taskCategory();

    @EntityAttributePolicy(entityClass = TaskSchedule.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = TaskSchedule.class, actions = EntityPolicyAction.ALL)
    void taskSchedule();

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.ALL)
    void user();

    @MenuPolicy(menuIds = {"plan_Task.list", "plan_TaskCategory.list", "plan_DayPlan"})
    @ViewPolicy(viewIds = {"plan_Task.list", "plan_TaskCategory.list", "plan_DayPlan", "plan_MainView", "plan_Task.detail", "plan_TaskCategory.detail"})
    void screens();
}