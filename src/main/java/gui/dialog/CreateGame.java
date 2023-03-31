package gui.dialog;

import java.util.ArrayList;

import game.Game;
import game.GameSettings;
import game.Player;
import game.LAN.Client;
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
    private Button cancel, start;

    private boolean white;

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
        HBox oneNameArea = new HBox(oneLabel, oneField);

        color = new ChoiceBox<String>();
        color.getItems().addAll("Random", "White", "Black");

        VBox oneBox = new VBox(oneNameArea, color);

        Label twoLabel = new Label("Player 2 Name:");
        twoField = new TextField("Player 2");
        HBox twoNameArea = new HBox(twoLabel, twoField);

        type = new ChoiceBox<String>();
        type.getItems().addAll("Local", "Online"/* , "Bot" */);
        type.setOnAction(ae -> {

            boolean local = type.getValue().equals("Local");

            twoField.setDisable(!local);

        });

        VBox twoBox = new VBox(twoNameArea, type);

        HBox players = new HBox(oneBox, twoBox);
        players.setAlignment(Pos.CENTER);
        players.setFillHeight(true);
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
        perSide.setSpacing(5);
        Label perSideLabel = new Label("Time per side (M:S): ");
        Label perSideDivider = new Label(":");

        minPerSide = new Spinner<Integer>(0, 9999, 10);
        secPerSide = new Spinner<Integer>(0, 59, 0);

        minPerSide.setEditable(true);
        secPerSide.setEditable(true);

        minPerSide.setPrefWidth(100);
        secPerSide.setPrefWidth(100);

        perSide.getChildren().addAll(perSideLabel, minPerSide, perSideDivider, secPerSide);

        HBox perMove = new HBox();
        perMove.setAlignment(Pos.CENTER_LEFT);

        perMove.setSpacing(5);
        Label perMoveLabel = new Label("Time added per move (M:S): ");
        Label perMoveDivider = new Label(":");

        IntegerSpinnerValueFactory smVF = new IntegerSpinnerValueFactory(0, 9999, 0);
        IntegerSpinnerValueFactory ssVF = new IntegerSpinnerValueFactory(0, 59, 0, 1);

        minPerMove = new Spinner<Integer>(smVF);
        secPerMove = new Spinner<Integer>(ssVF);

        minPerMove.setEditable(true);
        secPerMove.setEditable(true);
        minPerMove.setPrefWidth(100);
        secPerMove.setPrefWidth(100);

        perMove.getChildren().addAll(perMoveLabel, minPerMove, perMoveDivider, secPerMove);

        VBox timeControl = new VBox(perSide, perMove);

        setDisabledTime(true);

        useTimeBox.setOnAction(ev -> {
            setDisabledTime(!useTimeBox.isSelected());
        });

        // Status Label
        sLabel = new Label("");
        sLabel.setVisible(false);
        HBox statusLabel = new HBox(sLabel);

        // Buttons
        start = new Button("Start");
        start.setOnAction(this::startAction);

        cancel = new Button("Cancel");
        cancel.setOnAction(this::cancelAction);

        HBox btns = new HBox(start, cancel);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setSpacing(10);

        VBox stgs = new VBox(players, presets, timeOpts, timeControl, statusLabel, btns);
        stgs.setFillWidth(true);
        stgs.setPadding(new Insets(10));
        stgs.setSpacing(10);
        stgs.setAlignment(Pos.CENTER);

        Scene s = new Scene(stgs);

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

    private void startAction(ActionEvent ae) {

        if (type.getValue().equals("Local")) {

            try {

                boolean oneWhite = color.getValue().equals("White");

                if (color.getValue().equals("Random"))
                    oneWhite = Math.random() >= 0.5;

                white = oneWhite;

                game = new Game((oneWhite ? oneField.getText() : twoField.getText()),
                        (oneWhite ? twoField.getText() : oneField.getText()),
                        Player.HUMAN,
                        Player.HUMAN,
                        new GameSettings(
                                ((minPerSide.getValue() * 60) + (secPerSide.getValue())),
                                ((minPerMove.getValue() * 60) + (secPerMove.getValue())),
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

        }

    }

    private void cancelAction(ActionEvent ae) {

        create = false;
        game = null;
        client = null;

    }

    private void setDisabledTime(boolean disable) {
        minPerSide.setDisable(disable);
        secPerSide.setDisable(disable);
        minPerMove.setDisable(disable);
        secPerMove.setDisable(disable);
    }

}
