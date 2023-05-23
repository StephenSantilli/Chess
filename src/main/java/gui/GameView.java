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
import gui.component.OpeningLabel;
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

/**
 * The display of a game of chess including the board, timers, move list, chat,
 * etc.
 */
public class GameView extends HBox implements GameListener {

    /**
     * An enumeration of the various colors the active user can be in the game view.
     */
    public enum Color {

        /**
         * The game is a two-player (pass and play) game.
         */
        TWO_PLAYER,

        /**
         * The user using this GameView is playing as white.
         */
        WHITE,

        /**
         * The user using this GameView is playing as black.
         */
        BLACK;

    }

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

    /**
     * The game that this GameView represents.
     */
    private Game game;

    /**
     * The LAN client that this GameView represents. Will be null if this is not a
     * LAN game.
     */
    private Client client;

    /**
     * The engine hook that this GameView represents. Will be null if this is not a
     * game against an engine.
     */
    private EngineHook engine;

    /**
     * The {@link App} that contains this GameView.
     */
    private App app;

    /**
     * The {@link GameInfo} pane contained in this GameView.
     */
    private GameInfo gameInfoPane;

    /**
     * The {@link Board} pane contained in this GameView.
     */
    private Board board;

    /**
     * The pane containing the move list and chat box in this GameView.
     */
    private GridPane listAndChatPane;

    /**
     * The {@link OpeningLabel} pane contained in this GameView.
     */
    private OpeningLabel openingLabelPane;

    /**
     * The {@link MoveList} pane contained in this GameView.
     */
    private MoveList moveListPane;

    /**
     * The scroll pane that contains the {@link #moveListPane}, allowing its
     * contents to be scrolled.
     */
    private ScrollPane scrollMoveList;

    /**
     * The {@link ChatArea} pane contained in this GameView.
     */
    private ChatArea chatBox;

    /**
     * The parent menu bar managed by this GameView.
     */
    private BarMenu menuBar;

    /**
     * The {@link GameMenu} contained in {@link #menuBar}.
     */
    private GameMenu gameMenu;

    /**
     * The {@link ViewMenu} contained in {@link #menuBar}.
     */
    private ViewMenu viewMenu;

    /**
     * The {@link EngineMenu} contained in {@link #menuBar}.
     */
    private EngineMenu engineMenu;

    /**
     * The dialog shown to the user when they are offered a draw by the other
     * player.
     */
    private Draw drawDialog;

    /**
     * The {@link Color} of the user who is using this GameView.
     */
    private Color color;

    /**
     * The index of the {@link Position} currently being displayed by this GameView.
     */
    private int currentPos;

    /**
     * Whether or not the board is flipped.
     * 
     * <p>
     * Note that the board being flipped ({@code flipped} being {@code true}) simply
     * means that white's pieces are displayed on top and black's pieces are
     * displayed on the bottom of the screen, regardless of the value of
     * {@link #color}.
     */
    private boolean flipped;

    /**
     * Whether or not the board should automatically flip so that the color whose
     * turn it is will be on the bottom after each move is made.
     * 
     * <p>
     * Only works when {@link #color} is equal to {@link Color.TWO_PLAYER}.
     */
    private boolean autoFlip;

