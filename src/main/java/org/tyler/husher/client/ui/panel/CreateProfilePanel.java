package org.tyler.husher.client.ui.panel;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
import org.tyler.husher.core.crypto.xsalsa20poly1305.Keys;
import org.tyler.husher.core.util.HashUtils;
import org.tyler.husher.core.util.ResourceUtils;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class CreateProfilePanel extends VBox {

    private static final int MAX_USERNAME_LENGTH = 16;

    private Label subtitleLabel;
    private Label statusLabel;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    public CreateProfilePanel() {

        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(4);

        Label titleLabel = new Label("Create a profile");
        titleLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "Bold", 24.0f));

        subtitleLabel = new Label("");
        subtitleLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "Medium", 11.0f));
        subtitleLabel.setOpacity(0.8f);

        VBox topBox = new VBox(titleLabel, subtitleLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setTranslateY(-20);

        Font buttonFont = ResourceUtils.getFont("UbuntuMono", "Regular", 13.0f);
        // Font buttonFont = ResourceUtils.getFont("JetBrainsMono", "Regular", 12.0f);

        statusLabel = new Label();
        statusLabel.setAlignment(Pos.CENTER_LEFT);
        statusLabel.setTextAlignment(TextAlignment.LEFT);
        statusLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 14.0f));
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusLabel.setTranslateY(-3);

        usernameField = new TextField();
        HBox.setHgrow(usernameField, Priority.ALWAYS);
        usernameField.setStyle("-fx-font-size: 12px; -fx-display-caret: true; -fx-highlight-fill: #7a7a7a; -fx-text-fill: #ecf0f1; -fx-background-color: #3d3d3d; -fx-prompt-text-fill: #aaa69d;");
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(230);
        usernameField.textProperty().addListener((observableValue, s, t1) -> statusLabel.setText(""));

        passwordField = new PasswordField();
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        passwordField.setStyle("-fx-font-size: 12px; -fx-display-caret: true; -fx-highlight-fill: #7a7a7a; -fx-text-fill: #ecf0f1; -fx-background-color: #3d3d3d; -fx-prompt-text-fill: #aaa69d;");
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(230);
        passwordField.textProperty().addListener((observableValue, s, t1) -> statusLabel.setText(""));

        confirmPasswordField = new PasswordField();
        HBox.setHgrow(confirmPasswordField, Priority.ALWAYS);
        confirmPasswordField.setStyle("-fx-font-size: 12px; -fx-display-caret: true; -fx-highlight-fill: #7a7a7a; -fx-text-fill: #ecf0f1; -fx-background-color: #3d3d3d; -fx-prompt-text-fill: #aaa69d;");
        confirmPasswordField.setPromptText("Confirm password");
        confirmPasswordField.setMaxWidth(230);
        confirmPasswordField.textProperty().addListener((observableValue, s, t1) -> statusLabel.setText(""));

        JFXButton createButton = new JFXButton("Create");
        createButton.setStyle("-fx-text-fill: #fff; -fx-background-color: #c0392b; -fx-background-radius: 5px;");
        createButton.setFont(buttonFont);
        createButton.setDisableVisualFocus(true);
        createButton.setMinWidth(passwordField.getMaxWidth());
        createButton.setOnAction(actionEvent -> createProfileAction());

        JFXButton closeButton = new JFXButton("Close");
        closeButton.setStyle("-fx-text-fill: #fff; -fx-background-color: #c0392b; -fx-background-radius: 5px;");
        closeButton.setFont(buttonFont);
        closeButton.setDisableVisualFocus(true);
        closeButton.setMinWidth(passwordField.getMaxWidth());

        VBox inputFieldsBox = new VBox(usernameField, passwordField, confirmPasswordField);
        inputFieldsBox.setAlignment(Pos.CENTER);
        inputFieldsBox.setSpacing(2);

        VBox buttonsBox = new VBox(createButton);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(2);
        buttonsBox.setMaxWidth(230);
        buttonsBox.setMinWidth(230);

        VBox centerBox = new VBox(statusLabel, inputFieldsBox, buttonsBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setMaxWidth(300);
        centerBox.setMinWidth(300);
        centerBox.setSpacing(2);
        centerBox.setTranslateY(-20);

        setOnAction(usernameField, () -> passwordField.requestFocus());
        setOnAction(passwordField, () -> confirmPasswordField.requestFocus());
        setOnAction(confirmPasswordField, this::createProfileAction);

        MainFrame.INSTANCE.setSize(400, 250); // TODO window size manager (avoid replacing user's window size)
        this.setTranslateY(14);
        this.getChildren().addAll(topBox, centerBox);
    }

    private void setOnAction(TextField textField, Runnable runnable) {
        textField.setOnAction(actionEvent -> {
            if (!textField.getText().isEmpty())
                runnable.run();
        });
    }

    private void createProfileAction() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String passwordConfirm = confirmPasswordField.getText();

            if (username.isBlank())
                throw new IllegalArgumentException("You must specify a username");

            if (password.isEmpty())
                throw new IllegalArgumentException("You must specify a password");

            if (passwordConfirm.isEmpty())
                throw new IllegalArgumentException("You must confirm your password");

            if (username.length() < 2 || username.length() > MAX_USERNAME_LENGTH)
                throw new IllegalArgumentException("Username must be between 2 and " + MAX_USERNAME_LENGTH + " characters");

            // if (matches)
            //     throw new IllegalArgumentException("Username contains illegal character(s)");

            if (!password.equals(passwordConfirm))
                throw new IllegalArgumentException("Passwords do not match");

            // ========================= GENERATE SEED FROM MNEMONICS =========================
            // StringBuilder sb = new StringBuilder();
            // byte[] entropy = new byte[Words.TWELVE.byteLength()];
            // new SecureRandom().nextBytes(entropy);
            // new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, sb::append);
            // String words = sb.toString();
            // System.out.println("Recovery phrase: " + words);
            // byte[] seed = new SeedCalculator(JavaxPBKDF2WithHmacSHA512.INSTANCE)
            //         .withWordsFromWordList(English.INSTANCE)
            //         .calculateSeed(List.of(words.split(" ")), "");
            // byte[] privateKey = HashUtils.sha256(seed);
            // ========================= GENERATE SEED FROM MNEMONICS =========================

            Path profilePath = Husher.getWorkingDir().resolve(username + ".hush");
            Profile profile = new Profile(username, profilePath);
            char[] hashedPassword = new String(HashUtils.sha256(password.getBytes(StandardCharsets.UTF_8))).toCharArray();
            UserData userData = new UserData(Keys.generatePrivateKey(), username, profilePath.toAbsolutePath().toString(), hashedPassword); // mnemonics restore system not implemented
            userData.save();
            Husher.setUserData(userData);
            Husher.getUserConfig().addProfile(profile);
            Husher.getUserConfig().setLastAccessedProfile(profile);
            MainFrame.INSTANCE.showPanel(new PostCreateProfilePanel());
        } catch (IllegalArgumentException e) {
            JOptionPane.showConfirmDialog(MainFrame.INSTANCE, e.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            statusLabel.setText(e.getMessage());
        }
    }
}
