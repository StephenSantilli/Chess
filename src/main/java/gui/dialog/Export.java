package gui.dialog;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import gui.GameView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
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

    private TextArea ta;

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

        Scene s = new Scene(vb, 500, 400);

        setScene(s);
        setTitle("Export Game");

        CheckBox cb = new CheckBox("Include clock");
        cb.setSelected(true);
        cb.setOnAction(e -> {
            includeClock = cb.isSelected();
            ta.setText(getPGN());
        });

        HBox hb = new HBox(cb);

        String output = getPGN();

        ta = new TextArea(output);
        ta.setEditable(false);

        VBox.setVgrow(ta, Priority.ALWAYS);

        HBox buttons = new HBox();

        Button exportButton = new Button("Export");
        exportButton.setOnAction(e -> {

            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new ExtensionFilter("PGN File", "*.pgn"));

            File f = chooser.showSaveDialog(new Stage());

            if (f != null) {

                try (PrintWriter scan = new PrintWriter(new FileWriter(f))) {

                    String[] lines = ta.getText().split("\n");
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
            content.putString(ta.getText());
            Clipboard.getSystemClipboard().setContent(content);

        });

        Button okButton = new Button("Ok");
        okButton.setOnAction(e -> {

            hide();

        });

        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.getChildren().addAll(exportButton, copyButton, okButton);

        vb.getChildren().addAll(hb, ta, buttons);

        vb.setSpacing(5);
        buttons.setSpacing(5);

        ta.setPrefColumnCount(85);
        ta.setPrefRowCount(15);

    }

    public String getPGN() {

        try {

            return board.getGame().exportPosition(true, includeClock);

        } catch (Exception e) {

            Dialog<Void> eDialog = new Dialog<Void>();

            eDialog.setTitle("Error Exporting Position");
            eDialog.setContentText("An error occurred while exporting the position.");
            eDialog.showAndWait();
            hide();

        }

        return "";
    }

}
