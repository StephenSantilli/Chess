package gui;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

/**
 * A transcoder for SVG icons of pieces. Will take a {@code .svg} image as input
 * and transcode it into an image that can be displayed within the program.
 */
public class PieceTranscoder extends ImageTranscoder {

    /**
     * The image that was transcoded from an SVG.
     */
    private BufferedImage img = null;

    /**
     * Whether or not the piece is white.
     */
    private final boolean white;

    /**
     * The code of the piece (e.g. 'K', 'P', etc.)
     */
    private final char pieceCode;

    /**
     * The height and width, in pixels, that the pieces on the board are.
     */
    private final double pieceSize;

    /**
     * Creates a new piece transcoder based on the current piece size, the color it
     * should be, and the code it should be.
     * 
     * @param pieceSize The height and width the piece image should be.
     * @param white     Whether or not the piece should be white.
     * @param pieceCode The letter code of the piece.
     * @throws Exception If there is an error transcoding the SVG.
     */
    public PieceTranscoder(double pieceSize, boolean white, char pieceCode) throws Exception {

        super();

        this.white = white;
        this.pieceCode = pieceCode;
        this.pieceSize = pieceSize;

        addTranscodingHint(PieceTranscoder.KEY_WIDTH, 45f * ((int) pieceSize / 45 + 2));
        addTranscodingHint(PieceTranscoder.KEY_HEIGHT, 45f * ((int) pieceSize / 45 + 2));

        try {

            TranscoderInput input = new TranscoderInput(
                    getClass().getResource("/img/" + (white ? "W" : "B") + pieceCode + ".svg").toURI().toString());

            transcode(input, null);

        } catch (Exception e) {
            throw new Exception("Piece image not found.");
        }

    }

    /**
     * Gets whether or not the piece is white.
     * 
     * @return {@link #white}
     */
    public boolean isWhite() {
        return white;
    }

    /**
     * Gets the code of the piece (e.g. 'K', 'P', etc.)
     * 
     * @return {@link #pieceCode}
     */
    public char getPieceCode() {
        return pieceCode;
    }

    /**
     * Gets the height and width, in pixels, that the pieces on the board are.
     * 
     * @return {@link #pieceSize}
     */
    public double getPieceSize() {
        return pieceSize;
    }

    /**
     * Outputs the image as an {@code ImageView}.
     * 
     * @return The transcoded image as an image view.
     */
    public ImageView toImageView() {

        ImageView i = new ImageView(SwingFXUtils.toFXImage(img, null));

        i.setManaged(false);
        i.setPreserveRatio(true);
        i.setFitWidth(pieceSize);

        return i;

    }

    @Override
    public BufferedImage createImage(int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return bi;
    }

    @Override
    public void writeImage(BufferedImage img, TranscoderOutput to) throws TranscoderException {
        this.img = img;
    }

}