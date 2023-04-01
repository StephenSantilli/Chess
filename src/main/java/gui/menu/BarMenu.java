package gui.menu;

import java.util.Arrays;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class BarMenu extends MenuBar {

    public BarMenu() {

        setUseSystemMenuBar(true);

        if (System.getProperty("os.name", "").toLowerCase().startsWith("mac")) {

            getMenus().add(new Menu("Help"));

        }

    }

    public void addAll(Menu... menus) {
        getMenus().addAll(0, Arrays.asList(menus));
    }

}
