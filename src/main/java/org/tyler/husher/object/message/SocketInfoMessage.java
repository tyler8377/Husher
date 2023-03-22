package org.tyler.husher.object.message;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class SocketInfoMessage implements Serializable {

    private final String ip;
    private final int port;

    public SocketInfoMessage(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public InetSocketAddress toSocketAddress() {
        return new InetSocketAddress(ip, port);
    }

    @Override
    public String toString() {
        return "SocketInfoMessage{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
