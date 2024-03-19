package com.checkers.server;

import java.util.UUID;

public class PlayerToken {
    private final UUID uuid;

    public PlayerToken() {
        this.uuid = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}