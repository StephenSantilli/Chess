package guifx;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

public class SVGTranscoder extends ImageTranscoder {

    private BufferedImage img = null;

    public SVGTranscoder(int pieceSize, String svgName) throws Exception {

        super();

        addTranscodingHint(SVGTranscoder.KEY_WIDTH, (float) pieceSize);
        addTranscodingHint(SVGTranscoder.KEY_HEIGHT, (float) pieceSize);

        File f = new File(getClass().getResource("/img/" + svgName + ".svg").toString());
        TranscoderInput input = new TranscoderInput(
                getClass().getResource("/img/" + svgName + ".svg").toURI().toString());

        transcode(input, null);

    }

    public ImageView getImageView() {

        ImageView i = new ImageView(SwingFXUtils.toFXImage(img, null));

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

}
