package org.tyler.husher.ui.component;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.tyler.husher.protocol.model.Channel;
import org.tyler.husher.protocol.model.Message;
import org.tyler.husher.protocol.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChannelPane extends VBox {

    private Channel channel;
    private List<MessageBox> messageBoxes;

    public ChannelPane(Channel channel) {
        this.channel = channel;
        this.messageBoxes = new ArrayList<>();
    }

    private void appendMessageBox(MessageBox messageBox) {
        HBox.setHgrow(messageBox, Priority.ALWAYS);
        messageBoxes.add(messageBox);
        this.getChildren().add(messageBox);
    }

    public synchronized void appendMessage(User authorUser, Message message) {
        MessageBox lastMessageBox = !messageBoxes.isEmpty() ? messageBoxes.get(messageBoxes.size() - 1) : null;
        MessageBox messageBox = null;

        if (lastMessageBox != null) {
            System.out.println(lastMessageBox.getAuthorUser() == authorUser);
            System.out.println(lastMessageBox.getAuthorUser());
            System.out.println(authorUser);
        }

        if (lastMessageBox != null && Objects.equals(lastMessageBox.getAuthorUser(), authorUser)) {
            System.out.println("bah oe");
            messageBox = lastMessageBox;
        }

        if (messageBox == null)
            messageBox = new MessageBox(authorUser);

        messageBox.appendMessage(message);

        if (messageBox != lastMessageBox)
            appendMessageBox(messageBox);
    }

}
