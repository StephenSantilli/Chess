package gui.dialog;

import java.util.ArrayList;

import game.Game;
import game.GameSettings;
import game.Player;
import game.LAN.Challenge;
import game.LAN.ChallengeServer;
import game.LAN.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CreateGame extends Stage {

    private TextField oneField, twoField;
    private ChoiceBox<String> color, type;
    private CheckBox useTimeBox;
    private Spinner<Integer> minPerSide, secPerSide, minPerMove, secPerMove;
    private Label sLabel;
    private Button search, cancel, start;

    private boolean white;

    private ChallengeServer server;

    private Game game;
    private Client client;
    private boolean create;

    public boolean isWhite() {
        return white;
    }

    public Game getGame() {
        return game;
    }

    public Client getClient() {
        return client;
    }

    public boolean isCreate() {
        return create;
    }

    public CreateGame(Window window) {

        // initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().setAll(((Stage) (window)).getIcons());

        create = false;

        // Player Settings Boxes
        Label oneLabel = new Label("Player 1 Name:");
        oneField = new TextField("Player 1");
        // HBox oneNameArea = new HBox(oneLabel, oneField);

        color = new ChoiceBox<String>();
        color.getItems().addAll("Random", "White", "Black");
        color.setValue("Random");

        VBox oneBox = new VBox(oneLabel, oneField, color);
        oneBox.setFillWidth(true);
        // oneBox.setSpacing(5);

        Label twoLabel = new Label("Player 2 Name:");
        twoField = new TextField("Player 2");
        // HBox twoNameArea = new HBox(twoLabel, twoField);

        type = new ChoiceBox<String>();
        type.getItems().addAll("Local", "Online"/* , "Bot" */);
        type.setValue("Local");

        type.setOnAction(ae -> {

            boolean local = type.getValue().equals("Local");

            twoField.setDisable(!local);

        });

        VBox twoBox = new VBox(twoLabel, twoField, type);
        twoBox.setFillWidth(true);
        // twoBox.setSpacing(5);

        HBox players = new HBox(oneBox, twoBox);
        players.setAlignment(Pos.CENTER);
        players.setFillHeight(true);
        // players.setSpacing(10);
        HBox.setHgrow(oneBox, Priority.ALWAYS);
        HBox.setHgrow(twoBox, Priority.ALWAYS);

        // Time Presets
        HBox presets = new HBox();

        // Time control options
        useTimeBox = new CheckBox("Use a time control");
        useTimeBox.setSelected(false);

        HBox timeOpts = new HBox(useTimeBox);
        timeOpts.setAlignment(Pos.CENTER_LEFT);

        // Time Control
        HBox perSide = new HBox();
        perSide.setAlignment(Pos.CENTER_LEFT);
        // perSide.setSpacing(5);
        Label perSideLabel = new Label("Time per side (M:S): ");
        Label perSideDivider = new Label(":");

        minPerSide = new Spinner<Integer>(0, 9999, 10);
        secPerSide = new Spinner<Integer>(0, 59, 0);

        minPerSide.setEditable(true);
        secPerSide.setEditable(true);

        minPerSide.setPrefWidth(75);
        secPerSide.setPrefWidth(75);

        perSide.getChildren().addAll(perSideLabel, minPerSide, perSideDivider, secPerSide);

        HBox perMove = new HBox();
        perMove.setAlignment(Pos.CENTER_LEFT);

        // perMove.setSpacing(5);
        Label perMoveLabel = new Label("Time added per move (M:S): ");
        Label perMoveDivider = new Label(":");

        IntegerSpinnerValueFactory smVF = new IntegerSpinnerValueFactory(0, 9999, 0);
        IntegerSpinnerValueFactory ssVF = new IntegerSpinnerValueFactory(0, 59, 0, 1);

        minPerMove = new Spinner<Integer>(smVF);
        secPerMove = new Spinner<Integer>(ssVF);

        minPerMove.setEditable(true);
        secPerMove.setEditable(true);
        minPerMove.setPrefWidth(75);
        secPerMove.setPrefWidth(75);

        perMove.getChildren().addAll(perMoveLabel, minPerMove, perMoveDivider, secPerMove);

        VBox timeControl = new VBox(perSide, perMove);
        // timeControl.setSpacing(10);
        timeControl.setFillWidth(true);

        setDisabledTime(true);

        useTimeBox.setOnAction(ev -> {
            setDisabledTime(!useTimeBox.isSelected());
        });

        // Status Label
        sLabel = new Label("");
        sLabel.setVisible(false);
        HBox statusLabel = new HBox(sLabel);

        // Buttons
        search = new Button("Search for LAN Challenge");
        search.setOnAction(this::searchAction);

        start = new Button("Start");
        start.setOnAction(this::startAction);

        cancel = new Button("Cancel");
        cancel.setOnAction(this::cancelAction);

        HBox btns = new HBox(search, start, cancel);
        btns.setAlignment(Pos.CENTER_RIGHT);
        // btns.setSpacing(10);

        VBox stgs = new VBox(players, presets, timeOpts, timeControl, statusLabel, btns);
        stgs.setFillWidth(true);
        stgs.setPadding(new Insets(10));
        // stgs.setSpacing(10);
        stgs.setAlignment(Pos.CENTER);

        Scene s = new Scene(stgs);
        s.getStylesheets().add(getClass().getResource("/css/style.css").toString());
        s.getStylesheets().add(getClass().getResource("/css/theme.css").toString());

        setOnShown(we -> {

            sizeToScene();
            setWidth(400);
            setMinWidth(400);
            setMinHeight(getHeight());
            setMaxHeight(getHeight());
            setMaxWidth(getWidth());

        });

        setTitle("Create Game");
        setScene(s);

    }

    private void showLabel(String text, boolean error) {

        sLabel.setVisible(true);
        sLabel.setTextFill(error ? Color.RED : Color.BLACK);
        sLabel.setText(text);

    }

    private void clearLabel() {
        sLabel.setVisible(false);
        sLabel.setText("");
    }

    private void searchAction(ActionEvent ae) {

        try {

            SearchDialog sd = new SearchDialog(getScene().getWindow());

            sd.setOnHiding(we -> {

                sd.getSearcher().stop();

                this.client = sd.getClient();
                if (client == null)
                    return;

                this.game = client.getGame();
                this.create = true;
                this.white = !client.isOppColor();

                hide();

            });

            sd.show();

        } catch (Exception e) {
            showLabel("Error opening search dialog: " + e.getMessage(), true);
        }

    }

    private void startAction(ActionEvent ae) {

        if (type.getValue().equals("Local")) {

            try {

                boolean oneWhite = color.getValue().equals("White");

                if (color.getValue().equals("Random"))
                    oneWhite = Math.random() >= 0.5;

                white = oneWhite;

                long timePerSide = useTimeBox.isSelected() ? ((minPerSide.getValue() * 60) + (secPerSide.getValue())) : -1;
                long timePerMove = useTimeBox.isSelected() ? ((minPerMove.getValue() * 60) + (secPerMove.getValue())) : -1;

                game = new Game((oneWhite ? oneField.getText() : twoField.getText()),
                        (oneWhite ? twoField.getText() : oneField.getText()),
                        Player.HUMAN,
                        Player.HUMAN,
                        new GameSettings(
                                timePerSide,
                                timePerMove,
                                true,
                                true,
                                true,
                                true));

                create = true;
                hide();

            } catch (Exception e) {

                showLabel(e.getMessage(), true);

            }

        } else if (type.getValue().equals("Online")) {

            try {

                // boolean oneWhite = color.getValue().equals("White");

                // if (color.getValue().equals("Random"))
                // oneWhite = Math.random() >= 0.5;

                // white = oneWhite;

                int c = Challenge.CHALLENGE_RANDOM;
                switch (color.getValue()) {
                    case "White":
                        c = Challenge.CHALLENGE_WHITE;
                        break;
                    case "Black":
                        c = Challenge.CHALLENGE_BLACK;
                        break;
                }

                Runnable gameCreated = () -> {

                    client = server.getClient();
                    game = client.getGame();
                    create = true;
                    server.stop();

                    Platform.runLater(() -> {
                        hide();
                    });

                };

                server = new ChallengeServer(
                        new Challenge(oneField.getText(), c, ((minPerSide.getValue() * 60) + (secPerSide.getValue())),
                                ((minPerMove.getValue() * 60) + (secPerMove.getValue())), null),
                        gameCreated);

                server.start();

                showLabel("Challenge visible...", false);
                setAllDisabled(true);

            } catch (Exception e) {

                showLabel(e.getMessage(), true);
                setAllDisabled(false);
                clearLabel();

            }

        }

    }

    private void setAllDisabled(boolean disable) {
        start.setDisable(disable);
        search.setDisable(disable);
        oneField.setDisable(disable);
        twoField.setDisable(disable);
        color.setDisable(disable);
        type.setDisable(disable);
        useTimeBox.setDisable(disable);
        minPerSide.setDisable(disable);
        secPerSide.setDisable(disable);
        minPerMove.setDisable(disable);
        secPerMove.setDisable(disable);
    }

    private void cancelAction(ActionEvent ae) {

        if (server != null) {

            server.stop();
            setAllDisabled(false);
            clearLabel();
            server = null;

        } else {
            create = false;
            game = null;
            client = null;

            hide();
        }

    }

    private void setDisabledTime(boolean disable) {
        minPerSide.setDisable(disable);
        secPerSide.setDisable(disable);
        minPerMove.setDisable(disable);
        secPerMove.setDisable(disable);
    }

}
