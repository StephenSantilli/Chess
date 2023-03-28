package gui.dialog;

import gui.GameView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        buttons.getChildren().addAll(copyButton, okButton);

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
