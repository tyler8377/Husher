package org.tyler.husher.util;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {

    /*
        Exceptions
     */

    public static Throwable findRootCause(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }

    public static String throwableToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    /*
        JavaFX
     */

    public static String hexToRGBA(String hex, float a) {
        Color c = Color.decode(hex);
        return String.format("rgba(%s,%s,%s,%s)", c.getRed(), c.getGreen(), c.getBlue(), a);
    }

}
