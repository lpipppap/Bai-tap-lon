module com.auction {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.auction to javafx.graphics, javafx.fxml;
    opens com.auction.controller to javafx.fxml;

    opens com.auction.auction to org.junit.platform.commons;
    opens com.auction.model to org.junit.platform.commons;

    exports com.auction;
    exports com.auction.model;
    exports com.auction.auction;
}