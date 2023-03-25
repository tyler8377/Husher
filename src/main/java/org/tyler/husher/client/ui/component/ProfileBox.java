package org.tyler.husher.client.ui.component;

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
import org.tyler.husher.client.user.Profile;
import org.tyler.husher.client.ui.MainFrame;
import org.tyler.husher.client.ui.panel.UnlockProfilePanel;
import org.tyler.husher.core.util.ResourceUtils;

public class ProfileBox extends GridPane {

    private final Profile profile;
    private Label removeButton;

    public ProfileBox(Profile profile) {
        this.profile = profile;
        this.init();
    }

    private void init() {
        ImageView avatarView = new ImageView(new Image(ResourceUtils.openResource("images/icon.png")));
        avatarView.setFitWidth(24);
        avatarView.setFitHeight(avatarView.getFitWidth());
        Rectangle clip = new Rectangle(avatarView.getFitWidth(), avatarView.getFitWidth());
        clip.setArcWidth(45);
        clip.setArcHeight(45);
        avatarView.setClip(clip);

        Label usernameLabel = new Label(profile.getUsername());
        usernameLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "SemiBold", 12.0f));
        // usernameLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 14.0f));
        usernameLabel.setOpacity(0.9f);
        usernameLabel.setTranslateY(3);
        usernameLabel.setMaxWidth(150);

        Label profilePathLabel = new Label(profile.getPath());
        profilePathLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 12.0f));
        profilePathLabel.setOpacity(0.7f);

        JFXButton backgroundButton = new JFXButton();
        GridPane.setHgrow(backgroundButton, Priority.ALWAYS);
        GridPane.setVgrow(backgroundButton, Priority.ALWAYS);
        backgroundButton.setMaxWidth(Integer.MAX_VALUE);
        backgroundButton.setMaxHeight(Integer.MAX_VALUE);
        backgroundButton.setDisableVisualFocus(true);
        backgroundButton.setStyle("-fx-background-color: #181818; -fx-background-radius: 5px; -fx-background-color: linear-gradient(to left, #181818, #242424)");

        UnlockProfilePanel unlockProfilePanel = new UnlockProfilePanel();
        unlockProfilePanel.setSelectedProfile(profile);
        backgroundButton.setOnAction(event -> MainFrame.INSTANCE.showPanel(unlockProfilePanel));

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

    public Profile getProfile() {
        return profile;
    }

    public Label getRemoveButton() {
        return removeButton;
    }
}
