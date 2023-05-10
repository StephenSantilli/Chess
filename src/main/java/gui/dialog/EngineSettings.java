package gui.dialog;

import java.util.ArrayList;
import game.engine.EngineHook;
import game.engine.UCIButton;
import game.engine.UCICheck;
import game.engine.UCICombo;
import game.engine.UCIEngine;
import game.engine.UCIOption;
import game.engine.UCISpin;
import game.engine.UCIString;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class EngineSettings extends Stage {

    private EngineHook hook;

    public EngineSettings(Window window, EngineHook hook) {

        final UCIEngine engine = hook.getEngine();
        this.hook = hook;

        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().setAll(((Stage) (window)).getIcons());

        VBox vb = new VBox();
        vb.setPadding(new Insets(10));

        ScrollPane sp = new ScrollPane(vb);
        sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        sp.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);

        HBox db = new HBox();
        db.setSpacing(5);
        db.setAlignment(Pos.CENTER_LEFT);

        Label depth = new Label("Depth:");
        Spinner<Integer> dSpinner = new Spinner<>(1, 999, hook.getDepth());
        dSpinner.getValueFactory().valueProperty().addListener(v -> {
            hook.setDepth(dSpinner.getValue());
        });

        db.getChildren().addAll(depth, dSpinner);

        HBox bm = new HBox();
        bm.setSpacing(5);
        bm.setAlignment(Pos.CENTER_LEFT);

        Label bestM = new Label("Output best move:");
        CheckBox bCheckBox = new CheckBox();
        bCheckBox.setOnAction(ae -> hook.setBestMove(bCheckBox.isSelected()));
        bCheckBox.setSelected(hook.isBestMove());

        bm.getChildren().addAll(bestM, bCheckBox);

        vb.getChildren().addAll(db, bm);

        final ArrayList<UCIOption> opts = engine.getOpts();

        for (int i = 0; i < opts.size(); i++) {

            final UCIOption o = opts.get(i);

            HBox hb = new HBox();
            hb.setSpacing(10);

            Label l = new Label(o.getName() + ":");
            l.setAlignment(Pos.CENTER_LEFT);

            hb.getChildren().add(l);
            hb.setAlignment(Pos.CENTER_LEFT);

            if (o instanceof UCIButton) {

                final UCIButton c = (UCIButton) o;

                Button btn = new Button("Set");
                btn.setAlignment(Pos.CENTER_LEFT);

                btn.setOnAction(ae -> {
                    try {

                        c.set(null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                hb.getChildren().addAll(btn);

            } else if (o instanceof UCICheck) {

                final UCICheck c = (UCICheck) o;

                CheckBox check = new CheckBox();
                check.setAlignment(Pos.CENTER_LEFT);
                check.setSelected(c.getValue());
                check.setOnAction(ae -> {
                    try {

                        c.set(check.isSelected());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                hb.getChildren().addAll(check);

            } else if (o instanceof UCICombo) {

                final UCICombo c = (UCICombo) o;

                ComboBox<String> combo = new ComboBox<>();

                combo.getItems().setAll(c.getVars());
                combo.setValue(c.getValue());
                combo.setOnAction(ae -> {
                    try {

                        c.set(combo.getValue());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                hb.getChildren().addAll(combo);

            } else if (o instanceof UCISpin) {

                final UCISpin c = (UCISpin) o;

                Spinner<Integer> spin = new Spinner<Integer>(c.getMin(), c.getMax(), c.getValue());
                spin.setEditable(true);
                spin.getValueFactory().valueProperty().addListener(v -> {
                    try {

                        c.set(spin.getValue());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                hb.getChildren().addAll(spin);

            } else if (o instanceof UCIString) {

                final UCIString c = (UCIString) o;

                TextField field = new TextField();
                field.setAlignment(Pos.CENTER_LEFT);

                field.setText(c.getValue());

                field.textProperty().addListener(v -> {
                    try {

                        c.set(field.getText());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                hb.getChildren().addAll(field);

            }

            vb.getChildren().add(hb);

        }
        vb.setSpacing(10);
        Button close = new Button("Close");
        close.setAlignment(Pos.CENTER_RIGHT);
        close.setOnAction(ae -> {
            try {
                engine.waitReady();

            } catch (Exception e) {
                e.printStackTrace();
            }
            hide();
        });

        HBox btns = new HBox(new Region(), close);
        vb.getChildren().add(btns);

        Scene s = new Scene(sp);
        // setWidth(500);
        // setMinWidth(500);
        // setMinHeight(400);
        // setHeight(400);
        setOnShown(we -> {

            // sizeToScene();

            // setMaxHeight(getHeight());
            // setMaxWidth(getWidth());

        });

        setTitle("Engine Settings");
        setScene(s);

    }

}