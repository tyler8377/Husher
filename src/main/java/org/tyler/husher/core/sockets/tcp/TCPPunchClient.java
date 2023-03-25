package org.tyler.husher.core.sockets.tcp;

import org.tyler.husher.core.crypto.xsalsa20poly1305.Keys;
import org.tyler.husher.core.network.message.SocketInfoMessage;
import org.tyler.husher.core.node.RelayNode;
import org.tyler.husher.core.util.HexUtils;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPPunchClient {

    private final TCPClient<Serializable> client;
    private final byte[] destPublicKey;
    private final byte[] ownPrivateKey;
    private final byte[] ownPublicKey;

    public TCPPunchClient(RelayNode node) throws Exception {
        Socket socket = new Socket(node.getIp(), node.getPort());
        this.destPublicKey = HexUtils.hexStringToBytes(node.getPublicKey());
        this.ownPrivateKey = Keys.generatePrivateKey();
        this.ownPublicKey = Keys.generatePublicKey(ownPrivateKey);
        this.client = new TCPClient<>(socket);
        System.out.println("Connected to server (client=" + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort() + ")" );
        System.out.println("Sending public key to server... pub=" + HexUtils.bytesToHex(ownPublicKey));
        client.send(ownPublicKey);
        System.out.println("Public key sent!");
    }

    public Socket waitForPeer(String identifier) throws Exception {
        String localIp = client.getSocket().getInetAddress().getHostAddress();
        int localPort = client.getSocket().getLocalPort();

        client.sendEncrypted(identifier, destPublicKey, ownPrivateKey);
        System.out.println("Sent identifier to server. Now waiting for peer info.");

        SocketInfoMessage peerInfo = (SocketInfoMessage) client.receiveEncrypted(ownPrivateKey, destPublicKey);
        System.out.println("Received peer info: " + peerInfo.toString());

        client.close();
        Socket peerSo = new Socket();
        peerSo.setReuseAddress(true);
        peerSo.bind(new InetSocketAddress(localIp, localPort));
        peerSo.connect(new InetSocketAddress(peerInfo.getIp(), peerInfo.getPort()));
        return peerSo;
    }

}
