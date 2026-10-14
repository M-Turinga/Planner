package com.company.planner.security;

import com.company.planner.entity.Task;
import com.company.planner.entity.TaskCategory;
import com.company.planner.entity.TaskSchedule;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.model.SecurityScope;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityflowui.role.UiMinimalPolicies;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "UI: minimal access", code = UiMinimalRole.CODE, scope = SecurityScope.UI)
public interface UiMinimalRole extends UiMinimalPolicies {

    String CODE = "ui-minimal";

    @ViewPolicy(viewIds = {"plan_MainView", "plan_DayPlan", "plan_TaskCategory.list", "plan_Task.list"})
    void main();

    @ViewPolicy(viewIds = "plan_LoginView")
    @SpecificPolicy(resources = "ui.loginToUi")
    void login();

    @EntityAttributePolicy(entityClass = Task.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Task.class, actions = EntityPolicyAction.ALL)
    void task();

    @EntityAttributePolicy(entityClass = TaskCategory.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = TaskCategory.class, actions = EntityPolicyAction.ALL)
    void taskCategory();

    @EntityAttributePolicy(entityClass = TaskSchedule.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = TaskSchedule.class, actions = EntityPolicyAction.ALL)
    void taskSchedule();
}
