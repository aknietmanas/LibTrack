module com.libtrack {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.libtrack to javafx.fxml;
    opens com.libtrack.model to javafx.fxml;

    exports com.libtrack;
    exports com.libtrack.model;
    exports com.libtrack.controller;
    opens com.libtrack.controller to javafx.fxml;
    exports com.libtrack.dao;
    opens com.libtrack.dao to javafx.fxml;
}
