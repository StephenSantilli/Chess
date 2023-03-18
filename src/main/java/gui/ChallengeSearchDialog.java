package gui;

import java.util.ArrayList;

import game.Game;
import game.LAN.Challenge;
import game.LAN.Searcher;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

public class ChallengeSearchDialog extends Stage {

    private int timePerSide, timePerMove;

    private ObservableList<Challenge> oList;

    private Searcher searcher;

    private ArrayList<Challenge> hosts;

    public int getTimePerMove() {
        return timePerMove;
    }

    public int getTimePerSide() {
        return timePerSide;
    }

    private Runnable hostUpdateChecker = () -> {

        try {

            while (getScene().getWindow().isShowing()) {
                hosts = searcher.getHosts();
                setOList();
                Thread.sleep(500);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    public ChallengeSearchDialog(Window window, Game game) throws Exception {

        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().setAll(((Stage) (window)).getIcons());

        searcher = new Searcher();
        searcher.search();

        hosts = new ArrayList<Challenge>();

        VBox items = new VBox();

        oList = FXCollections.observableArrayList();
        TableView<Challenge> hostList = new TableView<Challenge>(oList);
        hostList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Challenge, String> nameCol = new TableColumn<>("Name");
        nameCol.setMaxWidth(Integer.MAX_VALUE);
        nameCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Challenge, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<Challenge, String> p) {
                        return new ReadOnlyObjectWrapper<>(p.getValue().getName());
                    }
                });

        TableColumn<Challenge, String> colorCol = new TableColumn<>("Your Color");
        colorCol.setMaxWidth(Integer.MAX_VALUE);

        colorCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Challenge, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<Challenge, String> p) {
                        return new ReadOnlyObjectWrapper<>(
                                p.getValue().getColor() == Challenge.CHALLENGE_RANDOM ? "Random"
                                        : (p.getValue().getColor() == Challenge.CHALLENGE_WHITE ? "Black" : "White"));
                    }
                });

        TableColumn<Challenge, String> sideTimeCol = new TableColumn<>("Time Per Side (s)");
        sideTimeCol.setMaxWidth(Integer.MAX_VALUE);

        sideTimeCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Challenge, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<Challenge, String> p) {
                        return new ReadOnlyObjectWrapper<>(p.getValue().getTimePerSide() + "");
                    }
                });

        TableColumn<Challenge, String> moveTimeCol = new TableColumn<>("Time Added Per Move (s)");
        moveTimeCol.setMaxWidth(Integer.MAX_VALUE);

        moveTimeCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Challenge, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<Challenge, String> p) {
                        return new ReadOnlyObjectWrapper<>(p.getValue().getTimePerMove() + "");
                    }
                });

        TableColumn<Challenge, String> addressCol = new TableColumn<>("Address");
        addressCol.setMaxWidth(Integer.MAX_VALUE);

        addressCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Challenge, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<Challenge, String> p) {
                        return new ReadOnlyObjectWrapper<>(p.getValue().getAddress().toString());
                    }
                });

        hostList.getColumns().setAll(nameCol, colorCol, sideTimeCol, moveTimeCol, addressCol);

        items.getChildren().add(hostList);

        Scene s = new Scene(items);
        setOnShown(we -> {
            new Thread(hostUpdateChecker).start();
            sizeToScene();
        });

        setOnHidden(we -> {
            searcher.stop();
        });

        setTitle("Search for Challenges");
        setScene(s);

    }

    private void setOList() {

        Platform.runLater(() -> {

            oList.setAll(hosts);

        });

    }

}
