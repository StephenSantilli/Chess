package gui.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class PGN extends Stage {

    private TextArea field;
    private Button set, cancel;

    private boolean create;

    private String pgn;

    public String getPgn() {
        return pgn;
    }

    public boolean isCreate() {
        return create;
    }

    public PGN(Window window) {

        // initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().setAll(((Stage) (window)).getIcons());

        pgn = "";

        VBox vb = new VBox();

        set = new Button("Start Game");
        set.setOnAction(ev -> {
            pgn = field.getText();
            hide();
        });

        cancel = new Button("Cancel");
        cancel.setOnAction(ev -> {
            hide();
        });

        HBox btns = new HBox(set, cancel);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setSpacing(10);

        field = new TextArea();
        field.setPromptText("Enter PGN...");
        VBox.setVgrow(field, Priority.ALWAYS);

        vb.getChildren().addAll(field, btns);
        vb.setPadding(new Insets(10));
        vb.setSpacing(10);

        Scene s = new Scene(vb);
        setWidth(500);
        setMinWidth(500);
        setMinHeight(400);
        setHeight(400);
        setOnShown(we -> {

            // sizeToScene();

            setMaxHeight(getHeight());
            setMaxWidth(getWidth());

        });

        setTitle("Start Game from PGN");
        setScene(s);

    }

}
