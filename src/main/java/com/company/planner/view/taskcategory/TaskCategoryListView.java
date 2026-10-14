package com.company.planner.view.taskcategory;

import com.company.planner.entity.TaskCategory;

import com.company.planner.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "task-categories", layout = MainView.class)
@ViewController(id = "plan_TaskCategory.list")
@ViewDescriptor(path = "task-category-list-view.xml")
@LookupComponent("taskCategoriesDataGrid")
@DialogMode(width = "64em")
public class TaskCategoryListView extends StandardListView<TaskCategory> {

}