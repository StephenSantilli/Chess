package gui.board;

import game.Square;
import gui.GameView;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class Coordinates extends Pane {

    private GameView gameView;

    public Coordinates(GameView gameView) {

        this.gameView = gameView;

    }

    public void draw() {

        getChildren().clear();

        final Board board = gameView.getBoard();
        final double squareSize = board.getSquareSize();

        for (int r = 8; r > 0; r--) {

            final Square square = new Square(gameView.isFlipped() ? 8 : 1, r);
            final double inset = squareSize / 40;

            Text l = new Text(r + "");

            l.setId(square.isLightSquare() ? "lightCoord" : "darkCoord");

            Bounds lb = l.getBoundsInLocal();
            l.setLayoutX(board.getXBySquare(square) + inset + (lb.getWidth() / 2.0));
            l.setLayoutY(board.getYBySquare(square) + inset + (lb.getHeight() / 2.0));

            getChildren().add(l);

        }

        for (int f = 1; f <= 8; f++) {

            final Square square = new Square(f, gameView.isFlipped() ? 8 : 1);
            final double inset = squareSize / 40;

            Text l = new Text((char) (f + 96) + "");

            l.setId(square.isLightSquare() ? "lightCoord" : "darkCoord");

            Bounds lb = l.getBoundsInLocal();
            l.setLayoutX(board.getXBySquare(square) + squareSize - lb.getWidth() - inset - (lb.getWidth() / 2.0));
            l.setLayoutY(board.getYBySquare(square) + squareSize - inset - (lb.getHeight() / 2.0));

            getChildren().add(l);

        }

    }

}
