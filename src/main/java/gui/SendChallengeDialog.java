package gui;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class SendChallengeDialog extends Stage {

    private TextArea ta;

    public SendChallengeDialog(Window owner) {

        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);

        setResizable(false);

        VBox vb = new VBox();
        vb.setPadding(new Insets(15, 15, 15, 15));

        Scene s = new Scene(vb);

        setScene(s);
        setTitle("Send Challenge");

        

    }

}
