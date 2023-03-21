package org.tyler.husher.ui.component;

import javafx.scene.layout.HBox;

public class ChannelBox extends HBox {

    private final ChannelPane channelPane;

    public ChannelBox(ChannelPane channelPane) {
        this.channelPane = channelPane;
    }

    public ChannelPane getChannelPane() {
        return channelPane;
    }
}
