package com.larrykin.snaptap.controllers;

import com.larrykin.snaptap.enums.ActionType;
import com.larrykin.snaptap.models.Hotkey;
import com.larrykin.snaptap.models.Profile;
import com.larrykin.snaptap.services.HotkeyManager;
import com.larrykin.snaptap.services.ProfileManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
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
}