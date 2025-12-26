module org.agaafar.pharmacy {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.agaafar.pharmacy to javafx.fxml;
    exports org.agaafar.pharmacy;
    exports org.agaafar.pharmacy.controllers;
    opens org.agaafar.pharmacy.controllers to javafx.fxml;
}