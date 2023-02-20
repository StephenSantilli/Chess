import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUILoadPositionDialog extends JDialog implements ActionListener {

    private String importText;

    private JTextArea gameImport;

    public String getImportText() {
        return importText;
    }

    public GUILoadPositionDialog(JFrame owner, String title) {

        super(owner, title, true);

        importText = "";

        setLayout(new FlowLayout());

        gameImport = new JTextArea();
        gameImport.setPreferredSize(new Dimension(500, 500));

        gameImport.setToolTipText("Paste game in PGN format here.");
        JButton okButton = new JButton("Import");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        add(gameImport);
        add(okButton);
        add(cancelButton);

        pack();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        switch (e.getActionCommand()) {
            case "Import":
                importText = gameImport.getText();
                break;
        }
        setVisible(false);
    }

}
