package gui;

import game.Game;
import game.LAN.Challenge;
import game.LAN.ChallengeServer;
import game.LAN.Client;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class GameSettingsDialog extends Stage {

    private long timePerSide, timePerMove;

    private ChallengeServer server;

    private Button createChallenge, searchForChallenge, startButton, cancelButton;

    private CheckBox timeBox;

    private Spinner<Integer> minPerSide, secPerSide, minPerMove, secPerMove;

    private Label perSideLabel, perSideDivider, perMoveLabel, perMoveDivider, eLabel;

    private ChallengeSearchDialog search;

    private boolean create;
    private Client client;

    public boolean isCreate() {
        return create;
    }

    public Client getClient() {
        return client;
    }

    public long getTimePerMove() {
        return timePerMove;
    }

    public long getTimePerSide() {
        return timePerSide;
    }

    private void setDisabledTime() {

        boolean d = !timeBox.isSelected();
        minPerSide.setDisable(d);
        secPerSide.setDisable(d);
        minPerMove.setDisable(d);
        secPerMove.setDisable(d);
        perSideLabel.setDisable(d);
        perSideDivider.setDisable(d);
        perMoveLabel.setDisable(d);
        perMoveDivider.setDisable(d);

    }

    private Runnable gameCreatedCallbackServer = () -> {

        Platform.runLater(() -> {
            client = server.getClient();
            create = true;
            server.stop();
            hide();
        });

    };

    private Runnable gameCreatedCallbackSearcher = () -> {

        Platform.runLater(() -> {
            client = search.getClient();
            create = true;

            timePerSide = client.getGame().getSettings().getTimePerSide();
            timePerMove = client.getGame().getSettings().getTimePerMove();

            hide();

        });

    };

    public GameSettingsDialog(Window window, GameView board) {

        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);

        setResizable(false);

        getIcons().setAll(((Stage) (window)).getIcons());

        create = false;
        server = null;
        client = null;

        VBox items = new VBox();
        items.setSpacing(5);
        items.setPadding(new Insets(10, 10, 10, 10));

        Scene s = new Scene(items);

        timeBox = new CheckBox("Use a time control");
        HBox checkHBox = new HBox(timeBox);
        checkHBox.setAlignment(Pos.CENTER_LEFT);

        HBox perSide = new HBox();
        perSide.setAlignment(Pos.CENTER_LEFT);
        perSide.setSpacing(5);
        perSideLabel = new Label("Time per side (M:S): ");
        perSideDivider = new Label(":");

        minPerSide = new Spinner<Integer>(0, 9999, 10);
        secPerSide = new Spinner<Integer>(0, 60, 0);

        minPerSide.setPrefWidth(100);
        secPerSide.setPrefWidth(100);

        perSide.getChildren().addAll(perSideLabel, minPerSide, perSideDivider, secPerSide);

        HBox perMove = new HBox();
        perMove.setAlignment(Pos.CENTER_LEFT);

        perMove.setSpacing(5);
        perMoveLabel = new Label("Time added per move (M:S): ");
        perMoveDivider = new Label(":");

        IntegerSpinnerValueFactory smVF = new IntegerSpinnerValueFactory(0, 9999, 0);
        IntegerSpinnerValueFactory ssVF = new IntegerSpinnerValueFactory(0, 59, 0, 1);

        minPerMove = new Spinner<Integer>(smVF);
        secPerMove = new Spinner<Integer>(ssVF);

        minPerMove.setPrefWidth(100);
        secPerMove.setPrefWidth(100);

        perMove.getChildren().addAll(perMoveLabel, minPerMove, perMoveDivider, secPerMove);

        timeBox.setSelected(false);
        setDisabledTime();

        timeBox.setOnAction(ev -> {
            setDisabledTime();
        });

        eLabel = new Label("");
        eLabel.setVisible(false);
        eLabel.setTextFill(Color.RED);
        HBox errorLabel = new HBox(eLabel);

        HBox btns = new HBox();
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setSpacing(5);

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {

            if (server != null) {

                server.stop();
                server = null;

                createChallenge.setDisable(false);
                searchForChallenge.setDisable(false);
                startButton.setDisable(false);

                cancelButton.setText("Cancel");
                createChallenge.setText("Create LAN Challenge");

                sizeToScene();

            } else {

                timePerSide = -1;
                timePerMove = -1;

                hide();

            }

        });

        startButton = new Button("Start 2-Player Game");
        startButton.setOnAction(e -> {

            timePerSide = !timeBox.isSelected() ? -1
                    : ((minPerSide.getValue() * 60) + (secPerSide.getValue())) * 1000;
            timePerMove = !timeBox.isSelected() ? -1
                    : ((minPerMove.getValue() * 60) + (secPerMove.getValue())) * 1000;
            create = true;
            hide();

        });

        createChallenge = new Button("Create LAN Challenge");
        createChallenge.setOnAction(e -> {

            ChallengeSendDialog cDialog = new ChallengeSendDialog(getScene().getWindow());

            cDialog.setOnHidden(we -> {

                if (!cDialog.isCreate())
                    return;

                timePerSide = !timeBox.isSelected() ? -1
                        : ((minPerSide.getValue() * 60) + (secPerSide.getValue())) * 1000;
                timePerMove = !timeBox.isSelected() ? -1
                        : ((minPerMove.getValue() * 60) + (secPerMove.getValue())) * 1000;

                try {

                    server = new ChallengeServer(
                            new Challenge(cDialog.getName(), cDialog.getColor(), timePerSide, timePerMove, null),
                            gameCreatedCallbackServer);
                    server.start();

                    createChallenge.setDisable(true);
                    searchForChallenge.setDisable(true);
                    startButton.setDisable(true);

                    createChallenge.setText("Waiting...");
                    cancelButton.setText("Cancel Search");

                    sizeToScene();

                    App.prefs.put("username", cDialog.getName());

                } catch (Exception ex) {
                    eLabel.setText(ex.getMessage());
                    eLabel.setVisible(true);

                }

            });

            cDialog.show();

        });

        searchForChallenge = new Button("Search for LAN Challenge");
        searchForChallenge.setOnAction(e -> {

            try {

                search = new ChallengeSearchDialog(getScene().getWindow(), gameCreatedCallbackSearcher);
                search.setOnHidden(we -> {

                    client = search.getClient();
                    search.getSearcher().stop();

                });

                search.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        btns.getChildren().addAll(createChallenge, searchForChallenge, startButton, cancelButton);

        items.getChildren().addAll(checkHBox, perSide, perMove, errorLabel, btns);

        setX(window.getX() + (window.getWidth() / 3.0));
        setY(window.getY() + (window.getHeight() / 3.0));
        setScene(s);
        setTitle("Configure Game Settings");

        setOnShown(e -> {

            sizeToScene();

            if (board.getGame() != null) {

                Dialog<ButtonType> confirm = new Dialog<>();
                confirm.initOwner(getScene().getWindow());
                confirm.setContentText("Starting a new game will stop the current one. Are you sure?");
                confirm.setTitle("Confirm New Game");

                ButtonType yes = new ButtonType("Yes", ButtonData.OK_DONE);
                ButtonType no = new ButtonType("No", ButtonData.CANCEL_CLOSE);

                confirm.getDialogPane().getButtonTypes().addAll(yes, no);

                confirm.setOnHidden(ev -> {
                    if (confirm.getResult().getText().equals("Yes")) {

                        if (board.getGame().getResult() == Game.RESULT_IN_PROGRESS)
                            board.getGame().markGameOver(Game.RESULT_TERMINATED, Game.REASON_OTHER);

                        if (board.getClient() != null)
                            board.getClient().stop();

                        board.setGame(null);
                        board.setPos(0);
                        board.setClient(null);

                        board.getBoard().boardUpdated();

                    }
                });

                confirm.showAndWait();

            }

        });

    }
}
