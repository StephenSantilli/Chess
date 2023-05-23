package gui.menu;

import java.util.Arrays;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

/**
 * The parent menu bar that contains the menu tabs used by the {@link gui.App}.
 */
public class BarMenu extends MenuBar {

    /**
     * Creates a new parent menu bar.
     */
    public BarMenu() {

        setUseSystemMenuBar(true);

        getMenus().add(new HelpMenu());

    }

    /**
     * Adds one or more menus to the menu bar.
     * 
     * @param menus The menu(s) to add to the menu bar.
     */
    public void addAll(Menu... menus) {
        getMenus().addAll(0, Arrays.asList(menus));
    }

}
