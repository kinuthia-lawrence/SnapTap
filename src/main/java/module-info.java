module com.larrykin.snaptap {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires com.fasterxml.jackson.databind;
    requires com.github.kwhat.jnativehook;
    requires java.prefs;
    requires javafx.fxml;

    requires org.slf4j;



    opens com.larrykin.snaptap to javafx.fxml;
    opens com.larrykin.snaptap.controllers to javafx.fxml;
    exports com.larrykin.snaptap;
    exports com.larrykin.snaptap.models to com.fasterxml.jackson.databind;
    exports com.larrykin.snaptap.enums to com.fasterxml.jackson.databind;
}

