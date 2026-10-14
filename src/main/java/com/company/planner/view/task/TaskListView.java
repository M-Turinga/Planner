package com.company.planner.view.task;

import com.company.planner.entity.Task;

import com.company.planner.entity.TaskCategory;
import com.company.planner.entity.TaskStatus;
import com.company.planner.service.TaskStatusService;
import com.company.planner.view.main.MainView;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.grid.DataGridColumn;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "tasks", layout = MainView.class)
@ViewController(id = "plan_Task.list")
@ViewDescriptor(path = "task-list-view.xml")
@LookupComponent("tasksDataGrid")
@DialogMode(width = "64em")
public class TaskListView extends StandardListView<Task> {

    @ViewComponent
    private DataGrid<Task> tasksDataGrid;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private Notifications notifications;

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private ComboBox<TaskCategory> categoryFilter;

    @ViewComponent
    private ComboBox<TaskStatus> statusFilter;

    @ViewComponent
    private CollectionLoader<Task> tasksDl;

    @ViewComponent
    private CollectionContainer<Task> tasksDc;

    @Subscribe
    public void onInit(final InitEvent event) {
        categoryFilter.setItems(
                dataManager.load(TaskCategory.class)
                        .query("select c from plan_TaskCategory c")
                        .list()
        );
        categoryFilter.setItemLabelGenerator(TaskCategory::getName);

        statusFilter.setItems(TaskStatus.values());
        statusFilter.setItemLabelGenerator(status -> switch (status) {
            case IN_PROGRESS -> "In Progress";
            case COMPLETED -> "Completed";
            case EXPIRED -> "Expired";
            case DEADLINE_DAY -> "Deadline Day!";
        });

        checkAndUpdateTaskStatuses();
        applyFilters();
    }

    @Subscribe(target = Target.CONTROLLER)
    public void onReady(ReadyEvent event) {
        Grid.Column<Task> statusColumn = tasksDataGrid.getColumnByKey("status");
        if (statusColumn != null) {
            statusColumn.setRenderer(new ComponentRenderer<>(task -> {
                Span span = new Span();
                if (task.getStatus() != null) {
                    switch (task.getStatus()) {
                        case IN_PROGRESS:
                            span.setText("In Progress");
                            span.getStyle().set("color", "#1976D2");
                            break;
                        case COMPLETED:
                            span.setText("Completed");
                            span.getStyle().set("color", "#2E7D32");
                            break;
                        case EXPIRED:
                            span.setText("Expired");
                            span.getStyle().set("color", "#C62828");
                            break;
                        case DEADLINE_DAY:
                            span.setText("Deadline Day");
                            span.getStyle().set("color", "#E65100");
                            break;
                        default:
                            span.setText("—");
                    }
                } else {
                    span.setText("—");
                }
                return span;
            }));
        }

        DataGridColumn<Task> descColumn = tasksDataGrid.getColumnByKey("description");
        if (descColumn != null) {
            descColumn.setRenderer(new ComponentRenderer<>(task -> {

                        String description = task.getDescription();

                        Span span = new Span();
                        span.getStyle().set("cursor", "pointer");
                        span.getStyle().set("color", "#1976D2");
                        span.getStyle().set("text-decoration", "underline dotted");

                        if (description == null || description.isBlank()) {
                            span.setText("—");
                            span.getStyle().set("cursor", "default");
                            span.getStyle().set("color", "gray");
                            span.getStyle().set("text-decoration", "none");
                        } else {
                            String shortText = description.length() > 40
                                    ? description.substring(0, 40) + "…"
                                    : description;
                            span.setText(shortText);

                            span.addClickListener(e -> {
                                Dialog dialog = uiComponents.create(Dialog.class);
                                dialog.setHeaderTitle("Description: " + task.getName());
                                dialog.setWidth("500px");

                                Paragraph fullText = new Paragraph(description);
                                fullText.getStyle().set("white-space", "pre-wrap");

                                VerticalLayout content = new VerticalLayout(fullText);
                                content.setPadding(false);
                                dialog.add(content);

                                JmixButton closeButton = uiComponents.create(JmixButton.class);
                                closeButton.setText("Close");
                                closeButton.addClickListener(ev -> dialog.close());
                                dialog.getFooter().add(closeButton);

                                dialog.open();
                            });
                        }

                        return span;
                    }))
                    .setWidth("250px")
                    .setFlexGrow(0);
        }
    }

    @Subscribe("categoryFilter")
    public void onCategoryFilterChange(
            final AbstractField.ComponentValueChangeEvent<ComboBox<TaskCategory>, TaskCategory> event) {
        applyFilters();
    }

    @Subscribe("statusFilter")
    public void onStatusFilterChange(
            final AbstractField.ComponentValueChangeEvent<ComboBox<TaskStatus>, TaskStatus> event) {
        applyFilters();
    }

    private void applyFilters() {
        TaskCategory category = categoryFilter.getValue();
        TaskStatus status = statusFilter.getValue();

        tasksDl.setParameter("category", category);
        tasksDl.setParameter("status", status != null ? status.getId() : null);
        tasksDl.load();
    }

    private void checkAndUpdateTaskStatuses() {
        int updatedCount = taskStatusService.updateAllTasksStatuses();
        if (updatedCount > 0) {
            notifications.show(updatedCount + " task status(es) have been auto-updated");
        }
    }
}