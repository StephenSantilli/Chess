package gui;

import game.Game;
import game.LAN.Searcher;
import game.LAN.Server;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class GameSettingsDialog extends Stage {

    private int timePerSide, timePerMove;

    public int getTimePerMove() {
        return timePerMove;
    }

    public int getTimePerSide() {
        return timePerSide;
    }

    public GameSettingsDialog(Window window, Game game) {

        initOwner(window);
        initModality(Modality.WINDOW_MODAL);

        VBox items = new VBox();
        items.setSpacing(5);
        items.setPadding(new Insets(10, 10, 10, 10));
        
        Scene s = new Scene(items);

        HBox perSide = new HBox();
        perSide.setSpacing(5);
        Label perSideLabel = new Label("Time per side (M:S): ");
        Label perSideDivider = new Label(":");

        Spinner<Integer> minPerSide = new Spinner<Integer>(0, 9999, 10);
        Spinner<Integer> secPerSide = new Spinner<Integer>(0, 60, 0);

        minPerSide.setPrefWidth(100);
        secPerSide.setPrefWidth(100);

        perSide.getChildren().addAll(perSideLabel, minPerSide, perSideDivider, secPerSide);

        HBox perMove = new HBox();
        perMove.setSpacing(5);
        Label perMoveLabel = new Label("Time added per move (M:S): ");
        Label perMoveDivider = new Label(":");

        IntegerSpinnerValueFactory smVF = new IntegerSpinnerValueFactory(0, 9999, 0);
        IntegerSpinnerValueFactory ssVF = new IntegerSpinnerValueFactory(0, 59, 0, 1);

        Spinner<Integer> minPerMove = new Spinner<Integer>(smVF);
        Spinner<Integer> secPerMove = new Spinner<Integer>(ssVF);

        minPerMove.setPrefWidth(100);
        secPerMove.setPrefWidth(100);

        perMove.getChildren().addAll(perMoveLabel, minPerMove, perMoveDivider, secPerMove);

        HBox btns = new HBox();
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setSpacing(5);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            timePerSide = -1;
            timePerMove = -1;
            hide();
        });

        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            timePerSide = ((minPerSide.getValue() * 60) + (secPerSide.getValue()));
            timePerMove = ((minPerMove.getValue() * 60) + (secPerMove.getValue()));
            hide();
        });

        Button createChallenge = new Button("Create Challenge");
        createChallenge.setOnAction(e -> {

            try {

                Server serv = new Server("test");
                serv.start();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        Button searchForChallenge = new Button("Search for Challenge");
        searchForChallenge.setOnAction(e -> {

            try {

                ChallengeSearchDialog search = new ChallengeSearchDialog(getScene().getWindow(), game);
                search.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        btns.getChildren().addAll(createChallenge, searchForChallenge, cancelButton, startButton);

        items.getChildren().addAll(perSide, perMove, btns);

        setOnShown(e -> {
            sizeToScene();
        });

        setTitle("Configure Game Settings");
        setScene(s);

    }

}
