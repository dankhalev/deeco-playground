package com.khalev.efd.visualization;

class EnvironmentMap {

    Boolean[][] map;

    EnvironmentMap(int sizeX, int sizeY) {
        map = new Boolean[sizeX][sizeY];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = false;
            }
        }
    }

    void addVisibleObstacle(int x, int y) {
        map[x][y] = true;
    }

    boolean isOccupied(int x, int y) {
        return map[x][y];
    }

    int getHeight() {
        return map[0].length;
    }
    int getWidth() {
        return map.length;
    }
}
