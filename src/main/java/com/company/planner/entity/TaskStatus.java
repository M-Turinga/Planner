package com.company.planner.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;

public enum TaskStatus implements EnumClass<String> {

    IN_PROGRESS("A"),
    COMPLETED("B"),
    EXPIRED("C"),
    DEADLINE_DAY("D");

    private final String id;

    TaskStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TaskStatus fromId(String id) {
        for (TaskStatus at : TaskStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}