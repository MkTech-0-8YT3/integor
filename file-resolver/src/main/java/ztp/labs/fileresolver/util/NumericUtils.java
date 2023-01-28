package ztp.labs.fileresolver.util;

import java.util.Random;

public class NumericUtils {

    /**
     * Note: This method uses insecure random
     */
    public static int getRandom(int max) {
        return new Random().nextInt(max);
    }

}
