package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class InfoPane extends VBox {

    private GUITimer topTimer, bottomTimer;
    private Label topName, bottomName;
    private Region spacer;
    private GameView board;

    public GUITimer getTopTimer() {
        return topTimer;
    }

    public void setTopTimer(GUITimer topTimer) {
        this.topTimer = topTimer;
    }

    public GUITimer getBottomTimer() {
        return bottomTimer;
    }

    public void setBottomTimer(GUITimer bottomTimer) {
        this.bottomTimer = bottomTimer;
    }

    public Label getTopName() {
        return topName;
    }

    public void setTopName(Label topName) {
        this.topName = topName;
    }

    public Label getBottomName() {
        return bottomName;
    }

    public void setBottomName(Label bottomName) {
        this.bottomName = bottomName;
    }

    public InfoPane(GameView board) {

        this.board = board;

        // - Top timer
        topTimer = new GUITimer(board, board.isFlipped());
        topTimer.setAlignment(Pos.CENTER);

        topName = new Label();
        topName.setId("nameLabel");
        topName.setAlignment(Pos.CENTER);

        VBox topInfoBox = new VBox(topName, topTimer);
        topInfoBox.setId("infoBox");
        topInfoBox.setAlignment(Pos.TOP_CENTER);

        // - Spacer
        spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // - Bottom timer
        this.bottomTimer = new GUITimer(board, !board.isFlipped());

        bottomTimer.setAlignment(Pos.CENTER);

        bottomName = new Label();
        bottomName.setId("nameLabel");

        bottomName.setAlignment(Pos.CENTER);

        VBox bottomInfoBox = new VBox(bottomTimer, bottomName);
        bottomInfoBox.setId("infoBox");
        topInfoBox.setAlignment(Pos.BOTTOM_CENTER);

        setId("infoPane");
        getChildren().addAll(topInfoBox, spacer, bottomInfoBox);

    }

    void updateTimers() {
    
        topTimer.update();
        bottomTimer.update();

    }

}
