package gui.menu;

import gui.GameView;
import gui.dialog.EngineSettings;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

public class EngineMenu extends Menu {

    private MenuItem settings;

    private GameView board;

    public EngineMenu(GameView board) {

        super("Engine");
        this.board = board;

        settings = new MenuItem("Settings");
        settings.setOnAction(ae -> {
            EngineSettings stgs = new EngineSettings(board.getScene().getWindow(), board.getEngine());
            stgs.showAndWait();
        });
        settings.setAccelerator(KeyCombination.keyCombination("Shortcut+B"));

        getItems().addAll(settings);

    }

    public void update() {


    }

}
