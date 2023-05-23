package gui.dialog;

import java.io.File;
import java.util.Random;
import game.Game;
import game.GameSettings;
import game.Player;
import game.Position;
import game.LAN.Challenge;
import game.LAN.ChallengeServer;
import game.LAN.Client;
import game.PGN.PGNParser;
import game.engine.EngineHook;
import game.engine.UCIEngine;
import gui.App;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * The dialog the user uses to create a new game.
 */
public class CreateGame extends Stage {

    /**
     * The name of player one.
     */
    private TextField oneName;

    /**
     * The name of player two.
     */
    private TextField twoName;

    /**
     * The field which allows the user to enter a custom starting position in FEN
     * format.
     */
    private TextField fenField;

    /**
     * The dropdown box which allows the user to select if player one should be
     * white, black, or a random color.
     */
    private ChoiceBox<String> color;

    /**
     * The dropdown box which allows the user to select the type of player that
     * player two should be.
     */
    private ChoiceBox<String> type;

    /**
     * The checkbox which allows the user to select if a time control should be
     * used.
     */
    private CheckBox useTimeBox;

    /**
     * The checkbox which allows the user to select if they would like to start from
     * a custom position.
     */
    private CheckBox useFenBox;

    /**
     * A spinner which allows the user to set how many minutes each side will get.
     */
    private Spinner<Integer> minPerSide;

    /**
     * A spinner which allows the user to set how many seconds each side will get.
     */
    private Spinner<Integer> secPerSide;

    /**
     * A spinner which allows the user to set how many minutes each side will get
     * added on per move.
     */
    private Spinner<Integer> minPerMove;

    /**
     * A spinner which allows the user to set how many seconds each side will get
     * added on per move.
     */
    private Spinner<Integer> secPerMove;

    /**
     * A spinner which allows the user to choose which Chess960 starting position to
     * start from.
     */
    private Spinner<Integer> startId;

    /**
     * A label which is used to display information or errors.
     */
    private Label sLabel;

    /**
     * A button which, when clicked, shows the user the {@link ImportPGN} prompt.
     */
    private Button fromPgn;

    /**
     * A button which displays the {@link SearchLAN} dialog.
     */
    private Button search;

    /**
     * A button which cancels the game creating process.
     */
    private Button cancel;

    /**
     * The button that starts the game with the entered settings.
     */
    private Button start;

    /**
     * The button that generates a random Chess960 start.
     */
    private Button gen960;

    /**
     * The button that resets the start position to the default position.
     */
    private Button reset960;

    /**
     * The separator that spaces out {@link #sLabel}.
     */
    private Separator sep;

    /**
     * Whether or not player one is white.
     */
    private boolean white;

    /**
     * The client used to make a challenge discoverable on the LAN network.
     */
    private ChallengeServer server;

    /**
     * The game created by this dialog.
     */
    private Game game;

    /**
     * The LAN client of the game created by this dialog.
     */
    private Client client;

    /**
     * The engine hook associated with the game created by this dialog.
     */
    private EngineHook engine;

    /**
     * Whether or not a game should be created.
     */
    private boolean create;

