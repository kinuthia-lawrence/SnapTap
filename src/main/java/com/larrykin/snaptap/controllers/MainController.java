package com.larrykin.snaptap.controllers;

import com.larrykin.snaptap.enums.ActionType;
import com.larrykin.snaptap.models.Hotkey;
import com.larrykin.snaptap.models.Profile;
import com.larrykin.snaptap.services.HotkeyManager;
import com.larrykin.snaptap.services.ProfileManager;
import com.larrykin.snaptap.utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

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
    private Button statusButton;

    @FXML
    private Label uptimeLabel;

    @FXML
    private Label activeHotkeysLabel;

    @FXML
    private Button stopServiceBtn;

    @FXML
    private TextField searchField;
    @FXML
    private Button dashboardTab;
    @FXML
    private Button keyboardMapTab;
    @FXML
    private Button addHotkeyTab;
    @FXML
    private VBox dashboardContent;

    @FXML
    private VBox addHotkeyContent;

    @FXML
    private VBox keyboardMapContent;

    private HotkeyManager hotkeyManager;
    private ProfileManager profileManager;
    private Map<String, VBox> hotkeyCards = new HashMap<>();
    private int[] usageCounts = new int[100]; // Simple usage tracking for demo

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hotkeyManager = new HotkeyManager();
        profileManager = new ProfileManager();

        setupUIBindings();
        setupSampleData();
        loadHotkeysFromCurrentProfile();

        runToggle.setText(" ");
        addProfileBtn.setOnAction(e -> showAddProfileDialog());
        setupSearchFunctionality();
    }


    private void showAddProfileDialog() {
        // Create a custom dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add New Profile");
        dialog.setHeaderText("Add New Profile");

        // Get the DialogPane and set preferred size
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefSize(450, 250);

        // Create content
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(30, 25, 20, 25));
        grid.setMaxWidth(Double.MAX_VALUE);

        TextField profileName = new TextField();
        profileName.setPrefHeight(40);
        profileName.setPromptText("e.g., Gaming, Office, Coding");

        Label nameLabel = new Label("Profile Name:");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        grid.add(nameLabel, 0, 0);
        grid.add(profileName, 0, 1);
        GridPane.setColumnSpan(profileName, 2);
        GridPane.setFillWidth(profileName, true);

        // Set content
        dialogPane.setContent(grid);

        // Style the dialog according to current theme
        boolean isDarkMode = ThemeManager.loadThemeState();
        String backgroundColor = isDarkMode ? "#212529" : "#F9FAFB";
        String secondaryBgColor = isDarkMode ? "#383e46" : "#FFFFFF";
        String textColor = isDarkMode ? "#ffffff" : "#333333";
        String borderColor = isDarkMode ? "#5D6A76" : "#E0E0E0";

        // Apply style to dialog
        dialogPane.setStyle(
                "-fx-background-color: " + backgroundColor + ";"
        );

        // Style dialog header
        dialogPane.lookupAll(".header-panel").forEach(node ->
                node.setStyle("-fx-background-color: " + secondaryBgColor + ";" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-width: 0 0 1 0;"));

        dialogPane.lookupAll(".header-panel .label").forEach(node ->
                node.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: 18px; -fx-font-weight: bold;"));

        // Style text field
        profileName.setStyle(
                "-fx-background-color: " + secondaryBgColor + ";" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-radius: 4px;"
        );

        // Style content text including labels
        dialogPane.lookupAll(".content .label").forEach(node ->
                node.setStyle("-fx-text-fill: " + textColor + ";"));

        // Style buttons
        dialogPane.lookupAll(".button").forEach(node -> {
            String buttonStyle = "-fx-background-color: " + (isDarkMode ? "#4A555E" : "#F0F2F4") + ";" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-background-radius: 5px;";
            node.setStyle(buttonStyle);
        });

        // Add buttons
        ButtonType createButtonType = new ButtonType("Create Profile", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Style create button specially
        Button createButton = (Button) dialogPane.lookupButton(createButtonType);
        createButton.setStyle(
                "-fx-background-color: #2A9D8F;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-padding: 10px 20px;"
        );

        // Set result converter
        dialog.setResultConverter(buttonType -> {
            if (buttonType == createButtonType) {
                return profileName.getText();
            }
            return null;
        });

        // Show dialog and handle result
        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                Profile newProfile = profileManager.createProfile(name.trim());
                profileCombo.getItems().add(newProfile.getName());
                profileCombo.setValue(newProfile.getName());
            }
        });
    }

    @FXML
    private void switchTab(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        dashboardTab.getStyleClass().remove("selected-tab");
        keyboardMapTab.getStyleClass().remove("selected-tab");
        addHotkeyTab.getStyleClass().remove("selected-tab");

        clickedButton.getStyleClass().add("selected-tab");

        dashboardContent.setVisible(false);
        keyboardMapContent.setVisible(false);
        addHotkeyContent.setVisible(false);

        if (clickedButton == dashboardTab) {
            dashboardContent.setVisible(true);
        } else if (clickedButton == keyboardMapTab) {
            keyboardMapContent.setVisible(true);
        } else if (clickedButton == addHotkeyTab) {
            addHotkeyContent.setVisible(true);
        }
    }

    private void setupUIBindings() {
        List<Profile> allProfiles = profileManager.getAllProfiles();
        profileCombo.getItems().clear();
        allProfiles.forEach(profile -> profileCombo.getItems().add(profile.getName()));

        profileCombo.setValue("Default");

        profileCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                for (Profile profile : allProfiles) {
                    if (profile.getName().equals(newValue)) {
                        profileManager.setActiveProfile(profile.getId());
                        loadHotkeysFromCurrentProfile();
                        break;
                    }
                }
            }
        });

        runToggle.setSelected(true);
        runToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            statusButton.setText(newVal ? "Running" : "Stopped");
            statusButton.setStyle("-fx-background-color: " + (newVal ? "#2A9D8F" : "#8D8D8D"));
            stopServiceBtn.setText(newVal ? "Stop Service" : "Start Service");
            stopServiceBtn.setStyle("-fx-background-color: " + (newVal ? "#dc3545" : "#2A9D8F"));
        });

        stopServiceBtn.setOnAction(e -> runToggle.setSelected(false));
    }

    private void setupSampleData() {
        Profile activeProfile = profileManager.getActiveProfile();

        // If no active profile exists yet, wait until after profile setup
        if (activeProfile == null) {
            return;
        }

        Hotkey googleHotkey = new Hotkey(
                UUID.randomUUID().toString(),
                "Open Google",
                "Ctrl+Alt+G",
                ActionType.URL,
                "https://www.google.com"
        );

        // Add to profile and register with hotkey manager
        activeProfile.addHotkey(googleHotkey);
        profileManager.saveProfile(activeProfile);
        hotkeyManager.registerHotkey(googleHotkey);

        logger.info("Sample hotkey created and added to profile: {}", googleHotkey.getName());
    }

    private void loadHotkeysFromCurrentProfile() {
        hotkeyCardContainer.getChildren().clear();
        hotkeyCards.clear();

        Profile activeProfile = profileManager.getActiveProfile();
        if (activeProfile != null) {
            activeHotkeysLabel.setText(activeProfile.getHotkeys().size() + "/" + activeProfile.getHotkeys().size());

            if (activeProfile.getHotkeys().isEmpty()) {
                Label noHotkeysLabel = new Label("No hotkeys in this profile. Add some hotkeys!");
                noHotkeysLabel.getStyleClass().add("status-hint");
                hotkeyCardContainer.getChildren().add(noHotkeysLabel);
            } else {
                for (Hotkey hotkey : activeProfile.getHotkeys()) {
                    VBox card = createHotkeyCard(hotkey);
                    hotkeyCardContainer.getChildren().add(card);
                    hotkeyCards.put(hotkey.getId(), card);
                }
            }
        } else {
            Label noProfileLabel = new Label("No active profile found. Please create a profile.");
            noProfileLabel.getStyleClass().add("status-hint");
            hotkeyCardContainer.getChildren().add(noProfileLabel);
            logger.warn("No active profile found when trying to load hotkeys");
        }
    }

    private VBox createHotkeyCard(Hotkey hotkey) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));

        HBox topSection = new HBox(10);
        topSection.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox titleSection = new VBox(5);
        HBox.setHgrow(titleSection, Priority.ALWAYS);
        Label titleLabel = new Label(hotkey.getName());
        titleLabel.getStyleClass().add("card-title");

        HBox actionBox = new HBox(5);
        actionBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        FontIcon icon = new FontIcon();

        switch (hotkey.getActionType()) {
            case URL -> icon.setIconLiteral("fas-globe");
            case APPLICATION -> icon.setIconLiteral("fas-laptop");
            case FILE_FOLDER -> icon.setIconLiteral("fas-folder");
        }
        icon.setIconSize(12);

        Label actionLabel = new Label(hotkey.getActionData());
        actionLabel.getStyleClass().add("card-subtitle");
        actionBox.getChildren().addAll(icon, actionLabel);

        titleSection.getChildren().addAll(titleLabel, actionBox);

        ToggleButton enabledToggle = new ToggleButton("Enabled");
        enabledToggle.getStyleClass().add("enabled-toggle");
        enabledToggle.setSelected(hotkey.isEnabled());
        enabledToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            hotkey.setEnabled(newVal);
            profileManager.updateHotkey(profileManager.getActiveProfile().getId(), hotkey);
        });

        Button deleteButton = new Button();
        deleteButton.getStyleClass().add("delete-button");
        FontIcon deleteIcon = new FontIcon("fas-trash");
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setOnAction(e -> {
            Profile profile = profileManager.getActiveProfile();
            profile.getHotkeys().removeIf(h -> h.getId().equals(hotkey.getId()));
            profileManager.saveProfile(profile);
            hotkeyCardContainer.getChildren().remove(card);
            hotkeyCards.remove(hotkey.getId());
        });

        topSection.getChildren().addAll(titleSection, enabledToggle, deleteButton);

        Separator separator = new Separator();

        HBox bottomSection = new HBox(10);
        bottomSection.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox keysBox = new HBox(5);
        keysBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(keysBox, Priority.ALWAYS);

        String[] keys = hotkey.getKeyCombo().split("\\+");
        for (String key : keys) {
            Label keyLabel = new Label(key.trim());
            keyLabel.getStyleClass().add("key-label");
            keysBox.getChildren().add(keyLabel);
        }

        int index = Math.abs(hotkey.getId().hashCode() % 100);
        Button usageButton = new Button("Used " + usageCounts[index] + " times");
        usageButton.getStyleClass().add("usage-button");

        bottomSection.getChildren().addAll(keysBox, usageButton);

        card.getChildren().addAll(topSection, separator, bottomSection);

        return card;
    }

    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterHotkeys(newValue);
        });
    }

    private void filterHotkeys(String query) {
        hotkeyCardContainer.getChildren().clear();
        String lowerCaseQuery = query.toLowerCase();

        Profile activeProfile = profileManager.getActiveProfile();
        if (activeProfile != null) {
            activeProfile.getHotkeys().stream()
                    .filter(hotkey -> hotkey.getName().toLowerCase().contains(lowerCaseQuery) || hotkey.getActionData().toLowerCase().contains(lowerCaseQuery))
                    .forEach(hotkey -> {
                        VBox card = createHotkeyCard(hotkey);
                        hotkeyCardContainer.getChildren().add(card);
                    });
        }
    }
}