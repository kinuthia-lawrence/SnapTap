package com.larrykin.snaptap.controllers;

import com.larrykin.snaptap.enums.ActionType;
import com.larrykin.snaptap.models.Hotkey;
import com.larrykin.snaptap.services.HotkeyManager;
import com.larrykin.snaptap.services.ProfileManager;
import com.larrykin.snaptap.utils.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class MainController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private TableView<Hotkey> hotkeyTable;

    @FXML
    private VBox hotkeyCardContainer;

    @FXML
    private ComboBox<String> profileCombo;

    @FXML
    private Button addProfileBtn;

    @FXML
    private ToggleButton runToggle;

    @FXML
    private Label statusLabel;

    @FXML
    private Label uptimeLabel;

    @FXML
    private Label activeHotkeysLabel;

    @FXML
    private Button stopServiceBtn;

    @FXML
    private TextField searchField;




    private HotkeyManager hotkeyManager;
    private ProfileManager profileManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hotkeyManager = new HotkeyManager();
        profileManager = new ProfileManager();

        // Example: Create a test hotkey
        setupSampleData();

        // TODO: Initialize other controls
        setupUIBindings();
    }

    private void setupUIBindings() {
        // Bind profile selector
        profileCombo.getItems().addAll("Default", "Gaming", "Productivity");
        profileCombo.setValue("Default");

        // Configure toggle button
        runToggle.setSelected(true);
        runToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            statusLabel.setText(newVal ? "Running" : "Stopped");
            statusLabel.getStyleClass().removeAll("status-active", "status-inactive");
            statusLabel.getStyleClass().add(newVal ? "status-active" : "status-inactive");
        });

        // Configure stop button
        stopServiceBtn.setOnAction(e -> runToggle.setSelected(false));

        // Update active hotkeys count
        activeHotkeysLabel.setText("1/1");
    }

    private void showHelpDialog() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("SnapTap Help");
        helpAlert.setHeaderText("SnapTap Keyboard Shortcut Manager");
        helpAlert.setContentText("This application allows you to create and manage custom keyboard shortcuts.\n\n" +
                "For more information, visit the documentation website.");
        helpAlert.showAndWait();
    }

    private void setupSampleData() {
        Hotkey googleHotkey = new Hotkey(
                UUID.randomUUID().toString(),
                "Open Google",
                "Ctrl+Alt+G",
                ActionType.URL,
                "https://www.google.com"
        );
        hotkeyManager.registerHotkey(googleHotkey);
        logger.info("Sample hotkey created: {}", googleHotkey.getName());
    }
}