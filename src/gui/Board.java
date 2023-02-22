package gui;
import javax.swing.*;
import game.Game;
import game.Move;
import game.Piece;
import game.Square;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Board extends JPanel {

    private static final Color GREEN = new Color(23, 153, 64);
    private static final Color WHITE = new Color(231, 232, 220);

    private int squareWidth = 100;
    private int squareHeight = 100;

    private Square active = null;
    private Square prevActive = null;
    private Game game;
    private ArrayList<GUIPiece> pcs;

    private App app;

    public void boardUpdate() {
        repaint();
        revalidate();
    }

    public char promptForPromote(Move m) {

        PromoteDialog promoteScreen = new PromoteDialog(app, "Select Promotion Piece");
        Square s = m.getDestination();
        int ry = (squareWidth * 8) - (s.getRank() * squareHeight) + (m.isWhite() ? squareWidth : -squareWidth);
        int rx = (s.getFile() * squareWidth);
        promoteScreen.setLocation(rx, ry);
        promoteScreen.setVisible(true);
        return promoteScreen.getPromoteType();

    }

    public Board(App app) {

        this.pcs = new ArrayList<GUIPiece>();

        setPreferredSize(new Dimension(squareWidth * 8, squareHeight * 8));
        setBounds(0, 0, squareWidth * 8, squareHeight * 8);

        setBackground(Color.BLUE);
        active = null;
        game = new Game();

        this.app = app;

    }

    public Game getGame() {
        return game;
    }

    public Square getActive() {
        return active;
    }

    public void setActive(Square active) {
        this.prevActive = this.active;
        this.active = active;
    }

    public void squareClicked() {

        if (prevActive != null) {
            Piece a = game.getActivePos().getPieceAtSquare(active);
            Piece pa = game.getActivePos().getPieceAtSquare(prevActive);

            if (pa != null) {

                try {
                    Move m = new Move(prevActive, active, game.getActivePos());

                    if (a != null && pa.isWhite() == a.isWhite()) {
                        prevActive = null;
                    } else {

                        this.prevActive = null;
                        this.active = null;
                        game.makeMove(m);

                    }

                } catch (Exception e) {
                    this.prevActive = null;
                    this.active = null;
                    System.out.println(e);
                }
            }

        }

    }

    private void drawSquare(int x, int y, Graphics gr) {
        int rx = (x * 100);
        int ry = 700 - (y * 100);
        gr.fillRect(rx, ry, 100, 100);

        if (y == 0) {
            gr.setColor(Color.darkGray);
            gr.setFont(gr.getFont().deriveFont(18));
            gr.drawString(((char) ((x + 1) + 96)) + "", rx + 88, ry + 95);
        }

        if (x == 0) {
            gr.setColor(Color.darkGray);
            gr.setFont(gr.getFont().deriveFont(18));
            gr.drawString((y + 1) + "", rx + 5, ry + 15);

        }

    }

    public void paintComponent(Graphics gr) {

        super.paintComponent(gr);

        squareClicked();

        Color c = WHITE;

        for (int i = 0; i < 8; i++) {

            for (int j = 0; j < 8; j++) {

                gr.setColor(c);

                if (active != null && active.getFile() == i + 1 && active.getRank() == (j + 1))
                    gr.setColor(Color.RED);

                drawSquare(i, j, gr);

                if (c == WHITE)
                    c = GREEN;
                else
                    c = WHITE;

            }

            if (c == WHITE)
                c = GREEN;
            else
                c = WHITE;

        }

        pcs.clear();

        ArrayList<Piece> pieces = game.getActivePos().getPieces();
        Piece d = null;
        for (int i = 0; i < pieces.size(); i++) {

            Piece p = pieces.get(i);
            if (p != null) {
                if (dragging != null && p.equals(dragging.getPiece())) {
                    d = p;
                    continue;
                } else {

                    drawPiece(p, gr);
                }
            }

        }

        if (active != null) {

            Piece p = game.getActivePos().getPieceAtSquare(active);
            if (p != null) {
                ArrayList<Move> pMoves = new ArrayList<Move>();

                for (int i = 0; i < game.getActivePos().getMoves().size(); i++) {
                    Move o = game.getActivePos().getMoves().get(i);
                    if (o.getPiece().equals(p))
                        pMoves.add(o);
                }

                gr.setColor(Color.YELLOW);
                for (int i = 0; i < pMoves.size(); i++) {

                    Move m = pMoves.get(i);
                    Square dest = m.getDestination();
                    gr.fillOval((dest.getFile() - 1) * 100 + 35, (8 - dest.getRank()) * 100 + 35, 30, 30);

                }
            }

        }

        if (d != null) {
            drawPiece(d, gr);
        }

    }

    private void drawPiece(Piece p, Graphics gr) {

        // JSVGCanvas canvas = new SVGPiece(p);
        

        ImageIcon image = new ImageIcon(getClass().getResource("/img/" + (p.isWhite() ? "W" : "B") + p.getCode() + ".png"));
        int ix = (p.getSquare().getFile() - 1) * 100 + 5;
        int iy = 700 - ((p.getSquare().getRank() - 1) * 100 - 5);

        if (dragging != null && p.equals(dragging.getPiece())) {
            ix = dragging.getX();
            iy = dragging.getY();
        }

        gr.drawImage(image.getImage(), ix, iy, 90, 90, image.getImageObserver());
        
        // Component comp = add(canvas);
        //comp.setLocation(ix, iy);

        if (game.getActivePos().isInCheck() && p.getCode() == 'K' && p.isWhite() == game.getActivePos().isWhite())
            gr.fillOval(ix, iy, 20, 20);

        pcs.add(new GUIPiece(image, p, ix, iy));
    }

    public void boardClicked(int x, int y) {

        Square clicked = getSquareByLoc(x, y);

        if ((active != null && active.equals(clicked))) {
            active = null;
            prevActive = null;
            repaint();
        } else {
            // Piece search = game.getActivePos().getPieceAtSquare(clicked);
            if (active != null) {

                /*
                 * if (search != null && game.isWhiteTurn(false) != search.isWhite()) {
                 * active = null;
                  * prevActive = null;
                 * } else
                 */
                setActive(clicked);

                repaint();

            }

        }

    }

    public static Square getSquareByLoc(int x, int y) {

        return new Square((x / 100) + 1, (((800 - y)) / 100) + 1);

    }

    private GUIPiece dragging;

    public void mousePressed(MouseEvent e) {

        Square loc = getSquareByLoc(e.getX(), e.getY());

        Piece p = game.getActivePos().getPieceAtSquare(loc);

        for (int i = 0; i < pcs.size() && p != null && p.isWhite() == game.isWhiteTurn(false); i++) {
            GUIPiece gp = pcs.get(i);
            if (gp.getPiece().equals(p)) {
                dragging = gp;
                break;
            }
        }

        if (dragging != null) {
            dragging.setX(e.getX() - 45);
            dragging.setY(e.getY() - 45);

            repaint();
        }

    }

    public void mouseReleased(MouseEvent e) {

        if (dragging != null) {
            Square s = getSquareByLoc(e.getX(), e.getY());
            if (!s.isValid())
                return;

            boolean old = prevActive != null && active != null && prevActive.equals(active);

            Piece search = game.getActivePos().getPieceAtSquare(s);
            if (search != null && game.isWhiteTurn(false) == search.isWhite())
                prevActive = null;
            else
                prevActive = dragging.getPiece().getSquare();

            if (prevActive != null || (search != null && game.isWhiteTurn(false) == search.isWhite())) {

                active = s;

            }
            dragging = null;
            if (old) {

                boardClicked(e.getX(), e.getY());

            } else {

                repaint();

            }

        } else {
            boardClicked(e.getX(), e.getY());
        }

    }

    public void mouseExited(MouseEvent e) {
        if (dragging != null) {
            dragging = null;
            repaint();
        }

    }

    public void mouseDragged(MouseEvent e) {

        if (dragging != null) {
            dragging.setX(e.getX() - 45);
            dragging.setY(e.getY() - 45);

            repaint();
        }

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && !e.isShiftDown()) {

            try {
                game.setCurrentPos(game.getCurrentPos() - 1);
                repaint();

            } catch (Exception ex) {
                System.out.println(ex);
            }

        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !e.isShiftDown()) {

            try {
                game.setCurrentPos(game.getCurrentPos() + 1);
                repaint();

            } catch (Exception ex) {
                System.out.println(ex);
            }

        } else if (e.getKeyCode() == KeyEvent.VK_UP) {

            try {
                game.setCurrentPos(0);
            } catch (Exception ex) {
                System.out.println(ex);
            }

        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {

            try {
                game.setCurrentPos(game.getPositions().size() - 1);
            } catch (Exception ex) {
                System.out.println(ex);
            }

        }
    }

    public void actionPerformed(ActionEvent e) {

        try {
            int pos = Integer.parseInt(e.getActionCommand());
            game.setCurrentPos(pos);
        } catch (Exception ex) {
        }
    }

}