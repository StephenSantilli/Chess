package guifx;

import game.Game;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class BarMenu extends MenuBar {
    
    private GameMenu gameMenu;

    public GameMenu getGameMenu() {
        return gameMenu;
    }

    public BarMenu(Game g) {

        setUseSystemMenuBar(true);

        gameMenu = new GameMenu(g);

        getMenus().add(gameMenu);
    }
    
}
