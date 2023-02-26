package guifx;

import java.io.File;

import org.girod.javafx.svgimage.LoaderParameters;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import org.girod.javafx.svgimage.ScaleQuality;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board extends Canvas {

    public Board(int width, int height) {

        super(width, height);

        GraphicsContext g = getGraphicsContext2D();

        boolean dark = false;
        for (int r = 0; r < 8; r++) {

            for (int c = 0; c < 8; c++, dark = !dark) {
                g.setFill(dark ? Color.GREEN : Color.WHITE);
                g.fillRect(c * 100, r * 100, 100, 100);
                LoaderParameters params = new LoaderParameters();
                params.centerImage = true;
                params.applyViewportPosition = true;
                SVGImage svg = SVGLoader.load(getClass().getResource("/img/BB.svg"), params);

                Image sv = svg.toImageScaled(ScaleQuality.RENDER_QUALITY, 80 / svg.getLayoutBounds().getWidth(), 80 /
                        svg.getLayoutBounds().getHeight());

                g.drawImage(sv, c * 100, r * 100);

            }
            dark = !dark;

        }

    }

}
