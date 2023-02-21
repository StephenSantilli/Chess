package gui;
import java.awt.event.KeyEvent;
import java.awt.*;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.tools.Tool;

import game.Game;

public class MenuBar extends JMenuBar {

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

    public MenuBar(App app) {
        super();

        this.app = app;

        JMenu game = new JMenu("Game");

        int metaKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        undo = new JMenuItem("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke('Z', metaKey));
        undo.setActionCommand("Undo");

        redo = new JMenuItem("Redo");
        redo.setAccelerator(KeyStroke.getKeyStroke('Z', metaKey + KeyEvent.SHIFT_DOWN_MASK));
        redo.setActionCommand("Redo");

        JMenuItem importType = new JMenu("Load");

        paste = new JMenuItem("Paste PGN");
        paste.setAccelerator(KeyStroke.getKeyStroke('P', metaKey + KeyEvent.SHIFT_DOWN_MASK));
        paste.setActionCommand("Paste");

        importFile = new JMenuItem("Import from file");
        importFile.setAccelerator(KeyStroke.getKeyStroke('L', metaKey + KeyEvent.SHIFT_DOWN_MASK));
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
