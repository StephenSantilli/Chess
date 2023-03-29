package gui.board;

import game.Square;
import gui.GameView;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

public class Coordinates extends Pane {

    private GameView gameView;

    public Coordinates(GameView gameView) {
        this.gameView = gameView;
    }

    public void draw() {

        final Board board = gameView.getBoard();

        getChildren().clear();

        final double squareSize = board.getSquareSize();

        for (int r = 8; r > 0; r--) {

            final Square square = new Square(1, r);
            final double inset = squareSize / 40;
            Text l = new Text(r + "");
            Bounds lb = l.getBoundsInLocal();
            l.setTextOrigin(VPos.CENTER);
            l.setLayoutX(board.getXBySquare(square) + inset + (lb.getWidth() / 2.0));
            l.setLayoutY(board.getYBySquare(square) + inset + (lb.getHeight() / 2.0));

            l.setId(square.isLightSquare() ? "lightCoord" : "darkCoord");

            getChildren().add(l);

        }

        for (int f = 1; f <= 8; f++) {

            final Square square = new Square(f, 1);
            final double inset = squareSize / 40;

            Text l = new Text((char) (f + 96) + "");
            Bounds lb = l.getBoundsInLocal();
            l.setTextOrigin(VPos.CENTER);
            l.setLayoutX(board.getXBySquare(square) + squareSize - lb.getWidth() - inset - (lb.getWidth() / 2.0));
            l.setLayoutY(board.getYBySquare(square) + squareSize - inset - (lb.getHeight() / 2.0));

            l.setId(square.isLightSquare() ? "lightCoord" : "darkCoord");

            getChildren().add(l);

        }

    }

}
