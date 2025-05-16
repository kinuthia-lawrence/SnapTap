package com.larrykin.snaptap;

import com.larrykin.snaptap.controllers.DashboardController;
import com.larrykin.snaptap.utils.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
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
    public void start(Stage stage
    ) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(DashboardController.class.getResource("/fxml/Dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        // Set the background color directly on the root node
        scene.getRoot().setStyle("-fx-background-color: #212529;");
        boolean isDarkMode = ThemeManager.loadThemeState();
        ThemeManager.applyTheme(scene, isDarkMode);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * --module-path "C:\Program Files\Java\javafx-sdk-21.0.6\lib" --add-modules javafx.controls,javafx.fxml
     * */
}
