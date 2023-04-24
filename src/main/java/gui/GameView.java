package gui;

import java.util.Optional;

import game.Game;
import game.GameEvent;
import game.GameListener;
import game.Game.Reason;
import game.Game.Result;
import game.GameEvent.Type;
import game.LAN.Client;
import game.engine.EngineHook;
import gui.board.Board;
import gui.component.ChatArea;
import gui.component.GameInfo;
import gui.component.MoveList;
import gui.dialog.CreateGame;
import gui.dialog.Draw;
import gui.menu.BarMenu;
import gui.menu.EngineMenu;
import gui.menu.GameMenu;
import gui.menu.ViewMenu;
import javafx.application.Platform;
import javafx.geometry.BoundingBox;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.WindowEvent;

public class GameView extends HBox implements GameListener {

    public static final int TWO_PLAYER = 0;
    public static final int WHITE = 1;
    public static final int BLACK = 2;

    private Game game;
    private Client client;
    private EngineHook engine;

    private App app;

    private GameInfo infoPane;
    private Board board;
    private GridPane listAndChat;

    private MoveList moveList;
    private ScrollPane scrollMoveList;
    private ChatArea chatBox;

    private BarMenu menuBar;
    private GameMenu gameMenu;
    private ViewMenu viewMenu;
    private EngineMenu engineMenu;

    private Draw drawDialog;

    private int color;
    private int currentPos;

    private boolean flipped;
    private boolean autoFlip;

    // Getters/Setters

    public App getApp() {
        return app;
    }

    public Board getBoard() {
        return board;
    }

    public EngineHook getEngine() {
        return engine;
    }

    public void setEngine(EngineHook engine) {
        this.engine = engine;
    }

    public GameInfo getInfoPane() {
        return infoPane;
    }

    public MoveList getMoveList() {
        return moveList;
    }

    public ScrollPane getScrollMoveList() {
        return scrollMoveList;
    }

    public BarMenu getMenuBar() {
        return menuBar;
    }

    public GameMenu getGameMenu() {
        return gameMenu;
    }

    public ViewMenu getViewMenu() {
        return viewMenu;
    }

