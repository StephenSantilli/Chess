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

/**
 * Dialog that allows the user to adjust the options of the engine.
 */
public class EngineSettings extends Stage {

    /**
     * The engine hook.
     */
    private EngineHook hook;

    /**
     * The spinner that sets the best move depth.
     */
    private Spinner<Integer> bestMoveDepthSpinner;

    /**
     * Creates a new engine settings dialog.
     * 
     * @param window The window that owns this dialog.
     * @param hook   The engine hook that this dialog is setting the settings of.
     */
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

        // Non-UCI options

        // Depth of searching
        HBox depthBox = new HBox();
        depthBox.setSpacing(5);
        depthBox.setAlignment(Pos.CENTER_LEFT);

        Label depthLabel = new Label("Depth:");
        Spinner<Integer> depthSpinner = new Spinner<>(1, 999, hook.getDepth());
        depthSpinner.getValueFactory().valueProperty().addListener(v -> {
            hook.setDepth(depthSpinner.getValue());
        });

        depthBox.getChildren().addAll(depthLabel, depthSpinner);

        // Best move checkbox
        HBox bestMoveBox = new HBox();
        bestMoveBox.setSpacing(5);
        bestMoveBox.setAlignment(Pos.CENTER_LEFT);

        Label bestMoveLabel = new Label("Output best move:");

        CheckBox bestMoveCheckBox = new CheckBox();
        bestMoveCheckBox.setSelected(hook.isBestMove());
        bestMoveCheckBox.setOnAction(ae -> toggleBestMove(bestMoveCheckBox.isSelected()));

        bestMoveBox.getChildren().addAll(bestMoveLabel, bestMoveCheckBox);

        // Best move depth spinner
        HBox bestMoveDepthBox = new HBox();
        bestMoveDepthBox.setSpacing(5);
        bestMoveDepthBox.setAlignment(Pos.CENTER_LEFT);

        Label bestMoveDepthLabel = new Label("Output best move depth:");
        bestMoveDepthSpinner = new Spinner<>(1, 999, hook.getBestMoveDepth());
        bestMoveDepthSpinner.setDisable(!bestMoveCheckBox.isSelected());

        bestMoveDepthSpinner.getValueFactory().valueProperty().addListener(v -> {
            hook.setBestMoveDepth(bestMoveDepthSpinner.getValue());
        });

        bestMoveDepthBox.getChildren().addAll(bestMoveDepthLabel, bestMoveDepthSpinner);

        vb.getChildren().addAll(depthBox, bestMoveBox, bestMoveDepthBox);

        // End non-UCI options

        final ArrayList<UCIOption<?>> opts = engine.getOpts();

        for (int i = 0; i < opts.size(); i++) {

            final UCIOption<?> opt = opts.get(i);

            HBox optBox = new HBox();
            optBox.setSpacing(10);

            Label optLabel = new Label(opt.getName() + ":");
            optLabel.setAlignment(Pos.CENTER_LEFT);

            optBox.getChildren().add(optLabel);
            optBox.setAlignment(Pos.CENTER_LEFT);

            if (opt instanceof UCIButton) {

                final UCIButton casted = (UCIButton) opt;

                Button btn = new Button("Set");
                btn.setAlignment(Pos.CENTER_LEFT);

                btn.setOnAction(ae -> {

                    try {

                        casted.set(null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });

                optBox.getChildren().addAll(btn);

            } else if (opt instanceof UCICheck) {

                final UCICheck c = (UCICheck) opt;

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

                optBox.getChildren().addAll(check);

            } else if (opt instanceof UCICombo) {

                final UCICombo c = (UCICombo) opt;

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

                optBox.getChildren().addAll(combo);

            } else if (opt instanceof UCISpin) {

                final UCISpin c = (UCISpin) opt;

                Spinner<Integer> spin = new Spinner<Integer>(c.getMin(), c.getMax(), c.getValue());
                spin.setEditable(true);

                spin.getValueFactory().valueProperty().addListener(v -> {

                    try {

                        c.set(spin.getValue());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });

                optBox.getChildren().addAll(spin);

            } else if (opt instanceof UCIString) {

                final UCIString c = (UCIString) opt;

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

                optBox.getChildren().addAll(field);

            }

            vb.getChildren().add(optBox);

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

        setTitle("Engine Settings");
        setScene(s);

    }

    /**
     * Toggles whether or not best moves should be displayed and appropriately
     * disables the {@link #bestMoveDepthSpinner}.
     * 
     * @param selected Whether or not the "Output best move" box is selected.
     */
    private void toggleBestMove(boolean selected) {
        hook.setBestMove(selected);
        bestMoveDepthSpinner.setDisable(!selected);
    }

}