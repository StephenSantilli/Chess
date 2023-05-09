package gui.component;

import gui.GameView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class OpeningLabel extends VBox {

    private GameView gv;
    private Text l;

    public OpeningLabel(GameView gv) {

        this.gv = gv;
        l = new Text();
        l.setId("openingText");

        Region topReg = new Region(), botReg = new Region();
        VBox.setVgrow(topReg, Priority.ALWAYS);
        VBox.setVgrow(botReg, Priority.ALWAYS);

        HBox hb = new HBox(l);
        hb.setId("openingBox");
        getChildren().addAll(topReg, hb, botReg);

    }

    public void update() {

        l.setWrappingWidth(gv.getMoveList().getWidth() - (l.getLayoutX() - gv.getMoveList().getLayoutX()));

        if (gv.getGame() == null || gv.getGame().getLastPos() == null) {
            l.setText("");
            return;
        }

        l.setText(
                gv.getGame().getPositions().get(gv.getCurrentPos()).getOpening() == null ? ""
                        : gv.getGame()
                                .getPositions().get(gv.getCurrentPos()).getOpening().getName());

    }

}
