package com.company.planner.view.dayplan;


import com.company.planner.entity.Task;
import com.company.planner.entity.TaskCategory;
import com.company.planner.entity.TaskSchedule;
import com.company.planner.entity.TaskStatus;
import com.company.planner.service.TaskStatusService;
import com.company.planner.view.main.MainView;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.datepicker.TypedDatePicker;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import io.jmix.flowui.UiComponents;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Route(value = "day-plan", layout = MainView.class)
@ViewController(id = "plan_DayPlan")
@ViewDescriptor(path = "day-plan.xml")
public class DayPlan extends StandardView {
    @Autowired
    private Notifications notifications;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private DialogWindows dialogWindows;

    @Autowired
    private TaskStatusService taskStatusService;

    @ViewComponent
    private ComboBox<TaskCategory> categoryFilter;

    @ViewComponent
    private ComboBox<TaskStatus> statusFilter;

    @ViewComponent
    private CollectionContainer<TaskSchedule> taskSchedulesDc;

    @ViewComponent
    private CollectionLoader<TaskSchedule> taskSchedulesDl;

    @ViewComponent
    private DatePicker currentDateField;

    @ViewComponent
    private DataGrid<TaskSchedule> taskSchedulesDataGrid;


    private void checkAndUpdateTaskStatuses() {
        int updatedCount = taskStatusService.updateAllTasksStatuses();
        if (updatedCount > 0) {
            notifications.show(updatedCount + " task status(es) have been auto-updated");
        }
    }

