package com.larrykin.snaptap.controllers;

import com.larrykin.snaptap.enums.ActionType;
import com.larrykin.snaptap.models.Hotkey;
import com.larrykin.snaptap.services.HotkeyManager;
import com.larrykin.snaptap.services.ProfileManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
    private ScrollPane keyboardMapContent;
    @FXML
    private VBox addHotkeyContent;
    @FXML
    private GridPane keyboardGrid1;


    private HotkeyManager hotkeyManager;
    private ProfileManager profileManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hotkeyManager = new HotkeyManager();
        profileManager = new ProfileManager();

        // Example: Create a test hotkey
        setupSampleData();
        runToggle.setText(" ");

        // TODO: Initialize other controls
        setupUIBindings();
    }


  @FXML
  private void switchTab(ActionEvent event) {
      // Get the clicked button
      Button clickedButton = (Button) event.getSource();

      // Remove selected class from all buttons
      dashboardTab.getStyleClass().remove("selected-tab");
      keyboardMapTab.getStyleClass().remove("selected-tab");
      addHotkeyTab.getStyleClass().remove("selected-tab");

      // Add selected class to clicked button
      clickedButton.getStyleClass().add("selected-tab");

      // Hide all content panes
      dashboardContent.setVisible(false);
      keyboardGrid1.setVisible(false); // Updated reference
      addHotkeyContent.setVisible(false);

      // Show the appropriate content based on which button was clicked
      if (clickedButton == dashboardTab) {
          dashboardContent.setVisible(true);
      } else if (clickedButton == keyboardMapTab) {
          keyboardGrid1.setVisible(true); // Updated reference
      } else if (clickedButton == addHotkeyTab) {
          addHotkeyContent.setVisible(true);
      }
  }

    private void setupUIBindings() {
        // Bind profile selector
        profileCombo.getItems().addAll("Default", "Gaming", "Productivity");
        profileCombo.setValue("Default");

        // Configure toggle button
        runToggle.setSelected(true);
        runToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            // Status button changes
            statusButton.setText(newVal ? "Running" : "Stopped");
            statusButton.setStyle("-fx-background-color: " + (newVal ? "#2A9D8F" : "#8D8D8D"));

            // Stop/Start button changes
            stopServiceBtn.setText(newVal ? "Stop Service" : "Start Service");
            stopServiceBtn.setStyle("-fx-background-color: " + (newVal ? "#dc3545" : "#2A9D8F"));
        });

        // Configure stop button
        stopServiceBtn.setOnAction(e -> runToggle.setSelected(false));

        // Update active hotkeys count
        activeHotkeysLabel.setText("1/1");
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