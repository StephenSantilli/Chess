package gui.menu;

import gui.GameView;
import gui.dialog.EngineSettings;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

/**
 * The menu bar tab that contains engine options.
 */
public class EngineMenu extends Menu {

    /**
     * The menu item which allows the user to open the engine settings dialog.
     */
    private MenuItem settings;

    /**
     * Creates a new "Engine" menu tab.
     * 
     * @param gameView The {@link GameView} that manages this menu tab.
     */
    public EngineMenu(GameView gameView) {

        super("Engine");

        settings = new MenuItem("Settings");
        settings.setOnAction(ae -> {
            EngineSettings stgs = new EngineSettings(gameView.getScene().getWindow(), gameView.getEngine());
            stgs.showAndWait();
        });

        settings.setAccelerator(KeyCombination.keyCombination("Shortcut+B"));

        getItems().addAll(settings);

    }

}
