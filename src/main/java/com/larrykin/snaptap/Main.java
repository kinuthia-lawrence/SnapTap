package com.larrykin.snaptap;

import com.larrykin.snaptap.controllers.MainController;
import com.larrykin.snaptap.utils.ThemeManager;
import com.larrykin.snaptap.utils.TrayManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class Main extends Application {
    private TrayManager trayManager;

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param stage the primary stage for this application, onto which
     *              the application scene can be set.
     *              Applications may create other stages, if needed, but they will not be
     *              primary stages.
     * @throws Exception if something goes wrong
     */

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/fxml/Main.fxml"));

        // Load the root node from FXML
        Parent root = fxmlLoader.load();

        // Create a BorderPane as the new root container
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(root);

        // Create toolbar for custom window buttons
        HBox windowControls = new HBox(5);
        windowControls.setAlignment(Pos.CENTER_RIGHT);
        windowControls.setPadding(new Insets(5, 10, 5, 10));

        // Create theme button
        Button themeButton = new Button();
        themeButton.setGraphic(new FontIcon("fas-adjust"));
        themeButton.setTooltip(new Tooltip("Toggle Theme"));
        themeButton.getStyleClass().add("window-button");
        themeButton.setOnAction(e -> ThemeManager.toggleTheme(stage.getScene()));

        // Create help button
        Button helpButton = new Button();
        helpButton.setGraphic(new FontIcon("fas-question-circle"));
        helpButton.setTooltip(new Tooltip("Help"));
        helpButton.getStyleClass().add("window-button");
        helpButton.setOnAction(e -> showHelpDialog());

        // Add buttons to the toolbar
        windowControls.getChildren().addAll(themeButton, helpButton);

        // Add the toolbar to the top of the BorderPane
        borderPane.setTop(windowControls);

        // Create scene with the BorderPane as root
        Scene scene = new Scene(borderPane, 800, 600);
        scene.getRoot().setStyle("-fx-background-color: #212529;");

        boolean isDarkMode = ThemeManager.loadThemeState();
        ThemeManager.applyTheme(scene, isDarkMode);

        stage.setTitle("SnapTap");
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/logo.png")));
        stage.setScene(scene);
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

    /**
     * --module-path "C:\Program Files\Java\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml
     * */
}
