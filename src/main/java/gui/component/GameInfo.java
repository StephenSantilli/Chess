package gui.component;

import gui.GameView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GameInfo extends VBox {

    private GUITimer topTimer, bottomTimer;
    private GUITimerHistory topTimerHistory, bottomTimerHistory;
    private Label topName, bottomName;
    private Region spacer;

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

    public GUITimerHistory getTopTimerHistory() {
        return topTimerHistory;
    }

    public void setTopTimerHistory(GUITimerHistory topHistory) {
        this.topTimerHistory = topHistory;
    }

    public GUITimerHistory getBottomTimerHistory() {
        return bottomTimerHistory;
    }

    public void setBottomTimerHistory(GUITimerHistory bottomHistory) {
        this.bottomTimerHistory = bottomHistory;
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

    public GameInfo(GameView board) {

        // - Top timer
        topName = new Label();
        topName.setId("nameLabel");
        topName.setAlignment(Pos.CENTER);

        topTimer = new GUITimer(board, !board.isFlipped());
        topTimer.setAlignment(Pos.CENTER);

        topTimerHistory = new GUITimerHistory(board, !board.isFlipped());
        topTimerHistory.setAlignment(Pos.CENTER);

        VBox topInfoBox = new VBox(topName, topTimer, topTimerHistory);
        topInfoBox.setId("infoBox");
        topInfoBox.setAlignment(Pos.TOP_CENTER);

        // - Spacer
        spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // - Bottom timer
        bottomTimerHistory = new GUITimerHistory(board, board.isFlipped());
        bottomTimerHistory.setAlignment(Pos.CENTER);

        bottomTimer = new GUITimer(board, board.isFlipped());
        bottomTimer.setAlignment(Pos.CENTER);

        bottomName = new Label();
        bottomName.setId("nameLabel");

        bottomName.setAlignment(Pos.CENTER);

        VBox bottomInfoBox = new VBox(bottomTimerHistory, bottomTimer, bottomName);
        bottomInfoBox.setId("infoBox");
        topInfoBox.setAlignment(Pos.BOTTOM_CENTER);

        setId("infoPane");
        getChildren().addAll(topInfoBox, spacer, bottomInfoBox);

    }

    public void updateTimers() {

        topTimer.update();
        bottomTimer.update();
        topTimerHistory.update();
        bottomTimerHistory.update();

    }

    public Region getSpacer() {
        return spacer;
    }

    public void setSpacer(Region spacer) {
        this.spacer = spacer;
    }

}
