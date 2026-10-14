package com.company.planner.view.taskcategory;

import com.company.planner.entity.TaskCategory;
import com.company.planner.entity.User;
import com.company.planner.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "task-categories/:id", layout = MainView.class)
@ViewController(id = "plan_TaskCategory.detail")
@ViewDescriptor(path = "task-category-detail-view.xml")
@EditedEntityContainer("taskCategoryDc")
public class TaskCategoryDetailView extends StandardDetailView<TaskCategory> {

    @Autowired
    private CurrentAuthentication currentAuthentication;


    @Subscribe
    public void onInitEntity(final InitEntityEvent<TaskCategory> event) {
        TaskCategory category = event.getEntity();
        if (category.getUser() == null) {
            category.setUser((User) currentAuthentication.getUser());
        }
    }
}