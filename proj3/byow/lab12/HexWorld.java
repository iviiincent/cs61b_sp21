package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

class HexWorld {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    private static class Position {
        private final int x;
        private final int y;

        private static final Position STARTING_POS = new Position(0, 0);

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private Position shift(int dx, int dy) {
            return new Position(x + dx, y + dy);
        }

        private Position getUpperRightPosition(int size) {
            return shift(2 * size - 1, size);
        }

        private Position getLowerRightPosition(int size) {
            return shift(2 * size - 1, -size);
        }
    }

    /**
     * Fills the world with Tileset.Nothing.
     */
    private static void fillWithNothing(TETile[][] world) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Picks a RANDOM tiles.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(6);
        return switch (tileNum) {
            case 0 -> Tileset.WALL;
            case 1 -> Tileset.FLOWER;
            case 2 -> Tileset.MOUNTAIN;
            case 3 -> Tileset.WATER;
            case 4 -> Tileset.FLOOR;
            case 5 -> Tileset.TREE;

            default -> Tileset.NOTHING;
        };
    }

    /**
     * Draws one row, starting with B blank tile and T TILE tiles.
     */
    private static void drawHexHelper(TETile[][] world, Position p, TETile tile, int b, int t) {
        int x = p.x;
        int y = p.y;
        for (int i = b; i < b + t; ++i) {
            world[x + i][y] = tile;
        }
    }

    /**
     * Draws one hexagon.
     *
     * @param p    After filling the hexagon to a square, the position of the most left and
     *             lowest pixel.
     * @param size The size of the hexagon.
     * @param tile The type of the pixels of the hexagon.
     */
    private static void drawHex(TETile[][] world, Position p, TETile tile, int size) {
        int x = p.x;
        int y = p.y;
        for (int i = 0; i < size; i++) {
            drawHexHelper(
                    world, new Position(x, y + i),
                    tile, size - i - 1, size + 2 * i);
            drawHexHelper(
                    world, new Position(x, y + 2 * size - i - 1),
                    tile, size - i - 1, size + 2 * i);
        }
    }

    /**
     * Draws a column of hexagons.
     *
     * @param p    The starting position of the col of hexagons.
     * @param size The size of the hexagons.
     * @param num  The number of the hexagons in this column.
     */
    private static void drawColHexagons(TETile[][] world, Position p, int size, int num) {
        int x = p.x;
        int y = p.y;

        for (int i = 0; i < num; i++) {
            drawHex(world, new Position(x, y), randomTile(), size);
            y += 2 * size;
        }
    }

    /**
     * Draws the world.
     *
     * @param hexSize The size of the hexagons.
     * @param hexNum  The number of the hexagons in the first column.
     */
    private static void drawWorld(TETile[][] world, int hexSize, int hexNum) {
        Position p = Position.STARTING_POS.shift(0, 3 * hexSize);
        int maxNum = hexNum + 2;

        for (int num = hexNum; num < maxNum; num++) {
            drawColHexagons(world, p, hexSize, num);
            p = p.getLowerRightPosition(hexSize);
        }

        for (int num = maxNum; num >= hexNum; num--) {
            drawColHexagons(world, p, hexSize, num);
            p = p.getUpperRightPosition(hexSize);
        }

    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);

        drawWorld(world, 4, 3);

        ter.renderFrame(world);
    }
}