    /**
     * Creates a new game view.
     * 
     * @param app     The app that will contain this GameView.
     * @param menuBar The menu bar that will contain the menus managed by this
     *                GameView.
     */
    public GameView(App app, BarMenu menuBar) {

        this.app = app;
        this.menuBar = menuBar;

        color = Color.TWO_PLAYER;
        flipped = false;

        // Info Pane
        gameInfoPane = new GameInfo(this);

        // Move list & chat box
        openingLabelPane = new OpeningLabel(this);

        scrollMoveList = new ScrollPane();
        scrollMoveList.setId("scrollMoveList");

        moveListPane = new MoveList(this, scrollMoveList);

        scrollMoveList.setContent(moveListPane);
        scrollMoveList.setMaxWidth(Double.MAX_VALUE);

        chatBox = new ChatArea(this);
        chatBox.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHgrow(scrollMoveList, Priority.ALWAYS);
        GridPane.setHgrow(chatBox, Priority.ALWAYS);
        GridPane.setHgrow(openingLabelPane, Priority.ALWAYS);

        listAndChatPane = new GridPane();
        listAndChatPane.setId("listAndChat");

        RowConstraints ro = new RowConstraints();
        ro.setFillHeight(true);
        ro.setPercentHeight(5);

        RowConstraints rm = new RowConstraints();
        rm.setFillHeight(true);
        rm.setPercentHeight(65);

        RowConstraints cm = new RowConstraints();
        cm.setFillHeight(true);
        cm.setPercentHeight(35);

        ColumnConstraints col = new ColumnConstraints();
        col.setFillWidth(true);
        col.setMinWidth(250);
        col.setMaxWidth(350);

        listAndChatPane.getRowConstraints().setAll(ro, rm, cm);
        listAndChatPane.getColumnConstraints().setAll(col);

        listAndChatPane.add(openingLabelPane, 0, 0);
        listAndChatPane.add(scrollMoveList, 0, 1);
        listAndChatPane.add(chatBox, 0, 2);

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
        getChildren().addAll(gameInfoPane, board, listAndChatPane);

        listAndChatPane.setViewOrder(1);
        gameInfoPane.setViewOrder(1);
        board.setViewOrder(0);

        HBox.setHgrow(gameInfoPane, Priority.ALWAYS);
        HBox.setHgrow(board, Priority.SOMETIMES);
        HBox.setHgrow(listAndChatPane, Priority.ALWAYS);

        board.draw();

    }

    /**
     * Gets the opening label pane.
     * 
     * @return {@link #openingLabelPane}.
     */
    public OpeningLabel getOpeningLabelPane() {
        return openingLabelPane;
    }

    /**
     * Gets the app that contains this GameView.
     * 
     * @return {@link #app}
     */
    public App getApp() {
        return app;
    }

    /**
     * Gets the board.
     * 
     * @return {@link #board}
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the engine hook.
     * 
     * @return {@link #engine}
     */
    public EngineHook getEngine() {
        return engine;
    }

    /**
     * Sets the engine hook.
     * 
     * @param engine The engine hook to set {@link #engine} to.
     */
    public void setEngine(EngineHook engine) {
        this.engine = engine;
    }

    /**
     * Gets the {@link GameInfo} pane.
     * 
     * @return {@link #gameInfoPane}
     */
    public GameInfo getGameInfoPane() {
        return gameInfoPane;
    }

    /**
     * Gets the {@link MoveList} pane.
     * 
     * @return {@link #moveListPane}
     */
    public MoveList getMoveListPane() {
        return moveListPane;
    }

    /**
     * Gets the scroll pane that contains {@link #moveListPane}.
     * 
     * @return {@link #scrollMoveList}
     */
    public ScrollPane getScrollMoveList() {
        return scrollMoveList;
    }

    /**
     * Gets the {@link BarMenu} that contains the menu items managed by this
     * GameView.
     * 
     * @return {@link #menuBar}
     */
    public BarMenu getMenuBar() {
        return menuBar;
    }

    /**
     * Gets the {@link GameMenu} menu.
     * 
     * @return {@link #gameMenu}
     */
    public GameMenu getGameMenu() {
        return gameMenu;
    }

    /**
     * Gets the {@link ViewMenu} menu.
     * 
     * @return {@link #viewMenu}
     */
    public ViewMenu getViewMenu() {
        return viewMenu;
    }

    /**
     * Gets the {@link EngineMenu} menu.
     * 
     * @return {@link #engineMenu}
     */
    public EngineMenu getEngineMenu() {
        return engineMenu;
    }

    /**
     * Gets the {@link Draw} dialog.
     * 
     * @return {@link #drawDialog}
     */
    public Draw getDrawDialog() {
        return drawDialog;
    }

    /**
     * Gets the game represented by this GameView.
     * 
     * @return {@link #game}
     */
    public Game getGame() {
        return game;
    }

    /**
     * Sets the game represented by this GameView.
     * 
     * @param game The game to set {@link #game} to.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Gets the client represented by this GameView.
     * 
     * @return {@link #client}
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sets the client represented by this GameView.
     * 
     * @param client The client to set {@link #client} to.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Gets the color of the user using this GameView.
     * 
     * @return {@link #color}
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the index of the curent {@link game.Position} being displayed by this
     * GameView.
     * 
     * @return {@link #currentPos}
     */
    public int getCurrentPos() {
        return currentPos;
    }

