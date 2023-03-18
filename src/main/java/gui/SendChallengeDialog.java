package gui;

import java.util.function.UnaryOperator;

import game.LAN.Challenge;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class SendChallengeDialog extends Stage {

    private String name;
    private int color;
    private boolean create;

    private Button createButton;

    private TextField nameField;

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public boolean isCreate() {
        return create;
    }

    public SendChallengeDialog(Window owner) {

        this.create = false;

        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);

        setResizable(false);

        VBox vb = new VBox();
        vb.setPadding(new Insets(15, 15, 15, 15));
        vb.setSpacing(5);

        Scene s = new Scene(vb);

        setScene(s);
        setTitle("Create LAN Challenge");

        HBox nameChoices = new HBox();

        Label nameLabel = new Label("Enter your name (max 15 characters):");

        nameField = new TextField();
        nameField.setOnAction(ev -> {
            setCreateButton();
        }); 

        UnaryOperator<TextFormatter.Change> op = ev -> {
            if(ev.getText().length() > 15) {
                ev.setText(ev.getText().substring(0, 15));
            }
            return ev;
        };
        nameField.setTextFormatter(new TextFormatter<String>(op));
        

        nameChoices.getChildren().addAll(nameLabel, nameField);

        HBox colorChoices = new HBox();

        Label colorLabel = new Label("Choose a color:");
        
        ChoiceBox<String> choiceBox = new ChoiceBox<String>();
        choiceBox.getItems().addAll("Random", "White", "Black");
        choiceBox.setValue("Random");
        choiceBox.setOnAction(ev -> {

            String selection = choiceBox.getValue();
            switch (selection) {
                case "White":
                    color = Challenge.CHALLENGE_WHITE;
                    break;
                case "Black":
                    color = Challenge.CHALLENGE_BLACK;
                    break;
                default:
                    color = Challenge.CHALLENGE_RANDOM;
            }

            setCreateButton();

        });

        colorChoices.getChildren().addAll(colorLabel, choiceBox);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(ev -> {
            create = false;
            hide();
        });

        createButton = new Button("Create challenge");
        createButton.setOnAction(ev -> {

            create = true;

            name = nameField.getText();

            hide();

        });

        HBox buttonBox = new HBox(cancelButton, createButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        vb.getChildren().addAll(nameChoices, colorChoices, buttonBox);
        

    }

    private void setCreateButton() {

        if(nameField.getText().length() <= 0) {

            createButton.setDisable(true);

        }

    }

}
