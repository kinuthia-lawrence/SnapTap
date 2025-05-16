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

        // Create app icon and title
        HBox titleBox = new HBox(5);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Create an icon container with background
        StackPane iconContainer = new StackPane();
        iconContainer.setStyle("-fx-background-color: #2A9D8F; -fx-background-radius: 3;");
        iconContainer.setPrefSize(24, 24);
        iconContainer.setMinSize(24, 24);
        iconContainer.setMaxSize(24, 24);

        // Add app icon
        ImageView appIcon = new ImageView();
        appIcon.setFitHeight(18);
        appIcon.setFitWidth(18);
        appIcon.setPreserveRatio(true);

        // Load the image using input stream to avoid path issues
        try {
            appIcon.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/logo.png")));
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

        // Create window control buttons
        Button themeButton = new Button();
        themeButton.setGraphic(new FontIcon("fas-adjust"));
        themeButton.setTooltip(new Tooltip("Toggle Theme"));
        themeButton.getStyleClass().add("window-button");
        themeButton.setOnAction(e -> ThemeManager.toggleTheme(stage.getScene()));

        Button helpButton = new Button();
        helpButton.setGraphic(new FontIcon("fas-question-circle"));
        helpButton.setTooltip(new Tooltip("Help"));
        helpButton.getStyleClass().add("window-button");
        helpButton.setOnAction(e -> showHelpDialog());

        Button minimizeButton = new Button();
        minimizeButton.setGraphic(new FontIcon("fas-minus"));
        minimizeButton.setTooltip(new Tooltip("Minimize"));
        minimizeButton.getStyleClass().add("window-button");
        minimizeButton.setOnAction(e -> stage.setIconified(true));

        Button maximizeButton = new Button();
        maximizeButton.setGraphic(new FontIcon("fas-expand"));
        maximizeButton.setTooltip(new Tooltip("Maximize"));
        maximizeButton.getStyleClass().add("window-button");
        maximizeButton.setOnAction(e -> {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
                maximizeButton.setGraphic(new FontIcon("fas-expand"));
            } else {
                stage.setMaximized(true);
                maximizeButton.setGraphic(new FontIcon("fas-compress"));
            }
        });

        Button closeButton = new Button();
        closeButton.setGraphic(new FontIcon("fas-times"));
        closeButton.setTooltip(new Tooltip("Close"));
        closeButton.getStyleClass().addAll("window-button", "close-button");
        closeButton.setOnAction(e -> Platform.exit());

        // Add window controls to the window bar
        windowBar.getChildren().addAll(themeButton, helpButton, minimizeButton, maximizeButton, closeButton);

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
        Scene scene = new Scene(root, 1200, 600);

        // Apply theme
        boolean isDarkMode = ThemeManager.loadThemeState();
        ThemeManager.applyTheme(scene, isDarkMode);

        stage.setTitle("SnapTap");
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/logo.png")));
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        // Initialize system tray
        trayManager = new TrayManager(stage);
    }

    private void showHelpDialog() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("SnapTap Help");
        helpAlert.setHeaderText("SnapTap Keyboard Shortcut Manager");
        helpAlert.setContentText("This application allows you to create and manage custom keyboard shortcuts.\n\n" +
                "For more information, visit the documentation website.");
        helpAlert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}