    /**
     * Changes the curent {@link game.Position} being displayed by this
     * GameView.
     * 
     * @param pos The index of the position.
     */
    public void setCurrentPos(int pos) {
        this.currentPos = pos;
    }

    /**
     * Whether or not the board is flipped.
     * 
     * @return {@link #flipped}
     * @see #flipped
     */
    public boolean isFlipped() {
        return flipped;
    }

    /**
     * Whether or not the board will automatically flip.
     * 
     * @return {@link #autoFlip}
     * @see #autoFlip
     */
    public boolean isAutoFlip() {
        return autoFlip;
    }

    /**
     * Checks if it is {@link #color}'s turn. Will always return true when
     * {@link #color} is equal to {@link gui.GameView.Color#TWO_PLAYER}.
     * 
     * @return If it is the player's turn.
     */
    public boolean isTurn() {

        return color.equals(Color.TWO_PLAYER)
                || (color.equals(Color.BLACK) && !game.getLastPos().isWhite())
                || (color.equals(Color.WHITE) && game.getLastPos().isWhite());

    }

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

        moveListPane.posChanged(currentPos);

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
     * Shows the user the game setup dialog and allows them to create a new game.
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
                color = Color.TWO_PLAYER;
            else if (client != null)
                color = setup.isWhite() ? Color.WHITE : Color.BLACK;
            else if (engine != null)
                color = !engine.isWhite() ? Color.WHITE : Color.BLACK;

            currentPos = 0;

            try {

                if (client == null)
                    game.startGame();
                else {
                    moveListPane.initMoveList();
                    chatBox.update();
                    engineMenu.setVisible(engine != null);

                    goToLastPos();

                    if (!color.equals(Color.TWO_PLAYER) && isFlipped() == (color.equals(Color.WHITE)))
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

    /**
     * Handles key events.
     * 
     * @param ev The eventfrom pressing the key.
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

                moveListPane.initMoveList();
                chatBox.update();
                engineMenu.setVisible(engine != null);

                goToLastPos();

                if (!color.equals(Color.TWO_PLAYER) && isFlipped() == (color.equals(Color.WHITE)))
                    flip();

                if (!app.getStage().isFocused())
                    app.getStage().toFront();

            } else if (event.getType() == Type.MOVE) {

                currentPos = event.getCurrIndex();

                if (color.equals(Color.TWO_PLAYER) && autoFlip && game.getLastPos().isWhite() == flipped)
                    flip();

                board.draw(true, event.getPrev(), event.getCurr(), event.getPrevIndex() > event.getCurrIndex());

                moveListPane.boardUpdated();
                moveListPane.posChanged(currentPos);

                gameMenu.update();
                viewMenu.update();

                if (!color.equals(Color.TWO_PLAYER) && event.getCurr().isWhite() != (color.equals(Color.WHITE))) {
                    if (!app.getStage().isFocused())
                        app.getStage().toFront();
                }

            } else if (event.getType() == Type.DRAW_OFFER) {

                if ((color.equals(Color.WHITE) || color.equals(Color.BLACK))
                        && game.getDrawOfferer().isWhite() == (color.equals(Color.WHITE)))
                    return;

                drawDialog = new Draw(this, game.getPlayer(client.isOppWhite()).getName());

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
                moveListPane.initMoveList();

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

                if (!color.equals(Color.TWO_PLAYER) && event.getMessage() != null
                        && event.getMessage().getPlayer().isWhite() != (color.equals(Color.WHITE))) {
                    if (!app.getStage().isFocused())
                        // app.getStage().toFront();
                        java.awt.Toolkit.getDefaultToolkit().beep();
                }

            } else if (event.getType() == Type.PAUSED || event.getType() == Type.RESUMED) {
                gameMenu.update();
                gameInfoPane.updateTimers();
                board.getPausePane().setVisible(game.isPaused());
            }

        });

    }

    /**
     * Initializes the menus and adds them to the menu bar.
     */
    private void initMenus() {

        viewMenu = new ViewMenu(this);
        gameMenu = new GameMenu(this);
        engineMenu = new EngineMenu(this);

        menuBar.addAll(gameMenu, viewMenu, engineMenu);

    }

}