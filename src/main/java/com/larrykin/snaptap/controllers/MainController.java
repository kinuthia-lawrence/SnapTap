package com.larrykin.snaptap.controllers;

import com.larrykin.snaptap.enums.ActionType;
import com.larrykin.snaptap.models.Hotkey;
import com.larrykin.snaptap.models.Profile;
import com.larrykin.snaptap.services.HotkeyManager;
import com.larrykin.snaptap.services.ProfileManager;
import com.larrykin.snaptap.utils.ThemeManager;
import com.larrykin.snaptap.utils.Utilities;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

/**
 * Controller class for managing the main application UI and its interactions.
 * Implements the `Initializable` interface to set up the UI components and bindings.
 */
public class MainController implements Initializable {
    /**
     * Logger for logging events and errors
     */
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    //* FXML components    */
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
    @FXML
    private FontIcon actionIcon;

    public Label hotkeyPreview;
    public Button saveHotkeyBtn;
    public TextField actionField;
    public ComboBox actionTypeCombo;
    public GridPane keyboardGrid;
    public TextField nameField;
    public ToggleButton ctrlToggle;
    public ToggleButton altToggle;
    public ToggleButton shiftToggle;
    public ToggleButton winToggle;
    private HotkeyManager hotkeyManager;
    private final ProfileManager profileManager = new ProfileManager();
    private Map<String, VBox> hotkeyCards = new HashMap<>();
    private Timeline uptimeTimeline;
    private long startTime = 0;
    private long elapsedTimeMillis = 0;
    private boolean running = false;
    private ToggleGroup keyboardToggleGroup = new ToggleGroup();

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up the UI components, binds data, and prepares the application for user interaction.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if not known.
     * @param resources The resources used to localize the root object, or null if not available.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hotkeyManager = HotkeyManager.getInstance();
        hotkeyManager.setMainController(this);

        Profile activeProfile = profileManager.getActiveProfile();
        if (activeProfile != null) {
            hotkeyManager.setMasterSwitch(activeProfile.isActive());
            profileManager.registerProfileHotkeys(activeProfile);
        }
        updateUI(profileManager.getActiveProfile());


        // initialize timer
        setupUptimeTimer();
        setupUIBindings();
        setupSampleData();
        loadHotkeysFromCurrentProfile();

