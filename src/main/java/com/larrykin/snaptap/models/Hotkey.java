package com.larrykin.snaptap.models;

import com.larrykin.snaptap.enums.ActionType;

import java.io.Serializable;

public class Hotkey implements Serializable {
    private String id;
    private String name;
    private String keyCombo;
    private ActionType actionType;
    private String actionData;
    private boolean enabled;

    // Constructor
    public Hotkey(String id, String name, String keyCombo, ActionType actionType, String actionData) {
        this.id = id;
        this.name = name;
        this.keyCombo = keyCombo;
        this.actionType = actionType;
        this.actionData = actionData;
        this.enabled = true;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyCombo() {
        return keyCombo;
    }

    public void setKeyCombo(String keyCombo) {
        this.keyCombo = keyCombo;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}