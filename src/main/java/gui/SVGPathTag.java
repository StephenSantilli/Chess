package gui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

public class SVGPathTag extends SVGElement {

    private String d;
    private String style;
    private double scale;
    private String transform;

    private static final String PATH_REGEX = "(?<path>(<path(( style=\"(?<style>[^\"]*)\")|( d=\"(?<line>[^\"]*)\")|( transform=\"(?<transform>[^\"]*)\")|[^>])*>))";

    public SVGPathTag(String tag, double scale) {

        Matcher m = Pattern.compile(PATH_REGEX).matcher(tag);
        m.find();
        d = m.group("line");
        style = m.group("style");
        this.scale = scale;

        this.transform = m.group("transform");

    }

    public ArrayList<Shape> draw(ArrayList<String> styles) {

        SVGPath l = new SVGPath();

        String newD = "";
        Matcher digits = Pattern.compile("([\\d\\.]+)").matcher(d);
        int lastEnd = 0;
        while (digits.find()) {

            String num = digits.group().equals("0") || digits.group().equals("1")  ? digits.group() : (Double.parseDouble(digits.group()) * scale) + "";
            newD += d.substring(lastEnd, digits.start()) + num;
            lastEnd = digits.end();

        }
        newD += d.substring(lastEnd);
        d = newD;

        l.setContent(d);
        for (String sty : styles) {

            SVGPiece.setStyle(l, sty, scale);

        }
        SVGPiece.setStyle(l, style, scale);

        if(transform != null && !transform.equals("")) {

            Matcher mm = Pattern.compile("matrix\\((?<a>[-\\d\\.]*),(?<b>[-\\d\\.]*),(?<c>[-\\d\\.]*),(?<d>[-\\d\\.]*),(?<e>[-\\d\\.]*),(?<f>[-\\d\\.]*)\\)").matcher(transform);
            mm.find();
            double am = Double.parseDouble(mm.group("a")) * scale, bm = Double.parseDouble(mm.group("b")) * scale, cm = Double.parseDouble(mm.group("c"))
                    * scale, dm = Double.parseDouble(mm.group("d"))
                            * scale, em = Double.parseDouble(mm.group("e")), fm = Double.parseDouble(mm.group("f")) * scale;

            l.setTranslateX((am) + (cm)+ (em));
            l.setTranslateY((bm) + (dm)+ (fm));
        }
        
        ArrayList<Shape> sh = new ArrayList<Shape>();
        sh.add(l);

        return sh;

    }

    public String getD() {
        return d;
    }

    public String getStyle() {
        return style;
    }

}