package org.tyler.husher.core.network.requests;

import org.tyler.husher.core.util.RandomIdProvider;

import java.io.Serializable;

public class Request implements Serializable {

    private final String id;
    private final String username;

    public Request(String username) {
        this.id = RandomIdProvider.generateBase62();
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