    @Subscribe(target =  Target.CONTROLLER)
    public void onReady(final ReadyEvent event) {

        Grid.Column<TaskSchedule> checkboxColumn = taskSchedulesDataGrid.addColumn(new ComponentRenderer<>(item -> {
                    Checkbox checkbox = new Checkbox();
                    checkbox.setValue(item.getIsCompleted() != null && item.getIsCompleted());

                    checkbox.addValueChangeListener(e -> {
                        item.setIsCompleted(e.getValue());
                        Task task = item.getTask();

                        if (e.getValue()) {
                            task.setStatus(TaskStatus.COMPLETED);
                        } else {
                            TaskStatus newStatus = taskStatusService.calculateStatus(task);
                            task.setStatus(newStatus);
                        }
                        System.out.println("New status: " + task.getStatus());
                        dataManager.saveWithoutReload(task);
                        dataManager.saveWithoutReload(item);
                        taskSchedulesDataGrid.getDataProvider().refreshItem(item);
                        notifications.show("Task " + (e.getValue() ? "done" : "undone"));
                    });

                    return checkbox;
                })).setHeader("")
                .setKey("completedCheckbox")
                .setWidth("60px")
                .setFlexGrow(0);
        taskSchedulesDataGrid.setColumnPosition(checkboxColumn, 0);

        Grid.Column<TaskSchedule> statusColumn = taskSchedulesDataGrid.getColumnByKey("statusColumn");
        if (statusColumn != null) {
            statusColumn.setRenderer(new ComponentRenderer<>(item -> {
                Task task = item.getTask();
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
                            span.setText("Deadline Day!");
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

        taskSchedulesDataGrid.addColumn(
                        new ComponentRenderer<>(item -> {
                            JmixButton removeBtn = uiComponents.create(JmixButton.class);
                            removeBtn.setIcon(VaadinIcon.TRASH.create());
                            removeBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
                            removeBtn.setTooltipText("Remove from schedule");

                            removeBtn.addClickListener(e -> {
                                dataManager.remove(item);
                                taskSchedulesDl.load();
                                notifications.show("Task removed from schedule");
                            });

                            return removeBtn;
                        })
                )
                .setHeader("")
                .setKey("removeColumn")
                .setWidth("60px")
                .setFlexGrow(0);

        Grid.Column<TaskSchedule> descColumn = taskSchedulesDataGrid.getColumnByKey("descriptionColumn");
        if (descColumn != null) {
            descColumn.setRenderer(new ComponentRenderer<>(item -> {
                        Task task = item.getTask();
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

        taskSchedulesDataGrid.addColumn(
                        new ComponentRenderer<>(item -> {
                            JmixButton editBtn = uiComponents.create(JmixButton.class);
                            editBtn.setIcon(VaadinIcon.EDIT.create());
                            editBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
                            editBtn.setTooltipText("Edit task");

                            editBtn.addClickListener(e -> dialogWindows.detail(this, Task.class)
                                    .editEntity(item.getTask())
                                    .withAfterCloseListener(closeEvent -> taskSchedulesDl.load())
                                    .open());

                            return editBtn;
                        })
                )
                .setHeader("")
                .setKey("editColumn")
                .setWidth("60px")
                .setFlexGrow(0);


    }


    @Subscribe
    public void onInit(final InitEvent event) {
        final LocalDate today  = LocalDate.now();
        currentDateField.setValue(today);
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

    @Subscribe(id = "prevDayBtn", subject = "clickListener")
    public void onPrevDayBtnClick(final ClickEvent<JmixButton> event) {
        LocalDate newDate = currentDateField.getValue().minusDays(1);
        currentDateField.setValue(newDate);
        checkAndUpdateTaskStatuses();
        applyFilters();
    }

    @Subscribe(id = "nextDayBtn", subject = "clickListener")
    public void onNextDayBtnClick(final ClickEvent<JmixButton> event) {
        LocalDate newDate = currentDateField.getValue().plusDays(1);
        currentDateField.setValue(newDate);
        checkAndUpdateTaskStatuses();
        applyFilters();
    }

    @Subscribe(id = "todayBtn", subject = "clickListener")
    public void onTodayBtnClick(final ClickEvent<JmixButton> event) {
        final LocalDate today  = LocalDate.now();
        currentDateField.setValue(today);
        checkAndUpdateTaskStatuses();
        applyFilters();
    }

    @Subscribe(id = "addTaskBtn", subject = "clickListener")
    public void onAddTaskBtnClick(final ClickEvent<JmixButton> event) {

        dialogWindows.lookup(this, Task.class).withSelectHandler(tasks -> {
            if (!tasks.isEmpty()) {
                int addedCount = 0;
                for (Task task : tasks) {
                    if(addTaskToSchedule(task)){ addedCount++; }
                }
                if(addedCount > 0) {
                    notifications.show(tasks.size() + " task(s) have been added");
                }
            }
        }).open();
    }

    private boolean addTaskToSchedule(Task task) {
        LocalDate currentDate = currentDateField.getValue();
        boolean alreadyExists = taskSchedulesDc.getItems().stream()
                .anyMatch(schedule -> schedule.getTask().equals(task)
                        && schedule.getPlanDate().equals(currentDate));

        if (alreadyExists){
            notifications.show("This task has already been added on " + currentDate);
            return false;
        }

        taskStatusService.updateTaskStatus(task);

        TaskSchedule schedule = dataManager.create(TaskSchedule.class);
        schedule.setTask(task);
        schedule.setPlanDate(currentDate);
        schedule.setIsCompleted(false);

        if (task.getStatus() == null || task.getStatus() == TaskStatus.COMPLETED) {
            task.setStatus(TaskStatus.IN_PROGRESS);
            dataManager.saveWithoutReload(task);
        }

        int maxOrder = taskSchedulesDc.getItems().stream()
                .mapToInt((s -> s.getOrderNum() != null ? s.getOrderNum() : 0))
                .max()
                .orElse(0);
        schedule.setOrderNum(maxOrder + 1);

        dataManager.saveWithoutReload(schedule);
        applyFilters();

        return true;
    }

    @Subscribe("currentDateField")
    public void onCurrentDateFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<TypedDatePicker<LocalDate>,
            LocalDate> event) {
        if (event.getValue() != null) {
            checkAndUpdateTaskStatuses();
            applyFilters();
        }
    }

    @Subscribe("taskSchedulesDataGrid.removeFromSchedule")
    public void onTaskSchedulesDataGridRemoveFromSchedule(final ActionPerformedEvent event) {
        TaskSchedule selected = taskSchedulesDataGrid.getSingleSelectedItem();
        if (selected == null){
            notifications.show("Select the task to be deleted");
            return;
        }
        dataManager.remove(selected);
        applyFilters();
        notifications.show("The task has been removed from the schedule");
    }

    @Subscribe("categoryFilter")
    public void onCategoryFilterChange(final AbstractField.ComponentValueChangeEvent<ComboBox<TaskCategory>, TaskCategory> event) {
        applyFilters();
    }

    @Subscribe("statusFilter")
    public void onStatusFilterChange(final AbstractField.ComponentValueChangeEvent<ComboBox<TaskStatus>, TaskStatus> event) {
        applyFilters();
    }

    private void applyFilters() {
        TaskCategory category = categoryFilter.getValue();
        TaskStatus status = statusFilter.getValue();

        taskSchedulesDl.setParameter("selectedDate", currentDateField.getValue());
        taskSchedulesDl.setParameter("category", category);
        taskSchedulesDl.setParameter("status", status != null ? status.getId() : null);
        taskSchedulesDl.load();
    }

}