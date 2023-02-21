package gui;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.synth.SynthLookAndFeel;

import game.BoardListener;
import game.Move;

import java.util.*;

class App extends JFrame implements BoardListener, ActionListener, MouseInputListener, KeyListener {

    private Board gb;

    public Board getGb() {
        return gb;
    }

    private MovePane movePane;
    private MenuBar menuBar;
    private JScrollPane scrollPane;

    public App(String title) {

        super(title);
/*         SynthLookAndFeel synth = new SynthLookAndFeel();
        try {
            synth.load(getClass().getResourceAsStream("/style.xml"), this.getClass());
            UIManager.setLookAndFeel(synth);
        } catch (Exception e) {
            System.out.println("Could not set look and feel.");
            e.printStackTrace();
        } */

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        this.gb = new Board(this);
        add(gb);

        this.movePane = new MovePane(gb.getGame(), this);
        scrollPane = new JScrollPane(movePane);
        add(scrollPane);

        layout.putConstraint(SpringLayout.WEST, gb, 10, SpringLayout.WEST, getContentPane());
        layout.putConstraint(SpringLayout.NORTH, gb, 10, SpringLayout.NORTH, getContentPane());

        layout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.EAST, gb);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.NORTH, this);

        layout.putConstraint(SpringLayout.EAST, getContentPane(), 10, SpringLayout.EAST, scrollPane);
        layout.putConstraint(SpringLayout.SOUTH, getContentPane(), 10, SpringLayout.SOUTH, scrollPane);

        this.menuBar = new MenuBar(this);
        setJMenuBar(menuBar);

        pack();
        // setResizable(false);
        gb.getGame().addListener(this);
        gb.addMouseListener(this);
        gb.addMouseMotionListener(this);
        addKeyListener(this);

    }

    public static void main(String[] args) {
        App frame = new App("Chess");
        frame.setVisible(true);
        frame.boardUpdated();
    }

    @Override
    public void boardUpdated() {

        repaint();
        movePane.boardUpdate();
        gb.boardUpdate();
        menuBar.boardUpdate();
        movePane.revalidate();

    }

    @Override
    public char promptForPromote(Move move) {

        return gb.promptForPromote(move);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getComponent() == gb)
            gb.mousePressed(e);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getComponent() == gb)
            gb.mouseReleased(e);

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getComponent() == gb)
            gb.mouseExited(e);

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getComponent() == gb)
            gb.mouseDragged(e);

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyPressed(KeyEvent e) {
        gb.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("Paste")) {

            LoadPositionDialog dialog = new LoadPositionDialog(this, "Load Position");
            dialog.setVisible(true);

            try {
                gb.getGame().importPosition(dialog.getImportText());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error", JOptionPane.PLAIN_MESSAGE);
            }

            // TODO add are you sure prompt

        } else if (e.getActionCommand().equals("Import File")) {

            FileDialog fileChooser = new FileDialog(this, "Import PGN");
            fileChooser.setFilenameFilter(new PGNFilter());
            fileChooser.setVisible(true);

            try {
                Scanner scan = new Scanner(new FileReader(fileChooser.getDirectory() + fileChooser.getFile()));
                String str = "";
                while (scan.hasNextLine()) {
                    str += scan.nextLine() + " ";
                }
                scan.close();
                str = str.trim();

                gb.getGame().importPosition(str);

            } catch (Exception ex) {

                JDialog errorDialog = new JDialog(this, "Error", false);
                errorDialog.add(new JTextArea(ex.getLocalizedMessage()));
                errorDialog.setVisible(true);

            }

        } else if (e.getActionCommand().equals("Redo")) {
            gb.getGame().redoMove();
        } else if (e.getActionCommand().equals("Undo")) {
            gb.getGame().undoMove();
        } else
            gb.actionPerformed(e);

    }

}
