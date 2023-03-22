package org.tyler.husher.ui.panel;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.tyler.husher.protocol.model.Channel;
import org.tyler.husher.protocol.model.Message;
import org.tyler.husher.protocol.model.User;
import org.tyler.husher.ui.MainFrame;
import org.tyler.husher.ui.UIHelper;
import org.tyler.husher.ui.component.ChannelBox;
import org.tyler.husher.ui.component.ChannelPane;
import org.tyler.husher.ui.component.NetworkIndicatorBox;
import org.tyler.husher.util.ResourceUtils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class HomePanel extends GridPane {

    private HBox centerBoxContainer;
    private TextField inputTextField;
    private ChannelPane focusedChannelPane;

    public HomePanel() {
        GridPane.setHgrow(this, Priority.ALWAYS);
        GridPane.setVgrow(this, Priority.ALWAYS);
        this.setStyle("-fx-background-color: #191919;");

        VBox explorerBox = new VBox();
        GridPane.setHgrow(explorerBox, Priority.ALWAYS);
        GridPane.setVgrow(explorerBox, Priority.ALWAYS);
        GridPane.setMargin(explorerBox, new Insets(4, 4, 4, 4));
        explorerBox.setSpacing(8);
        explorerBox.setAlignment(Pos.TOP_CENTER);

        int size3 = 12;
        JFXButton homeButton = new JFXButton(" ");
        // ImageView homeIconView = new ImageView("https://www.pinclipart.com/picdir/middle/178-1785162_hexagon-clipart-black-and-white.png");
        // ImageView homeIconView = new ImageView("https://www.nicepng.com/png/full/115-1153942_home-logo-png.png");
        ImageView homeIconView = new ImageView();
        homeIconView.setFitWidth(size3);
        homeIconView.setFitHeight(size3);
        homeIconView.setOpacity(0.8f);
        homeIconView.setTranslateX(-2.5);
        homeButton.setGraphic(homeIconView);
        homeButton.setStyle("-fx-font-size: 12px; -fx-background-color: #3d3d3d;");
        size3 = 24;
        homeButton.setMinSize(size3, size3);
        homeButton.setMaxSize(size3, size3);

        String defaultSearchTextFieldStyle = "-fx-font-size: 12px; -fx-text-fill: #c1c1c1; -fx-background-color: #3d3d3d;";
        String hoverSearchTextFieldStyle = "-fx-font-size: 12px; -fx-text-fill: #d8d8d8; -fx-background-color: #4f4f4f;";

        size3 = 22;
        JFXButton searchTextField = new JFXButton("Search");
        HBox.setHgrow(searchTextField, Priority.ALWAYS);
        // searchTextField.setPromptText("Search");
        // searchTextField.setMinHeight(size3);
        // searchTextField.setMaxHeight(size3);
        searchTextField.setMaxWidth(Integer.MAX_VALUE);
        searchTextField.setAlignment(Pos.CENTER_LEFT);
        searchTextField.setStyle(defaultSearchTextFieldStyle);
        ImageView searchIconView = new ImageView(new Image(ResourceUtils.openResource("images/search.png")));
        searchIconView.setOpacity(0.6f);
        searchIconView.setFitWidth(12);
        searchIconView.setTranslateY(-1);
        searchIconView.setFitHeight(searchIconView.getFitWidth());
        searchTextField.setGraphic(searchIconView);
        searchTextField.setGraphicTextGap(4);
        searchTextField.setOnMouseEntered(event -> searchTextField.setStyle(hoverSearchTextFieldStyle));
        searchTextField.setOnMouseExited(event -> searchTextField.setStyle(defaultSearchTextFieldStyle));
        searchTextField.setDisableVisualFocus(true);

        HBox topBox = new HBox(searchTextField);
        // topBox.setStyle("-fx-background-color: red;");
        topBox.setSpacing(4);

        VBox channelsListBox = new VBox();
        GridPane.setHgrow(channelsListBox, Priority.ALWAYS);
        GridPane.setVgrow(channelsListBox, Priority.ALWAYS);
        channelsListBox.setSpacing(4);
        channelsListBox.setPrefHeight(Integer.MAX_VALUE);

        List<HBox> channels = new ArrayList<>();
        channels.add(createChannelBox(new Channel(0, 0, "rez disney", null, null), "#a29bfe"));
        channels.add(createChannelBox(new Channel(0, 0, "rez netflix", null, null), "#ff7675"));
        channels.add(createChannelBox(new Channel(0, 0, "mafé group", null, null), "#74b9ff"));

        HBox createChannelBox = new HBox();
        Label createChannelLabel = new Label("+");
        createChannelLabel.setFont(ResourceUtils.getMonoFont("Regular", 14.0f));
        createChannelBox.setAlignment(Pos.CENTER);
        createChannelBox.setMinHeight(20);
        createChannelBox.setMaxHeight(20);
        createChannelBox.setStyle("-fx-border-color: white; -fx-border-radius: 5px; -fx-border-style: dashed;");
        createChannelBox.getChildren().addAll(createChannelLabel);
        createChannelBox.setOpacity(0.8f);
        createChannelBox.setOnMouseEntered(event -> createChannelBox.setOpacity(0.9f));
        createChannelBox.setOnMouseExited(event -> createChannelBox.setOpacity(0.8f));
        createChannelBox.setOnMouseClicked(event -> {
            JOptionPane.showInputDialog(null, "Give it a name:", "Create channel", JOptionPane.QUESTION_MESSAGE);
        });

        channelsListBox.getChildren().addAll(channels);
        channelsListBox.getChildren().addAll(createChannelBox);

        int size = 32;
        ImageView avatarView = new ImageView(new Image(ResourceUtils.openResource("images/icon.jpg")));
        GridPane.setHgrow(avatarView, Priority.ALWAYS);
        GridPane.setVgrow(avatarView, Priority.ALWAYS);
        avatarView.setFitWidth(size);
        avatarView.setFitHeight(size);
        Rectangle clip = new Rectangle(size, size);
        clip.setArcWidth(45);
        clip.setArcHeight(45);
        avatarView.setClip(clip);
        avatarView.setTranslateX(6);

        double radius = 4;
        Circle statusCircle = new Circle(radius);
        GridPane.setHalignment(statusCircle, HPos.CENTER);
        GridPane.setValignment(statusCircle, VPos.CENTER);
        statusCircle.setStyle("-fx-fill: #27ae60;");

        Circle statusContainerCircle = new Circle(radius + 2);
        GridPane.setHalignment(statusContainerCircle, HPos.CENTER);
        GridPane.setValignment(statusContainerCircle, VPos.CENTER);
        statusContainerCircle.setStyle("-fx-fill: #090909;");

        GridPane statusPane = new GridPane();
        GridPane.setHalignment(statusPane, HPos.RIGHT);
        GridPane.setValignment(statusPane, VPos.BOTTOM);
        // statusPane.setStyle("-fx-background-color: red;");
        statusPane.setMaxSize(statusContainerCircle.getRadius(), statusContainerCircle.getRadius());
        statusPane.setTranslateX(6);
        statusPane.setTranslateY(1);
        statusPane.getChildren().addAll(statusContainerCircle, statusCircle);

        GridPane avatarPane = new GridPane();
        avatarPane.setMinSize(size, size);
        avatarPane.setMaxSize(size, size);
        avatarPane.getChildren().addAll(avatarView, statusPane);

        avatarPane.setOnMouseClicked(event -> {
            boolean visible = statusPane.getOpacity() > 0;
            double fromValue = visible ? 1.0 : 0.0;
            double toValue = visible ? 0.0 : 1.0;
            FadeTransition transition = new FadeTransition(Duration.millis(200), statusPane);
            transition.setFromValue(fromValue);
            transition.setToValue(toValue);
            transition.play();
        });

        Label usernameLabel = new Label("tyler");
        usernameLabel.setFont(ResourceUtils.getFont("Poppins", "Medium", 12.0f));

        Label activityLabel = new Label("Online");
        activityLabel.setOpacity(0.8f);
        activityLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 11.0f));

        HBox statusBox = new HBox(activityLabel);
        // statusBox.setSpacing(6);

        VBox userInfoBox = new VBox(usernameLabel, statusBox);
        userInfoBox.setAlignment(Pos.CENTER_LEFT);

        float defaultOpacity = 0.6f;
        float hoverOpacity = 0.8f;
        String style = "-fx-background-radius: 5px; -fx-background-color: %s;";
        int size1 = 16;

        Label settingsIconButton = new Label();
        ImageView settingsIconView = new ImageView(new Image(ResourceUtils.openResource("images/settings.png")));
        settingsIconView.setFitWidth(size1);
        settingsIconView.setFitHeight(size1);
        settingsIconButton.setGraphic(settingsIconView);
        settingsIconButton.setOpacity(defaultOpacity);
        size1 *= 2;
        settingsIconButton.setMinSize(size1, size1);
        settingsIconButton.setMaxSize(size1, size1);
        settingsIconButton.setTranslateX(-1);
        settingsIconButton.setAlignment(Pos.CENTER);

        settingsIconButton.setOnMouseEntered(event -> {
            settingsIconButton.setOpacity(hoverOpacity);
            settingsIconButton.setStyle(String.format(style, "#212121"));
        });
        settingsIconButton.setOnMouseExited(event -> {
            settingsIconButton.setOpacity(defaultOpacity);
            settingsIconButton.setStyle(String.format(style, "transparent"));
        });

        Region filler = new Region();
        HBox.setHgrow(filler, Priority.ALWAYS);

        HBox toolbarBox = new HBox(settingsIconButton);
        toolbarBox.setAlignment(Pos.CENTER);
        toolbarBox.setTranslateX(-4);
        // toolbarBox.setStyle("-fx-background-color: red;");

        HBox profileContainerBox = new HBox(avatarPane, userInfoBox, filler, toolbarBox);
        HBox.setHgrow(profileContainerBox, Priority.ALWAYS);
        profileContainerBox.setSpacing(14);
        profileContainerBox.setAlignment(Pos.CENTER_LEFT);
        profileContainerBox.setStyle("-fx-background-radius: 5px; -fx-background-color: #181818;");
        profileContainerBox.setOnMouseEntered(event -> {
            // TODO clickable background button
        });

        HBox profileBox = new HBox(profileContainerBox);
        GridPane.setMargin(profileBox, new Insets(3));
        profileBox.setMaxHeight(44);
        profileBox.setMinHeight(44);

        NetworkIndicatorBox networkIndicatorBox = new NetworkIndicatorBox(14, 3, 8, 1, Color.web("#2ecc71"));
        networkIndicatorBox.setTranslateX(6);

        Label networkStatusLabel = new Label("P2P Connections: 2");
        networkStatusLabel.setStyle("-fx-text-fill: #2ecc71;");
        networkStatusLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 12.0f));

        Label networkDetailsLabel = new Label("D 5.12MB  U 2.77MB");
        // networkDetailsLabel.setStyle("-fx-text-fill: #2ecc71;");
        networkDetailsLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 11.0f));
        networkDetailsLabel.setOpacity(0.5f);

        VBox networkInfoVBox = new VBox(networkStatusLabel, networkDetailsLabel);
        networkInfoVBox.setTranslateY(7);
        networkInfoVBox.setSpacing(2);

        HBox networkInfoContainerBox = new HBox(networkIndicatorBox, networkInfoVBox);
        HBox.setHgrow(networkInfoContainerBox, Priority.ALWAYS);
        networkInfoContainerBox.setSpacing(14);
        networkInfoContainerBox.setAlignment(Pos.CENTER_LEFT);
        networkInfoContainerBox.setStyle("-fx-background-radius: 5px; -fx-background-color: #181818;");

        HBox networkInfoBox = new HBox(networkInfoContainerBox);
        GridPane.setMargin(networkInfoBox, new Insets(3));
        networkInfoBox.setMaxHeight(38);
        networkInfoBox.setMinHeight(38);

        VBox bottomLeftBox = new VBox(networkInfoBox, profileBox);
        GridPane.setHgrow(bottomLeftBox, Priority.ALWAYS);
        GridPane.setMargin(bottomLeftBox, new Insets(3));
        bottomLeftBox.setSpacing(3);
        // bottomLeftBox.setAlignment(Pos.BOTTOM_CENTER);
        // bottomLeftBox.setStyle("-fx-background-color: red;");

        explorerBox.getChildren().addAll(topBox, channelsListBox, bottomLeftBox);

        GridPane leftPane = new GridPane();
        GridPane.setVgrow(leftPane, Priority.ALWAYS);
        leftPane.setStyle("-fx-background-color: #121212;");
        leftPane.getChildren().addAll(explorerBox);
        GridPane centerPane = createCenterPane();
        GridPane.setVgrow(centerPane, Priority.ALWAYS);

        widthProperty().addListener((observable, oldValue, newValue) -> {
            int width = newValue.intValue();
            int lWidth = 160;
            leftPane.setMaxWidth(lWidth);
            leftPane.setMinWidth(lWidth);
            centerPane.setMaxWidth(width - lWidth);
            centerPane.setMinWidth(width - lWidth);
            centerPane.setTranslateX(lWidth);
        });

        getChildren().addAll(leftPane, centerPane);
        setFocusedChannelPane(null);

        MainFrame.INSTANCE.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                focusInputField();
            }
        });
    }

    private ChannelBox createChannelBox(Channel channel, String color) {
        GridPane container = new GridPane();
        GridPane.setVgrow(container, Priority.ALWAYS);
        GridPane.setHgrow(container, Priority.ALWAYS);
        container.setPrefWidth(Integer.MAX_VALUE);
        container.setMaxWidth(Integer.MAX_VALUE);

        JFXButton containerButton = new JFXButton();
        GridPane.setVgrow(containerButton, Priority.ALWAYS);
        GridPane.setHgrow(containerButton, Priority.ALWAYS);
        containerButton.setPrefWidth(Integer.MAX_VALUE);
        containerButton.setMaxWidth(Integer.MAX_VALUE);
        containerButton.setMinHeight(40);
        containerButton.setMaxHeight(containerButton.getMinHeight());
        containerButton.setStyle("-fx-background-color: transparent;");

        Label channelLabel = new Label(channel.getName());
        channelLabel.setOpacity(0.9f);
        channelLabel.setFont(ResourceUtils.getFont("Poppins", "Medium", 12.0f));

        Label descriptionLabel = new Label("3 Members");
        descriptionLabel.setOpacity(0.8f);
        descriptionLabel.setFont(ResourceUtils.getFont("UbuntuMono", "Regular", 12.0f));

        Label notificationLabel = new Label("2");
        // channelLabel.setFont(ResourceManager.getMontserratFont("Medium", 12.0f));
        notificationLabel.setStyle("-fx-background-color: rgb(231, 76, 60); -fx-background-radius: 15px;");
        notificationLabel.setMinWidth(15);
        notificationLabel.setMinHeight(15);
        notificationLabel.setAlignment(Pos.CENTER);
        notificationLabel.setVisible(false);

        int size = 32;
        ImageView iconView = new ImageView(new Image(ResourceUtils.openResource("images/icon.jpg")));

        iconView.setFitWidth(size);
        iconView.setFitHeight(size);
        iconView.setFitWidth(size);
        iconView.setFitHeight(size);
        Rectangle clip = new Rectangle(size, size);
        clip.setArcWidth(45);
        clip.setArcHeight(45);
        iconView.setClip(clip);

        Region filler = new Region();
        GridPane.setHgrow(filler, Priority.ALWAYS);
        HBox.setHgrow(filler, Priority.ALWAYS);

        iconView.setMouseTransparent(true);
        channelLabel.setMouseTransparent(true);
        notificationLabel.setMouseTransparent(true);
        // containerButton.setMouseTransparent(true);

        VBox detailsBox = new VBox(channelLabel, descriptionLabel);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        detailsBox.setTranslateX(8);

        HBox infoBox = new HBox(iconView, detailsBox);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setMouseTransparent(true);
        infoBox.setTranslateX(4);

        container.getChildren().addAll(containerButton, infoBox);
        // container.getChildren().addAll(containerButton);

        containerButton.setOnMouseEntered(event -> containerButton.setStyle("-fx-background-color: #212121;"));
        containerButton.setOnMouseExited(event -> containerButton.setStyle("-fx-background-color: transparent;"));

        ChannelPane channelPane = new ChannelPane(channel);
        ChannelBox channelBox = new ChannelBox(channelPane);
        HBox.setHgrow(channelBox, Priority.ALWAYS);
        channelBox.setMaxWidth(Integer.MAX_VALUE);
        channelBox.getChildren().addAll(container);

        // channelBox.setAlignment(Pos.CENTER_LEFT);
        // channelBox.setTextAlignment(TextAlignment.LEFT);
        // channelBox.setStyle("-fx-background-color: " + Utils.hexToRGBA(color, 0.4f) + "; -fx-background-radius: 5px;");
        channelBox.setStyle("-fx-background-color: transparent; -fx-background-radius: 5px;");
        channelBox.setMinHeight(20);
        containerButton.setOnMouseClicked(event -> setFocusedChannelPane(channelPane));
        return channelBox;
    }

    private GridPane createCenterPane() {
        VBox vBox = new VBox();
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        GridPane.setVgrow(vBox, Priority.ALWAYS);
        GridPane.setMargin(vBox, new Insets(4, 4, 4, 4));
        // vBox.setStyle("-fx-border-color: red;");

        HBox topBox = new HBox();
        topBox.setMinHeight(28);
        topBox.setAlignment(Pos.CENTER_LEFT);

        Font textFont = ResourceUtils.getFont("RobotoMono", "Regular", 13.0f);
        Label topTitleLabel = new Label("rez disney");
        topTitleLabel.setFont(textFont);
        topBox.getChildren().addAll(topTitleLabel);

        centerBoxContainer = new HBox();
        VBox.setVgrow(centerBoxContainer, Priority.ALWAYS);

        inputTextField = new TextField();
        HBox.setHgrow(inputTextField, Priority.ALWAYS);
        inputTextField.setStyle("-fx-font-size: 12px; -fx-display-caret: true; -fx-highlight-fill: #7a7a7a; -fx-text-fill: #ecf0f1; -jfx-focus-color: " + UIHelper.COLOR_DARK_RED + "; -fx-background-color: #3d3d3d; -fx-prompt-text-fill: #aaa69d;");
        inputTextField.setPromptText("Message");
        // inputTextField.setMinHeight(24);
        // inputTextField.setMaxHeight(24);

        centerBoxContainer.setOnMouseClicked(event -> centerBoxContainer.requestFocus());
        inputTextField.setOnAction(event -> fireInputTextFieldActionEvent());

        HBox bottomBox = new HBox();
        // bottomBox.setStyle("-fx-border-color: red;");
        bottomBox.getChildren().addAll(inputTextField);

        vBox.getChildren().addAll(centerBoxContainer, bottomBox);

        GridPane pane = new GridPane();
        // pane.setStyle("-fx-background-color: #121212;");
        vBox.setStyle("-fx-background-color: #191919;");
        pane.getChildren().addAll(vBox);
        return pane;
    }

    private void fireInputTextFieldActionEvent() {
        String inputText = inputTextField.getText().trim();
        if (inputText.isBlank())
            return;

        inputTextField.setText("");

        if (focusedChannelPane != null) {
            User authorUser = new User(0, "ted", null);
            Message message = new Message(System.currentTimeMillis(), 0, 0, inputText);
            focusedChannelPane.appendMessage(authorUser, message);
        }
    }

    public void setFocusedChannelPane(ChannelPane channelPane) {
        this.focusedChannelPane = channelPane;
        this.inputTextField.setDisable(channelPane == null);

        if (channelPane != null) {
            HBox.setHgrow(channelPane, Priority.ALWAYS);
            this.centerBoxContainer.getChildren().clear();
            this.centerBoxContainer.getChildren().add(channelPane);
            focusInputField();
        }
    }

    public void focusInputField() {
        inputTextField.requestFocus();
    }
}
