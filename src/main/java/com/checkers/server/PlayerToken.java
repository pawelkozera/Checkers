package com.checkers.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class PlayerToken {
    private final UUID uuid;
    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;


    public PlayerToken(Socket clientSocket) {
        this.uuid = UUID.randomUUID();
        this.clientSocket = clientSocket;

        try {
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creating input/output streams: " + e.getMessage());
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public String toString() {
        return uuid.toString();
    }

    public void closeConnection() throws IOException {
        outputStream.close();
        inputStream.close();
        clientSocket.close();
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }
}