package guifx;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

public class PieceTranscoder extends ImageTranscoder {

    private BufferedImage img = null;

    private boolean color;
    private char pieceCode;

    public PieceTranscoder(int pieceSize, boolean color, char pieceCode) throws Exception {

        super();
        this.color = color;
        this.pieceCode = pieceCode;

        addTranscodingHint(KEY_PIXEL_UNIT_TO_MILLIMETER, (25.4f/1200));
        addTranscodingHint(PieceTranscoder.KEY_WIDTH, (float) pieceSize);
        addTranscodingHint(PieceTranscoder.KEY_HEIGHT, (float) pieceSize);

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
        i.setManaged(false);

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

    public BufferedImage getBufferedImage() {
        return img;
    }

    public boolean isColor() {
        return color;
    }

    public char getPieceCode() {
        return pieceCode;
    }

}
