package gui.dialog;

import gui.component.GameView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Draw extends Stage {

    private Button yes, no;

    private boolean accept;

    public boolean isAccept() {
        return accept;
    }

    public Draw(GameView board, String offererName) {

        initOwner(board.getScene().getWindow());
        getIcons().setAll(((Stage) (board.getScene().getWindow())).getIcons());

        setResizable(false);

        VBox vb = new VBox();
        vb.setPadding(new Insets(15, 15, 15, 15));

        Scene s = new Scene(vb, 300, 100);

        setScene(s);
        setTitle("Draw Offered");

        Label label = new Label(offererName + " offered a draw. Accept?");
        accept = false;

        HBox buttons = new HBox();

        yes = new Button("Yes");
        yes.setOnAction(ev -> {

            accept = true;
            hide();

        });

        no = new Button("No");
        no.setOnAction(ev -> {

            accept = false;
            hide();

        });

        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.getChildren().addAll(yes, no);

        vb.getChildren().addAll(label, buttons);

        vb.setSpacing(5);
        buttons.setSpacing(5);

    }

}
