package gui.menu;

import gui.App;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.StageStyle;

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

        aboutDialog.initStyle(StageStyle.UNIFIED);

        aboutDialog.getDialogPane().getButtonTypes().setAll(new ButtonType("Ok"));

        ImageView icon = new ImageView(getClass().getResource("/img/icon_48x48.png").toString());

        aboutDialog.setGraphic(icon);
        aboutDialog.setHeaderText("Chess by Stephen Santilli");

        Text label = new Text("Pieces made by Cburnett, CC BY-SA 3.0 ");

        Hyperlink link = new Hyperlink("http://creativecommons.org/licenses/by-sa/3.0/");
        link.setOnAction(le -> {
            App.hostServices.showDocument(link.getText());
        });

        Text end = new Text(" - via Wikimedia Commons");

        TextFlow flow = new TextFlow(label, link, end);

        VBox content = new VBox(flow);

        aboutDialog.getDialogPane().setContent(content);

        aboutDialog.show();

    }

}
