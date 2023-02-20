import javax.swing.*;
import java.awt.*;

public class GUIErrorDialog extends JDialog {
    
    public GUIErrorDialog(Frame owner, String error) {

        super(owner, error);

        setLayout(new FlowLayout());


        
        pack();

    }

}
