package gui.dialog;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import game.GameSettings;
import game.LAN.Challenge;
import game.LAN.ChallengeSearcher;
import game.LAN.Client;
import gui.App;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * The dialog that allows the user to search for LAN challenges on their local
 * network.
 */
public class SearchLAN extends Stage {

    /**
     * The challenges found by searching.
     */
    private ArrayList<Challenge> challenges;

    /**
     * An observable list of challenges found (mirrors {@link #challenges}). This is
     * used by the {@link #challengeTable}.
     */
    private ObservableList<Challenge> observableChallengesList;

    /**
     * The client used to search for challenges.
     * 
     * @see ChallengeSearcher
     */
    private ChallengeSearcher searcher;

    /**
     * The client created if the user accepts a LAN challenge. Will be {@code null}
     * if the user has not accepted a challenge.
     */
    private Client client;

    /**
     * The button used to refresh the search results.
     */
    private Button refresh;

    /**
     * The table that displays the challenges found.
     */
    private TableView<Challenge> challengeTable;

    /**
     * The task that is run which will constantly check if new challenges have been
     * found and subsequently update {@link #observableChallengesList}.
     */
    private Runnable hostUpdateChecker = () -> {

        try {

            while (getScene().getWindow().isShowing()) {

                challenges = searcher.getChallenges();
                setOList();
                Thread.sleep(250);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    /**
     * The callback that is to be run when the {@link ChallengeSearcher} is finished
     * searching for challenges.
     */
    private Runnable searchDoneCallback = () -> {

        Platform.runLater(() -> {

            refresh.setText("Refresh");
            refresh.setDisable(false);

            challengeTable.setPlaceholder(new Label("No challenges found."));

        });

    };

    /**
     * Creates a new dialog which the user can use to search for LAN challenges.
     * 
     * @param window The window that is the owner of this dialog.
     * @throws Exception If there is an error initializing the
     *                   {@link ChallengeSearcher} client.
     */
    public SearchLAN(Window window) throws Exception {

        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);

        getIcons().setAll(((Stage) (window)).getIcons());

        searcher = new ChallengeSearcher();
        challenges = new ArrayList<Challenge>();

        observableChallengesList = FXCollections.observableArrayList();

        challengeTable = new TableView<Challenge>(observableChallengesList);
        challengeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        challengeTable.setPlaceholder(new Label("Searching for challenges..."));

        VBox items = new VBox();
        items.setPadding(new Insets(10));

        TableColumn<Challenge, String> nameCol = new TableColumn<>("Name");

        nameCol.setCellValueFactory(p -> {
            return new ReadOnlyObjectWrapper<>(p.getValue().getName());
        });

        TableColumn<Challenge, String> fenCol = new TableColumn<>("Starting FEN");

        fenCol.setCellValueFactory(p -> {
            return new ReadOnlyObjectWrapper<>(p.getValue().getFen().equals(GameSettings.DEFAULT_FEN)
                    ? "Default"
                    : p.getValue().getFen());
        });

        TableColumn<Challenge, String> colorCol = new TableColumn<>("Your Color");

        colorCol.setCellValueFactory(p -> {
            return new ReadOnlyObjectWrapper<>(
                    p.getValue().getColor() == Challenge.CHALLENGE_RANDOM ? "Random"
                            : (p.getValue().getColor() == Challenge.CHALLENGE_WHITE ? "Black" : "White"));
        });

        TableColumn<Challenge, String> sideTimeCol = new TableColumn<>("Time Per\nSide (s)");

        sideTimeCol.setCellValueFactory(p -> {
            return new ReadOnlyObjectWrapper<>(
                    p.getValue().getTimePerSide() > 0 ? (p.getValue().getTimePerSide() + "") : "-");
        });

        TableColumn<Challenge, String> moveTimeCol = new TableColumn<>("Time Added\nPer Move (s)");

        moveTimeCol.setCellValueFactory(p -> {
            return new ReadOnlyObjectWrapper<>(
                    p.getValue().getTimePerMove() > 0 ? (p.getValue().getTimePerMove() + "") : "-");
        });

        TableColumn<Challenge, String> addressCol = new TableColumn<>("Address");

        addressCol.setCellValueFactory(p -> {
            return new ReadOnlyObjectWrapper<>(p.getValue().getAddress().toString());
        });

        challengeTable.setRowFactory(tv -> {

            TableRow<Challenge> row = new TableRow<>();
            row.setOnMouseClicked(me -> {

                if (me.getButton().equals(MouseButton.PRIMARY) && me.getClickCount() >= 2) {

                    try {

                        Runnable gameCreated = () -> {

                            searcher.stop();
                            Platform.runLater(() -> {
                                if (isShowing())
                                    hide();
                            });

                        };

                        client = new Client(row.getItem().getAddress(),
                                App.prefs.get("p1name", "User"),
                                -1,
                                null,
                                gameCreated);

                        client.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            return row;

        });

        challengeTable.getColumns()
                .setAll(Arrays.asList(nameCol, fenCol, colorCol, sideTimeCol, moveTimeCol, addressCol));

        HBox btns = new HBox();
        btns.setAlignment(Pos.CENTER_RIGHT);

        Button directConnect = new Button("Direct Connect");
        directConnect.setOnAction(ev -> {

            TextInputDialog dcDiag = new TextInputDialog();

            dcDiag.setTitle("Direct Connect");
            dcDiag.setHeaderText("Enter IP address...");
            dcDiag.setGraphic(null);

            dcDiag.getEditor().setMinWidth(200);

            dcDiag.getDialogPane().getButtonTypes().setAll(new ButtonType("Connect",
                    ButtonData.OK_DONE),
                    ButtonType.CANCEL);

            dcDiag.initOwner(getScene().getWindow());

            dcDiag.setOnHidden(we -> {
                try {

                    Runnable gameCreated = () -> {

                        searcher.stop();

                        Platform.runLater(() -> {

                            hide();

                        });

                    };

                    if (dcDiag.getResult() != null && !dcDiag.getResult().equals("")) {

                        client = new Client(InetAddress.getByName(dcDiag.getResult()),
                                App.prefs.get("p1name", "User"),
                                -1,
                                null, gameCreated);

                        client.start();

                    }

                } catch (Exception e) {

                    client = null;

                    Dialog<Void> eDg = new Dialog<>();
                    eDg.initOwner(getScene().getWindow());
                    eDg.setTitle("Error Creating Game");
                    eDg.setContentText("Error connecting: " + e.getMessage());

                    eDg.getDialogPane().getButtonTypes().add(ButtonType.OK);

                    eDg.showAndWait();

                }
            });

            dcDiag.show();

        });

        refresh = new Button("Searching...");
        refresh.setDisable(true);

        refresh.setOnAction(ev -> {

            try {

                refresh.setText("Searching...");
                refresh.setDisable(true);

                challengeTable.setPlaceholder(new Label("Searching for challenges..."));

                searcher.stop();
                searcher.search(searchDoneCallback);

            } catch (Exception e) {

            }

        });

        btns.getChildren().addAll(directConnect, refresh);

        items.getChildren().addAll(challengeTable, btns);

        searcher.search(searchDoneCallback);

        Scene s = new Scene(items);
        s.setFill(Color.TRANSPARENT);
        s.getStylesheets().setAll(window.getScene().getStylesheets());

        setOnShown(we -> {

            new Thread(hostUpdateChecker, "Host Update Checker").start();
            sizeToScene();

        });

        setTitle("Search for LAN Challenges");
        setScene(s);

    }

    /**
     * Gets the client created if the user accepts a LAN challenge. Will be
     * {@code null}
     * if the user has not accepted a challenge.
     * 
     * @return The {@link Client}.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Gets the client used to search for challenges.
     * 
     * @return The {@link ChallengeSearcher}
     */
    public ChallengeSearcher getSearcher() {
        return searcher;
    }

    /**
     * Updates the observable list.
     */
    private void setOList() {

        Platform.runLater(() -> {

            observableChallengesList.setAll(challenges);

        });

    }

}
