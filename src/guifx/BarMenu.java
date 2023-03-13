package guifx;

import game.Game;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Window;

public class BarMenu extends MenuBar {
    
/*     private FileMenu fileMenu;

    public FileMenu getFileMenu() {
        return fileMenu;
    } */

    public BarMenu(Window window) {

        setUseSystemMenuBar(true);

        // fileMenu = new FileMenu(window);

        // getMenus().addAll(fileMenu);

    }
    
}
