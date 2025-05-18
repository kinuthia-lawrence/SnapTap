package com.larrykin.snaptap.services;

import com.larrykin.snaptap.models.Hotkey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HotkeyManager {
    private static final Logger logger = LoggerFactory.getLogger(HotkeyManager.class);
    private Map<String, Hotkey> registeredHotkeys = new HashMap<>();
    private boolean masterSwitch = true; // Reflects the active profile status
    private boolean systemRunning = true; // Indicates if the system is running in the background

    public void setMasterSwitch(boolean isActive) {
        this.masterSwitch = isActive;
        logger.info("Master switch set to: {}", isActive ? "Active" : "Inactive");
    }

    public void setSystemRunning(boolean isRunning) {
        this.systemRunning = isRunning;
        logger.info("System running status set to: {}", isRunning ? "Running" : "Stopped");
    }

   public void registerHotkey(Hotkey hotkey) {
       registeredHotkeys.put(hotkey.getId(), hotkey);
       logger.info("Registered hotkey: {} with combination: {}", hotkey.getName(), hotkey.getKeyCombo());
   }

    public void executeAction(Hotkey hotkey) {
        logger.info("Executing action for hotkey: {}", hotkey.getName());
        if (!masterSwitch || !systemRunning || !hotkey.isEnabled()) {
            logger.warn("Action not executed. Conditions not met: masterSwitch={}, systemRunning={}, hotkeyEnabled={}",
                    masterSwitch, systemRunning, hotkey.isEnabled());
            return;
        }

        switch (hotkey.getActionType()) {
            case URL -> openUrl(hotkey.getActionData());
            case APPLICATION -> launchApplication(hotkey.getActionData());
            case FILE_FOLDER -> openFileOrFolder(hotkey.getActionData());
        }
    }


    public Map<String, Hotkey> getRegisteredHotkeys() {
        return new HashMap<>(registeredHotkeys);
    }

    // Implementation methods
    private void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
            logger.info("Opened URL: {}", url);
        } catch (Exception e) {
            logger.error("Failed to open URL: {}", url, e);
        }
    }

    private void launchApplication(String path) {
        try {
            new ProcessBuilder(path).start();
            logger.info("Launched application: {}", path);
        } catch (IOException e) {
            logger.error("Failed to launch application: {}", path, e);
        }
    }

    private void openFileOrFolder(String path) {
        try {
            Desktop.getDesktop().open(new File(path));
            logger.info("Opened file/folder: {}", path);
        } catch (IOException e) {
            logger.error("Failed to open file/folder: {}", path, e);
        }
    }
}