package com.maulss.core.bukkit.imgmessage;

import com.maulss.core.util.ArrayWrapper;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.util.ChatPaginator;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public final class ImageMessage {

    private final static char TRANSPARENT_CHAR = ' ';
    private final Color[] colors = {
            new Color(0, 0, 0),		new Color(0, 0, 170),		new Color(0, 170, 0),		new Color(0, 170, 170),
            new Color(170, 0, 0),	    new Color(170, 0, 170),	new Color(255, 170, 0),		new Color(170, 170, 170),
            new Color(85, 85, 85),	    new Color(85, 85, 255),	new Color(85, 255, 85),		new Color(85, 255, 255),
            new Color(255, 85, 85),	new Color(255, 85, 255),	new Color(255, 255, 85),	new Color(255, 255, 255),};
    private String[] lines;

    public ImageMessage(final BufferedImage image,
                        final int height) {
        this(image, height, ImageChar.BLOCK);
    }

    public ImageMessage(final BufferedImage image,
                        final int height,
                        final ImageChar imgChar) {
        this(image, height, imgChar.getChar());
    }

    public ImageMessage(final BufferedImage image,
                        final int height,
                        final char imgChar) {
        Validate.notNull(image, "image");

        ChatColor[][] chatColors = toChatColorArray(image, height);
        lines = toImgMessage(chatColors, imgChar);
    }

    public ImageMessage(final ChatColor[][] chatColors) {
        this(chatColors, ImageChar.BLOCK);
    }

    public ImageMessage(final ChatColor[][] chatColors,
                        final ImageChar imgChar) {
        this(chatColors, imgChar.getChar());
    }

    public ImageMessage(final ChatColor[][] chatColors,
                        final char imgChar) {
        Validate.notNull(chatColors, "chatColors");
        lines = toImgMessage(chatColors, imgChar);
    }

    public ImageMessage(final String... imgLines) {
        lines = Validate.noNullElements(imgLines, "imgLines");
    }

    public ImageMessage appendText(final String... text) {
        for (int y = 0; y < lines.length; y++)
            if (text.length > y)
                lines[y] += " " + text[y];

        return this;
    }

    public ImageMessage appendCenteredText(final String... text) {
        Validate.noNullElements(text, "text");
        for (int y = 0; y < lines.length; y++) {
            if (text.length > y)
                lines[y] = lines[y] + center(text[y], ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH - lines[y].length());
            else
                return this;
        }
        return this;
    }

    private ChatColor[][] toChatColorArray(final BufferedImage image,
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

    private String[] toImgMessage(final ChatColor[][] colors,
                                  final char imgchar) {
        String[] lines = new String[colors[0].length];
        for (int y = 0; y < colors[0].length; y++) {
            StringBuilder line = new StringBuilder();
            for (ChatColor[] color1 : colors) {
                ChatColor color = color1[y];
                line.append(color != null ? color1[y].toString() + imgchar : TRANSPARENT_CHAR);
            }

            lines[y] = line.toString() + ChatColor.RESET;
        }

        return lines;
    }

    private BufferedImage resizeImage(final BufferedImage originalImage,
                                      final int width,
                                      final int height) {
        AffineTransform af = new AffineTransform();
        af.scale(width / (double) originalImage.getWidth(), height / (double) originalImage.getHeight());

        AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(originalImage, null);
    }

    private double getDistance(final Color c1,
                               final Color c2) {
        double rmean = (c1.getRed() + c2.getRed()) / 2.0;
        double r = c1.getRed() - c2.getRed();
        double g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return (2 + rmean / 256.0) * r * r + (4.0) * g * g + (2 + (255 - rmean) / 256.0) * b * b;
    }

    private boolean areIdentical(final Color c1,
                                 final Color c2) {
        return Math.abs(c1.getRed() - c2.getRed()) <= 5 &&
                Math.abs(c1.getGreen() - c2.getGreen()) <= 5 &&
                Math.abs(c1.getBlue() - c2.getBlue()) <= 5;
    }

    private ChatColor getClosestChatColor(final Color color) {
        if (color.getAlpha() < 128)
            return null;

        int index = 0;
        double best = -1;

        for (int i = 0; i < colors.length; i++)
            if (areIdentical(colors[i], color))
                return ChatColor.values()[i];

        for (int i = 0; i < colors.length; i++) {
            double distance = getDistance(color, colors[i]);
            if (distance < best || best == -1) {
                best = distance;
                index = i;
            }
        }

        return ChatColor.values()[index];
    }

    private String center(final String s,
                          final int length) {
        if (s.length() > length)
            return s.substring(0, length);
        else if (s.length() == length)
            return s;
        else {
            int leftPadding = (length - s.length()) / 2;
            StringBuilder leftBuilder = new StringBuilder();
            for (int i = 0; i < leftPadding; i++)
                leftBuilder.append(" ");
            return leftBuilder.toString() + s;
        }
    }

    public String[] getLines() {
        return lines;
    }

    public void apply(Consumer<ArrayWrapper<String>> function) {
        function.accept(new ArrayWrapper<>(lines));
    }
}