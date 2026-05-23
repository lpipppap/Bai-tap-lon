module com.auction {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.auction to javafx.graphics, javafx.fxml;
    opens com.auction.controller to javafx.fxml;

    exports com.auction;
}