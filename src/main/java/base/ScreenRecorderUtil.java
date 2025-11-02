package base;

import org.monte.media.Format;
import org.monte.media.math.Rational;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;

import static org.monte.media.AudioFormatKeys.MediaType;
import static org.monte.media.AudioFormatKeys.MediaTypeKey;
import static org.monte.media.VideoFormatKeys.*;

public class ScreenRecorderUtil {

    private CustomScreenRecorder screenRecorder;

    public void start(String name) throws Exception {
        File outDir = new File("target/videos");
        if (!outDir.exists()) outDir.mkdirs();

        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle area = new Rectangle(0, 0, s.width, s.height);

        screenRecorder = new CustomScreenRecorder(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice().getDefaultConfiguration(),
                area,
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO,
                        EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        DepthKey, 24,
                        FrameRateKey, Rational.valueOf(15),
                        QualityKey, 1.0f,
                        KeyFrameIntervalKey, 15 * 60),
                new Format(MediaTypeKey, MediaType.VIDEO,
                        EncodingKey, "black",
                        FrameRateKey, Rational.valueOf(30)),
                null,
                outDir,
                name
        );
        screenRecorder.start();
    }

    public File stop() throws Exception {
        if (screenRecorder != null) {
            screenRecorder.stop();
            if (!screenRecorder.getCreatedMovieFiles().isEmpty()) {
                return screenRecorder.getCreatedMovieFiles().get(0);
            }
        }
        return null;
    }
}
