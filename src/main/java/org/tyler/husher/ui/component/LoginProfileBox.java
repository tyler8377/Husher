package org.tyler.husher.ui.component;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import org.tyler.husher.ui.MainFrame;
import org.tyler.husher.ui.panel.UnlockPanel;
import org.tyler.husher.util.ResourceUtils;

import java.nio.file.Path;

public class LoginProfileBox extends GridPane {

    private final String username;
    private final Path profilePath;
    private Label removeButton;

    public LoginProfileBox(String username, Path profilePath) {
        this.username = username;
        this.profilePath = profilePath;
        this.init();
    }

    private void init() {
        ImageView avatarView = new ImageView(new Image(ResourceUtils.openResource("images/icon.jpg")));
        avatarView.setFitWidth(24);
        avatarView.setFitHeight(avatarView.getFitWidth());
        Rectangle clip = new Rectangle(avatarView.getFitWidth(), avatarView.getFitWidth());
        clip.setArcWidth(45);
        clip.setArcHeight(45);
        avatarView.setClip(clip);

        Label usernameLabel = new Label(username);
        usernameLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "SemiBold", 12.0f));
        // usernameLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 14.0f));
        usernameLabel.setOpacity(0.9f);
        usernameLabel.setTranslateY(3);

        Label profilePathLabel = new Label(profilePath.toAbsolutePath().toString());
        profilePathLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 12.0f));
        profilePathLabel.setOpacity(0.7f);

        JFXButton backgroundButton = new JFXButton();
        GridPane.setHgrow(backgroundButton, Priority.ALWAYS);
        GridPane.setVgrow(backgroundButton, Priority.ALWAYS);
        backgroundButton.setMaxWidth(Integer.MAX_VALUE);
        backgroundButton.setMaxHeight(Integer.MAX_VALUE);
        backgroundButton.setDisableVisualFocus(true);
        backgroundButton.setStyle("-fx-background-color: #181818; -fx-background-radius: 5px; -fx-background-color: linear-gradient(to left, #181818, #242424)");

        UnlockPanel unlockPanel = new UnlockPanel();
        unlockPanel.setSelectedProfile(username, profilePath);
        backgroundButton.setOnAction(event -> MainFrame.INSTANCE.showPanel(unlockPanel));

        Region filler = new Region();
        HBox.setHgrow(filler, Priority.ALWAYS);

        float defaultOpacity = 0.6f;
        float hoverOpacity = 1.0f;
        String style = "-fx-background-radius: 5px; -fx-background-color: %s;";
        int size = 20;

        removeButton = new Label();
        GridPane.setHalignment(removeButton, HPos.RIGHT);
        ImageView removeIconView = new ImageView(new Image(ResourceUtils.openResource("images/close.png")));
        removeIconView.setFitWidth(8);
        removeIconView.setFitHeight(removeIconView.getFitWidth());
        removeIconView.setOpacity(defaultOpacity);
        removeButton.setAlignment(Pos.CENTER);
        removeButton.setGraphic(removeIconView);
        removeButton.setMinSize(size, size);
        removeButton.setMaxSize(size, size);
        removeButton.setTranslateX(-6);
        removeButton.setOnMouseEntered(event -> {
            removeButton.setOpacity(hoverOpacity);
            removeButton.setStyle(String.format(style, "#262626"));
        });
        removeButton.setOnMouseExited(event -> {
            removeButton.setOpacity(defaultOpacity);
            removeButton.setStyle(String.format(style, "transparent"));
        });

        HBox containerBox = new HBox(avatarView, usernameLabel, filler);
        GridPane.setMargin(containerBox, new Insets(4));
        containerBox.setSpacing(7);
        // containerBox.setTranslateX(2);
        containerBox.setMouseTransparent(true);

        GridPane.setHgrow(this, Priority.ALWAYS);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(backgroundButton, containerBox, removeButton);
    }

    public String getUsername() {
        return username;
    }

    public Path getProfilePath() {
        return profilePath;
    }

    public Label getRemoveButton() {
        return removeButton;
    }
}
