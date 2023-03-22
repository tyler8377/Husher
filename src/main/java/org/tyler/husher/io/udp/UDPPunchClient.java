package org.tyler.husher.io.udp;

import org.tyler.husher.io.tcp.TCPClient;
import org.tyler.husher.object.message.SocketInfoMessage;
import org.tyler.husher.util.typo.Pair;

import java.io.Serializable;
import java.net.Socket;

public class UDPPunchClient {

    private final TCPClient<Serializable> client;

    public UDPPunchClient(String serverIp, int serverPort) throws Exception {
        Socket socket = new Socket(serverIp, serverPort);
        this.client = new TCPClient<>(socket);
        System.out.println("Connected to server (client=" + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort() + ")" );
    }

    public Pair<SocketInfoMessage, SocketInfoMessage> waitForPeersInfo(String identifier) throws Exception {
        String localIp = client.getSocket().getInetAddress().getHostAddress();
        int localPort = client.getSocket().getLocalPort();
        SocketInfoMessage localSocketInfo = new SocketInfoMessage(localIp, localPort);

        client.send(identifier);
        System.out.println("Sent identifier to server. Now waiting for peer info.");

        SocketInfoMessage peerInfo = (SocketInfoMessage) client.receive();
        System.out.println("Received peer info: " + peerInfo.toString());
        client.close();
        return new Pair<>(peerInfo, localSocketInfo);
    }

}
