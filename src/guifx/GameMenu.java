package guifx;

import java.io.File;
import java.util.Scanner;

import PGNParser.PGNParser;
import game.BoardMoveListener;
import game.Game;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class GameMenu extends Menu implements BoardMoveListener {
    
    private MenuItem undo, redo, pgnImport;
    private Menu gameImport, gameExport;
    private Game game;

    public GameMenu(Game game, Window owner) {

        super("Game");
        this.game = game;

        undo = new MenuItem("Undo");
        undo.setAccelerator(KeyCombination.keyCombination("Shortcut+Z"));
        undo.setDisable(!game.canUndo());
        undo.setOnAction(e -> {

            game.undoMove();

        });

        redo = new MenuItem("Redo");
        redo.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+Z"));
        redo.setDisable(!game.canRedo());
        redo.setOnAction(e -> {
            game.redoMove();
        });

        gameImport = new Menu("Import");
        pgnImport = new MenuItem("Import from PGN file");
        pgnImport.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+I"));
        pgnImport.setOnAction(e -> {
           
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Import from PGN file");
            chooser.setSelectedExtensionFilter(new ExtensionFilter("PGN File", "pgn"));;
            File file = chooser.showOpenDialog(owner);
            if(file != null && file.exists() && file.canRead()) {
                try {
                    
                    String str = "";
                    Scanner s = new Scanner(file);
                    while(s.hasNextLine()) {

                        str += s.nextLine() + "\n";

                    }
                    s.close();
                    PGNParser pgn = new PGNParser(str);
                    game.importPosition(pgn);

                } catch(Exception ex) {

                    ex.printStackTrace();

                }


            } else {
                System.out.println("Error reading file.");
            }

        });
        gameImport.getItems().add(pgnImport);

        getItems().addAll(undo, redo, gameImport);

    }

    @Override
    public void moveMade() {
        // TODO Auto-generated method stub

    }

    @Override
    public void undoMove() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetMoves() {
        // TODO Auto-generated method stub

    }

    @Override
    public void posChanged(int old, int curr) {
        // TODO Auto-generated method stub
        undo.setDisable(!game.canUndo());
        redo.setDisable(!game.canRedo());
    }

    @Override
    public void redoMove() {
        // TODO Auto-generated method stub

    }

}
