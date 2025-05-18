package com.larrykin.snaptap.services;

import com.larrykin.snaptap.controllers.MainController;
import com.larrykin.snaptap.models.Hotkey;
import com.larrykin.snaptap.models.Profile;
import javafx.application.Platform;
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
    private static HotkeyManager instance; // Singleton instance
    private final Map<String, Hotkey> registeredHotkeys = new HashMap<>();
    private boolean masterSwitch = true; // Reflects the active profile status
    private boolean systemRunning = true; // Indicates if the system is running in the background
    private MainController mainController;

    // Private constructor to prevent instantiation
    private HotkeyManager() {
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // Public method to get the singleton instance
    public static synchronized HotkeyManager getInstance() {
        if (instance == null) {
            instance = new HotkeyManager();
        }
        return instance;
    }

    public void setMasterSwitch(boolean isActive) {
        this.masterSwitch = isActive;
        logger.info("Master switch set to: {}", isActive ? "Active" : "Inactive");
    }

    public void setSystemRunning(boolean isRunning) {
        this.systemRunning = isRunning;
        logger.info("System running status set to: {}", isRunning ? "Running" : "Stopped");
    }

    public void registerHotkey(Hotkey hotkey) {
        if (hotkey.getKeyCombo().split("\\+").length < 2) {
            logger.error("Hotkey '{}' is invalid. A hotkey must consist of at least two keys.", hotkey.getName());
            return;
        }

        if (registeredHotkeys.values().stream()
                .anyMatch(existingHotkey -> existingHotkey.getKeyCombo().equals(hotkey.getKeyCombo()))) {
            logger.error("Hotkey '{}' is invalid. Duplicate key combination: {}", hotkey.getName(), hotkey.getKeyCombo());
            return;
        }

        registeredHotkeys.put(hotkey.getId(), hotkey);
        logger.info("Registered hotkey: {} with combination: {}", hotkey.getName(), hotkey.getKeyCombo());
    }

    public Map<String, Hotkey> getRegisteredHotkeys() {
        return registeredHotkeys;
    }

    public void executeAction(Hotkey hotkey) {
        logger.info("Executing action for hotkey: {}", hotkey.getName());
        if (!masterSwitch || !systemRunning || !hotkey.isEnabled()) {
            logger.warn("Action execution skipped. Master switch: {}, System running: {}, Hotkey enabled: {}",
                    masterSwitch, systemRunning, hotkey.isEnabled());
            return;
        }

        boolean actionSuccess = false;

        switch (hotkey.getActionType()) {
            case URL -> actionSuccess = openUrl(hotkey.getActionData());
            case APPLICATION -> actionSuccess = launchApplication(hotkey.getActionData());
            case FILE_FOLDER -> actionSuccess = openFileOrFolder(hotkey.getActionData());
        }

        if (actionSuccess) {
            hotkey.incrementUsageCount();
            logger.info("Hotkey '{}' usage count incremented to {}", hotkey.getName(), hotkey.getUsageCount());

            // Save the updated profile
            ProfileManager profileManager = new ProfileManager();
            Profile activeProfile = profileManager.getActiveProfile();
            if (activeProfile != null) {
                profileManager.updateHotkey(activeProfile.getId(), hotkey);
                if (mainController != null) {
                    Platform.runLater(() -> {
                        mainController.reloadData();
                        logger.info("Reloaded data in MainController after hotkey action.");
                    });
                } else {
                    logger.warn("MainController instance is not set. Unable to reload data.");
                }
            }
        }
    }

    private boolean openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
            logger.info("Opened URL: {}", url);
            return true;
        } catch (Exception e) {
            logger.error("Failed to open URL: {}", url, e);
            return false;
        }
    }

    private boolean launchApplication(String application) {
        try {
            logger.info("Launching application: {}", application);
            Runtime.getRuntime().exec(application);
            logger.info("Launched application: {}", application);
            return true;
        } catch (IOException e) {
            logger.error("Failed to launch application: {}", application, e);
            return false;
        }
    }

    private boolean openFileOrFolder(String path) {
        try {
            logger.info("Opening file/folder: {}", path);
            Desktop.getDesktop().open(new File(path));
            logger.info("Opened file/folder: {}", path);
            return true;
        } catch (IOException e) {
            logger.error("Failed to open file/folder: {}", path, e);
            return false;
        }
    }
}