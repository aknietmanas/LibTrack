module com.libtrack.libtrack {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.libtrack.libtrack to javafx.fxml;
    exports com.libtrack.libtrack;
}
