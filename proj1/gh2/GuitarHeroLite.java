package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHeroLite {
    public static final double CONCERT_A = 440.0;
    public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);
    private static String keyboard;
    private static GuitarString[] guitarStrings;

    private static void createGuitarStrings() {
        keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        guitarStrings = new GuitarString[keyboard.length()];
        for (int i = 0; i < guitarStrings.length; i++) {
            guitarStrings[i] = new GuitarString(440 * Math.pow(2, (double) (i - 24) / 12));
        }
    }

    public static void main(String[] args) {
        createGuitarStrings();

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int idx = keyboard.indexOf(key);
                if (idx != -1) {
                    guitarStrings[idx].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for (GuitarString gs : guitarStrings) {
                sample += gs.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < keyboard.length(); i++) {
                guitarStrings[i].tic();
            }
        }
    }
}
