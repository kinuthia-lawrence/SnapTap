package com.larrykin.snaptap.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Profile {
    private String id;
    private String name;
    private List<Hotkey> hotkeys;
    private boolean active;

    public Profile() {
        this.id = UUID.randomUUID().toString();
        this.hotkeys = new ArrayList<>();
        this.active = false;
    }

    public Profile(String name) {
        this();
        this.name = name;
    }

    // Getters and setters
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

    public List<Hotkey> getHotkeys() {
        return hotkeys;
    }

    public void setHotkeys(List<Hotkey> hotkeys) {
        this.hotkeys = hotkeys;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addHotkey(Hotkey hotkey) {
        this.hotkeys.add(hotkey);
    }

    public void removeHotkey(String hotkeyId) {
        this.hotkeys.removeIf(hotkey -> hotkey.getId().equals(hotkeyId));
    }
}