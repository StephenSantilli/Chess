package guifx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ExportDialog extends Stage {

    private TextArea ta;

    public ExportDialog(Board b) {

        // setResizable(false);

        VBox vb = new VBox();
        vb.setPadding(new Insets(15, 15, 15, 15));

        Scene s = new Scene(vb, 500, 400);

        setScene(s);

        String output = "";
        try {
            output = b.getGame().exportPosition();
        } catch (Exception e) {

            Dialog<Void> eDialog = new Dialog<Void>();

            eDialog.setTitle("Error Exporting Position");
            eDialog.setContentText("An error occurred while exporting the position.");
            eDialog.showAndWait();
            hide();

        }

        ta = new TextArea(output);

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

        vb.getChildren().addAll(ta, buttons);

        ta.setPrefColumnCount(85);
        ta.setPrefRowCount(15);

    }

}
