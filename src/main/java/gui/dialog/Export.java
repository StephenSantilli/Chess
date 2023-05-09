package gui.dialog;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import game.GameSettings;
import gui.GameView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class Export extends Stage {

    private TextArea pgnArea;
    private TextField fenArea;

    private boolean includeClock;

    private GameView board;

    public Export(GameView board) {

        this.board = board;
        includeClock = true;

        initOwner(board.getScene().getWindow());
        initModality(Modality.WINDOW_MODAL);
        getIcons().setAll(((Stage) (board.getScene().getWindow())).getIcons());

        setResizable(false);

        VBox vb = new VBox();
        vb.setPadding(new Insets(15, 15, 15, 15));

        Scene s = new Scene(vb, 550, 500);

        setScene(s);
        setTitle("Export Game");

        // FEN Area
        Label fenLabel = new Label("FEN");
        fenArea = new TextField(getFEN());
        fenArea.setEditable(false);

        VBox.setVgrow(fenArea, Priority.ALWAYS);

        VBox fen = new VBox(fenLabel, fenArea);
        fen.setSpacing(5);
        fen.setFillWidth(true);

        VBox.setVgrow(fen, Priority.ALWAYS);

        // PGN Area
        Label pgnLabel = new Label("PGN");

        pgnArea = new TextArea(getPGN());
        pgnArea.setEditable(false);

        VBox.setVgrow(pgnArea, Priority.ALWAYS);

        VBox pgn = new VBox(pgnLabel, pgnArea);
        pgn.setSpacing(5);
        pgn.setFillWidth(true);

        VBox.setVgrow(pgn, Priority.ALWAYS);

        // Include clock checkbox
        CheckBox cb = new CheckBox("Include clock");
        if (board.getGame().getSettings().getTimePerSide() > -1)
            cb.setSelected(true);
        else {
            cb.setSelected(false);
            cb.setDisable(true);
        }

        cb.setOnAction(e -> {
            includeClock = cb.isSelected();
            pgnArea.setText(getPGN());
        });

        HBox opts = new HBox(cb);

        HBox buttons = new HBox();

        Button exportButton = new Button("Save");
        exportButton.setOnAction(e -> {

            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new ExtensionFilter("PGN File", "*.pgn"));
            chooser.setTitle("Save PGN");

            File f = chooser.showSaveDialog(new Stage());

            if (f != null) {

                try (PrintWriter scan = new PrintWriter(new FileWriter(f))) {

                    String[] lines = pgnArea.getText().split("\n");
                    for (int i = 0; i < lines.length; i++) {

                        scan.println(lines[i]);

                    }

                } catch (Exception er) {
                    Dialog<Void> eDg = new Dialog<>();
                    eDg.initOwner(getScene().getWindow());
                    eDg.setTitle("Error Exporting PGN");
                    eDg.setContentText(er.getMessage());

                    eDg.getDialogPane().getButtonTypes().add(ButtonType.OK);

                    eDg.showAndWait();
                }

            }

        });

        Button copyButton = new Button("Copy");
        copyButton.setOnAction(e -> {

            ClipboardContent content = new ClipboardContent();
            content.putString(pgnArea.getText());
            Clipboard.getSystemClipboard().setContent(content);

        });

        Button okButton = new Button("Ok");
        okButton.setOnAction(e -> {

            hide();

        });

        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.getChildren().addAll(exportButton, copyButton, okButton);

        vb.getChildren().addAll(fen, pgn, opts, buttons);

        vb.setSpacing(10);
        buttons.setSpacing(10);

        pgnArea.setPrefColumnCount(85);
        pgnArea.setPrefRowCount(15);

    }

    public String getFEN() {

        try {

            return board.getGame().getLastPos().toString();

        } catch (Exception e) {

            Dialog<Void> eDialog = new Dialog<Void>();

            eDialog.setTitle("Error Exporting Position");
            eDialog.setContentText("An error occurred while exporting the position: " + e.getMessage());
            eDialog.showAndWait();
            hide();

        }

        return "";
    }

    public String getPGN() {

        try {

            if (!board.getGame().getSettings().getFen().equals(GameSettings.DEFAULT_FEN)
                    && board.getGame().getPositions().get(0).getMoveNumber() != 0)
                return "Cannot export PGN from custom position that does not start at move 1.";

            return board.getGame().exportPosition(true, includeClock);

        } catch (Exception e) {

            Dialog<Void> eDialog = new Dialog<Void>();

            eDialog.setTitle("Error Exporting Position");
            eDialog.setContentText("An error occurred while exporting the position: " + e.getMessage());

            eDialog.showAndWait();
            hide();

        }

        return "";
    }

}