    /**
     * Creates a new game creation dialog.
     * 
     * @param window The window that owns this dialog.
     */
    public CreateGame(Window window) {

        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().setAll(((Stage) (window)).getIcons());
        setResizable(false);

        create = false;

        // Player Settings Boxes
        Label oneLabel = new Label("Player 1:");
        oneName = new TextField(App.prefs.get("p1name", "Player 1"));

        color = new ChoiceBox<String>();
        color.getItems().addAll("Random", "White", "Black");
        color.setValue("Random");
        color.setMaxWidth(Double.MAX_VALUE);

        VBox oneBox = new VBox(oneLabel, color, oneName);
        oneBox.setFillWidth(true);
        oneBox.setSpacing(5);

        Label twoLabel = new Label("Player 2:");
        twoName = new TextField(App.prefs.get("p2name", "Player 2"));

        type = new ChoiceBox<String>();
        type.setMaxWidth(Double.MAX_VALUE);

        type.getItems().setAll("Two Player", "Online");

        String st = App.prefs.get("Engines", "");
        if (!st.equals("")) {
            String[] ss = st.split(";");
            for (String e : ss) {
                type.getItems().add("Engine (" + e + ")");
            }
            type.getItems().add("Clear engines...");

        }

        type.getItems().add("Register a new engine...");

        type.setValue("Two Player");

        type.setOnAction(ae -> {

            if (type.getValue().startsWith("Register")) {
                ae.consume();
                registerNew();

                type.getItems().setAll("Two Player", "Online");

                String str = App.prefs.get("Engines", "");
                if (!str.equals("")) {
                    String[] ss = str.split(";");
                    for (String e : ss) {
                        type.getItems().add("Engine (" + e + ")");
                    }
                    type.getItems().add("Clear engines...");

                }

                type.getItems().add("Register a new engine...");

                type.setValue("Two Player");

                return;

            } else if (type.getValue().startsWith("Clear")) {
                ae.consume();
                type.getItems().setAll("Two Player", "Online");

                App.prefs.put("Engines", "");

                type.getItems().add("Register a new engine...");

                clearLabel();
                type.setValue("Two Player");

                return;

            }

            boolean local = !type.getValue().equals("Online");

            twoName.setDisable(!type.getValue().equals("Two Player"));
            start.setText(local ? "Start" : "Send Challenge");
            sizeToScene();

        });

        VBox twoBox = new VBox(twoLabel, type, twoName);
        twoBox.setFillWidth(true);
        twoBox.setSpacing(5);

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
        timeControl.setFillWidth(true);

        setDisabledTime(true);

        useTimeBox.setOnAction(ev -> {
            setDisabledTime(!useTimeBox.isSelected());
        });

        useFenBox = new CheckBox("Use custom FEN");
        useFenBox.setSelected(false);

        useFenBox.setOnAction(ev -> {
            setDisabledFen(!useFenBox.isSelected());
        });

        gen960 = new Button("Randomize (Chess960)");
        gen960.setOnAction(ae -> generate960());
        gen960.setDisable(true);

        reset960 = new Button("Reset");
        reset960.setOnAction(ae -> startId.getValueFactory().setValue(518));
        reset960.setDisable(true);

        IntegerSpinnerValueFactory siVf = new IntegerSpinnerValueFactory(0, 959, 518);
        startId = new Spinner<>(siVf);
        startId.setPrefWidth(75);
        startId.setDisable(true);
        startId.setEditable(true);

        startId.valueProperty().addListener(v -> {
            try {

                fenField.setText(Position.generate960Start(startId.getValue()));

            } catch (Exception e) {
                showLabel(e.getMessage(), true);

            }
        });

        HBox fenOpts = new HBox(useFenBox, gen960, reset960, startId);
        fenOpts.setSpacing(5);
        fenOpts.setAlignment(Pos.CENTER_LEFT);

        fenField = new TextField(GameSettings.DEFAULT_FEN);
        fenField.setDisable(true);

        VBox fen = new VBox(fenOpts, fenField);
        fen.setSpacing(5);
        fen.setFillWidth(true);

        // Status Label
        sep = new Separator(Orientation.HORIZONTAL);

        sLabel = new Label("");
        sLabel.setVisible(false);
        sep.setVisible(false);
        HBox statusLabel = new HBox(sLabel);

        // Buttons
        fromPgn = new Button("Start from PGN");
        fromPgn.setOnAction(ae -> importPgn());

        search = new Button("Search for LAN Challenge");
        search.setOnAction(ae -> searchAction());

        start = new Button("Start");
        start.setOnAction(ae -> startAction(null));

        cancel = new Button("Cancel");
        cancel.setOnAction(ae -> cancelAction());

        HBox btns = new HBox(fromPgn, search, start, cancel);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setSpacing(5);

        VBox opts = new VBox(players, presets, timeOpts, timeControl, fen, statusLabel, btns);
        opts.setFillWidth(true);
        opts.setPadding(new Insets(10));
        opts.setAlignment(Pos.CENTER);
        VBox.setVgrow(opts, Priority.ALWAYS);

        Scene s = new Scene(opts);
        s.getStylesheets().setAll(window.getScene().getStylesheets());

        setOnShown(we -> {

            sizeToScene();

        });

        setTitle("Create Game");
        setScene(s);

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
     * Gets if the player is white.
     * 
     * @return {@link #white}
     */
    public boolean isWhite() {
        return white;
    }

    /**
     * Gets the game created by this dialog.
     * 
     * @return {@link #game}
     */
    public Game getGame() {
        return game;
    }

    /**
     * Gets the client created by this dialog.
     * 
     * @return {@link #client}
     */
    public Client getClient() {
        return client;
    }

    /**
     * Gets whether or not a game should be created.
     * 
     * @return {@link #create}
     */
    public boolean isCreate() {
        return create;
    }

    /**
     * Prompts the user with a file chooser, where they should select the engine
     * they would like to register.
     */
    private void registerNew() {

        FileChooser chooser = new FileChooser();

        File f = chooser.showOpenDialog(new Stage());

        if (f == null || !f.exists()) {

            Dialog<Void> eDg = new Dialog<>();
            eDg.initOwner(getScene().getWindow());
            eDg.setTitle("Error registering engine.");
            eDg.setContentText("Engine file not found.");

            eDg.getDialogPane().getButtonTypes().add(ButtonType.OK);

            eDg.showAndWait();

            return;

        }

        App.prefs.put("Engine (" + f.getName() + ")", f.getPath());
        String engines = App.prefs.get("Engines", "");
        App.prefs.put("Engines", engines + f.getName() + ";");

    }

    /**
     * Generates a random Chess960 starting position.
     */
    private void generate960() {

        try {

            // Generate random number from 0 to 959
            Random rand = new Random();
            final int sid = rand.nextInt(0, 960);

            startId.getValueFactory().setValue(sid);

        } catch (Exception e) {

            showLabel(e.getMessage(), true);

        }

    }

    /**
     * Prompts the user to import a game that is in the PGN format.
     */
    private void importPgn() {

        ImportPGN pDialog = new ImportPGN(getOwner());

        pDialog.showAndWait();

        if (pDialog.isCreate() && !pDialog.getPgn().equals("")) {

            try {

                PGNParser parser = new PGNParser(pDialog.getPgn());

                startAction(parser);

                // game = new Game(parser,
                // new GameSettings(
                // !useFenBox.isSelected() ? GameSettings.DEFAULT_FEN : fenField.getText(),
                // 0,
                // 0,
                // true,
                // true,
                // true,
                // true),
                // false);

                // create = true;
                // hide();

            } catch (Exception e) {

                showLabel(e.getMessage(), true);

            }

        }

    }

    /**
     * Sets whether or not the FEN fields should be disabled.
     * 
     * @param disable If the fields should be disabled.
     */
    private void setDisabledFen(boolean disable) {
        fenField.setDisable(disable);
        gen960.setDisable(disable);
        startId.setDisable(disable);
        reset960.setDisable(disable);
    }

    /**
     * Shows {@link #sLabel} with the given text.
     * 
     * @param text  The text to set {@link #sLabel} to.
     * @param error Whether or not this message is an error message.
     */
    private void showLabel(String text, boolean error) {

        sLabel.setVisible(true);
        sep.setVisible(true);
        sLabel.setTextFill(error ? Color.RED : Color.BLACK);
        sLabel.setText(text);

    }

    /**
     * Clears {@link #sLabel} and hides it.
     */
    private void clearLabel() {
        sLabel.setVisible(false);
        sep.setVisible(false);
        sLabel.setText("");
    }

    /**
     * Prompts the user with a dialog which allows them to search for LAN challenges
     * on their local network.
     */
    private void searchAction() {

        try {

            App.prefs.put("p1name", oneName.getText());

            SearchLAN sd = new SearchLAN(getScene().getWindow());

            sd.setOnHiding(we -> {

                sd.getSearcher().stop();

                this.client = sd.getClient();
                if (client == null)
                    return;

                this.game = client.getGame();
                this.create = true;
                this.white = !client.isOppWhite();

                hide();

            });

            sd.show();

        } catch (Exception e) {
            showLabel("Error opening search dialog: " + e.getMessage(), true);
        }

    }

    /**
     * Starts the game with the given settings.
     * 
     * @param parser The PGNParser associated with this game.
     */
    private void startAction(PGNParser parser) {

        App.prefs.put("p1name", oneName.getText());
        App.prefs.put("p2name", twoName.getText());

        if (type.getValue().equals("Online") && parser == null) {

            try {

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
                        new Challenge(oneName.getText(),
                                !useFenBox.isSelected() ? GameSettings.DEFAULT_FEN : fenField.getText(),
                                c,
                                ((minPerSide.getValue() * 60) + (secPerSide.getValue())),
                                ((minPerMove.getValue() * 60) + (secPerMove.getValue())), null),
                        gameCreated);

                server.start();

                showLabel("Challenge visible on local network...", false);
                setAllDisabled(true);

            } catch (Exception e) {

                showLabel(e.getMessage(), true);
                setAllDisabled(false);
                clearLabel();

            }

        } else if (type.getValue().startsWith("Engine")) {

            try {

                boolean oneWhite = color.getValue().equals("White");

                if (color.getValue().equals("Random"))
                    oneWhite = Math.random() >= 0.5;

                white = oneWhite;

                long timePerSide = useTimeBox.isSelected() ? ((minPerSide.getValue() * 60) + (secPerSide.getValue()))
                        : -1;
                long timePerMove = useTimeBox.isSelected() ? ((minPerMove.getValue() * 60) + (secPerMove.getValue()))
                        : -1;

                String path = App.prefs.get(type.getValue(), "");

                if (path.equals(""))
                    throw new Exception("Engine not found.");

                UCIEngine en = new UCIEngine(new File(path));

                if (parser == null) {

                    game = new Game((oneWhite ? oneName.getText() : en.getName()),
                            (oneWhite ? en.getName() : oneName.getText()),
                            oneWhite ? Player.Type.HUMAN : Player.Type.PROGRAM,
                            !oneWhite ? Player.Type.HUMAN : Player.Type.PROGRAM,
                            new GameSettings((!useFenBox.isSelected() ? GameSettings.DEFAULT_FEN : fenField.getText()),
                                    timePerSide,
                                    timePerMove,
                                    true,
                                    true,
                                    true,
                                    true));

                } else {

                    game = new Game(parser,
                            new GameSettings(
                                    !useFenBox.isSelected() ? GameSettings.DEFAULT_FEN : fenField.getText(),
                                    0,
                                    0,
                                    true,
                                    true,
                                    true,
                                    true),
                            false);

                }

                engine = new EngineHook(en, game, !oneWhite);

                EngineSettings stgs = new EngineSettings(getOwner(), engine);
                stgs.showAndWait();
                en.waitReady();

                create = true;
                hide();

            } catch (Exception e) {
                e.printStackTrace();
                showLabel(e.getMessage(), true);

            }

        } else {

            try {

                boolean oneWhite = color.getValue().equals("White");

                if (color.getValue().equals("Random"))
                    oneWhite = Math.random() >= 0.5;

                white = oneWhite;

                long timePerSide = useTimeBox.isSelected() ? ((minPerSide.getValue() * 60) + (secPerSide.getValue()))
                        : -1;
                long timePerMove = useTimeBox.isSelected() ? ((minPerMove.getValue() * 60) + (secPerMove.getValue()))
                        : -1;

                if (parser == null) {

                    game = new Game((oneWhite ? oneName.getText() : twoName.getText()),
                            (oneWhite ? twoName.getText() : oneName.getText()),
                            Player.Type.HUMAN,
                            Player.Type.HUMAN,
                            new GameSettings((!useFenBox.isSelected() ? GameSettings.DEFAULT_FEN : fenField.getText()),
                                    timePerSide,
                                    timePerMove,
                                    true,
                                    true,
                                    true,
                                    true));

                } else {

                    game = new Game(parser,
                            new GameSettings(
                                    !useFenBox.isSelected() ? GameSettings.DEFAULT_FEN : fenField.getText(),
                                    0,
                                    0,
                                    true,
                                    true,
                                    true,
                                    true),
                            false);

                }

                create = true;
                hide();

            } catch (Exception e) {

                showLabel(e.getMessage(), true);

            }

        }

    }

    /**
     * Sets all fields as disabled or enabled.
     * 
     * @param disable Whether or not the fields should be disabled or enabled.
     */
    private void setAllDisabled(boolean disable) {
        start.setDisable(disable);
        search.setDisable(disable);
        oneName.setDisable(disable);
        boolean local = type.getValue().equals("Two Player");
        twoName.setDisable(!local);
        color.setDisable(disable);
        type.setDisable(disable);
        useTimeBox.setDisable(disable);
        setDisabledTime(disable ? true : !useTimeBox.isSelected());
        cancel.setText(disable ? "Stop" : "Cancel");
        setDisabledFen(disable ? true : !useFenBox.isSelected());
        fromPgn.setDisable(disable);
        useFenBox.setDisable(disable);
    }

    /**
     * Cancels the game creation process.
     */
    private void cancelAction() {

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

    /**
     * Disables/enables the time fields.
     * 
     * @param disable Whether or not the fields should be disabled.
     */
    private void setDisabledTime(boolean disable) {
        minPerSide.setDisable(disable);
        secPerSide.setDisable(disable);
        minPerMove.setDisable(disable);
        secPerMove.setDisable(disable);
    }

}
