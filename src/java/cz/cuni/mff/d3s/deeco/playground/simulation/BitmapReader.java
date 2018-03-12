package cz.cuni.mff.d3s.deeco.playground.simulation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Reads an image to produce an {@link EnvironmentMap} from it.
 *
 * @author Danylo Khalyeyev
 */
class BitmapReader {

    private BufferedImage img;
    private int sizeX;
    private int sizeY;

    BitmapReader(File bitmap) throws IOException {
        this.img = ImageIO.read(bitmap);
        this.sizeX = img.getWidth();
        this.sizeY = img.getHeight();
    }

    /**
     * Reads a bitmap and constructs an {@link EnvironmentMap}.
     * @return a created {@link EnvironmentMap}
     */
    EnvironmentMap readBitmap() {
        EnvironmentMap map = new EnvironmentMap(sizeX, sizeY);
        //read an image and add a line wherever a black pixel occurs near a non-black pixel
        for (int y = -1; y <= sizeY; y++) {
            for (int x = -1; x <= sizeX; x++) {
                if (isBlack(x, y)) {
                    if (!isBlack(x, y-1))
                        map.horizontal[y].add(new Line(x, x+1, y, false));
                    if (!isBlack(x, y+1))
                        map.horizontal[y+1].add(new Line(x, x+1, y+1, false));
                    if (!isBlack(x-1, y))
                        map.vertical[x].add(new Line(y, y+1, x, true));
                    if (!isBlack(x+1, y))
                        map.vertical[x+1].add(new Line(y, y+1, x+1, true));
                }
            }
        }
        //merge lines where possible to reduce their count
        for (int i = 0; i < map.horizontal.length; i++) {
            for (int j = 0; j < map.horizontal[i].size(); j++) {
                boolean b = true;
                while (b) {
                    Line start = map.horizontal[i].get(j);
                    if (j == map.horizontal[i].size() - 1) {
                        b = false;
                    } else {
                        Line next = map.horizontal[i].get(j + 1);
                        if (start.end == next.start) {
                            map.horizontal[i].remove(j);
                            map.horizontal[i].remove(j);
                            map.horizontal[i].add(j, new Line(start.start, next.end, start.horizon, start.isVertical));
                        } else {
                            b = false;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < map.vertical.length; i++) {
            for (int j = 0; j < map.vertical[i].size(); j++) {
                boolean b = true;
                while (b) {
                    Line start = map.vertical[i].get(j);
                    if (j == map.vertical[i].size() - 1) {
                        b = false;
                    } else {
                        Line next = map.vertical[i].get(j + 1);
                        if (start.end == next.start) {
                            map.vertical[i].remove(j);
                            map.vertical[i].remove(j);
                            map.vertical[i].add(j, new Line(start.start, next.end, start.horizon, start.isVertical));
                        } else {
                            b = false;
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * Creates a boolean representation of the map of obstacles.
     * @return a boolean representation of the map of obstacles
     */
    boolean[][] getBooleanRepresentation() {
        boolean[][] map = new boolean[sizeX][sizeY];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (isBlack(x, y)) {
                    map[x][y] = true;
                } else {
                    map[x][y] = false;
                }
            }
        }
        return map;
    }

    /**
     * Creates an empty map without obstacles, only with boundaries.
     * @param sizeX width of created map
     * @param sizeY height of created map
     * @return an empty map without obstacles
     */
    static EnvironmentMap createEmptyMap(int sizeX, int sizeY) {
        EnvironmentMap map = new EnvironmentMap(sizeX, sizeY);

        map.horizontal[0].add(new Line(0, sizeX, 0, false));
        map.horizontal[sizeY].add(new Line(0, sizeX, sizeY, false));
        map.vertical[0].add(new Line(0, sizeY, 0, true));
        map.vertical[sizeX].add(new Line(0, sizeY, sizeX, true));

        return map;
    }

    /**
     * Tests whether a pixel is black.
     * @param x X-coordinate of a pixel
     * @param y Y-coordinate of a pixel
     * @return true if a pixel is black or lies outside of the image, false otherwise
     */
    private boolean isBlack(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return true;
        }
        int rgb = img.getRGB(x, y);
        int red = (rgb >> 16) & 0x000000FF;
        int green = (rgb >> 8) & 0x000000FF;
        int blue = (rgb) & 0x000000FF;
        return red == 0 && green == 0 && blue == 0;
    }

}
