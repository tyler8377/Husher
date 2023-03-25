package org.tyler.husher.client.ui;

import javafx.application.Application;
import javafx.stage.Stage;
import org.tyler.husher.core.util.FXUtils;

public class FxApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            MainFrame mainFrame = MainFrame.INSTANCE;
            mainFrame.initWindow();
            mainFrame.initComponents();
            mainFrame.setVisible(true);
        } catch (Throwable t) {
            FXUtils.handleException(getClass(), t);
            System.exit(1);
        }
    }
}
