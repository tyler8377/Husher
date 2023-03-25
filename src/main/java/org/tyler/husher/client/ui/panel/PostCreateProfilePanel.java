package org.tyler.husher.client.ui.panel;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.tyler.husher.client.ui.MainFrame;
import org.tyler.husher.core.util.ResourceUtils;

public class PostCreateProfilePanel extends VBox {

    public PostCreateProfilePanel() {
        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(4);

        Label titleLabel = new Label("Profile created");
        titleLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "Bold", 24.0f));

        // subtitleLabel = new Label("Note: You will NOT be able to recover your data in case of password/file loss.");
        // subtitleLabel.setFont(ResourceUtils.getFont("JetBrainsMono", "Medium", 11.0f));
        // subtitleLabel.setOpacity(0.8f);
        // subtitleLabel.setWrapText(true);
        // subtitleLabel.setMaxWidth(320);
        // subtitleLabel.setTextAlignment(TextAlignment.CENTER);

        VBox topBox = new VBox(titleLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setTranslateY(-30);

        Font buttonFont = ResourceUtils.getFont("UbuntuMono", "Regular", 13.0f);

        String text =
                "• You will NOT be able to recover your data if you lose your password or your profile's (.hush) file.\n" +
                "• No data is synchronized with your profile across the network. Your messages are exchanged between peers and stored only on YOUR computer.\n";

        int width = 320;

        Label importantLabel = new Label("Important: ");
        importantLabel.setAlignment(Pos.CENTER_LEFT);
        importantLabel.setTextAlignment(TextAlignment.LEFT);
        importantLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Bold", 14.0f));
        importantLabel.setWrapText(true);
        importantLabel.setMaxWidth(width);

        Label noteLabel = new Label(text);
        noteLabel.setAlignment(Pos.CENTER_LEFT);
        noteLabel.setTextAlignment(TextAlignment.LEFT);
        noteLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 13.0f));
        noteLabel.setWrapText(true);
        noteLabel.setMaxWidth(width);

        TextFlow textFlow = new TextFlow(importantLabel, noteLabel);
        textFlow.setTranslateY(20);

        JFXButton continueButton = new JFXButton("Continue");
        continueButton.setStyle("-fx-text-fill: #fff; -fx-background-color: #c0392b; -fx-background-radius: 5px;");
        continueButton.setFont(buttonFont);
        continueButton.setDisableVisualFocus(true);
        continueButton.setMinWidth(100);
        continueButton.setOnAction(actionEvent -> continueAction());

        VBox buttonsBox = new VBox(continueButton);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(2);
        buttonsBox.setMaxWidth(230);
        buttonsBox.setMinWidth(230);
        buttonsBox.setTranslateY(30);

        VBox centerBox = new VBox(textFlow, buttonsBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setMaxWidth(300);
        centerBox.setMinWidth(300);
        centerBox.setSpacing(2);
        centerBox.setTranslateY(-40);

        MainFrame.INSTANCE.setSize(400, 250); // TODO window size manager (avoid replacing user's window size)
        this.setTranslateY(14);
        this.getChildren().addAll(topBox, centerBox);
    }

    private void continueAction() {
        MainFrame.INSTANCE.showPanel(new HomePanel());
    }
}