        runToggle.setText(" ");
        setListeners();
        setupSearchFunctionality();
        setupKeyboardToggleGroup();
        setupSaveButtonValidation();
    }

    /**
     * Sets up listeners for various UI components, including buttons, toggle buttons,
     * and combo boxes, to handle user interactions and update the application state accordingly.
     */
    private void setListeners() {

        //* Profile btn listener
        addProfileBtn.setOnAction(e -> showAddProfileDialog());

        //* Background service toggle button listener
        runToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Profile activeProfile = profileManager.getActiveProfile();
            if (activeProfile != null) {
                activeProfile.setActive(newVal);
                profileManager.saveProfile(activeProfile);
                updateUI(activeProfile);
            }
        });


        //* Modifiers Toggle button listeners
        ctrlToggle.selectedProperty().addListener((observable, oldValue, newValue) -> updateHotkeyPreview());
        altToggle.selectedProperty().addListener((observable, oldValue, newValue) -> updateHotkeyPreview());
        shiftToggle.selectedProperty().addListener((observable, oldValue, newValue) -> updateHotkeyPreview());
        winToggle.selectedProperty().addListener((observable, oldValue, newValue) -> updateHotkeyPreview());


        //* Action Type ComboBox Listener
        // Set up dynamic prompt text and icons for action field
        actionTypeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switch (newVal.toString()) {
                    case "Website" -> {
                        actionField.setPromptText("https://example.com");
                        actionIcon.setIconLiteral("fas-globe");
                    }
                    case "Application" -> {
                        actionField.setPromptText("notepad or `C:\\Windows\\System32\\notepad.exe` ");
                        actionIcon.setIconLiteral("fas-laptop");
                    }
                    case "File or Folder" -> {
                        actionField.setPromptText("/path/to/file/or/folder");
                        actionIcon.setIconLiteral("fas-folder");
                    }
                }
            }
        });

        // Initialize combobox with cell factory for icons
        actionTypeCombo.setCellFactory(param -> new ListCell<String>() {
            private final FontIcon icon = new FontIcon();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    icon.setIconSize(16);

                    switch (item) {
                        case "Website" -> icon.setIconLiteral("fas-globe");
                        case "Application" -> icon.setIconLiteral("fas-laptop");
                        case "File or Folder" -> icon.setIconLiteral("fas-folder");
                    }

                    setGraphic(icon);
                }
            }
        });

        //  show selected icon
        actionTypeCombo.setButtonCell(new ListCell<String>() {
            private final FontIcon icon = new FontIcon();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    icon.setIconSize(16);

                    switch (item) {
                        case "Website" -> icon.setIconLiteral("fas-globe");
                        case "Application" -> icon.setIconLiteral("fas-laptop");
                        case "File or Folder" -> icon.setIconLiteral("fas-folder");
                    }

                    setGraphic(icon);
                }
            }
        });

        // Set default selection
        actionTypeCombo.getSelectionModel().selectFirst();
    }


    /**
     * Displays a dialog for adding a new profile.
     * The dialog includes a text field for entering the profile name and buttons for creating or canceling the profile.
     */
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

        // Set column constraints to allow expansion
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);
        grid.getColumnConstraints().addAll(col1);

        Label nameLabel = new Label("Profile Name:");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextField profileName = new TextField();
        profileName.setPrefHeight(40);
        profileName.setPromptText("e.g., Gaming, Office, Coding");
        profileName.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(profileName, Priority.ALWAYS);
        GridPane.setFillWidth(profileName, true);

        grid.add(nameLabel, 0, 0);
        grid.add(profileName, 0, 1);

        // Set content
        dialogPane.setContent(grid);

        // Style the dialog according to current theme
        boolean isDarkMode = ThemeManager.loadThemeState();
        String backgroundColor = isDarkMode ? "#212529" : "#F9FAFB";
        String secondaryBgColor = isDarkMode ? "#383e46" : "#FFFFFF";
        String textColor = isDarkMode ? "#ffffff" : "#333333";
        String borderColor = isDarkMode ? "#5D6A76" : "#E0E0E0";

        // Apply style to dialog
        dialogPane.setStyle("-fx-background-color: " + backgroundColor + ";");

        // Style dialog header
        dialogPane.lookupAll(".header-panel").forEach(node -> node.setStyle("-fx-background-color: " + secondaryBgColor + ";" + "-fx-border-color: " + borderColor + ";" + "-fx-border-width: 0 0 1 0;"));

        dialogPane.lookupAll(".header-panel .label").forEach(node -> node.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: 18px; -fx-font-weight: bold;"));

        // Style text field
        profileName.setStyle("-fx-background-color: " + secondaryBgColor + ";" + "-fx-text-fill: " + textColor + ";" + "-fx-border-color: " + borderColor + ";" + "-fx-border-radius: 4px;");

        // Style content text including labels
        dialogPane.lookupAll(".content .label").forEach(node -> node.setStyle("-fx-text-fill: " + textColor + ";"));

        // Style buttons
        dialogPane.lookupAll(".button").forEach(node -> {
            String buttonStyle = "-fx-background-color: " + (isDarkMode ? "#4A555E" : "#F0F2F4") + ";" + "-fx-text-fill: " + textColor + ";" + "-fx-background-radius: 5px;";
            node.setStyle(buttonStyle);
        });

        // Add buttons
        ButtonType createButtonType = new ButtonType("Create Profile", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Style create button specially
        Button createButton = (Button) dialogPane.lookupButton(createButtonType);
        createButton.setStyle("-fx-background-color: #2A9D8F;" + "-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-background-radius: 5px;" + "-fx-padding: 10px 20px;");

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
                profileManager.setActiveProfile(newProfile.getId());
                reloadData();
            }
        });
    }

    /**
     * Switches between different tabs in the application.
     * Updates the visibility of content sections based on the selected tab.
     *
     * @param event The action event triggered by clicking a tab button.
     */
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
            updateKeyboardHeatmap();
        } else if (clickedButton == addHotkeyTab) {
            addHotkeyContent.setVisible(true);
        }
    }


    /**
     * Sets up a timer to track the application's uptime.
     * The timer updates the uptime label every second and handles start/pause/resume functionality.
     */
    private void setupUptimeTimer() {
        // Create a timeline that updates every second
        uptimeTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateUptimeLabel()));
        uptimeTimeline.setCycleCount(Animation.INDEFINITE);

        // Start the timer when application launches
        startTime = System.currentTimeMillis();
        uptimeTimeline.play();
        running = true;

        // Add listeners to toggle and stop button
        runToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                resumeTimer();
            } else {
                pauseTimer();
            }
        });

        stopServiceBtn.setOnAction(e -> {
            if (runToggle.isSelected()) {
                runToggle.setSelected(false);
                pauseTimer();
            } else {
                runToggle.setSelected(true);
                resumeTimer();
            }
        });
    }

    /**
     * Updates the uptime label with the formatted elapsed time.
     * The time is displayed in HH:MM:SS format.
     */
    private void updateUptimeLabel() {
        if (running) {
            long currentTime = System.currentTimeMillis();
            elapsedTimeMillis = (currentTime - startTime) + elapsedTimeMillis;
            startTime = currentTime;
        }

        // Format the time as HH:MM:SS
        long seconds = elapsedTimeMillis / 1000;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        uptimeLabel.setText(timeString);
    }

    /**
     * Pauses the uptime timer and updates the elapsed time.
     */
    private void pauseTimer() {
        if (running) {
            uptimeTimeline.pause();
            long currentTime = System.currentTimeMillis();
            elapsedTimeMillis += (currentTime - startTime);
            running = false;
        }
    }

    /**
     * Resumes the uptime timer and updates the start time.
     */
    private void resumeTimer() {
        if (!running) {
            startTime = System.currentTimeMillis();
            uptimeTimeline.play();
            running = true;
        }
    }


    /**
     * Sets up the UI bindings for various components, including profile selection,
     * toggle buttons, and action type combo box.
     */
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
                        reloadData();
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
    }

    /**
     * Sets up sample data for the application.
     * This includes creating a default profile and adding sample hotkeys if no profiles exist.
     */
    private void setupSampleData() {
        // Check if there are no other profiles
        List<Profile> allProfiles = profileManager.getAllProfiles();
        if (allProfiles.size() > 1) {
            return;
        }

        // Get the active profile (Default profile)
        Profile activeProfile = profileManager.getActiveProfile();
        if (activeProfile == null || !activeProfile.getName().equals("Default")) {
            return;
        }

        // Check if the Default profile is empty
        if (!activeProfile.getHotkeys().isEmpty()) {
            return;
        }

        // Create the "Open Google" hotkey
        Hotkey googleHotkey = new Hotkey(UUID.randomUUID().toString(), "Open Google", "Ctrl+G", ActionType.URL, "https://www.google.com");

        // Create the "Open IntelliJ" hotkey
        Hotkey intellijHotkey = new Hotkey(UUID.randomUUID().toString(), "Command Prompt", "Ctrl+Shift+C", ActionType.APPLICATION, "cmd");

        // Add hotkeys to the profile and register them
        activeProfile.addHotkey(googleHotkey);
        activeProfile.addHotkey(intellijHotkey);
        profileManager.saveProfile(activeProfile);
        hotkeyManager.registerHotkey(googleHotkey);
        hotkeyManager.registerHotkey(intellijHotkey);

        logger.info("Sample hotkeys created and added to profile: {}, {}", googleHotkey.getName(), intellijHotkey.getName());
    }

    /**
     * Loads hotkeys from the currently active profile and displays them in the UI.
     * Clears existing hotkey cards and creates new ones based on the active profile's hotkeys.
     */
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

        updateKeyboardHeatmap();
    }

    /**
     * Creates a card UI element for a hotkey.
     * The card displays the hotkey's name, action type, key combination, and usage count.
     *
     * @param hotkey The hotkey object to be displayed in the card.
     * @return A VBox containing the hotkey card UI elements.
     */
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
        enabledToggle.setText(hotkey.isEnabled() ? "Enabled" : "Disabled"); // Set initial text based on state
        enabledToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            hotkey.setEnabled(newVal);
            profileManager.updateHotkey(profileManager.getActiveProfile().getId(), hotkey);
            enabledToggle.setText(newVal ? "Enabled" : "Disabled"); // Update text based on state
        });

        Button deleteButton = new Button();
        deleteButton.getStyleClass().add("delete-button");
        FontIcon deleteIcon = new FontIcon("fas-trash");
        deleteIcon.setIconColor(javafx.scene.paint.Color.RED); // Set icon color to red
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
        String color = ThemeManager.loadThemeState() ? "rgba(255,255,255,0.2)" : "rgba(0,0,0,0.2)";
        separator.setStyle("-fx-background: " + color + ";");

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


        Button usageButton = new Button("Used " + hotkey.getUsageCount() + " times");
        usageButton.getStyleClass().add("usage-button");

        bottomSection.getChildren().addAll(keysBox, usageButton);

        card.getChildren().addAll(topSection, separator, bottomSection);

        return card;
    }

    /**
     * Sets up the search functionality for filtering hotkeys based on user input.
     * Listens for changes in the search field and updates the displayed hotkeys accordingly.
     */
    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterHotkeys(newValue);
        });
    }

    /**
     * Filters the displayed hotkeys based on the user's search query.
     * Clears existing hotkey cards and creates new ones that match the query.
     *
     * @param query The search query entered by the user.
     */
    private void filterHotkeys(String query) {
        hotkeyCardContainer.getChildren().clear();
        String lowerCaseQuery = query.toLowerCase();

        Profile activeProfile = profileManager.getActiveProfile();
        if (activeProfile != null) {
            activeProfile.getHotkeys().stream().filter(hotkey -> hotkey.getName().toLowerCase().contains(lowerCaseQuery) || hotkey.getActionData().toLowerCase().contains(lowerCaseQuery)).forEach(hotkey -> {
                VBox card = createHotkeyCard(hotkey);
                hotkeyCardContainer.getChildren().add(card);
            });
        }
    }

    /**
     * Updates the keyboard heatmap by applying styles to keys based on their usage count.
     * Keys are highlighted based on how frequently they are used in hotkeys.
     */
    private void updateKeyboardHeatmap() {
        // Find all keyboard keys in the keyboardMapContent
        if (keyboardMapContent != null) {
            // Find all GridPanes that contain keyboard layout
            keyboardMapContent.lookupAll(".keyboard-key").forEach(node -> {
                if (node instanceof Button keyButton) {
                    String keyName = keyButton.getText();

                    // First remove any existing usage styling
                    keyButton.getStyleClass().remove("used-key");
                    keyButton.getStyleClass().remove("frequently-used-key");

                    // Check usage count and apply appropriate style
                    int keyUsageCount = getKeyUsageCount(keyName);
                    if (keyUsageCount > 3) {
                        keyButton.getStyleClass().add("frequently-used-key");
                    } else if (keyUsageCount > 0) {
                        keyButton.getStyleClass().add("used-key");
                    }
                }
            });
        }
    }

    /**
     * Gets the usage count of a specific key across all hotkeys in the active profile.
     * This is used to determine how frequently a key is used in hotkey combinations.
     *
     * @param keyName The name of the key to check (e.g., "Ctrl", "Alt").
     * @return The total usage count of the specified key.
     */
    private int getKeyUsageCount(String keyName) {
        int count = 0;
        Profile activeProfile = profileManager.getActiveProfile();
        if (activeProfile != null) {
            for (Hotkey hotkey : activeProfile.getHotkeys()) {
                if (hotkey.getKeyCombo().contains(keyName) && hotkey.getUsageCount() > 0) {
                    count += hotkey.getUsageCount();
                }
            }
        }
        return count;
    }


    /**
     * Sets up the keyboard toggle group for managing key combinations.
     * Each key button is added to a toggle group to allow for single selection.
     */
    public void setupKeyboardToggleGroup() {
        // Find all ToggleButtons in the keyboardGrid
        keyboardGrid.lookupAll(".toggle-button").forEach(node -> {
            if (node instanceof ToggleButton toggleButton) {
                // Add each button to the toggle group
                toggleButton.setToggleGroup(keyboardToggleGroup);

                // Add listener for selections
                toggleButton.setOnAction(e -> updateHotkeyPreview());
            }
        });
    }


    /**
     * Updates the hotkey preview label based on the selected modifiers and key.
     * This method constructs a string representation of the hotkey combination
     * and updates the preview label accordingly.
     */
    private void updateHotkeyPreview() {
        StringBuilder hotkeyText = new StringBuilder();

        // Check which modifiers are selected
        if (ctrlToggle.isSelected()) hotkeyText.append("Ctrl+");
        if (altToggle.isSelected()) hotkeyText.append("Alt+");
        if (shiftToggle.isSelected()) hotkeyText.append("Shift+");
        if (winToggle.isSelected()) hotkeyText.append("Win+");

        // Add the currently selected key (if any)
        String selectedKey = getSelectedKey();
        if (selectedKey != null && !selectedKey.isEmpty()) {
            hotkeyText.append(selectedKey);
        } else {
            // Remove the trailing "+" if no key is selected
            if (hotkeyText.length() > 0 && hotkeyText.charAt(hotkeyText.length() - 1) == '+') {
                hotkeyText.deleteCharAt(hotkeyText.length() - 1);
            }
        }

        // Update the preview label
        hotkeyPreview.setText(hotkeyText.toString());
    }


    /**
     * Gets the currently selected key from the keyboard grid.
     * This method iterates through the grid to find the selected key button.
     *
     * @return The text of the selected key button, or an empty string if none is selected.
     */
    private String getSelectedKey() {
        // Find the selected key button in the keyboard grid
        for (Node row : keyboardGrid.getChildren()) {
            if (row instanceof HBox) {
                for (Node node : ((HBox) row).getChildren()) {
                    if (node instanceof ToggleButton && ((ToggleButton) node).isSelected()) {
                        return ((ToggleButton) node).getText();
                    }
                }
            }
        }
        return "";
    }

    /**
     * Sets up validation for the save button based on user input in the hotkey creation form.
     * The button is enabled only when all required fields are filled correctly.
     */
    private void setupSaveButtonValidation() {
        saveHotkeyBtn.setDisable(true); // Initially disabled

        // Add listeners to validate fields
        ChangeListener<Object> validationListener = (observable, oldValue, newValue) -> validateSaveButton();

        nameField.textProperty().addListener(validationListener);
        actionField.textProperty().addListener(validationListener);
        actionTypeCombo.getSelectionModel().selectedItemProperty().addListener(validationListener);
        keyboardToggleGroup.selectedToggleProperty().addListener(validationListener);
    }

    /**
     * Validates the save button based on the current state of the input fields.
     * The button is enabled only when all required fields are filled correctly.
     */
    private void validateSaveButton() {
        boolean isNameEmpty = nameField.getText() == null || nameField.getText().trim().isEmpty();
        boolean isActionEmpty = actionField.getText() == null || actionField.getText().trim().isEmpty();
        boolean isActionTypeUnchanged = actionTypeCombo.getSelectionModel().getSelectedItem() == null;
        boolean isHotkeyInvalid = getSelectedKey().isEmpty() || (!ctrlToggle.isSelected() && !altToggle.isSelected() && !shiftToggle.isSelected() && !winToggle.isSelected());

        saveHotkeyBtn.setDisable(isNameEmpty || isActionEmpty || isActionTypeUnchanged || isHotkeyInvalid);
    }

    /**
     * Saves the hotkey created by the user in the hotkey creation form.
     * This method validates the input, creates a new hotkey object, and adds it to the active profile.
     */
    @FXML
    private void saveHotkey() {
        // Create a new hotkey
        String name = nameField.getText().trim();
        String action = actionField.getText().trim();
        String hotkeyCombo = hotkeyPreview.getText();
        String actionType = actionTypeCombo.getSelectionModel().getSelectedItem().toString();
        ActionType type;
        switch (actionType) {
            case "Website" -> type = ActionType.URL;
            case "Application" -> type = ActionType.APPLICATION;
            case "File or Folder" -> type = ActionType.FILE_FOLDER;
            default -> throw new IllegalArgumentException("Unknown action type: " + actionType);
        }

        Hotkey newHotkey = new Hotkey(UUID.randomUUID().toString(), name, hotkeyCombo, type, action);

        // Save the hotkey to the current profile
        Profile activeProfile = profileManager.getActiveProfile();
        if (activeProfile != null) {
            // Check if the hotkey already exists
            boolean hotkeyExists = activeProfile.getHotkeys().stream().anyMatch(hotkey -> hotkey.getKeyCombo().equalsIgnoreCase(newHotkey.getKeyCombo()));

            if (hotkeyExists) {
                // Show error alert if the hotkey already exists
                Utilities.showCustomAlert(Alert.AlertType.ERROR, "Error", "Duplicate Hotkey", "A hotkey with the same key combination already exists in the active profile.");
                return;
            }

            try {
                activeProfile.addHotkey(newHotkey);
                profileManager.saveProfile(activeProfile);
                reloadData();

                // Show success alert
                Utilities.showCustomAlert(
                        Alert.AlertType.INFORMATION,
                        "Success",
                        "Hotkey Saved",
                        "The hotkey has been successfully saved."
                );
            } catch (Exception e) {
                logger.error("An error occurred while saving the hotkey: {}", e.getMessage());
                Utilities.showCustomAlert(
                        Alert.AlertType.ERROR,
                        "Error",
                        "Failed to Save Hotkey",
                        "An error occurred while saving the hotkey. Please try again."
                );
                return;
            }

            // Clear fields and preview
            nameField.clear();
            actionField.clear();
            actionTypeCombo.getSelectionModel().selectFirst();
            hotkeyPreview.setText("");
            keyboardToggleGroup.getToggles().forEach(toggle -> toggle.setSelected(false));
            ctrlToggle.setSelected(false);
            altToggle.setSelected(false);
            shiftToggle.setSelected(false);
            winToggle.setSelected(false);

            validateSaveButton(); // Revalidate the button
        }
    }


    /**
     * Updates the UI components based on the current profile's state.
     * This includes updating the active hotkeys label and the run toggle button.
     *
     * @param profile The current profile to update the UI for.
     */
    private void updateUI(Profile profile) {
        if (profile != null) {
            Platform.runLater(() -> {
                activeHotkeysLabel.setText(profile.getHotkeys().size() + "/" + profile.getHotkeys().size());
                runToggle.setSelected(profile.isActive());
            });
        }
    }

    /**
     * Reloads the data in the MainController.
     * This method updates the active profile, reloads hotkeys, and refreshes the UI components.
     */
    public void reloadData() {
        Profile activeProfile = profileManager.getActiveProfile();
        if (activeProfile != null) {
            // Update the master switch in HotkeyManager
            hotkeyManager.setMasterSwitch(activeProfile.isActive());

            // Unregister all existing hotkeys
            hotkeyManager.getRegisteredHotkeys().clear();

            // Re-register all hotkeys from the active profile
            profileManager.registerProfileHotkeys(activeProfile);

            // Update the UI components
            updateUI(activeProfile);
            loadHotkeysFromCurrentProfile();
            updateKeyboardHeatmap();
            logger.info("Reloaded data in MainController for profile: {}", activeProfile.getName());
        } else {
            logger.warn("No active profile found during reload");
        }
    }
}