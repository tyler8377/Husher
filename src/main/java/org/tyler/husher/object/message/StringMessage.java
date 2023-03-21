package org.tyler.husher.object.message;

public class StringMessage extends NetworkMessage {

    private final String message;

    public StringMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
