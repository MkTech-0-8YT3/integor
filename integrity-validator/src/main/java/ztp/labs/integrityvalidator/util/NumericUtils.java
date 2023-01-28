package ztp.labs.integrityvalidator.util;

import java.util.Random;

public class NumericUtils {

    public static int getRandom(int max) {
        return new Random().nextInt(max);
    }

}
