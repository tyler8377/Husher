package org.tyler.husher.client.user;

import org.tyler.husher.core.sockets.tcp.TCPClient;
import org.tyler.husher.core.network.requests.Request;
import org.tyler.husher.core.node.RelayNode;
import org.tyler.husher.core.node.RelayNodeProvider;
import org.tyler.husher.core.crypto.xsalsa20poly1305.Keys;
import org.tyler.husher.core.util.HexUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.Proxy;
import java.net.Socket;

public class UserClient {

    private final UserData userData;
    private Proxy proxy;

    public UserClient(UserData userData) {
        this.userData = userData;
    }

    public TCPClient<Serializable> connect(RelayNode node) throws IOException {
        Socket socket = proxy != null ? new Socket(proxy) : new Socket();
        TCPClient<Serializable> client = new TCPClient<>(socket);
        return client;
    }

    public void sendRequest(Request request) throws Exception {
        RelayNode node = new RelayNodeProvider().getOfflineNodeList().stream().findAny().orElseThrow();
        TCPClient<Serializable> client = connect(node);
        byte[] ownPrivateKey = Keys.generatePrivateKey();
        byte[] ownPublicKey = Keys.generatePublicKey(ownPrivateKey);
        byte[] destPublicKey = HexUtils.hexStringToBytes(node.getPublicKey());

        // PeerConnectionRequest connectionRequest = client.sendEncrypted(request, ownPrivateKey, destPublicKey);


    }

    public UserData getUserData() {
        return userData;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public Proxy getProxy() {
        return proxy;
    }
}
