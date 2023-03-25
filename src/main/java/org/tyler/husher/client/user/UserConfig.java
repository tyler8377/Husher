package org.tyler.husher.client.user;

import org.tyler.husher.core.util.SerializationUtils;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class UserConfig implements Serializable {

    private String path;
    private Deque<Profile> profiles;

    public UserConfig(String path) {
        this.path = path;
        this.profiles = new ArrayDeque<>();
    }

    public UserConfig(Path path) {
        this(path.toAbsolutePath().toString());
    }

    public void init() {
        List<Profile> sortedProfiles = profiles.stream().filter(profile -> Files.exists(Path.of(profile.getPath()))).collect(Collectors.toList());
        profiles.clear();
        profiles.addAll(sortedProfiles);
    }

    public void save() {
        try {
            byte[] objBytes = SerializationUtils.serialize(this);
            System.out.println("saving " + path);
            Files.write(Path.of(path), objBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save config", e);
        }
    }

    public void addProfile(Profile profile) {
        this.profiles.addFirst(profile);
        save();
    }

    public void removeProfile(Profile profile) {
        this.profiles.remove(profile);
        save();
    }

    public void setLastAccessedProfile(Profile profile) {
        profiles.remove(profile);
        profiles.addFirst(profile);
        save();
    }

    public String getPath() {
        return path;
    }

    public Deque<Profile> getProfiles() {
        return profiles;
    }
}
