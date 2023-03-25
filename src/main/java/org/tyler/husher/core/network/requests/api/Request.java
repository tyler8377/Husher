package org.tyler.husher.core.network.requests.api;

import org.tyler.husher.core.util.RandomIdProvider;

import java.io.Serializable;

public abstract class Request implements Serializable {

    private final String requestId;

    protected Request() {
        this.requestId = RandomIdProvider.generateBase62();
    }

    public abstract String getUsername();

    public abstract String[] getRecipients();

    public String getRequestId() {
        return requestId;
    }
}
