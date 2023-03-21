package org.tyler.husher.object.network;

public class DHTNode {

    private final String ip;
    private final int port;
    private final String publicKey;

    public DHTNode(String ip, int port, String publicKey) {
        this.ip = ip;
        this.port = port;
        this.publicKey = publicKey;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public String toString() {
        return "DHTNode{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }
}
