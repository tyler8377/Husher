package org.tyler.husher.protocol.model;

public class Message {

    private final long id;
    private final long authorId;
    private final long channelId;
    private final String content;

    public Message(long id, long authorId, long channelId, String content) {
        this.id = id;
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getId() {
        return id;
    }

    public long getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }
}
