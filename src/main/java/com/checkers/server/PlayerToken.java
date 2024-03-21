package com.checkers.server;

import java.net.Socket;
import java.util.UUID;

public class PlayerToken {
    private final UUID uuid;
    private Socket clientSocket;

    public PlayerToken(Socket clientSocket) {
        this.uuid = UUID.randomUUID();
        this.clientSocket = clientSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}