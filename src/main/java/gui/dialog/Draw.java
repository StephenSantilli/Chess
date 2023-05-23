package gui.dialog;

import gui.GameView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The dialog that is displayed when the user is sent a draw offer.
 */
public class Draw extends Stage {

    /**
     * The button that accepts the draw offer.
     */
    private Button yes;

    /**
     * The button that declines the draw offer.
     */
    private Button no;

    /**
     * Whether or not the draw offer was accepted.
     */
    private boolean accept;

    /**
     * Creates a new draw offer dialog.
     * 
     * @param gameView    The GameView that created this dialog.
     * @param offererName The name of the player who offered the draw offer.
     */
    public Draw(GameView gameView, String offererName) {

        initOwner(gameView.getScene().getWindow());
        getIcons().setAll(((Stage) (gameView.getScene().getWindow())).getIcons());

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

    /**
     * Gets whether or not the user accepted the draw offer.
     * 
     * @return {@link #accept}
     */
    public boolean isAccept() {
        return accept;
    }

}
