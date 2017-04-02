package com.khalev.efd.visualization;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class BitmapProcessor {

    BufferedImage img;

    EnvironmentMap readBitmap(File bitmap) throws IOException {
        img = ImageIO.read(bitmap);
        int sizeY = img.getHeight();
        int sizeX = img.getWidth();
        EnvironmentMap map = new EnvironmentMap(sizeX, sizeY);
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                if (isBlack(x, y)) {
                    map.addVisibleObstacle(x, y);
                }
            }
        }
        return map;
    }

    boolean isBlack(int x, int y) {
        int rgb = img.getRGB(x, y);
        int red = (rgb >> 16 ) & 0x000000FF;
        int green = (rgb >> 8 ) & 0x000000FF;
        int blue = (rgb) & 0x000000FF;
        return red == 0 && blue == 0 && green == 0;
    }
}
