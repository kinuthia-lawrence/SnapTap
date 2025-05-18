package com.larrykin.snaptap.models;

import com.larrykin.snaptap.enums.ActionType;

import java.io.Serializable;


/**
 * Represents a hotkey configuration, including its unique identifier, name,
 * key combination, action type, associated action data, and usage details.
 * This class is serializable to allow saving and loading hotkey configurations.
 */
public class Hotkey implements Serializable {
    private String id;
    private String name;
    private String keyCombo;
    private ActionType actionType;
    private String actionData;
    private boolean enabled;
    private int usageCount;

    //

    /**
     * Default constructor for the Hotkey class.
     * Initializes the hotkey as enabled by default.
     * default constructor required for deserialization
     */
    public Hotkey() {
        this.enabled = true;
    }


    /**
     * Constructor for the Hotkey class.
     *
     * @param id         Unique identifier for the hotkey.
     * @param name       Name of the hotkey.
     * @param keyCombo   Key combination associated with the hotkey.
     * @param actionType Type of action to be performed when the hotkey is triggered.
     * @param actionData Additional data associated with the action.
     */
    public Hotkey(String id, String name, String keyCombo, ActionType actionType, String actionData) {
        this.id = id;
        this.name = name;
        this.keyCombo = keyCombo;
        this.actionType = actionType;
        this.actionData = actionData;
        this.enabled = true;
    }

    /**
     * Getters and Setters for the Hotkey class.
     */
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


    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public void incrementUsageCount() {
        this.usageCount++;
    }
}