package com.company.planner.view.task;

import com.company.planner.entity.Task;
import com.company.planner.entity.User;
import com.company.planner.service.TaskStatusService;
import com.company.planner.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "tasks/:id", layout = MainView.class)
@ViewController(id = "plan_Task.detail")
@ViewDescriptor(path = "task-detail-view.xml")
@EditedEntityContainer("taskDc")
@DialogMode(width = "50em", height = "AUTO")
public class TaskDetailView extends StandardDetailView<Task> {

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private TaskStatusService taskStatusService;

    @Subscribe
    public void onBeforeSave(BeforeSaveEvent event) {
        Task task = getEditedEntity();
        task.setStatus(taskStatusService.calculateStatus(task));
    }

    @Subscribe
    public void onInitEntity(final InitEntityEvent<Task> event) {
        Task task = event.getEntity();
        if (task.getUser() == null) {
            task.setUser((User) currentAuthentication.getUser());
        }
    }
}