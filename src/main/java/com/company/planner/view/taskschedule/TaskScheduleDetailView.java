package com.company.planner.view.taskschedule;

import com.company.planner.entity.TaskSchedule;
import com.company.planner.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;


@Route(value = "task-schedules/:id", layout = MainView.class)
@ViewController(id = "plan_TaskSchedule.detail")
@ViewDescriptor(path = "task-schedule-detail-view.xml")
@EditedEntityContainer("taskScheduleDc")
public class TaskScheduleDetailView extends StandardDetailView<TaskSchedule> {
}