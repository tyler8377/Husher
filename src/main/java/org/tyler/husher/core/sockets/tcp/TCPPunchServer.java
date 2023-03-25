package org.tyler.husher.core.sockets.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tyler.husher.core.network.exception.PeerTimeoutException;
import org.tyler.husher.core.network.message.SocketInfoMessage;
import org.tyler.husher.core.util.HexUtils;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPPunchServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TCPPunchServer.class);

    private final InetAddress address;
    private final int port;
    private final Duration clientTimeout;
    private final Map<String, List<TCPClient<Serializable>>> queue;
    private final Map<TCPClient<Serializable>, byte[]> publicKeyMap;
    private final ExecutorService queueExecutor;
    private final byte[] ownPrivateKey;

    public TCPPunchServer(InetAddress address, int port, Duration clientTimeout, byte[] ownPrivateKey) {
        this.address = address;
        this.port = port;
        this.clientTimeout = clientTimeout;
        this.queue = new HashMap<>();
        this.publicKeyMap = new HashMap<>();
        this.queueExecutor = Executors.newCachedThreadPool();
        this.ownPrivateKey = ownPrivateKey;
    }

    private synchronized void handleClient(TCPClient<Serializable> client) throws Exception {
        LOGGER.info("Client has connected: {}:{}", client.getSocket().getInetAddress().getHostAddress(), client.getSocket().getPort());

        try {
            byte[] clientPublicKey = (byte[]) client.receive();
            publicKeyMap.put(client, clientPublicKey);

            System.out.println("Received client public key " + HexUtils.bytesToHex(clientPublicKey));
            // String identifier = (String) ((EncryptedObject) client.receive()).decrypt(ownPrivateKey, clientPublicKey);
            String identifier = (String) client.receiveEncrypted(ownPrivateKey, clientPublicKey);

            System.out.println("identifier = " + identifier);

            if (!queue.containsKey(identifier))
                queue.put(identifier, new ArrayList<>(List.of(client)));
            else
                queue.get(identifier).add(client);

            queueExecutor.execute(() -> {
                try {
                    long millis = clientTimeout.toMillis();;
                    Thread.sleep(millis);

                    if (!client.getSocket().isClosed())
                        client.send(new PeerTimeoutException(millis));
                } catch (Exception e) {
                    LOGGER.error("Error on timeout thread", e);
                }
            });

            TCPClient<?>[] localQueue = new TCPClient<?>[2];

            queue.forEach((key, value) -> {
                if (value.size() >= 2) {
                    localQueue[0] = value.get(0);
                    localQueue[1] = value.get(1);
                }
            });

            TCPClient<Serializable> client1 = (TCPClient<Serializable>) localQueue[0];
            TCPClient<Serializable> client2 = (TCPClient<Serializable>) localQueue[1];

            if (client1 != null && client2 != null) {
                queue.remove(identifier);

                SocketInfoMessage client1Info = new SocketInfoMessage(client1.getSocket().getInetAddress().getHostAddress(), client1.getSocket().getPort());
                SocketInfoMessage client2Info = new SocketInfoMessage(client2.getSocket().getInetAddress().getHostAddress(), client2.getSocket().getPort());

                LOGGER.info("Exchanging socket info between {} and {}", client1Info.getIp(), client2Info.getIp());

                client1.sendEncrypted(client2Info, publicKeyMap.get(client1), ownPrivateKey);
                client2.sendEncrypted(client1Info, publicKeyMap.get(client2), ownPrivateKey);
                client1.close();
                client2.close();
            }
        } catch (RuntimeException e) {
            client.send(e);
        } catch (Exception e) {
            client.send(new RuntimeException(e));
        }
    }

    @Override
    public void run() {
        try {
            try (ServerSocket server = new ServerSocket(port, 10, address)) {
                LOGGER.info("Listening on {}:{}", server.getInetAddress().getHostAddress(), server.getLocalPort());
                server.setReuseAddress(true);

                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    handleClient(new TCPClient<>(socket));
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            queue.clear();
        }
    }
}
