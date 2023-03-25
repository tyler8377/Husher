package org.tyler.husher.core.util;

import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class FXUtils {

    public static String hexToRGBA(String hex, float a) {
        Color c = Color.decode(hex);
        return String.format("rgba(%s,%s,%s,%s)", c.getRed(), c.getGreen(), c.getBlue(), a);
    }

    public static void handleException(Class<?> clazz, Throwable t) {
        LoggerFactory.getLogger(clazz).error("Exception on {}", clazz.getSimpleName(), t);
        Throwable root = ExceptionUtils.findRootCause(t);
        String message = root.getClass().getName() + ": " + root.getMessage();

        Object[] buttons = {
                "Copy details", "OK"
        };

        int choice = JOptionPane.showOptionDialog(null, message, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, buttons[1]);

        if (choice == 0) {
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(ExceptionUtils.throwableToString(root)), null);
            } catch (Exception e) {
                LoggerFactory.getLogger(FXUtils.class).error("Unable to copy stacktrace to the clipboard", e);
            }
        }
    }
}
