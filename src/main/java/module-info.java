module Chess {
    requires javafx.controls;
    requires javafx.swing;

    requires batik.all;

    opens xml.apis to batik.all;
    opens java.xml to javafx.base, javafx.controls, javafx.swing, javafx.baseEmpty;

    exports gui;
}
