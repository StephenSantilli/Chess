package gui;

import game.GameProperties;

import java.awt.Window;
import java.util.stream.Collectors;

import org.apache.xmlgraphics.util.dijkstra.Edge;

import game.Game;
import game.GameEvent;
import game.GameListener;
import game.LAN.Client;
import game.PGN.PGNParser;
import gui.board.Board;
import gui.component.ChatArea;
import gui.component.GameInfo;
import gui.component.MoveList;
import gui.dialog.Draw;
import gui.dialog.GameSetup;
import gui.menu.BarMenu;
import gui.menu.GameMenu;
import gui.menu.ViewMenu;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameView extends HBox implements GameListener {

    public static final int TWO_PLAYER = 0;
    public static final int WHITE = 1;
    public static final int BLACK = 2;

    private App app;
    private Board board;

    private GridPane listAndChat;

    private Pane boardPane;
    private GameInfo infoPane;

    private MoveList moveList;
    private ScrollPane scrollMoveList;
    private ChatArea chatBox;

    private BarMenu menuBar;
    private GameMenu gameMenu;
    private ViewMenu viewMenu;

    private Draw drawDialog;

    private Game game;
    private Client client;

    private int color;
    private int currentPos;

    private boolean flipped;
    private boolean autoFlip;

    // Getters/Setters
    public boolean isTurn() {

        if (color == TWO_PLAYER)
            return true;

        if (color == BLACK && !game.getLastPos().isWhite())
            return true;
        if (color == WHITE && game.getLastPos().isWhite())
            return true;

        return false;

    }

    public App getApp() {
        return app;
    }

    public Board getBoard() {
        return board;
    }

    public Pane getBoardPane() {
        return boardPane;
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

    public GameView(App app, BarMenu menuBar) throws Exception {

        this.app = app;
        this.menuBar = menuBar;

        color = TWO_PLAYER;
        flipped = false;

        infoPane = new GameInfo(this);

        scrollMoveList = new ScrollPane();
        moveList = new MoveList(this, scrollMoveList);

        scrollMoveList.setContent(moveList);
        scrollMoveList.setMaxWidth(Double.MAX_VALUE);

        scrollMoveList.setFitToWidth(true);
        scrollMoveList.setFitToHeight(true);
        scrollMoveList.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollMoveList.setVbarPolicy(ScrollBarPolicy.NEVER);

        chatBox = new ChatArea(this);
        chatBox.setMaxWidth(Double.MAX_VALUE);

        listAndChat = new GridPane();
        listAndChat.setVgap(5);
        listAndChat.setAlignment(Pos.CENTER_LEFT);

        listAndChat.add(scrollMoveList, 0, 0);
        listAndChat.add(chatBox, 0, 1);

        RowConstraints rm = new RowConstraints();
        rm.setFillHeight(true);
        rm.setPercentHeight(70);

        RowConstraints cm = new RowConstraints();
        cm.setFillHeight(false);
        cm.setPercentHeight(30);

        ColumnConstraints col = new ColumnConstraints();
        col.setFillWidth(true);
        GridPane.setHgrow(listAndChat, Priority.ALWAYS);
        GridPane.setHgrow(scrollMoveList, Priority.ALWAYS);
        GridPane.setHgrow(chatBox, Priority.ALWAYS);
        col.setMinWidth(250);
        col.setMaxWidth(350);

        listAndChat.getRowConstraints().setAll(rm, cm);
        listAndChat.getColumnConstraints().setAll(col);
        listAndChat.setId("listAndChat");

        board = new Board(this);

        initMenus();

        getChildren().addAll(infoPane, board, listAndChat);

        listAndChat.setViewOrder(1);
        infoPane.setViewOrder(1);
        board.setViewOrder(0);

        setOnMouseMoved(board.getMouseMoved());
        setOnMousePressed(board.getMousePressed());
        setOnMouseDragged(board.getMouseDragged());
        setOnMouseReleased(board.getMouseReleased());

        HBox.setHgrow(infoPane, Priority.ALWAYS);
        HBox.setHgrow(board, Priority.SOMETIMES);
        HBox.setHgrow(listAndChat, Priority.ALWAYS);

        HBox.setMargin(infoPane, new Insets(10, 5, 10, 5));
        HBox.setMargin(board, new Insets(10, 5, 10, 5));
        HBox.setMargin(listAndChat, new Insets(10, 5, 10, 5));

        setAlignment(Pos.CENTER);

        board.setPrefSize(board.getSquareSize() * 8, board.getSquareSize() * 8);
        board.setMinSize(board.getSquareSize() * 8, board.getSquareSize() * 8);

        app.getStage().addEventHandler(WindowEvent.WINDOW_SHOWN, (we -> {

            board.setBoardBounds(
                    board.localToScene(new BoundingBox(0, 0, board.getSquareSize() * 8, board.getSquareSize() * 8)));

            getScene().getWindow().widthProperty().addListener(board.getResizeEvent());
            getScene().getWindow().heightProperty().addListener(board.getResizeEvent());
            
        }));

        board.boardUpdated();

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

    public void setPos(int pos) {

        int old = currentPos;

        currentPos = pos;

        board.boardUpdated(Math.abs(pos - old) == 1, game.getPositions().get(old), game.getPositions().get(currentPos),
                old > currentPos);

        moveList.posChanged(currentPos);
        gameMenu.update();

    }

    public void setAutoFlip(boolean autoFlip) {

        this.autoFlip = autoFlip;

        if (game != null && game.getLastPos().isWhite() == flipped)
            flip();

    }

    void incPos() {

        if (currentPos + 1 < game.getPositions().size()) {

            setPos(currentPos + 1);

        }

    }

    void decPos() {

        if (currentPos - 1 >= 0) {

            setPos(currentPos - 1);
        }

    }

    void goToFirstPos() {

        setPos(0);

    }

    void goToLastPos() {

        setPos(game.getPositions().size() - 1);

    }

    public void flip() {

        flipped = !flipped;
        board.boardUpdated();

    }

    public void startGame(WindowEvent we) {

        final GameSetup setup = new GameSetup(getScene().getWindow(), this);

        setup.setOnHidden(e -> {

            if (setup.isCreate()) {

                if (game != null)
                    game.markGameOver(Game.RESULT_TERMINATED, Game.REASON_OTHER);

                if (setup.getClient() == null) {
                    try {

                        color = TWO_PLAYER;
                        if (!setup.getPgn().equals("")) {
                            game = new Game("White", "Black",
                                    new GameProperties(setup.getTimePerSide(), setup.getTimePerMove(), true, true, true,
                                            true));
                            game.importPosition(new PGNParser(setup.getPgn()));
                        } else if (!setup.getFen().equals(""))
                            game = new Game("White", "Black",
                                    new GameProperties(setup.getFen(), setup.getTimePerSide(), setup.getTimePerMove(),
                                            true, true, true,
                                            true));
                        else
                            game = new Game("White", "Black",
                                    new GameProperties(setup.getTimePerSide(), setup.getTimePerMove(), true, true, true,
                                            true));

                        game.addListener(this);

                        game.startGame();
                        board.boardUpdated();

                    } catch (Exception ex) {

                        Dialog<Void> eDg = new Dialog<>();
                        eDg.initOwner(getScene().getWindow());
                        eDg.setTitle("Error Creating Game");
                        eDg.setContentText(ex.getMessage());

                        eDg.getDialogPane().getButtonTypes().add(ButtonType.OK);

                        eDg.showAndWait();

                        ex.printStackTrace();

                    }

                } else {

                    client = setup.getClient();
                    game = client.getGame();
                    color = client.isOppColor() ? BLACK : WHITE;

                    game.addListener(this);

                    if (color == BLACK)
                        flip();

                    board.boardUpdated();

                }

                moveList.initMoveList();
                chatBox.update();

                if (!app.getStage().isFocused())
                    app.getStage().toFront();

            }

        });

        setup.showAndWait();

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
                    flip();

                board.boardUpdated(true, event.getPrev(), event.getCurr(), event.getPrevIndex() > event.getCurrIndex());

                moveList.boardUpdated();
                moveList.posChanged(currentPos);

                gameMenu.update();
                viewMenu.update();

                if (color != TWO_PLAYER && event.getCurr().isWhite() != (color == WHITE)) {
                    if (!app.getStage().isFocused())
                        app.getStage().toFront();
                }

            } else if (event.getType() == GameEvent.TYPE_DRAW_OFFER) {

                if ((color == WHITE || color == BLACK) && game.getLastPos().getDrawOfferer() == color)
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

            } else if (event.getType() == GameEvent.TYPE_MESSAGE) {

                chatBox.update();

                if (color != TWO_PLAYER && event.getMessage() != null
                        && event.getMessage().getPlayer().isWhite() != (color == WHITE)) {
                    if (!app.getStage().isFocused())
                        // app.getStage().toFront();
                        java.awt.Toolkit.getDefaultToolkit().beep();
                }

            }

        });

    }

}