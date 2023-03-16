package gui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Window;

public class BarMenu extends MenuBar {

    public BarMenu(Window window) {

        setUseSystemMenuBar(true);

        Menu help = new Menu("Help");
        getMenus().add(help);

    }
    
}
