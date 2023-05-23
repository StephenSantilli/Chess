package gui.menu;

import gui.App;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * The menu bar tab that contains help options.
 */
public class HelpMenu extends Menu {

    /**
     * Creates a new "Help" menu tab.
     */
    public HelpMenu() {

        super("Help");

        MenuItem about = new MenuItem("About");
        about.setOnAction(ae -> showAboutDialog());
        about.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+A"));

        MenuItem engine = new MenuItem("Adding Engines");
        engine.setOnAction(ae -> showEngineDialog());

        getItems().addAll(about, engine);

    }

    /**
     * Shows the "About" dialog, which contains information about the application.
     */
    private void showAboutDialog() {

        Dialog<Void> aboutDialog = new Dialog<Void>();

        aboutDialog.getDialogPane().getButtonTypes().setAll(new ButtonType("Ok"));

        ImageView icon = new ImageView(getClass().getResource("/img/icon_48x48.png").toString());

        aboutDialog.setHeaderText("Chess by Stephen Santilli");
        aboutDialog.setGraphic(icon);

        // Pieces attribution
        Text start = new Text("Pieces made by Cburnett, CC BY-SA 3.0 ");

        Hyperlink licenseLink = new Hyperlink("http://creativecommons.org/licenses/by-sa/3.0/");
        licenseLink.setOnAction(le -> {
            App.hostServices.showDocument(licenseLink.getText());
        });

        Text end = new Text(" - via Wikimedia Commons");

        TextFlow pieceAttribution = new TextFlow(start, licenseLink, end);

        VBox content = new VBox(pieceAttribution);

        aboutDialog.getDialogPane().setContent(content);

        aboutDialog.show();

    }

    /**
     * Shows the engine dialog, which contains instructions on how to set up an
     * engine to play against.
     */
    private void showEngineDialog() {

        Dialog<Void> engineDialog = new Dialog<Void>();

        engineDialog.getDialogPane().getButtonTypes().setAll(new ButtonType("Ok"));

        ImageView icon = new ImageView(getClass().getResource("/img/icon_48x48.png").toString());

        engineDialog.setHeaderText("Adding Engines");
        engineDialog.setGraphic(icon);

        // Pieces attribution
        Text start = new Text(
                "To play with a chess engine/bot, you will first need to download a UCI-compatible engine. Stockfish is a free, open-source, UCI-compatible engine that can be used with this program.\nYou can download it here: ");

        Hyperlink stockfishLink = new Hyperlink("https://stockfishchess.org/download/");
        stockfishLink.setOnAction(le -> {
            App.hostServices.showDocument(stockfishLink.getText());
        });

        Text end = new Text(
                "\nOnce you have downloaded an engine, you must open the new game dialog. Find the player type dropdown (will say \"Two Player\" by default.) In this dropdown, select \"Register a new engine...\" This will bring up a file chooser, and you should select the unix binary (Mac/Linux) or the exe (Windows) for the engine that you downloaded. Then you will be able to select the engine in the same dropdown and play against it.");

        TextFlow engineExplanation = new TextFlow(start, stockfishLink, end);
        engineExplanation.setMaxWidth(600);

        VBox content = new VBox(engineExplanation);

        engineDialog.getDialogPane().setContent(content);

        engineDialog.show();

    }

}
