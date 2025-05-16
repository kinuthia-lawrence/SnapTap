module com.larrykin.snaptap {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;

    opens com.larrykin.snaptap to javafx.fxml;
    exports com.larrykin.snaptap;
}