package org.tyler.husher.ui.panel;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.tyler.husher.HusherConstants;
import org.tyler.husher.ui.MainFrame;
import org.tyler.husher.ui.component.LoginProfileBox;
import org.tyler.husher.util.ResourceUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class WelcomePanel extends VBox {

    private static final Font labelFont = ResourceUtils.getMontserratFont("Medium", 15.0f);
    private static final Font buttonFont = ResourceUtils.getMontserratFont("Medium", 13.0f);

    private final Map<String, LoginProfileBox> profileBoxes;
    private final VBox profileButtonsBox;

    public WelcomePanel() {
        this.profileBoxes = new HashMap<>();

        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(4);

        Label titleLabel = new Label("Welcome to Husher");
        titleLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "Bold", 24.0f));

        Label subtitleLabel = new Label("v" + HusherConstants.VERSION);
        subtitleLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "Medium", 12.0f));
        subtitleLabel.setOpacity(0.8f);

        VBox topBox = new VBox(titleLabel, subtitleLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setTranslateY(-30);

        Font buttonFont = ResourceUtils.getFont("UbuntuMono", "Regular", 13.0f);
        // Font buttonFont = ResourceUtils.getFont("JetBrainsMono", "Regular", 12.0f);

        JFXButton createProfileButton = new JFXButton("Create profile");
        createProfileButton.setStyle("-fx-text-fill: #fff; -fx-background-color: #c0392b; -fx-background-radius: 5px;");
        createProfileButton.setFont(buttonFont);
        createProfileButton.setDisableVisualFocus(true);

        JFXButton restoreProfileButton = new JFXButton("Restore profile");
        restoreProfileButton.setStyle("-fx-text-fill: #fff; -fx-background-color: #c0392b; -fx-background-radius: 5px;");
        restoreProfileButton.setFont(buttonFont);
        restoreProfileButton.setDisableVisualFocus(true);
        restoreProfileButton.setOnAction(event -> restoreProfileAction());

        HBox buttonsBox = new HBox(createProfileButton, restoreProfileButton);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(4);
        buttonsBox.setMaxWidth(230);
        buttonsBox.setMinWidth(230);

        Label recentProfilesLabel = new Label("Recent profiles");
        recentProfilesLabel.setAlignment(Pos.CENTER_LEFT);
        recentProfilesLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "Medium", 14.0f));

        profileButtonsBox = new VBox();
        profileButtonsBox.setStyle("-fx-background-color: #181818; -fx-background-radius: 5px;");
        profileButtonsBox.setSpacing(2);
        ScrollPane profileButtonsScrollPane = new ScrollPane(profileButtonsBox);
        profileButtonsScrollPane.setFitToWidth(true);
        profileButtonsScrollPane.setMaxWidth(230);
        profileButtonsScrollPane.setMinWidth(230);
        profileButtonsScrollPane.setMaxHeight(70);

        VBox recentProfilesBox = new VBox(profileButtonsScrollPane);
        recentProfilesBox.setAlignment(Pos.CENTER);

        // VBox welcomeBox = new VBox();
        // GridPane.setHgrow(welcomeBox, Priority.ALWAYS);
        // GridPane.setVgrow(welcomeBox, Priority.ALWAYS);
        // GridPane.setHalignment(welcomeBox, HPos.CENTER);
        // GridPane.setValignment(welcomeBox, VPos.CENTER);
        // welcomeBox.setAlignment(Pos.CENTER);
        // welcomeBox.setMinSize(400, 240);
        // welcomeBox.setMaxSize(400, 240);
        // welcomeBox.setStyle("-fx-background-color: #131313; -fx-background-radius: 5px;");

        MainFrame.INSTANCE.setSize(400, 240);

        this.setTranslateY(20);
        this.getChildren().addAll(topBox, buttonsBox, recentProfilesBox);
    }

    private void restoreProfileAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Husher Profile", "*.hush"));
        Optional.ofNullable(fileChooser.showOpenDialog(MainFrame.INSTANCE.getJFXLayout().getScene().getWindow())).ifPresent(file -> {
            MainFrame.INSTANCE.showPanel(new UnlockPanel());
        });
    }

    public void addProfile(String username, Path profilePath) {
        LoginProfileBox profileBox = new LoginProfileBox(username, profilePath);
        profileBox.getRemoveButton().setOnMouseClicked(event -> removeProfile(username));
        profileButtonsBox.getChildren().add(profileBox);
        profileBoxes.put(username, profileBox);
    }

    public void removeProfile(String username) {
        LoginProfileBox profileBox = profileBoxes.get(username);
        profileButtonsBox.getChildren().remove(profileBox);
        profileBoxes.remove(username);
    }
}
