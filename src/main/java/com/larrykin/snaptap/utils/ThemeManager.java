package com.larrykin.snaptap.utils;

import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.prefs.Preferences;

public class ThemeManager {

    private static final String LIGHT_MODE = "/styles/light-mode.css";
    private static final String DARK_MODE = "/styles/dark-mode.css";


    private static final Logger log = LoggerFactory.getLogger(ThemeManager.class);


    /**
     * Toggles between light and dark theme
     *
     * @param scene The scene to apply the theme to
     */
    public static void toggleTheme(Scene scene) {
        // Get current theme state
        boolean isDarkMode = loadThemeState();

        // Toggle the theme
        isDarkMode = !isDarkMode;

        // Apply the new theme
        applyTheme(scene, isDarkMode);

        // Save the new theme state
        saveThemeState(isDarkMode);
    }

    /**
     * Applies the specified theme to the scene
     *
     * @param scene      The scene to apply the theme to
     * @param isDarkMode True for dark mode, false for light mode
     */
    public static void applyTheme(Scene scene, boolean isDarkMode) {
        if (scene == null) return;

        if (isDarkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(DARK_MODE)).toExternalForm());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(LIGHT_MODE)).toExternalForm());
        }
    }

    /**
     * Saves the current theme state to preferences
     *
     * @param isDarkMode True for dark mode, false for light mode
     */
    private static void saveThemeState(boolean isDarkMode) {
        Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
        prefs.putBoolean("darkMode", isDarkMode);
    }

    /**
     * Loads the saved theme state from preferences
     *
     * @return True if dark mode is enabled, false otherwise
     */
    public static boolean loadThemeState() {
        Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
        return prefs.getBoolean("darkMode", true); // Default to dark mode
    }
}
