package org.tyler.husher.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import org.tyler.husher.util.Utils;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class UIHelper {

    public static final int tabsYPos = 160;
    public static final int btnTabsHeight = 40;
    public static final int btnTabsWidth = 180;

    public static final String COLOR_DARK_RED = "#c0392b";
    public static final String COLOR_GREEN = "#2ecc71";
    public static final String COLOR_DARK_GREY = "#282828";
    public static final String COLOR_DARK_GREY2 = "#242424";

    public static final Font subtitleFont = ResourceManager.getMontserratFont("Bold", 18.0f);

    public static JFXTextField createStyledTextField() {
        JFXTextField textField = new JFXTextField();
        textField.setStyle("-fx-font-size: 14px; -fx-display-caret: true; -fx-highlight-fill: #7a7a7a; -fx-padding: 0.7em 0.57em; -fx-text-fill: #ecf0f1; -jfx-focus-color: " + UIHelper.COLOR_DARK_RED + "; -fx-background-color: #3d3d3d; -fx-prompt-text-fill: #aaa69d;");
        return textField;
    }

    public static JFXButton createStyledButton() {
        JFXButton button = new JFXButton();
        button.setStyle("-fx-text-fill: #fff; -fx-background-color: #c0392b; -fx-background-radius: 5px;");
        return button;
    }

    public static JFXToggleButton createStyledToggleButton() {
        float scale = 0.86f;
        JFXToggleButton toggleButton = new JFXToggleButton();
        toggleButton.setScaleX(scale);
        toggleButton.setScaleY(scale);
        toggleButton.setStyle("-jfx-toggle-color: " + COLOR_DARK_RED + "; -fx-text-fill: white;");
        return toggleButton;
    }

    public static JFXCheckBox createStyledCheckBox() {
        JFXCheckBox checkBox = new JFXCheckBox();
        float scale = 0.92f;
        checkBox.setStyle("-jfx-checked-color: " + COLOR_DARK_RED + ";");
        checkBox.setScaleX(scale);
        checkBox.setScaleY(scale);
        return checkBox;
    }

    public static Label createStyledSubTitleLabel() {
        Label label = new Label();
        label.setFont(subtitleFont);
        return label;
    }

    public static void handleException(Class<?> clazz, Throwable t) {
        LoggerFactory.getLogger(clazz).error("Exception on {}", clazz.getSimpleName(), t);
        Throwable root = Utils.findRootCause(t);
        String message = root.getClass().getName() + ": " + root.getMessage();

        Object[] buttons = {
            "Copy details", "OK"
        };

        int choice = JOptionPane.showOptionDialog(null, message, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, buttons[1]);
        
        if (choice == 0) {
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(Utils.throwableToString(root)), null);
            } catch (Exception e) {
                LoggerFactory.getLogger(UIHelper.class).error("Unable to copy stacktrace to the clipboard", e);
            }
        }
    }
}
