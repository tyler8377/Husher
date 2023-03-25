package org.tyler.husher.core.network.exception;

public class PeerTimeoutException extends RuntimeException {

    public PeerTimeoutException(long millis) {
        super("Peer hasn't connected (after " + millis + "ms)");
    }
}
