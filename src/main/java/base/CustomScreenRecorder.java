package base;

import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;


public class CustomScreenRecorder extends ScreenRecorder {

    private final String name;

    public CustomScreenRecorder(GraphicsConfiguration cfg,
                                Rectangle captureArea,
                                Format fileFormat,
                                Format screenFormat,
                                Format mouseFormat,
                                Format audioFormat,
                                File movieFolder,
                                String name) throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
        this.name = name;
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
        if (!movieFolder.exists()) {
            if (!movieFolder.mkdirs()) {
                throw new IOException("Video folder not created " + movieFolder.getAbsolutePath());
            }
        } else if (!movieFolder.isDirectory()) {
            throw new IOException("Video path not available " + movieFolder.getAbsolutePath());
        }
        String ext = Registry.getInstance().getExtension(fileFormat);
        return new File(movieFolder, name + "-" + System.currentTimeMillis() + "." + ext);
    }
}
