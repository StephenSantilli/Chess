package gui.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DirectConnect extends Stage {

    private TextField field;
    private Button set, cancel;

    private String ip;

    public String getIp() {
        return ip;
    }

    public DirectConnect(Window window) {

        // initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().setAll(((Stage) (window)).getIcons());

        ip = "";

        VBox vb = new VBox();

        set = new Button("Connect");
        set.setOnAction(ev -> {
            ip = field.getText();
            hide();
        });

        cancel = new Button("Cancel");
        cancel.setOnAction(ev -> {
            hide();
        });

        HBox btns = new HBox(set, cancel);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setSpacing(10);

        field = new TextField();
        field.setPromptText("Enter IP address...");

        vb.getChildren().addAll(field, btns);
        vb.setPadding(new Insets(10));
        vb.setSpacing(10);

        Scene s = new Scene(vb);

        setOnShown(we -> {

            sizeToScene();
            setWidth(500);
            setMinWidth(500);
            setMinHeight(getHeight());
            setMaxHeight(getHeight());
            setMaxWidth(getWidth());

        });

        setTitle("Direct Connect");
        setScene(s);

    }

}
