package org.tyler.husher.client.ui.component;

import com.jfoenix.controls.JFXTextArea;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.tyler.husher.core.network.model.Message;
import org.tyler.husher.core.network.model.User;
import org.tyler.husher.core.util.ResourceUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MessageBox extends HBox {

    private static final Font usernameFont = ResourceUtils.getFont("UbuntuMono", "Regular", 13.0f);
    private static final Font dateFont = ResourceUtils.getFont("UbuntuMono", "Regular", 12.0f);
    private static final Font messageFont = ResourceUtils.getFont("UbuntuMono", "Regular", 13.0f);

    private final User authorUser;
    private final VBox messagesVBox;
    private final Label dateLabel;

    public MessageBox(User authorUser) {
        this.authorUser = authorUser;

        int avatarViewSize = 32;
        ImageView avatarView = new ImageView(new Image(ResourceUtils.openResource("images/icon.png")));
        avatarView.setFitWidth(avatarViewSize);
        avatarView.setFitHeight(avatarViewSize);
        Rectangle clip = new Rectangle(avatarView.getFitWidth(), avatarView.getFitHeight());
        clip.setArcWidth(45);
        clip.setArcHeight(45);
        avatarView.setClip(clip);

        Label usernameLabel = new Label(authorUser.getUsername());
        usernameLabel.setFont(usernameFont);

        dateLabel = new Label();
        dateLabel.setFont(dateFont);
        dateLabel.setOpacity(0.4f);

        HBox topBox = new HBox(usernameLabel, dateLabel);
        topBox.setSpacing(8);
        messagesVBox = new VBox();

        VBox centerContainerBox = new VBox(topBox, messagesVBox);
        HBox.setHgrow(centerContainerBox, Priority.ALWAYS);

        this.setSpacing(8);
        this.getChildren().addAll(avatarView, centerContainerBox);
    }

    private JFXTextArea createMessageLabel(Message message) {
        JFXTextArea textArea = new JFXTextArea(message.getContent());
        // GridPane.setHgrow(textArea, Priority.ALWAYS);
        textArea.setFont(messageFont);
        textArea.setOpacity(0.9f);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setStyle("-fx-text-fill: white; -fx-padding: 0px; -jfx-focus-color: transparent;");
        textArea.setMinHeight(20);
        textArea.setPrefHeight(20);
        textArea.skinProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Skin<?>> ov, Skin<?> t, Skin<?> t1) {
                if (t1 != null && t1.getNode() instanceof Region) {
                    Region r = (Region) t1.getNode();
                    r.setBackground(Background.EMPTY);
                    r.getChildrenUnmodifiable().stream().
                            filter(n -> n instanceof Region).
                            map(n -> (Region) n).
                            forEach(n -> n.setBackground(Background.EMPTY));
                    r.getChildrenUnmodifiable().stream().
                            filter(n -> n instanceof Control).
                            map(n -> (Control) n).
                            forEach(c -> c.skinProperty().addListener(this));
                }
            }
        });
        return textArea;
    }

    public void appendMessage(Message message) {
        if (messagesVBox.getChildren().isEmpty()) {
            String systemLocale = System.getProperty("user.language");
            Locale locale = new Locale(systemLocale);
            String dateStr = DateFormat.getDateInstance(DateFormat.DATE_FIELD, locale).format(new Date());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            sdf.setTimeZone(TimeZone.getDefault());
            Date date = new Date(message.getId());
            dateStr += " " + sdf.format(date);
            dateLabel.setText(dateStr);
        }

        messagesVBox.getChildren().add(createMessageLabel(message));
    }

    public User getAuthorUser() {
        return authorUser;
    }
}
