import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.EventListener;

public class GUIMovePane extends JPanel implements Scrollable {

    private Game game;
    private ActionListener listener;

    private BoxLayout layout;

    public GUIMovePane(Game game, ActionListener listener) {

        // setPreferredSize(new Dimension(200, 800));

        setBackground(Color.WHITE);
        this.listener = listener;
        setFocusable(false);
        // setPreferredSize(new Dimension(200,800));
        // setBounds(0,0, 200, 800);
        // setMaximumSize(new Dimension(200,800));
        
        // setLayout(layout);
        layout = new BoxLayout(this, BoxLayout.Y_AXIS);

        setLayout(layout);
        this.game = game;

    }

    public void drawMoves() {

        ArrayList<Position> positions = game.getPositions();
        for (int i = 1; i < positions.size(); i += 2) {

            String label = null;
            String b1 = null;
            String b2 = null;
            label = (i / 2 + 1) + ".";

            try {

                Position one = positions.get(i);
                b1 = one.getMove().getMoveText();
                if (one.isCheckMate())
                    b1 += "#";
                else if (one.isGivingCheck())
                    b1 += "+";

                if (i + 1 < positions.size()) {
                    Position two = positions.get(i + 1);
                    b2 = two.getMove().getMoveText();
                    if (two.isCheckMate())
                        b2 += "#";
                    else if (two.isGivingCheck())
                        b2 += "+";
                }

                add(new GUIMoveRow(label, b1, b2, game, listener));

                add(new Box.Filler(new Dimension(1, 10), new Dimension(1, 10), new Dimension(1, 10)));
                int height = i * 60; 


            } catch (Exception e) {
                System.out.println(e);
            }

        }

        // revalidate();

    }

    public void paintComponent(Graphics gr) {

        super.paintComponent(gr);

    }

    public void boardUpdate() {

        repaint();
        removeAll();
        drawMoves();
        
        revalidate();

    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        // TODO Auto-generated method stub
        return new Dimension(200, 800);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 23;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        // TODO Auto-generated method stub
        return 23;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        // TODO Auto-generated method stub
        return false;
    }

}
