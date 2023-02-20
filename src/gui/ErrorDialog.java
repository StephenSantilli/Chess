package gui;
import javax.swing.*;
import java.awt.*;

public class ErrorDialog extends JDialog {
    
    public ErrorDialog(Frame owner, String error) {

        super(owner, error);

        setLayout(new FlowLayout());


        
        pack();

    }

}
