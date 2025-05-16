package com.larrykin.snaptap.services;

import com.larrykin.snaptap.models.Hotkey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HotkeyManager {
    private static final Logger logger = LoggerFactory.getLogger(HotkeyManager.class);
    private Map<String, Hotkey> registeredHotkeys = new HashMap<>();
    private boolean masterSwitch = true;

    public void registerHotkey(Hotkey hotkey) {
        // Register hotkey with system
        registeredHotkeys.put(hotkey.getId(), hotkey);
        logger.info("Registered hotkey: {}", hotkey.getName());
    }

    public void executeAction(Hotkey hotkey) {
        if (!masterSwitch || !hotkey.isEnabled()) {
            return;
        }

        switch (hotkey.getActionType()) {
            case URL -> openUrl(hotkey.getActionData());
            case APPLICATION -> launchApplication(hotkey.getActionData());
            case FILE_FOLDER -> openFileOrFolder(hotkey.getActionData());
        }
    }

    // Implementation methods
    private void openUrl(String url) {
        //todo: Implementation
    }

    private void launchApplication(String path) {
        //TODO: Implementation
    }

    private void openFileOrFolder(String path) {
        //TODO: Implementation
    }
}