package org.tyler.husher.protocol.model;

public class Channel {

    private final long id;
    private final long ownerId;
    private final String name;
    private final long[] members;
    private final long[] messages;

    public Channel(long id, long ownerId, String name, long[] members, long[] messages) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.members = members;
        this.messages = messages;
    }

    public long getId() {
        return id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public long[] getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public long[] getMessages() {
        return messages;
    }
}
