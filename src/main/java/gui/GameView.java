package gui;

import game.GameSettings;
import game.Game;
import game.GameEvent;
import game.GameListener;
import game.LAN.Client;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.WindowEvent;

public class GameView extends HBox implements GameListener {

    public static final int TWO_PLAYER = 0;
    public static final int WHITE = 1;
    public static final int BLACK = 2;

    private Game game;
    private Client client;

    private boolean flipped;
    private boolean autoFlip;

    private int color;
    private int currentPos;

    private App app;
    private Board board;

    private Pane boardPane;
    private InfoPane infoPane;

    private MovePane moveList;
    private ScrollPane scrollMoveList;

    private BarMenu menuBar;
    private GameMenu gameMenu;
    private ViewMenu viewMenu;

    private DrawDialog drawDialog;

    // Getters/Setters

    public int getCurrentPos() {
        return currentPos;
    }

    public boolean isAutoFlip() {
        return autoFlip;
    }

    public Pane getBoardPane() {
        return boardPane;
    }

    public InfoPane getInfoPane() {
        return infoPane;
    }

    public GameMenu getGameMenu() {
        return gameMenu;
    }

    public ViewMenu getViewMenu() {
        return viewMenu;
    }

    public Game getGame() {
        return game;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public int getColor() {
        return color;
    }

    public Board getBoard() {
        return board;
    }

    public MovePane getMoveList() {
        return moveList;
    }

    public ScrollPane getScrollMoveList() {
        return scrollMoveList;
    }

    public BarMenu getMenuBar() {
        return menuBar;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isTurn() {

        if (color == TWO_PLAYER)
            return true;

        if (color == BLACK && !game.getLastPos().isWhite())
            return true;
        if (color == WHITE && game.getLastPos().isWhite())
            return true;

        return false;

    }

    public GameView(App app, BarMenu menuBar) throws Exception {

        setId("gameView");

        this.app = app;
        this.menuBar = menuBar;

        color = TWO_PLAYER;
        flipped = false;

        infoPane = new InfoPane(this);

        scrollMoveList = new ScrollPane();
        moveList = new MovePane(this, scrollMoveList);

        scrollMoveList.setContent(moveList);
        scrollMoveList.setFitToWidth(true);
        scrollMoveList.setFitToHeight(true);
        scrollMoveList.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollMoveList.setVbarPolicy(ScrollBarPolicy.NEVER);
        scrollMoveList.setMinWidth(220);

        board = new Board(this);
        boardPane = new Pane(board);

        initMenus();

        getChildren().addAll(infoPane, boardPane, scrollMoveList);

        scrollMoveList.setViewOrder(1);
        infoPane.setViewOrder(1);
        board.setViewOrder(0);

        setOnMouseMoved(board.getMouseMoved());
        setOnMousePressed(board.getMousePressed());
        setOnMouseDragged(board.getMouseDragged());
        setOnMouseReleased(board.getMouseReleased());

        HBox.setHgrow(infoPane, Priority.SOMETIMES);
        HBox.setHgrow(boardPane, Priority.SOMETIMES);
        HBox.setHgrow(scrollMoveList, Priority.SOMETIMES);

        HBox.setMargin(infoPane, new Insets(5, 5, 5, 5));
        HBox.setMargin(boardPane, new Insets(5, 5, 5, 5));
        HBox.setMargin(scrollMoveList, new Insets(5, 5, 5, 5));

        boardPane.setPrefSize(board.getSquareSize() * 8, board.getSquareSize() * 8);

        app.getStage().addEventHandler(WindowEvent.WINDOW_SHOWN, (we -> {

            board.setMaxSize(board.getSquareSize() * 8, board.getSquareSize() * 8);
            board.setBoardBounds(
                    board.localToScene(new BoundingBox(0, 0, board.getSquareSize() * 8, board.getSquareSize() * 8)));

            boardPane.prefWidthProperty().bind(Bindings.min(boardPane.widthProperty(), boardPane.heightProperty()));
            boardPane.prefWidthProperty().bind(Bindings.min(boardPane.widthProperty(), boardPane.heightProperty()));

            boardPane.widthProperty().addListener(board.getResizeEvent());
            boardPane.heightProperty().addListener(board.getResizeEvent());

        }));

        board.boardUpdated();

    }

    public void setPos(int pos) {
        this.currentPos = pos;
    }

    public void setCurrentPos(int pos) {

        int old = currentPos;

        currentPos = pos;

        board.boardUpdated(Math.abs(pos - old) == 1, game.getPositions().get(old), game.getPositions().get(currentPos),
                old > currentPos);

        moveList.posChanged(currentPos);
        gameMenu.update();

    }

    // Actions

    public void keyHandler(KeyEvent ev) {

        if (game == null)
            return;

        if (ev.getCode() == (KeyCode.LEFT)) {

            decPos();

        } else if (ev.getCode() == (KeyCode.RIGHT)) {

            incPos();

        } else if (ev.getCode() == KeyCode.DOWN) {

            goToLastPos();

        } else if (ev.getCode() == KeyCode.UP) {

            goToFirstPos();

        } else if (ev.getCode() == KeyCode.ESCAPE) {

            board.clearSelection();

        }

    }

    void setAutoFlip(boolean autoFlip) {

        this.autoFlip = autoFlip;

        if (game != null && game.getLastPos().isWhite() == flipped)
            flipBoard();

    }

    void incPos() {

        if (currentPos + 1 < game.getPositions().size()) {

            setCurrentPos(currentPos + 1);

        }

    }

    void decPos() {

        if (currentPos - 1 >= 0) {

            setCurrentPos(currentPos - 1);
        }

    }

    void goToFirstPos() {

        setCurrentPos(0);

    }

    void goToLastPos() {

        setCurrentPos(game.getPositions().size() - 1);

    }

    void flipBoard() {

        flipped = !flipped;
        board.boardUpdated();

    }

    void startGame(WindowEvent we) {

        final GameSettingsDialog settings = new GameSettingsDialog(getScene().getWindow(), this);

        settings.setOnHidden(e -> {

            if (settings.isCreate()) {

                if (game != null)
                    game.markGameOver(Game.RESULT_TERMINATED, Game.REASON_OTHER);

                if (settings.getClient() == null) {
                    try {

                        color = TWO_PLAYER;

                        game = new Game("White", "Black",
                                new GameSettings(settings.getTimePerSide(), settings.getTimePerMove(), true, true, true,
                                        true));

                        game.addListener(this);

                        game.startGame();
                        board.boardUpdated();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else {

                    client = settings.getClient();
                    game = client.getGame();
                    color = client.isOppColor() ? BLACK : WHITE;

                    game.addListener(this);

                    if (color == BLACK)
                        flipBoard();

                    board.boardUpdated();

                }

                moveList.initMoveList();

            }

        });

        settings.showAndWait();

    }

    // Drawing

    public static String reasonToText(int reason) {

        switch (reason) {

            case Game.REASON_CHECKMATE:
                return " by checkmate.";
            case Game.REASON_FLAGFALL:
                return " by flagfall.";
            case Game.REASON_DEAD_INSUFFICIENT_MATERIAL:
                return " due to insufficient material.";
            case Game.REASON_DEAD_NO_POSSIBLE_MATE:
                return " due to dead position (no possible checkmate.)";
            case Game.REASON_FIFTY_MOVE:
                return " by fifty move rule.";
            case Game.REASON_REPETITION:
                return " by repetition.";
            case Game.REASON_STALEMATE:
                return " by stalemate.";
            case Game.REASON_RESIGNATION:
                return " by resignation.";
            default:
                return ".";

        }
    }

    // Initializers
    private void initMenus() {

        viewMenu = new ViewMenu(this);
        gameMenu = new GameMenu(this);

        menuBar.getMenus().addAll(gameMenu, viewMenu, new Menu("Help"));

    }

    // Event Handlers

    @Override
    public void onPlayerEvent(GameEvent event) {
        if (game == null)
            return;
        Platform.runLater(() -> {

            if (event.getType() == GameEvent.TYPE_MOVE) {

                currentPos = event.getCurrIndex();

                if (color == TWO_PLAYER && autoFlip && game.getLastPos().isWhite() == flipped)
                    flipBoard();

                board.boardUpdated(true, event.getPrev(), event.getCurr(), event.getPrevIndex() > event.getCurrIndex());

                moveList.boardUpdated();
                moveList.posChanged(currentPos);

                gameMenu.update();
                viewMenu.update();

            } else if (event.getType() == GameEvent.TYPE_DRAW_OFFER) {

                if ((color == WHITE || color == BLACK) && game.getLastPos().getDrawOfferer() == color)
                    return;

                drawDialog = new DrawDialog(this, game.getPlayer(client.isOppColor()).getName());

                drawDialog.setOnHidden(ev -> {

                    if (drawDialog.isAccept()) {

                        try {
                            game.acceptDrawOffer();
                        } catch (Exception e) {
                        }

                    }

                });

                drawDialog.show();

            } else if (event.getType() == GameEvent.TYPE_OVER) {

                if (game == null)
                    return;

                if (game.getResult() <= Game.RESULT_IN_PROGRESS || game.getResult() == Game.RESULT_TERMINATED)
                    return;

                Dialog<Void> over = new Dialog<Void>();
                over.setTitle("Game Over");

                String msg = "";
                if (game.getResult() == Game.RESULT_DRAW) {
                    msg = "Draw";
                } else if (game.getResult() == Game.RESULT_BLACK_WIN) {
                    msg = "Black win";
                } else if (game.getResult() == Game.RESULT_WHITE_WIN) {
                    msg = "White win";
                }

                msg += reasonToText(game.getResultReason());

                over.setContentText(msg);

                over.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

                over.showAndWait();

            }

        });

    }

}