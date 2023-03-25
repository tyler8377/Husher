package org.tyler.husher.core.sockets.tcp;

import org.tyler.husher.core.network.message.EncryptedObject;

import java.io.*;
import java.net.Socket;

public class TCPClient<T extends Serializable> {

    protected final Socket socket;
    protected final ObjectOutputStream out;
    protected final ObjectInputStream in;

    public TCPClient(Socket socket) throws IOException {
        try {
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public void close() {
        for (Closeable closeable : new Closeable[]{socket, in, out}) {
            try {
                closeable.close();
            } catch (Exception e) {
                System.err.println("Unable to close IO object: " + e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private T receiveObj(Object obj) {
        if (obj instanceof RuntimeException)
            throw (RuntimeException) obj;
        return (T) obj;
    }

    private void sendObj(Object obj) throws IOException {
        out.writeObject(obj);
        out.flush();
    }

    public T receive() throws IOException, ClassNotFoundException {
        return this.receiveObj(in.readObject());
    }

    public void send(T obj) throws IOException {
        sendObj(obj);
    }

    public void send(RuntimeException exception) throws IOException {
        sendObj(exception);
    }

    public void sendEncrypted(T obj, byte[] destPublicKey, byte[] ownPrivateKey) throws IOException {
        EncryptedObject encryptedObj = new EncryptedObject(obj, destPublicKey, ownPrivateKey);
        sendObj(encryptedObj);
    }

    public T receiveEncrypted(byte[] ownPrivateKey, byte[] destPublicKey) throws IOException, ClassNotFoundException {
        Object receivedObj = in.readObject();
        if (!(receivedObj instanceof EncryptedObject))
            throw new RuntimeException("Received object is not encrypted");


        // Object object = in.readObject();
        // System.err.println(object.getClass().getName());
        EncryptedObject encryptedObj = (EncryptedObject) receivedObj;

        Object obj = encryptedObj.decrypt(ownPrivateKey, destPublicKey);
        return this.receiveObj(obj);
    }

    public Socket getSocket() {
        return socket;
    }

}
