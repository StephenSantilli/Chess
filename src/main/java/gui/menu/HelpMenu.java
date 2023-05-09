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

public class HelpMenu extends Menu {

    public HelpMenu() {

        super("Help");

        MenuItem about = new MenuItem("About");
        about.setOnAction(ae -> showAboutDialog());
        about.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+A"));

        getItems().addAll(about);

    }

    private void showAboutDialog() {

        Dialog<Void> aboutDialog = new Dialog<Void>();

        aboutDialog.getDialogPane().getButtonTypes().setAll(new ButtonType("Ok"));

        ImageView icon = new ImageView(getClass().getResource("/img/icon_48x48.png").toString());

        aboutDialog.setHeaderText("Chess by Stephen Santilli");
        aboutDialog.setGraphic(icon);

        //Pieces attribution
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

}
