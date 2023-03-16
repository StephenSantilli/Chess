package gui;

import java.awt.Paint;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class SVGPiece {

    private int pieceSize;
    private boolean color;
    private char pieceType;

    private String SVG;
    private ArrayList<SVGPath> paths;

    public int getPieceSize() {
        return pieceSize;
    }

    public boolean isColor() {
        return color;
    }

    public char getPieceType() {
        return pieceType;
    }

    public SVGPiece(int pieceSize, boolean color, char pieceType) {

        this.pieceSize = pieceSize;
        this.color = color;
        this.pieceType = pieceType;

        try {

            Scanner s = new Scanner(new FileReader(
                    getClass().getResource("/img/" + (color ? "W" : "B") + pieceType + ".svg").toURI().getPath()));
            SVG = "";
            while (s.hasNextLine()) {
                SVG += s.nextLine();
            }
            s.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static final String GROUP_REGEX = "(<g(( transform=\"(?<transform>[^\"]*)\")|( style=\"(?<style>[^\"]*)\")|( [^>]*))*>)(?<content>[\\s\\S](?!/g>))*";
    private static final String PATH_REGEX = "(?<path>(<path(( style=\"(?<style>[^\"]*)\")|( d=\"(?<line>[^\"]*)\")|[^>])*>))";
    private static final String STYLE_REGEX = "(?<key>[A-z1-9-_]*)\\:(?<value>[^;]*);?";

    public static void setStyle(Shape path, String style, double scale) {

        if (style == null || style.equals(""))
            return;

        Matcher sp = Pattern.compile(STYLE_REGEX).matcher(style);

        while (sp.find()) {

            String key = sp.group("key");
            String value = sp.group("value");

            switch (key) {
                case "fill":
                    if (!value.equals("none"))
                        path.setFill(Color.web(value));
                    else
                        path.setFill(null);
                    break;
                case "fill-opacity":
                    path.setOpacity(Double.parseDouble(value));
                    break;
                case "fill-rule":
                    if(path instanceof SVGPath) ((SVGPath) path).setFillRule(value.equals("evenodd") ? FillRule.EVEN_ODD : FillRule.NON_ZERO);
                    break;
                case "stroke":
                    if (!value.equals("none"))
                        path.setStroke(Color.web(value));
                    else
                        path.setStroke(null);
                    break;
                case "stroke-width":
                    path.setStrokeWidth(Double.parseDouble(value) * scale);
                    break;
                case "stroke-linecap":
                    path.setStrokeLineCap((value.equals("round") ? (StrokeLineCap.ROUND)
                            : (value.equals("butt") ? StrokeLineCap.BUTT : StrokeLineCap.SQUARE)));
                    break;
                case "stroke-linejoin":
                    path.setStrokeLineJoin(value.equals("miter") ? StrokeLineJoin.MITER
                            : value.equals("bevel") ? StrokeLineJoin.BEVEL : StrokeLineJoin.ROUND);
                    break;
                case "stroke-miterlimit":
                    path.setStrokeMiterLimit(Double.parseDouble(value));
                    break;
                case "stroke-opacity":
                    path.setOpacity(Double.parseDouble(value));
                    break;

            }

        }

    }

    public Pane getImage() {

        SVGGroup parent = new SVGGroup(SVG, null, null, 2);
        ArrayList<Shape> paths = parent.draw(null);

        Pane pane = new Pane();
        pane.getChildren().addAll(paths);
        return pane;

    }

}
