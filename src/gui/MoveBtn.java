package gui;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;

public class MoveBtn extends JButton {
    
    public MoveBtn(String text) {

        super(text);
        setPreferredSize(new Dimension(80, 40));
        setBackground(Color.GRAY);
        setFocusable(false);

    }

}
