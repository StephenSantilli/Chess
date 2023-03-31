package gui.dialog;

import game.Game;
import game.LAN.Client;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class GameStart extends Stage {

    private TextField field;
    private Button cr, find;

    private Game game;
    private Client client;
    private boolean create;
    private boolean white;

    public Game getGame() {
        return game;
    }

    public Client getClient() {
        return client;
    }

    public boolean isCreate() {
        return create;
    }

    public boolean isWhite() {
        return white;
    }

    public GameStart(Window window) {

        // initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().setAll(((Stage) (window)).getIcons());



        cr = new Button("Create a new game");
        cr.setOnAction(ae -> {

            CreateGame createDialog = new CreateGame(window);

            createDialog.showAndWait();

            game = createDialog.getGame();
            client = createDialog.getClient();
            white = createDialog.isWhite();
            create = createDialog.isCreate();

            hide();

        });

        find = new Button("Search for a LAN challenge");
        find.setOnAction(ae -> {

            try {

                ChallengeSearch searchDialog = new ChallengeSearch(window, null);

                searchDialog.showAndWait();

            } catch (Exception e) {

            }

        });

        VBox buttons = new VBox(cr, find);
        buttons.setFillWidth(true);

        VBox.setVgrow(cr, Priority.ALWAYS);
        VBox.setVgrow(find, Priority.ALWAYS);

        Scene s = new Scene(buttons);

        setOnShown(we -> {

            sizeToScene();
            setWidth(500);
            setMinWidth(500);
            setMinHeight(getHeight());
            setMaxHeight(getHeight());
            setMaxWidth(getWidth());

        });

        setTitle("Get Started");
        setScene(s);

    }

}
