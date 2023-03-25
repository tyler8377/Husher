package org.tyler.husher.core.network.requests;

public class FriendRequest extends Request {

    private final String destId;

    public FriendRequest(String username, String destId) {
        super(username);
        this.destId = destId;
    }

    public String getDestId() {
        return destId;
    }
}
