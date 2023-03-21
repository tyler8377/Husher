package org.tyler.husher.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class FxApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            MainFrame mainFrame = MainFrame.INSTANCE;
            mainFrame.initWindow();
            mainFrame.initComponents();
            mainFrame.setVisible(true);
        } catch (Throwable t) {
            UIHelper.handleException(getClass(), t);
            System.exit(1);
        }
    }
}
