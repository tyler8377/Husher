package org.tyler.husher.client.ui;

import javafx.animation.FadeTransition;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import org.tyler.husher.client.Husher;
import org.tyler.husher.client.ui.panel.HomePanel;
import org.tyler.husher.client.ui.panel.WelcomePanel;
import org.tyler.husher.core.util.ResourceUtils;

import javax.imageio.ImageIO;
import javax.swing.*;

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
            setIconImage(ImageIO.read(ResourceUtils.openResource("images/icon.png")));
        } catch (Exception e) {
            System.err.println("ERROR: Unable to load icon: " + e);
        }
    }

    public void initComponents() {
        layout.getStylesheets().add("styles/styles.css");
        layout.getStylesheets().add("styles/scrollpane.css");
        layout.getStylesheets().add("styles/slider.css");
        layout.getStylesheets().add("styles/combobox.css");
        layout.setStyle("-fx-background-color: #121212;");

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

        WelcomePanel welcomePanel = new WelcomePanel();
        Husher.getUserConfig().getProfiles().forEach(welcomePanel::addProfile);
        showPanel(welcomePanel);

        // showPanel(new HomePanel());
    }

    public void showPanel(Pane pane) {
        layout.getChildren().clear();
        layout.getChildren().addAll(pane);

        FadeTransition transition = new FadeTransition(Duration.millis(500), pane);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.setAutoReverse(true);
        transition.play();
    }

    public Scene getScene() {
        return scene;
    }

    public GridPane getJFXLayout() {
        return layout;
    }
}