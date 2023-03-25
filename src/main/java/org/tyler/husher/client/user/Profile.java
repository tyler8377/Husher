package org.tyler.husher.client.user;

import java.io.Serializable;
import java.nio.file.Path;

public class Profile implements Serializable {

    private final String username;
    private final String path;

    public Profile(String username, String path) {
        this.username = username;
        this.path = path;
    }

    public Profile(String username, Path path) {
        this(username, path.toAbsolutePath().toString());
    }

    public String getUsername() {
        return username;
    }

    public String getPath() {
        return path;
    }
}
