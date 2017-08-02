package com.crossover.trial.journals.dto;

import com.crossover.trial.journals.model.Category;

import java.util.Objects;

public class SubscriptionDTO {

    private long id;

    private String name;

    private boolean active;

    public SubscriptionDTO(Category c) {
        name = c.getName();
        id = c.getId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(this instanceof SubscriptionDTO)) {
            return false;
        }

        SubscriptionDTO that = (SubscriptionDTO) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
