package gui;

import java.awt.Dimension;

import org.apache.batik.swing.*;

import game.Piece;

public class SVGPiece extends JSVGCanvas {

    public SVGPiece(Piece p) {

        super();
        setURI(getClass().getResource("/img/" + (p.isWhite() ? "W" : "B") + p.getCode() + ".svg").toString());
        setPreferredSize(new Dimension(85,85));
    }
    
}
