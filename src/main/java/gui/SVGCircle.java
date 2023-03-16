package gui;

import java.util.ArrayList;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class SVGCircle extends SVGElement {
    
    private double cx, cy, r, scale;

    public SVGCircle(double cx, double cy, double r, double scale) {



        this.cx = cx;
        this.cy = cy;
        this.r = r;
        this.scale = scale;

    }

    public ArrayList<Shape> draw(ArrayList<String> styles) {

        Circle c = new Circle(cx * scale, cy * scale, r * scale);
        for (String sty : styles) {

            SVGPiece.setStyle(c, sty, scale);

        }
        
        ArrayList<Shape> sh = new ArrayList<Shape>();
        sh.add(c);

        return sh;

    }

}  