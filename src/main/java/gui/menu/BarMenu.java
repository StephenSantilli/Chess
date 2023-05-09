package gui.menu;

import java.util.Arrays;

import gui.App;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class BarMenu extends MenuBar {

    public BarMenu() {

        setUseSystemMenuBar(true);

        getMenus().add(new HelpMenu());

    }

    public void addAll(Menu... menus) {
        getMenus().addAll(0, Arrays.asList(menus));
    }

}
