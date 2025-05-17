package com.larrykin.snaptap;

import com.larrykin.snaptap.controllers.MainController;
import com.larrykin.snaptap.utils.ThemeManager;
import com.larrykin.snaptap.utils.TrayManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

public class Main extends Application {
    private TrayManager trayManager;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/fxml/Main.fxml"));
        BorderPane root = fxmlLoader.load();

        // Get the top VBox from FXML
        VBox topContainer = (VBox) root.getTop();

        // Get the window bar (first row)
        HBox windowBar = (HBox) topContainer.getChildren().get(0);

        // Check the current theme
        boolean isDarkMode = ThemeManager.loadThemeState();

        // Create app icon and title
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Create an icon container with background
        StackPane iconContainer = new StackPane();
        iconContainer.setStyle("-fx-background-color: #1F2937; -fx-background-radius: 3; ");
        iconContainer.setPrefSize(36, 36);
        iconContainer.setMinSize(36, 36);
        iconContainer.setMaxSize(36, 36);

        // Add app icon
        ImageView appIcon = new ImageView();
        appIcon.setFitHeight(32);
        appIcon.setFitWidth(32);
        appIcon.setPreserveRatio(true);

        // Load the image using input stream to avoid path issues
        try {
            appIcon.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/logo.png")));
            // Set color based on theme
            appIcon.setStyle(isDarkMode ? "-fx-fill: white;" :
                    "-fx-fill: black; ");
            // Add icon to container
            iconContainer.getChildren().add(appIcon);
        } catch (Exception e) {
            System.err.println("Could not load app icon: " + e.getMessage());
        }

        // Add app title
        Label appTitle = new Label("SnapTap");
        appTitle.getStyleClass().add("window-title");

        titleBox.getChildren().addAll(iconContainer, appTitle);
        windowBar.getChildren().add(0, titleBox);

        // Set icon color based on theme
        javafx.scene.paint.Color iconColor = isDarkMode ?
                javafx.scene.paint.Color.WHITE :
                javafx.scene.paint.Color.BLACK;

        // Create window control buttons
        FontIcon helpIcon = new FontIcon("fas-question-circle");
        helpIcon.setIconColor(iconColor);
        Button helpButton = new Button();
        helpButton.setGraphic(helpIcon);
        helpButton.setTooltip(new Tooltip("Help"));
        helpButton.getStyleClass().add("window-button");
        helpButton.setOnAction(e -> showHelpDialog());

        // Theme button with icon based on current theme
        FontIcon themeIcon = new FontIcon(isDarkMode ? "fas-sun" : "fas-moon");
        themeIcon.setIconColor(iconColor);
        Button themeButton = new Button();
        themeButton.setGraphic(themeIcon);
        themeButton.setTooltip(new Tooltip("Toggle Theme"));
        themeButton.getStyleClass().add("window-button");
        Button minimizeButton = new Button();
        Button maximizeButton = new Button();
        Button closeButton = new Button();
        themeButton.setOnAction(e -> {
            boolean newTheme = !ThemeManager.loadThemeState();
            ThemeManager.toggleTheme(stage.getScene());

            // Update icon color for all icons
            javafx.scene.paint.Color newIconColor = newTheme ?
                    javafx.scene.paint.Color.WHITE :
                    javafx.scene.paint.Color.BLACK;

            // Update theme icon after toggle
            FontIcon newThemeIcon = new FontIcon(newTheme ? "fas-sun" : "fas-moon");
            newThemeIcon.setIconColor(newIconColor);
            ((FontIcon) helpButton.getGraphic()).setIconColor(newIconColor);
            themeButton.setGraphic(newThemeIcon);

            // Update other icons
            ((FontIcon) minimizeButton.getGraphic()).setIconColor(newIconColor);
            ((FontIcon) maximizeButton.getGraphic()).setIconColor(newIconColor);
            ((FontIcon) closeButton.getGraphic()).setIconColor(newIconColor);

            // Update app icon color
            appIcon.setStyle(newTheme ? "-fx-fill: white;" :
                    "-fx-fill: black; ");
        });

