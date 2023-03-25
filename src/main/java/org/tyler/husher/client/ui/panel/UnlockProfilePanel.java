package org.tyler.husher.client.ui.panel;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.tyler.husher.client.Husher;
import org.tyler.husher.client.ui.MainFrame;
import org.tyler.husher.client.user.Profile;
import org.tyler.husher.client.user.UserData;
import org.tyler.husher.core.util.ResourceUtils;

import javax.swing.*;
import java.util.Objects;

public class UnlockProfilePanel extends VBox {

    private Label subtitleLabel;
    private Label statusLabel;
    private PasswordField passwordField;
    private Profile profile;

    public UnlockProfilePanel() {
        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(4);

        Label titleLabel = new Label("Unlock your profile");
        titleLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "Bold", 24.0f));

        subtitleLabel = new Label("");
        subtitleLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "Medium", 12.0f));
        subtitleLabel.setOpacity(0.8f);

        VBox topBox = new VBox(titleLabel, subtitleLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setTranslateY(-30);

        Font buttonFont = ResourceUtils.getFont("UbuntuMono", "Regular", 13.0f);
        // Font buttonFont = ResourceUtils.getFont("JetBrainsMono", "Regular", 12.0f);

        statusLabel = new Label();
        statusLabel.setAlignment(Pos.CENTER_LEFT);
        statusLabel.setTextAlignment(TextAlignment.LEFT);
        statusLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 14.0f));
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusLabel.setTranslateY(-3);

        passwordField = new PasswordField();
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        passwordField.setStyle("-fx-font-size: 12px; -fx-display-caret: true; -fx-highlight-fill: #7a7a7a; -fx-text-fill: #ecf0f1; -fx-background-color: #3d3d3d; -fx-prompt-text-fill: #aaa69d;");
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(230);
        passwordField.setOnAction(actionEvent -> passwordValidateAction());
        passwordField.textProperty().addListener((observableValue, s, t1) -> statusLabel.setText(""));

        JFXButton unlockButton = new JFXButton("Unlock");
        unlockButton.setStyle("-fx-text-fill: #fff; -fx-background-color: #c0392b; -fx-background-radius: 5px;");
        unlockButton.setFont(buttonFont);
        unlockButton.setDisableVisualFocus(true);
        unlockButton.setMinWidth(passwordField.getMaxWidth());
        unlockButton.setOnAction(actionEvent -> passwordValidateAction());

        JFXButton closeButton = new JFXButton("Close");
        closeButton.setStyle("-fx-text-fill: #fff; -fx-background-color: #c0392b; -fx-background-radius: 5px;");
        closeButton.setFont(buttonFont);
        closeButton.setDisableVisualFocus(true);
        closeButton.setMinWidth(passwordField.getMaxWidth());

        VBox buttonsBox = new VBox(unlockButton, closeButton);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(2);
        buttonsBox.setMaxWidth(230);
        buttonsBox.setMinWidth(230);

        VBox centerBox = new VBox(statusLabel, passwordField, buttonsBox);
        centerBox.setAlignment(Pos.CENTER_LEFT);
        centerBox.setMaxWidth(230);
        centerBox.setMinWidth(230);

        MainFrame.INSTANCE.setSize(400, 240);
        this.setTranslateY(14);
        this.getChildren().addAll(topBox, centerBox);
    }

    private void passwordValidateAction() {
        Objects.requireNonNull(profile, "Profile must not be null");

        try {
            UserData userData = Husher.unlockProfile(profile, passwordField.getText());
            Husher.setUserData(userData);
            Husher.getUserConfig().setLastAccessedProfile(profile);
            MainFrame.INSTANCE.showPanel(new HomePanel());
        } catch (IllegalArgumentException e) {
            JOptionPane.showConfirmDialog(MainFrame.INSTANCE, e, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText(e.getMessage());
        }
    }

    public void setSelectedProfile(Profile profile) {
        this.profile = profile;
        subtitleLabel.setText(profile.getPath());
    }
}