    public Draw getDrawDialog() {
        return drawDialog;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getColor() {
        return color;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int pos) {
        this.currentPos = pos;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public boolean isAutoFlip() {
        return autoFlip;
    }

    public boolean isTurn() {

        return color == TWO_PLAYER
                || (color == BLACK && !game.getLastPos().isWhite())
                || (color == WHITE && game.getLastPos().isWhite());

    }

    public GameView(App app, BarMenu menuBar) throws Exception {

        this.app = app;
        this.menuBar = menuBar;

        color = TWO_PLAYER;
        flipped = false;

        // Info Pane
        infoPane = new GameInfo(this);

        // Move list & chat box
        scrollMoveList = new ScrollPane();
        scrollMoveList.setId("scrollMoveList");

        moveList = new MoveList(this, scrollMoveList);

        scrollMoveList.setContent(moveList);
        scrollMoveList.setMaxWidth(Double.MAX_VALUE);

        chatBox = new ChatArea(this);
        chatBox.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHgrow(scrollMoveList, Priority.ALWAYS);
        GridPane.setHgrow(chatBox, Priority.ALWAYS);

        listAndChat = new GridPane();
        listAndChat.setId("listAndChat");

        RowConstraints rm = new RowConstraints();
        rm.setFillHeight(true);
        rm.setPercentHeight(70);

        RowConstraints cm = new RowConstraints();
        cm.setFillHeight(true);
        cm.setPercentHeight(30);

        ColumnConstraints col = new ColumnConstraints();
        col.setFillWidth(true);
        col.setMinWidth(250);
        col.setMaxWidth(350);

        listAndChat.getRowConstraints().setAll(rm, cm);
        listAndChat.getColumnConstraints().setAll(col);

        listAndChat.add(scrollMoveList, 0, 0);
        listAndChat.add(chatBox, 0, 1);

        // Board
        board = new Board(this);

        board.setMinSize(board.getSquareSize() * 8, board.getSquareSize() * 8);
        board.setPrefSize(board.getSquareSize() * 8, board.getSquareSize() * 8);

        setOnMouseMoved(board.getMouseMoved());
        setOnMousePressed(board.getMousePressed());
        setOnMouseDragged(board.getMouseDragged());
        setOnMouseReleased(board.getMouseReleased());

        app.getStage().addEventHandler(WindowEvent.WINDOW_SHOWN, (we -> {

            board.setBoardBounds(
                    board.localToScene(new BoundingBox(0, 0, board.getSquareSize() * 8, board.getSquareSize() * 8)));

            getScene().getWindow().widthProperty().addListener(board.getResizeEvent());
            getScene().getWindow().heightProperty().addListener(board.getResizeEvent());

        }));

        // Menus
        initMenus();

        // Game view
        getChildren().addAll(infoPane, board, listAndChat);

        listAndChat.setViewOrder(1);
        infoPane.setViewOrder(1);
        board.setViewOrder(0);

        HBox.setHgrow(infoPane, Priority.ALWAYS);
        HBox.setHgrow(board, Priority.SOMETIMES);
        HBox.setHgrow(listAndChat, Priority.ALWAYS);

        board.draw();

    }

    // Actions

    /**
     * Sets the {@link #currentPos}.
     * 
     * @param pos The position to change to.
     */
    public void setPos(int pos) {

        int old = currentPos;

        currentPos = pos;

        board.draw(Math.abs(pos - old) == 1, game.getPositions().get(old), game.getPositions().get(currentPos),
                old > currentPos);

        moveList.posChanged(currentPos);

    }

    /**
     * Sets whether the board will automatically flip when a move has been made.
     * 
     * @param autoFlip Whether or not the board should autoflip.
     */
    public void setAutoFlip(boolean autoFlip) {

        this.autoFlip = autoFlip;

        if (game != null && game.getLastPos().isWhite() == flipped)
            flip();

    }

    /**
     * Increases {@link #currentPos} by one.
     */
    public void incPos() {

        if (currentPos + 1 < game.getPositions().size()) {

            setPos(currentPos + 1);

        }

    }

    /**
     * Decreases {@link #currentPos} by one.
     */
    public void decPos() {

        if (currentPos - 1 >= 0) {

            setPos(currentPos - 1);
        }

    }

    /**
     * Sets {@link #currentPos} to {@code 0}.
     */
    public void goToFirstPos() {

        setPos(0);

    }

    /**
     * Sets {@link #currentPos} to the last position of {@link #game}.
     */
    public void goToLastPos() {

        setPos(game.getPositions().size() - 1);

    }

    /**
     * Flips the board.
     */
    public void flip() {

        flipped = !flipped;
        board.draw();

    }

    /**
     * Shows the user the game setup dialog.
     */
    public void startGame() {

        final CreateGame setup = new CreateGame(getScene().getWindow());
        setup.showAndWait();

        if (setup.isCreate()) {

            if (game != null) {

                Dialog<ButtonType> confirm = new Dialog<>();
                confirm.initOwner(getScene().getWindow());

                confirm.setTitle("Confirm New Game");
                confirm.setContentText("Starting a new game will stop the current one. Are you sure?");

                ButtonType yes = new ButtonType("Yes", ButtonData.OK_DONE);
                ButtonType no = new ButtonType("No", ButtonData.CANCEL_CLOSE);

                confirm.getDialogPane().getButtonTypes().setAll(yes, no);

                Optional<ButtonType> result = confirm.showAndWait();

                if (result.get().getButtonData().equals(ButtonData.CANCEL_CLOSE))
                    return;

                if (game != null)
                    game.markGameOver(Result.TERMINATED, Game.Reason.OTHER);

            }

            client = setup.getClient();

            game = setup.getGame();
            game.addListener(this);

            engine = setup.getEngine();

            if (client == null && engine == null)
                color = TWO_PLAYER;
            else if (client != null)
                color = setup.isWhite() ? WHITE : BLACK;
            else if (engine != null)
                color = !engine.isWhite() ? WHITE : BLACK;

            currentPos = 0;

            try {

                if (client == null)
                    game.startGame();
                else {
                    moveList.initMoveList();
                    chatBox.update();
                    engineMenu.setVisible(engine != null);

                    goToLastPos();

                    if (color != TWO_PLAYER && isFlipped() == (color == WHITE))
                        flip();

                    if (!app.getStage().isFocused())
                        app.getStage().toFront();
                }

            } catch (Exception ex) {

                Dialog<Void> errDialog = new Dialog<>();
                errDialog.initOwner(getScene().getWindow());

                errDialog.setTitle("Error Starting Game");
                errDialog.setContentText(ex.getMessage());

                errDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

                errDialog.showAndWait();

            }

        }

    }

    // Drawing

    /**
     * Takes the numeric reason code for the game ending and turns it into text.
     * 
     * @param reason The numeric reason code for why the game ended.
     * @return The text reason.
     */
    public static String reasonToText(Reason reason) {

        switch (reason) {

            case CHECKMATE:
                return " by checkmate.";
            case FLAGFALL:
                return " by flagfall.";
            case DEAD_INSUFFICIENT_MATERIAL:
                return " due to insufficient material.";
            case DEAD_NO_POSSIBLE_MATE:
                return " due to dead position (no possible checkmate.)";
            case FIFTY_MOVE:
                return " by fifty move rule.";
            case REPETITION:
                return " by repetition.";
            case STALEMATE:
                return " by stalemate.";
            case RESIGNATION:
                return " by resignation.";
            default:
                return ".";

        }

    }

    // Initializers

    /**
     * Initializes the menus and adds them to the menu bar.
     */
    private void initMenus() {

        viewMenu = new ViewMenu(this);
        gameMenu = new GameMenu(this);
        engineMenu = new EngineMenu(this);

        menuBar.addAll(gameMenu, viewMenu, engineMenu);

    }

    // Event Handlers

    /**
     * Handles key events.
     * 
     * @param ev The event from pressing the key.
     */
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

    @Override
    public void onPlayerEvent(GameEvent event) {
        if (game == null)
            return;
        Platform.runLater(() -> {
            if (event.getType() == Type.STARTED) {

                moveList.initMoveList();
                chatBox.update();
                engineMenu.setVisible(engine != null);

                goToLastPos();

                if (color != TWO_PLAYER && isFlipped() == (color == WHITE))
                    flip();

                if (!app.getStage().isFocused())
                    app.getStage().toFront();

            } else if (event.getType() == Type.MOVE) {

                currentPos = event.getCurrIndex();

                if (color == TWO_PLAYER && autoFlip && game.getLastPos().isWhite() == flipped)
                    flip();

                board.draw(true, event.getPrev(), event.getCurr(), event.getPrevIndex() > event.getCurrIndex());

                moveList.boardUpdated();
                moveList.posChanged(currentPos);

                gameMenu.update();
                viewMenu.update();

                if (color != TWO_PLAYER && event.getCurr().isWhite() != (color == WHITE)) {
                    if (!app.getStage().isFocused())
                        app.getStage().toFront();
                }

            } else if (event.getType() == Type.DRAW_OFFER) {

                if ((color == WHITE || color == BLACK) && game.getDrawOfferer().isWhite() == (color == WHITE))
                    return;

                drawDialog = new Draw(this, game.getPlayer(client.isOppColor()).getName());

                drawDialog.setOnHidden(ev -> {

                    if (drawDialog.isAccept()) {

                        try {
                            game.acceptDrawOffer();
                        } catch (Exception e) {
                        }

                    } else {

                        try {
                            game.declineDrawOffer();
                        } catch (Exception e) {

                        }

                    }

                });

                drawDialog.show();

            } else if (event.getType() == Type.OVER) {

                if (game == null)
                    return;

                if (game.getResult() == Result.NOT_STARTED
                        || game.getResult() == Result.IN_PROGRESS
                        || game.getResult() == Result.TERMINATED)
                    return;

                gameMenu.update();
                moveList.initMoveList();

                Dialog<Void> over = new Dialog<Void>();
                over.setTitle("Game Over");

                String msg = "";
                if (game.getResult() == Result.DRAW) {
                    msg = "Draw";
                } else if (game.getResult() == Result.BLACK_WIN) {
                    msg = "Black win";
                } else if (game.getResult() == Result.WHITE_WIN) {
                    msg = "White win";
                }

                msg += reasonToText(game.getResultReason());

                over.setContentText(msg);

                over.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

                over.showAndWait();

            } else if (event.getType() == Type.MESSAGE) {

                chatBox.update();

                if (color != TWO_PLAYER && event.getMessage() != null
                        && event.getMessage().getPlayer().isWhite() != (color == WHITE)) {
                    if (!app.getStage().isFocused())
                        // app.getStage().toFront();
                        java.awt.Toolkit.getDefaultToolkit().beep();
                }

            } else if (event.getType() == Type.PAUSED || event.getType() == Type.RESUMED) {
                gameMenu.update();
                infoPane.updateTimers();
                board.getPausePane().setVisible(game.isPaused());
            }

        });

    }

}