        FontIcon minusIcon = new FontIcon("fas-minus");
        minusIcon.setIconColor(iconColor);
//        Button minimizeButton = new Button();
        minimizeButton.setGraphic(minusIcon);
        minimizeButton.setTooltip(new Tooltip("Minimize"));
        minimizeButton.getStyleClass().add("window-button");
        minimizeButton.setOnAction(e -> stage.setIconified(true));

        FontIcon maximizeIcon = new FontIcon("fas-expand");
        maximizeIcon.setIconColor(iconColor);
//        Button maximizeButton = new Button();
        maximizeButton.setGraphic(maximizeIcon);
        maximizeButton.setTooltip(new Tooltip("Maximize"));
        maximizeButton.getStyleClass().add("window-button");
        maximizeButton.setOnAction(e -> {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
                FontIcon expandIcon = new FontIcon("fas-expand");
                expandIcon.setIconColor(isDarkMode ? javafx.scene.paint.Color.WHITE : javafx.scene.paint.Color.BLACK);
                maximizeButton.setGraphic(expandIcon);
            } else {
                stage.setMaximized(true);
                FontIcon compressIcon = new FontIcon("fas-compress");
                compressIcon.setIconColor(isDarkMode ? javafx.scene.paint.Color.WHITE : javafx.scene.paint.Color.BLACK);
                maximizeButton.setGraphic(compressIcon);
            }
        });

        FontIcon closeIcon = new FontIcon("fas-times");
        closeIcon.setIconColor(iconColor);
