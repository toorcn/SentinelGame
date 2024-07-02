module com.sentinel.apg39 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.sentinel.apg39 to javafx.fxml;
    exports com.sentinel.apg39;
}