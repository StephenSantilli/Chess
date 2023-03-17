package gui;

import java.util.ArrayList;

import game.Game;
import game.LAN.Host;
import game.LAN.Searcher;
import game.LAN.Server;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

public class ChallengeSearchDialog extends Stage {

    private int timePerSide, timePerMove;

    private ObservableList<Host> oList;

    private Searcher searcher;

    public int getTimePerMove() {
        return timePerMove;
    }

    public int getTimePerSide() {
        return timePerSide;
    }

    private Runnable hostUpdateChecker = () -> {

        try {

            while (getScene().getWindow().isShowing()) {

                setOList(searcher.getHosts());
                Thread.sleep(500);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    public ChallengeSearchDialog(Window window, Game game) throws Exception {

        initOwner(window);
        // initModality(Modality.WINDOW_MODAL);

        searcher = new Searcher();
        searcher.search();

        VBox items = new VBox();

        oList = FXCollections.observableArrayList();
        TableView<Host> hostList = new TableView<Host>(oList);
        TableColumn<Host, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Host, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<Host, String> p) {
                        return new ReadOnlyObjectWrapper<>(p.getValue().getName());
                    }
                });

        TableColumn<Host, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Host, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<Host, String> p) {
                        return new ReadOnlyObjectWrapper<>(p.getValue().getAddress().toString());
                    }
                });

        hostList.getColumns().setAll(nameCol, addressCol);

        items.getChildren().add(hostList);

        Scene s = new Scene(items);
        setOnShown(e -> {
            new Thread(hostUpdateChecker).start();
            sizeToScene();
        });

        setTitle("Configure Game Settings");
        setScene(s);

    }

    private void setOList(ArrayList<Host> hosts) {

        Platform.runLater(() -> {

            oList.setAll(hosts);

        });

    }

}
