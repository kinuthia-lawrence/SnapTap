package com.larrykin.snaptap.utils;

import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ThemeManager {

    private static final String LIGHT_MODE = "/styles/light-mode.css";
    private static final String DARK_MODE = "/styles/dark-mode.css";

    private static final Logger log = LoggerFactory.getLogger(ThemeManager.class);


    // Apply the theme to the scene
    public static void applyTheme(Scene scene, boolean isDarkMode) {
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(DARK_MODE)).toExternalForm());
            log.info("Dark-mode applied");
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(ThemeManager.class.getResource(LIGHT_MODE)).toExternalForm());
            log.info( "light-mode applied");
        }
    }

    public static boolean loadThemeState() {
        return true;
    }

    public static void updateThemeState(boolean isDarkMode) {
        //TODO: Update Themestate
    }
}
