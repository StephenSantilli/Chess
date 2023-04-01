package gui;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

public class PieceTranscoder extends ImageTranscoder {

    private BufferedImage img = null;

    private final boolean color;
    private final char pieceCode;
    private final double pieceSize;

    public boolean isColor() {
        return color;
    }

    public char getPieceCode() {
        return pieceCode;
    }

    public PieceTranscoder(double pieceSize, boolean color, char pieceCode) throws Exception {

        super();

        this.color = color;
        this.pieceCode = pieceCode;
        this.pieceSize = pieceSize;

        addTranscodingHint(PieceTranscoder.KEY_WIDTH, 45f * ((int) pieceSize / 45 + 2));
        addTranscodingHint(PieceTranscoder.KEY_HEIGHT, 45f * ((int) pieceSize / 45 + 2));

        try {

            TranscoderInput input = new TranscoderInput(
                    getClass().getResource("/img/" + (color ? "W" : "B") + pieceCode + ".svg").toURI().toString());

            transcode(input, null);

        } catch (Exception e) {
            throw new Exception("Piece image not found.");
        }

    }

    public ImageView getImageView() {

        ImageView i = new ImageView(SwingFXUtils.toFXImage(img, null));

        i.setSmooth(false);
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