import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class GUIMenuBar extends JMenuBar {

    private App app;

    private JMenuItem undo;
    private JMenuItem redo;
    private JMenuItem paste;
    private JMenuItem importFile;

    public void boardUpdate() {

        Game g = app.getGb().getGame();

        undo.setEnabled(g.canUndo());
        redo.setEnabled(g.canRedo());

    }

    public GUIMenuBar(App app) {
        super();

        this.app = app;

        JMenu game = new JMenu("Game");

        undo = new JMenuItem("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke('Z', KeyEvent.META_DOWN_MASK));
        undo.setActionCommand("Undo");

        redo = new JMenuItem("Redo");
        redo.setAccelerator(KeyStroke.getKeyStroke('Z', KeyEvent.SHIFT_DOWN_MASK + KeyEvent.META_DOWN_MASK));
        redo.setActionCommand("Redo");

        JMenuItem importType = new JMenu("Load");

        paste = new JMenuItem("Paste PGN");
        paste.setAccelerator(KeyStroke.getKeyStroke('P', KeyEvent.META_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
        paste.setActionCommand("Paste");

        importFile = new JMenuItem("Import from file");
        importFile.setAccelerator(KeyStroke.getKeyStroke('L', KeyEvent.META_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
        importFile.setActionCommand("Import File");

        importType.add(paste);
        importType.add(importFile);

        undo.addActionListener(app);
        redo.addActionListener(app);
        paste.addActionListener(app);
        importFile.addActionListener(app);

        game.add(undo);
        game.add(redo);
        game.addSeparator();
        game.add(importType);

        add(game);

    }

}
