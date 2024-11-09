package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Objects;
import java.util.Random;

public class MemoryGame {
    /**
     * The width of the window of this game.
     */
    private final int width;

    /**
     * The height of the window of this game.
     */
    private final int height;

    /**
     * The current round the user is on.
     */
    private int round;

    /**
     * The Random object used to randomly generate Strings.
     */
    private final Random rand;

    /**
     * Whether or not the game is over.
     */
    private boolean gameOver;

    /**
     * Whether or not it is the player's turn. Controls the instruction is Watch or Type.
     */
    private boolean playerTurn;

    /**
     * The characters we generate random Strings from.
     */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Encouraging phrases. Used in the last section of the spec, 'Helpful UI'.
     */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!", "You got this!", "You're a star!", "Go Bears!", "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    /**
     * Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
     * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
     */
    public MemoryGame(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        this.rand = new Random(seed);
    }

    /**
     * Generate random string of letters of length n.
     *
     * @param n The length of randomly generated string.
     * @return Random generated string.
     */
    public String generateRandomString(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char ch = CHARACTERS[this.rand.nextInt(CHARACTERS.length)];
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Take the string and display it in the center of the screen.
     * If game is not over, display relevant game information at the top of the screen.
     *
     * @param s The string to be displayed.
     */

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.text(this.width / 2.0, this.height / 2.0, s);
        if (!this.gameOver) { // Top menu display.
            String encouragement = ENCOURAGEMENT[this.rand.nextInt(ENCOURAGEMENT.length)];
            String instruction = this.playerTurn ? "Type!" : "Watch!";

            StdDraw.text(this.width / 2.0, this.height - 1, instruction);
            StdDraw.textLeft(0, this.height - 1, "Round: " + this.round);
            StdDraw.textRight(this.width, this.height - 1, encouragement);
        }
        StdDraw.show();
    }

    /**
     * Display each character in letters, making sure to blank the screen between letters
     *
     * @param letters The letters to be displayed.
     */
    public void flashSequence(String letters) {
        for (char ch : letters.toCharArray()) {
            this.drawFrame(String.valueOf(ch));
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
    }

    /**
     * Read n letters of player input
     *
     * @param n The length of string waiting to be inputted.
     * @return The string user inputs.
     */
    public String solicitNCharsInput(int n) {
        StringBuilder inputBuilder = new StringBuilder();
        int count = 0;

        while (count < n) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            count += 1;
            char curChar = StdDraw.nextKeyTyped();
            inputBuilder.append(curChar);
        }
        return inputBuilder.toString();
    }

    /**
     * Set any relevant variables before the game starts
     */
    public void startGame() {
        this.gameOver = false;
        this.round = 1;

        while (!this.gameOver) {
            this.playerTurn = false;
            String shownStr = this.generateRandomString(this.round);
            this.flashSequence(shownStr);

            this.playerTurn = true;
            String inputStr = this.solicitNCharsInput(this.round);

            if (Objects.equals(shownStr, inputStr)) {
                this.round += 1;
            } else {
                this.gameOver = true;
            }
        }
        this.playerTurn = false;
        this.drawFrame("Game Over! You made it to round: " + this.round);
    }
}
