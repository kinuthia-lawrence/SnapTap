package com.larrykin.snaptap.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class Utilities {
    public static void showCustomAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        boolean isDarkMode = ThemeManager.loadThemeState();
        DialogPane dialogPane = alert.getDialogPane();
        if (isDarkMode) {
            dialogPane.setStyle("-fx-background-color: #212529; -fx-text-fill: #ffffff;");
            dialogPane.lookupAll(".header-panel").forEach(node -> node.setStyle("-fx-background-color: #000000;"));
            dialogPane.lookupAll(".header-panel .label").forEach(node -> node.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;"));
            dialogPane.lookupAll(".label").forEach(node -> node.setStyle("-fx-text-fill: #ffffff;"));
            dialogPane.lookupAll(".button").forEach(node -> node.setStyle("-fx-background-color: #4A555E; -fx-text-fill: #ffffff;"));
        } else {
            dialogPane.setStyle("-fx-background-color: #F9FAFB; -fx-text-fill: #000000;");
            dialogPane.lookupAll(".header-panel").forEach(node -> node.setStyle("-fx-background-color: #FFFFFF;"));
            dialogPane.lookupAll(".header-panel .label").forEach(node -> node.setStyle("-fx-text-fill: #000000; -fx-font-size: 18px; -fx-font-weight: bold;"));
            dialogPane.lookupAll(".label").forEach(node -> node.setStyle("-fx-text-fill: #000000;"));
            dialogPane.lookupAll(".button").forEach(node -> node.setStyle("-fx-background-color: #F0F2F4; -fx-text-fill: #000000;"));
        }

        alert.showAndWait();
    }
}