package com.maulss.core.bukkit.imgtext;

import org.apache.commons.lang3.Validate;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimatedMessage {

    private ImageText[] images;
    private int index = 0;

    public AnimatedMessage(final ImageText... images) {
        this.images = Validate.notNull(images, "images");
    }

    public AnimatedMessage(final File gifFile,
                           final int height) throws IOException {
        this(gifFile, height, ImageChar.BLOCK);
    }

    public AnimatedMessage(final File gifFile,
                           final int height,
                           final ImageChar imgChar) throws IOException {
        this(gifFile, height, imgChar.getChar());
    }

    public AnimatedMessage(final File gifFile,
                           final int height,
                           final char imgChar) throws IOException {
        List<BufferedImage> frames = getFrames(gifFile);
        images = new ImageText[frames.size()];
        for (int i = 0; i < frames.size(); i++)
            images[i] = new ImageText(frames.get(i), height, imgChar);
    }

    public List<BufferedImage> getFrames(final File input) {
        Validate.notNull(input, "input");
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

    public ImageText current() {
        return images[index];
    }

    public ImageText next() {
        index++;
        if (index >= images.length) {
            index = 0;
            return images[index];
        } else {
            return images[index];
        }
    }

    public ImageText previous() {
        index--;
        if (index <= 0) {
            index = images.length - 1;
            return images[index];
        } else {
            return images[index];
        }
    }

    public ImageText getIndex(final int index) {
        return images[index];
    }
}