//        Button closeButton = new Button();
        closeButton.setGraphic(closeIcon);
        closeButton.setTooltip(new Tooltip("Close"));
        closeButton.getStyleClass().addAll("window-button", "close-button");
        closeButton.setOnAction(e -> Platform.exit());

        // Add window controls to the window bar
        windowBar.getChildren().addAll(helpButton, themeButton, minimizeButton, maximizeButton, closeButton);

        // Make the window draggable from the window bar
        windowBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        windowBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // Create scene
        Scene scene = new Scene(root, 1200, 700);

        // Apply theme
        ThemeManager.applyTheme(scene, isDarkMode);

        stage.setTitle("SnapTap");
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/logo.png")));
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        addResizeListener(root, stage);
/*        stage.setMaximized(true); // Set maximized state on startup

        // Update maximize button icon to show compress instead of expand
        FontIcon compressIcon = new FontIcon("fas-compress");
        compressIcon.setIconColor(isDarkMode ? javafx.scene.paint.Color.WHITE : javafx.scene.paint.Color.BLACK);
        maximizeButton.setGraphic(compressIcon);*/

        stage.show();

        // Initialize system tray
        trayManager = new TrayManager(stage);
    }

    private void showHelpDialog() {
        boolean isDarkMode = ThemeManager.loadThemeState();
        String backgroundColor = isDarkMode ? "#212529" : "#F9FAFB";
        String textColor = isDarkMode ? "#ffffff" : "#333333";

        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("About SnapTap");
        helpAlert.setHeaderText("About SnapTap");
        helpAlert.setContentText("SnapTap is a hotkey manager that allows you to assign custom keyboard shortcuts " +
                "to perform various actions such as launching websites, opening applications, and executing files or folders.\n\n" +
                "How to use:\n" +
                "1. Create a new hotkey from the \"Add Hotkey\" tab\n" +
                "2. Define your key combination (e.g., Ctrl+G)\n" +
                "3. Select the action type (website, application, file)\n" +
                "4. Enter the action details\n" +
                "5. Save your hotkey\n\n" +
                "SnapTap will listen for your hotkeys in the background");

        // Apply theme-consistent styling to dialog pane
        helpAlert.getDialogPane().setStyle(
                "-fx-background-color: " + backgroundColor + ";"
        );

        // Style the dialog content properly
        helpAlert.getDialogPane().lookupAll(".content").forEach(node ->
                node.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: 14px;"));

        // Style header text
        helpAlert.getDialogPane().lookupAll(".header-panel .label").forEach(node ->
                node.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: 16px; -fx-font-weight: bold;"));

        // Style content text
        helpAlert.getDialogPane().lookupAll(".content-panel .label").forEach(node ->
                node.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: 14px;"));

        // Style buttons
        helpAlert.getDialogPane().lookupAll(".button").forEach(node ->
                node.setStyle("-fx-text-fill: " + textColor + ";"));

        helpAlert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void addResizeListener(BorderPane root, Stage stage) {
        final int RESIZE_MARGIN = 5;
        final boolean[] dragging = {false};
        final String[] currentEdge = {""};

        root.setOnMouseMoved(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            // Set cursor based on position
            if (mouseX < RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
                root.setCursor(javafx.scene.Cursor.NW_RESIZE);
            } else if (mouseX < RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
                root.setCursor(javafx.scene.Cursor.SW_RESIZE);
            } else if (mouseX > width - RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
                root.setCursor(javafx.scene.Cursor.NE_RESIZE);
            } else if (mouseX > width - RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
                root.setCursor(javafx.scene.Cursor.SE_RESIZE);
            } else if (mouseX < RESIZE_MARGIN) {
                root.setCursor(javafx.scene.Cursor.W_RESIZE);
            } else if (mouseX > width - RESIZE_MARGIN) {
                root.setCursor(javafx.scene.Cursor.E_RESIZE);
            } else if (mouseY < RESIZE_MARGIN) {
                root.setCursor(javafx.scene.Cursor.N_RESIZE);
            } else if (mouseY > height - RESIZE_MARGIN) {
                root.setCursor(javafx.scene.Cursor.S_RESIZE);
            } else {
                root.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });

        root.setOnMousePressed(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            if (mouseX < RESIZE_MARGIN) {
                if (mouseY < RESIZE_MARGIN) {
                    currentEdge[0] = "NW";
                } else if (mouseY > height - RESIZE_MARGIN) {
                    currentEdge[0] = "SW";
                } else {
                    currentEdge[0] = "W";
                }
                dragging[0] = true;
            } else if (mouseX > width - RESIZE_MARGIN) {
                if (mouseY < RESIZE_MARGIN) {
                    currentEdge[0] = "NE";
                } else if (mouseY > height - RESIZE_MARGIN) {
                    currentEdge[0] = "SE";
                } else {
                    currentEdge[0] = "E";
                }
                dragging[0] = true;
            } else if (mouseY < RESIZE_MARGIN) {
                currentEdge[0] = "N";
                dragging[0] = true;
            } else if (mouseY > height - RESIZE_MARGIN) {
                currentEdge[0] = "S";
                dragging[0] = true;
            }
        });

        root.setOnMouseDragged(event -> {
            if (!dragging[0]) return;

            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();
            double startX = stage.getX();
            double startY = stage.getY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            // Apply resize based on drag direction
            switch (currentEdge[0]) {
                case "N" -> {
                    double newHeight = height - (mouseY - startY);
                    if (newHeight > stage.getMinHeight()) {
                        stage.setY(mouseY);
                        stage.setHeight(newHeight);
                    }
                }
                case "S" -> stage.setHeight(mouseY - startY);
                case "E" -> stage.setWidth(mouseX - startX);
                case "W" -> {
                    double newWidth = width - (mouseX - startX);
                    if (newWidth > stage.getMinWidth()) {
                        stage.setX(mouseX);
                        stage.setWidth(newWidth);
                    }
                }
                case "NW" -> {
                    double newWidth = width - (mouseX - startX);
                    double newHeight = height - (mouseY - startY);
                    if (newWidth > stage.getMinWidth()) {
                        stage.setX(mouseX);
                        stage.setWidth(newWidth);
                    }
                    if (newHeight > stage.getMinHeight()) {
                        stage.setY(mouseY);
                        stage.setHeight(newHeight);
                    }
                }
                // Other cases for NE, SE, SW handle similarly
            }
        });

        root.setOnMouseReleased(event -> dragging[0] = false);
    }
}