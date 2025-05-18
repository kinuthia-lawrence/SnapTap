package com.larrykin.snaptap.utils;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
/**
 * Manages the system tray functionality for the application, including adding a tray icon
 * and handling tray menu actions.
 */
public class TrayManager {
    private static final Logger logger = LoggerFactory.getLogger(TrayManager.class);
    private TrayIcon trayIcon;
    private Stage mainStage;

    /**
     * Initializes the TrayManager with the main application stage and sets up the system tray.
     *
     * @param stage The main application stage.
     */
    public TrayManager(Stage stage) {
        this.mainStage = stage;
        setupTray();
    }

    /**
     * Sets up the system tray icon and menu. Adds functionality for showing the application
     * and exiting it through the tray menu.
     */
    private void setupTray() {
        if (!SystemTray.isSupported()) {
            logger.warn("System tray not supported");
            return;
        }

        Image image;
        SystemTray tray = SystemTray.getSystemTray();
        URL iconUrl = getClass().getResource("/images/icon.png");
        if (iconUrl == null) {
            logger.error("Tray icon image not found");
            // Use a default toolkit icon or create a simple one
            image = createDefaultImage();
        } else {
            image = Toolkit.getDefaultToolkit().getImage(iconUrl);
        }

        PopupMenu popup = new PopupMenu();
        MenuItem showItem = new MenuItem("Show");
        MenuItem exitItem = new MenuItem("Exit");

        // Action to show the main application window
        showItem.addActionListener(e -> Platform.runLater(() -> {
            mainStage.show();
            mainStage.toFront();
        }));

        // Action to exit the application
        exitItem.addActionListener(e -> {
            Platform.exit();
            System.exit(0);
        });

        popup.add(showItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon = new TrayIcon(image, "SnapTap", popup);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            logger.error("Could not add to system tray", e);
        }
    }

    /**
     * Creates a default 16x16 black image to use as a fallback tray icon.
     *
     * @return A simple 16x16 black image.
     */
    private Image createDefaultImage() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, 16, 16);
        g2d.dispose();
        return image;
    }
}