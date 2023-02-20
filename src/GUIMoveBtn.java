import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;

public class GUIMoveBtn extends JButton {
    
    public GUIMoveBtn(String text) {

        super(text);
        setPreferredSize(new Dimension(80, 40));
        setBackground(Color.BLACK);
        setFocusable(false);

    }

}
