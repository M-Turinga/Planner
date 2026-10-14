package com.company.planner.view.taskschedule;

import com.company.planner.entity.TaskSchedule;

import com.company.planner.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "task-schedules", layout = MainView.class)
@ViewController(id = "plan_TaskSchedule.list")
@ViewDescriptor(path = "task-schedule-list-view.xml")
@LookupComponent("taskSchedulesDataGrid")
@DialogMode(width = "64em")
public class TaskScheduleListView extends StandardListView<TaskSchedule> {

}