package gui.component;

import java.util.Date;
import game.Chat;
import game.Game;
import gui.GameView;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * A pane contains all of the chat components, including the entry field and the
 * history box.
 */
public class ChatArea extends VBox {

    /** The GameView that contains this chat area. */
    private GameView gameView;

    /**
     * The history box, which displays the previous chats.
     */
    private ChatHistoryBox historyBox;

    /**
     * The scroll pane that contains {@link #historyBox} in order to make it
     * scrollable.
     */
    private ScrollPane scroller;

    /**
     * The entry field, for sending new chats.
     */
    private TextField enter;

    /**
     * Creates a new chat area.
     * 
     * @param gameView The GameView that will contain this chat area.
     */
    public ChatArea(GameView gameView) {

        this.gameView = gameView;

        historyBox = new ChatHistoryBox(gameView);
        scroller = new ScrollPane(historyBox);
        scroller.setFitToHeight(true);
        scroller.setId("chatScroller");

        setMaxWidth(Double.MAX_VALUE);

        enter = new TextField();
        enter.setPromptText("Enter chat message...");
        enter.setOnAction(ev -> {

            if (enter.getText().equals(""))
                return;

            final Game game = gameView.getGame();
            try {

                gameView.getGame()
                        .sendMessage(new Chat(game.getPlayer(gameView.getColor().equals(GameView.Color.WHITE)),
                                new Date().getTime(), enter.getText()));

                enter.setText("");

            } catch (Exception ex) {

                Dialog<Void> eDg = new Dialog<>();
                eDg.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
                eDg.setTitle("Error");
                eDg.setContentText("Error sending chat message: " + ex.getMessage());
                eDg.showAndWait();

            }

        });

        setSpacing(5);
        VBox.setVgrow(scroller, Priority.ALWAYS);
        VBox.setVgrow(enter, Priority.NEVER);
        setFillWidth(true);

        getChildren().addAll(scroller, enter);

        update();

    }

    /**
     * Updates and redraws this chat area.
     */
    public void update() {

        Platform.runLater(() -> {

            if (gameView.getColor().equals(GameView.Color.TWO_PLAYER)) {
                historyBox.setDisable(true);
                enter.setDisable(true);

                setVisible(false);
                GridPane.setRowSpan(gameView.getScrollMoveList(), 2);

                return;

            } else {
                historyBox.setDisable(false);
                enter.setDisable(false);

                GridPane.setRowSpan(gameView.getScrollMoveList(), 1);

                setVisible(true);
            }

            historyBox.draw(() -> {

                applyCss();
                scroller.layout();

                scroller.setVvalue(scroller.getVmax());
                gameView.getBoard().getResizeEvent().changed(null, 0, 0);

            });

        });

    }

}
