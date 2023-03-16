package gui;

import java.util.ArrayList;

import javafx.scene.shape.Shape;

public abstract class SVGElement {
    
    public abstract ArrayList<Shape> draw(ArrayList<String> styles);

}
