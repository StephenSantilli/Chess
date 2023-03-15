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

    private void setStyle(SVGPath path, String style) {

        if (style == null || style.equals(""))
            return;

        Matcher sp = Pattern.compile(STYLE_REGEX).matcher(style);

        while (sp.find()) {

            String key = sp.group("key");
            String value = sp.group("value");

            switch (key) {

                case "fill":
                    path.setFill(value.equals("none") ? new Color(0,0,0,0) : Color.web(value));
                    break;
                case "fill-opacity":
                    path.setOpacity(Double.parseDouble(value));
                    break;
                case "fill-rule":
                    path.setFillRule(value.equals("evenodd") ? FillRule.EVEN_ODD : FillRule.NON_ZERO);
                    break;
                case "stroke":
                    path.setStroke(value.equals("none") ? new Color(0, 0, 0, 0) : Color.web(value));
                    break;
                case "stroke-width":
                    path.setStrokeWidth(Double.parseDouble(value));
                    break;
                case "stroke-linecap":
                    path.setStrokeLineCap(value.equals("round") ? StrokeLineCap.ROUND
                            : value.equals("butt") ? StrokeLineCap.BUTT : StrokeLineCap.SQUARE);
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

    private Shape groupPath(String group, String groupStyle) {

        Matcher mp = Pattern.compile(PATH_REGEX).matcher(group);
        Shape sv = new SVGPath();
        while (mp.find()) {

            SVGPath l = new SVGPath();
            setStyle(l, groupStyle);
            String sty = mp.group("style");
            setStyle(l, sty);
            String li = mp.group("line");
            l.setContent(li);
            sv = Shape.union(sv, l);
            System.out.println((color ? "White" : "Black") + pieceType + li);

        }

        return sv;

    }

    private Pane parseSVG() {

        Matcher m = Pattern.compile(GROUP_REGEX).matcher(SVG);
        Pane p = new Pane();

        while (m.find()) {

            String groupStyle = m.group("style");
            String group = m.group();

            Matcher mp = Pattern.compile(PATH_REGEX).matcher(group);
            
            p.getChildren().add(groupPath(group, groupStyle));

        }

        return p;

    }

    public Pane getImage() {

        return parseSVG();

    }

}
