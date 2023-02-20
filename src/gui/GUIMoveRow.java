package gui;

import javax.swing.*;

import game.Game;

import java.awt.*;
import java.awt.event.*;

public class GUIMoveRow extends JPanel {

    private Game game;

    public GUIMoveRow(String label, String b1, String b2, Game g, ActionListener listener) {

        this.game = g;

        setBackground(Color.LIGHT_GRAY);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(200, 50));
        setMinimumSize(new Dimension(200, 50));
        setMaximumSize(new Dimension(200, 50));
        setFocusable(false);

        if (label == null || b1 == null) {
            return;
        }

        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(20, 40));
        l.setHorizontalAlignment(JLabel.CENTER);
        add(l);

        int labelNumber = Integer.parseInt(label.substring(0, label.indexOf(".")));
        int b1Move = labelNumber * 2 - 1;
        int b2Move = labelNumber * 2;

        GUIMoveBtn btn1 = new GUIMoveBtn(b1);
        btn1.addActionListener(listener);
        btn1.setActionCommand("" + (b1Move));
        if (b1Move == g.getCurrentPos())
            btn1.setForeground(Color.GREEN);
        add(btn1);

        if (b2 != null) {
            GUIMoveBtn btn2 = new GUIMoveBtn(b2);
            btn2.addActionListener(listener);
            btn2.setActionCommand("" + (b2Move));
            if (b2Move == g.getCurrentPos())
                btn2.setForeground(Color.GREEN);
            add(btn2);
        }

    }



}
