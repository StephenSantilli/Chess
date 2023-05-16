package gui.dialog;

import java.io.File;
import java.io.FileReader;
import java.util.Optional;
import java.util.Scanner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class ImportPGN extends Stage {

    private TextArea field;
    private Button fromFile, set, cancel;

    private boolean create;

    private String pgn;

    public String getPgn() {
        return pgn;
    }

    public boolean isCreate() {
        return create;
    }

    public ImportPGN(Window window) {

        // initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().setAll(((Stage) (window)).getIcons());

        pgn = "";

        VBox vb = new VBox();

        fromFile = new Button("Import from File");
        fromFile.setOnAction(ev -> {

            if (!field.getText().equals("")) {

                Dialog<ButtonType> confirm = new Dialog<>();
                confirm.setTitle("Confirm");
                confirm.setContentText("Importing from a file will overwrite your current entry. Are you sure?");
                confirm.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

                Optional<ButtonType> result = confirm.showAndWait();

                if (result.isPresent() && result.get().equals(ButtonType.NO))
                    return;

            }

            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new ExtensionFilter("PGN File", "*.pgn"));

            File f = chooser.showOpenDialog(new Stage());

            if (f != null) {
                pgn = "";
                try (Scanner s = new Scanner(new FileReader(f))) {

                    while (s.hasNextLine()) {
                        String line = s.nextLine();
                        pgn += line + "\n";
                    }

                } catch (Exception e) {
                    Dialog<Void> eDg = new Dialog<>();
                    eDg.initOwner(getScene().getWindow());
                    eDg.setTitle("Error Importing PGN");
                    eDg.setContentText(e.getMessage());

                    eDg.getDialogPane().getButtonTypes().add(ButtonType.OK);

                    eDg.showAndWait();
                }

            }

            field.setText(pgn);

        });

        set = new Button("Set");
        set.setOnAction(ev -> {
            pgn = field.getText();
            create = true;
            hide();
        });

        cancel = new Button("Cancel");
        cancel.setOnAction(ev -> {
            hide();
        });

        HBox btns = new HBox(fromFile, set, cancel);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setSpacing(10);

        field = new TextArea();
        field.setPromptText("Enter PGN...");
        VBox.setVgrow(field, Priority.ALWAYS);

        vb.getChildren().addAll(field, btns);
        vb.setPadding(new Insets(10));
        vb.setSpacing(10);

        Scene s = new Scene(vb);
        setWidth(500);
        setMinWidth(500);
        setMinHeight(400);
        setHeight(400);
        setOnShown(we -> {

            setMaxHeight(getHeight());
            setMaxWidth(getWidth());

        });

        setTitle("Start Game from PGN");
        setScene(s);

    }

}