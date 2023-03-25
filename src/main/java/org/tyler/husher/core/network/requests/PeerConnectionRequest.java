package org.tyler.husher.core.network.requests;

public class PeerConnectionRequest extends Request {

    private final String ip;
    private final int port;

    public PeerConnectionRequest(String username, String ip, int port) {
        super(username);
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
