package guifx;

import game.Game;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Window;

public class BarMenu extends MenuBar {
    
    private GameMenu gameMenu;

    public GameMenu getGameMenu() {
        return gameMenu;
    }

    public BarMenu(Game g, Window window) {

        setUseSystemMenuBar(true);

        gameMenu = new GameMenu(g, window);

        getMenus().add(gameMenu);
    }
    
}
