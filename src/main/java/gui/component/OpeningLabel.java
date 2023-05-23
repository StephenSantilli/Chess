package gui.component;

import gui.GameView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * The pane that shows the opening that led to the position the game is
 * currently in.
 */
public class OpeningLabel extends VBox {

    /**
     * The GameView that displays this pane.
     */
    private GameView gameView;

    /**
     * The label that displays the opening.
     */
    private Text openingLabel;

    /**
     * Creates a new pane that displays the opening.
     * 
     * @param gameView The GameView that contains this pane.
     */
    public OpeningLabel(GameView gameView) {

        this.gameView = gameView;

        openingLabel = new Text();
        openingLabel.setId("openingText");

        Region topReg = new Region(), botReg = new Region();

        VBox.setVgrow(topReg, Priority.ALWAYS);
        VBox.setVgrow(botReg, Priority.ALWAYS);

        HBox hb = new HBox(openingLabel);
        hb.setId("openingBox");

        getChildren().addAll(topReg, hb, botReg);

    }

    /**
     * Updates the pane to display the current opening of
     * {@link GameView#getCurrentPos()}.
     */
    public void update() {

        openingLabel.setWrappingWidth(gameView.getMoveListPane().getWidth()
                - (openingLabel.getLayoutX() - gameView.getMoveListPane().getLayoutX()));

        if (gameView.getGame() == null || gameView.getGame().getLastPos() == null) {
            openingLabel.setText("");
            return;
        }

        openingLabel.setText(
                gameView.getGame().getPositions().get(gameView.getCurrentPos()).getOpening() == null ? ""
                        : gameView.getGame()
                                .getPositions().get(gameView.getCurrentPos()).getOpening().getName());

    }

}
