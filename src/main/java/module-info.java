module com.larrykin.snaptap {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.slf4j;
    requires java.desktop;

    opens com.larrykin.snaptap to javafx.fxml;
    opens com.larrykin.snaptap.controllers to javafx.fxml;
    exports com.larrykin.snaptap.controllers to javafx.fxml;
    exports com.larrykin.snaptap;
}