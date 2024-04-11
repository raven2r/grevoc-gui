module me.raven2r.translationview {
    requires javafx.controls;
    requires javafx.fxml;
    requires me.raven2r.grevoc;


    opens me.raven2r.translationview to javafx.fxml;
    exports me.raven2r.translationview;
}