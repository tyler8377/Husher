package org.tyler.husher.ui.panel;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import org.tyler.husher.ui.ResourceManager;
import org.tyler.husher.ui.UIHelper;

public class ConnectPanel extends GridPane {

    private static final Font labelFont = ResourceManager.getMontserratFont("Medium", 15.0f);
    private static final Font buttonFont = ResourceManager.getMontserratFont("Medium", 13.0f);

    public ConnectPanel() {
        this.setStyle("-fx-background-color: black;");



        getChildren().addAll();
    }

    private GridPane createTokensLayout() {
        Label tokensLabel = new Label("Tokens: ");
        GridPane.setHgrow(tokensLabel, Priority.ALWAYS);
        GridPane.setVgrow(tokensLabel, Priority.ALWAYS);
        GridPane.setHalignment(tokensLabel, HPos.LEFT);
        GridPane.setValignment(tokensLabel, VPos.CENTER);
        tokensLabel.setFont(labelFont);

        JFXButton loadTokensButton = UIHelper.createStyledButton();
        GridPane.setHgrow(loadTokensButton, Priority.ALWAYS);
        GridPane.setVgrow(loadTokensButton, Priority.ALWAYS);
        GridPane.setHalignment(loadTokensButton, HPos.RIGHT);
        GridPane.setValignment(loadTokensButton, VPos.CENTER);
        loadTokensButton.setText("Load tokens");
        loadTokensButton.setFont(buttonFont);
        // loadTokensButton.addEventHandler(MouseEvent.MOUSE_CLICKED, handler.loadTokensHandler(tokensLabel, loadTokensButton));

        GridPane layout = new GridPane();
        layout.getChildren().addAll(tokensLabel, loadTokensButton);
        return layout;
    }

    private GridPane createProxiesLayout() {
        Label proxiesLabel = new Label("Proxies: ");
        GridPane.setHgrow(proxiesLabel, Priority.ALWAYS);
        GridPane.setVgrow(proxiesLabel, Priority.ALWAYS);
        GridPane.setHalignment(proxiesLabel, HPos.LEFT);
        GridPane.setValignment(proxiesLabel, VPos.CENTER);
        proxiesLabel.setFont(labelFont);

        JFXButton loadProxiesButton = UIHelper.createStyledButton();
        GridPane.setHgrow(loadProxiesButton, Priority.ALWAYS);
        GridPane.setVgrow(loadProxiesButton, Priority.ALWAYS);
        GridPane.setHalignment(loadProxiesButton, HPos.RIGHT);
        GridPane.setValignment(loadProxiesButton, VPos.CENTER);
        loadProxiesButton.setText("Load proxies");
        loadProxiesButton.setFont(buttonFont);

        GridPane layout = new GridPane();
        layout.getChildren().addAll(proxiesLabel, loadProxiesButton);
        return layout;
    }
}
