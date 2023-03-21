package org.tyler.husher.ui;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.tyler.husher.ui.panel.HomePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    public static final MainFrame INSTANCE = new MainFrame();

    private GridPane layout;
    private Scene scene;
    private HBox container;

    public void initWindow() {
        layout = new GridPane();
        scene = new Scene(layout);

        JFXPanel panel = new JFXPanel();
        panel.setScene(scene);

        setTitle("Husher");
        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 440);
        setLocationRelativeTo(null);

        try {
            setIconImage(ImageIO.read(ResourceManager.openResource("/image/icon.jpg")));
        } catch (Exception e) {
            System.err.println("ERROR: Unable to load icon: " + e);
        }
    }

    public void initComponents() {
        layout.getStylesheets().add("styles.css");
        layout.getStylesheets().add("scrollpane.css");
        layout.getStylesheets().add("slider.css");
        layout.getStylesheets().add("combobox.css");
        layout.setStyle("-fx-background-color: #191919;");

        container = new HBox();
        GridPane.setVgrow(container, Priority.ALWAYS);
        GridPane.setHgrow(container, Priority.ALWAYS);

        // JFXSpinner loadingSpinner = new JFXSpinner();
        // GridPane.setHgrow(loadingSpinner, Priority.ALWAYS);
        // GridPane.setVgrow(loadingSpinner, Priority.ALWAYS);
        // GridPane.setHalignment(loadingSpinner, HPos.CENTER);
        // GridPane.setValignment(loadingSpinner, VPos.CENTER);
        // loadingSpinner.setMaxSize(40, 40);
        // layout.getChildren().addAll(loadingSpinner);

        // layout.getChildren().clear();

        HomePanel homePanel = new HomePanel();
        layout.getChildren().addAll(homePanel);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                homePanel.focusInputField();
            }
        });
    }

    public Scene getScene() {
        return scene;
    }

    public GridPane getJFXLayout() {
        return layout;
    }
}