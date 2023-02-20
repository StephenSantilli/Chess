import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUIPromoteDialog extends JDialog implements ActionListener {

    private char promoteType;

    public char getPromoteType() {
        return promoteType;
    }

    public GUIPromoteDialog(App owner, String title) {

        super(owner, title, true);

        promoteType = '?';

        setLayout(new FlowLayout());

        setUndecorated(true);

        Game g = owner.getGb().getGame();
        String color = g.isWhiteTurn(true) ? "W" : "B";

        JButton qButton = new JButton(new ImageIcon(color + "Q.png"));
        JButton rButton = new JButton(new ImageIcon(color + "R.png"));
        JButton bButton = new JButton(new ImageIcon(color + "B.png"));
        JButton nButton = new JButton(new ImageIcon(color + "N.png"));

        qButton.setActionCommand("Queen");
        rButton.setActionCommand("Rook");
        bButton.setActionCommand("Bishop");
        nButton.setActionCommand("Knight");

        qButton.addActionListener(this);
        rButton.addActionListener(this);
        bButton.addActionListener(this);
        nButton.addActionListener(this);

        add(qButton);
        add(rButton);
        add(bButton);
        add(nButton);

        pack();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        switch (e.getActionCommand()) {
            case "Queen":
                promoteType = 'Q';
                break;
            case "Rook":
                promoteType = 'R';
                break;
            case "Bishop":
                promoteType = 'B';
                break;
            case "Knight":
                promoteType = 'N';
                break;
        }
        setVisible(false);
    }

}
