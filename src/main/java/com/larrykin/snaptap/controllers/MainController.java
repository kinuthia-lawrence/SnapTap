package com.larrykin.snaptap.controllers;

    import com.larrykin.snaptap.enums.ActionType;
    import com.larrykin.snaptap.models.Hotkey;
    import com.larrykin.snaptap.services.HotkeyManager;
    import com.larrykin.snaptap.services.ProfileManager;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Label;
    import javafx.scene.control.TableView;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.net.URL;
    import java.util.ResourceBundle;
    import java.util.UUID;

    public class MainController implements Initializable {
        private static final Logger logger = LoggerFactory.getLogger(MainController.class);

        @FXML
        private Label welcomeText;

        @FXML
        private TableView<Hotkey> hotkeyTable;

        private HotkeyManager hotkeyManager;
        private ProfileManager profileManager;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            hotkeyManager = new HotkeyManager();
            profileManager = new ProfileManager();

            welcomeText.setText("Welcome to SnapTap Hotkey Manager");

            // Example: Create a test hotkey
            setupSampleData();
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
        }

        @FXML
        protected void onHelloButtonClick() {
            welcomeText.setText("SnapTap is running in the system tray");
            // Minimize to system tray
        }
    }