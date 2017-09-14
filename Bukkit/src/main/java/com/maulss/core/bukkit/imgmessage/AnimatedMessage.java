package com.maulss.core.bukkit.imgmessage;

import org.apache.commons.lang3.Validate;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimatedMessage {

    private ImageMessage[] images;
    private int index = 0;

    public AnimatedMessage(final ImageMessage... images) {
        this.images = Validate.notNull(images);
    }

    public AnimatedMessage(final File gifFile,
                           final int height,
                           final char imgChar) throws IOException {
        List<BufferedImage> frames = getFrames(gifFile);
        images = new ImageMessage[frames.size()];
        for (int i = 0; i < frames.size(); i++)
            images[i] = new ImageMessage(frames.get(i), height, imgChar);
    }

    public List<BufferedImage> getFrames(final File input) {
        Validate.notNull(input);
        List<BufferedImage> images = new ArrayList<>();
        try {
            ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
            reader.setInput(ImageIO.createImageInputStream(input));
            for (int i = 0, count = reader.getNumImages(true); i < count; i++)
                images.add(reader.read(i)); // read next frame from gif

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return images;
    }

    public ImageMessage current() {
        return images[index];
    }

    public ImageMessage next() {
        index++;
        if (index >= images.length) {
            index = 0;
            return images[index];
        } else {
            return images[index];
        }
    }

    public ImageMessage previous() {
        index--;
        if (index <= 0) {
            index = images.length - 1;
            return images[index];
        } else {
            return images[index];
        }
    }

    public ImageMessage getIndex(final int index) {
        return images[index];
    }
}