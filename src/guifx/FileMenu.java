package guifx;

import java.io.File;
import java.util.Scanner;

import PGNParser.PGNParser;
import game.BoardMoveListener;
import game.Game;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class FileMenu extends Menu {

    private MenuItem undo, redo, pgnImport, pause, resume;
    private Menu gameImport, gameExport;
    private Game game;

    public FileMenu(Window owner) {

        super("File");
        this.game = game;

        gameImport = new Menu("Import");
        pgnImport = new MenuItem("Import from PGN file");
        pgnImport.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+I"));
        pgnImport.setOnAction(e -> {

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Import from PGN file");
            chooser.setSelectedExtensionFilter(new ExtensionFilter("PGN File", "pgn"));
            ;
            File file = chooser.showOpenDialog(owner);

            Runnable readIn = () -> {
                if (file != null && file.exists() && file.canRead()) {
                    try {

                        String str = "";
                        Scanner s = new Scanner(file);
                        while (s.hasNextLine()) {

                            str += s.nextLine() + "\n";

                        }
                        s.close();
                        PGNParser pgn = new PGNParser(str);

                        game.importPosition(pgn);

                    } catch (Exception ex) {

                        ex.printStackTrace();

                    }

                } else {
                    System.out.println("Error reading file.");
                }
            };

            if (file != null && file.exists() && file.canRead()) {
                if (game.getPositions().size() > 1) {
                    Dialog<Boolean> confirm = new Dialog<Boolean>();
                    confirm.setContentText(
                            "Are you sure you want to import this position? It will overwrite your current game!");
                    confirm.getDialogPane().getButtonTypes().add(ButtonType.YES);
                    confirm.getDialogPane().getButtonTypes().add(ButtonType.NO);
                    confirm.getDialogPane().lookupButton(ButtonType.YES).addEventFilter(ActionEvent.ACTION, res -> {
                        readIn.run();
                    });

                    /*
                     * confirm.getDialogPane().lookupButton(ButtonType.NO).addEventFilter(
                     * ActionEvent.ACTION, res -> {
                     * confirm.hide();
                     * });
                     */

                    confirm.showAndWait();
                    // confirm.hide();

                } else
                    readIn.run();
            }

        });
        gameImport.getItems().add(pgnImport);

        getItems().addAll(gameImport);

    }



}
