package guifx;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.StackPane;

import org.girod.javafx.svgimage.*;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        SVGImage svg = SVGLoader.load(getClass().getResource("/img/BB.svg"));
        StackPane pane = new StackPane(svg);

        Scene s = new Scene(pane, 800, 800);
        stage.setScene(s);
        stage.show();

        // gr.fillRect(50, 50, 100, 100);

    }

    public static void main(String[] args) {

        launch();

    }

}