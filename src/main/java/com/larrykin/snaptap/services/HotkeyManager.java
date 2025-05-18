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

/**
 * HotkeyManager is a singleton class responsible for managing hotkeys in the application.
 * It handles the registration, execution, and validation of hotkeys.
 */
public class HotkeyManager {
    // Logger for logging messages
    private static final Logger logger = LoggerFactory.getLogger(HotkeyManager.class);

    // Singleton instance of HotkeyManager
    private static HotkeyManager instance;

    // Map to store registered hotkeys with their IDs
    private final Map<String, Hotkey> registeredHotkeys = new HashMap<>();

    // Reflects the active profile status
    private boolean masterSwitch = true;

    // Indicates if the system is running in the background
    private boolean systemRunning = true;

    // MainController instance for UI updates
    private MainController mainController;


    // Private constructor to prevent instantiation
    private HotkeyManager() {
    }


    /**
     * Returns the singleton instance of HotkeyManager.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    /**
     * Sets the MainController instance for UI updates.
     */
    public static synchronized HotkeyManager getInstance() {
        if (instance == null) {
            instance = new HotkeyManager();
        }
        return instance;
    }

    /**
     * Updates the master switch status.
     *
     * @param isActive Whether the master switch is active.
     */
    public void setMasterSwitch(boolean isActive) {
        this.masterSwitch = isActive;
        logger.info("Master switch set to: {}", isActive ? "Active" : "Inactive");
    }

    /**
     * Updates the system running status.
     *
     * @param isRunning Whether the system is running.
     */
    public void setSystemRunning(boolean isRunning) {
        this.systemRunning = isRunning;
        logger.info("System running status set to: {}", isRunning ? "Running" : "Stopped");
    }

    /**
     * Registers a hotkey if it is valid and not a duplicate.
     *
     * @param hotkey The hotkey to register.
     */
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

    /**
     * Returns the map of registered hotkeys.
     */
    public Map<String, Hotkey> getRegisteredHotkeys() {
        return registeredHotkeys;
    }

    /**
     * Executes the action associated with a hotkey.
     *
     * @param hotkey The hotkey to execute.
     */
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

    /**
     * Opens a URL in the default browser.
     *
     * @param url The URL to open.
     */
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

    /**
     * Launches an application using the provided path.
     *
     * @param application The path to the application.
     */
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

    /**
     * Opens a file or folder using the default file manager.
     *
     * @param path The path to the file or folder.
     */
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