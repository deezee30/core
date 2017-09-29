package com.maulss.core.bukkit.imgtext;

import com.maulss.core.util.ArrayWrapper;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.util.ChatPaginator;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import static com.maulss.core.bukkit.imgtext.ColorUtil.*;

public final class ImageText {

    private static final char TRANSPARENT_CHAR = ' ';

    private final String[] lines;

    public ImageText(final BufferedImage image,
                     final int height) {
        this(image, height, ImageChar.BLOCK);
    }

    public ImageText(final BufferedImage image,
                     final int height,
                     final ImageChar imgChar) {
        this(image, height, imgChar.getChar());
    }

    public ImageText(final BufferedImage image,
                     final int height,
                     final char imgChar) {
        this(toChatColorArray(image, height), imgChar);
    }

    public ImageText(final ChatColor[][] colors) {
        this(colors, ImageChar.BLOCK);
    }

    public ImageText(final ChatColor[][] colors,
                     final ImageChar imgChar) {
        this(colors, imgChar.getChar());
    }

    public ImageText(final ChatColor[][] colors,
                     final char imgChar) {
        Validate.notNull(colors, "colors");

        int len = colors[0].length;
        lines = new String[len];
        for (int y = 0; y < len; y++) {
            StringBuilder line = new StringBuilder();
            for (ChatColor[] color1 : colors) {
                ChatColor color = color1[y];
                line.append(color != null ? color1[y].toString() + imgChar : TRANSPARENT_CHAR);
            }

            lines[y] = line.toString() + ChatColor.RESET;
        }
    }

    public ImageText(final String... imgLines) {
        lines = Validate.noNullElements(imgLines, "imgLines");
    }

    public ImageText appendText(final String... text) {
        for (int y = 0; y < lines.length; y++)
            if (text.length > y)
                lines[y] += " " + text[y];

        return this;
    }

    public ImageText appendCenteredText(final String... text) {
        Validate.noNullElements(text, "text");
        for (int y = 0; y < lines.length; y++) {
            if (text.length > y)
                lines[y] = lines[y] + ColorUtil.center(text[y], ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH - lines[y].length());
            else
                return this;
        }
        return this;
    }

    public String[] getLines() {
        return lines;
    }

    public void apply(Consumer<ArrayWrapper<String>> function) {
        function.accept(new ArrayWrapper<>(lines));
    }

    public static ChatColor[][] toChatColorArray(final BufferedImage image,
                                                 final int height) {
        double ratio = (double) image.getHeight() / image.getWidth();
        int width = (int) (height / ratio);
        BufferedImage resized = resizeImage(image, width, height);

        ChatColor[][] chatImg = new ChatColor[resized.getWidth()][resized.getHeight()];
        for (int x = 0; x < resized.getWidth(); x++) {
            for (int y = 0; y < resized.getHeight(); y++) {
                int rgb = resized.getRGB(x, y);
                ChatColor closest = getClosestChatColor(new Color(rgb, true));
                chatImg[x][y] = closest;
            }
        }

        return chatImg;
    }

    public static BufferedImage resizeImage(final BufferedImage originalImage,
                                            final int width,
                                            final int height) {
        AffineTransform af = new AffineTransform();
        af.scale(width / (double) originalImage.getWidth(), height / (double) originalImage.getHeight());

        AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(originalImage, null);
    }
}