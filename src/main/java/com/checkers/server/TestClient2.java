package com.checkers.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TestClient2 {
    public static void main(String[] args) {
        final String SERVER_ADDRESS = "localhost";
        final int SERVER_PORT = 1025;

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            String messageFromServer = (String) inputStream.readObject();
            System.out.println("Message from Server: " + messageFromServer);

            outputStream.close();
            inputStream.close();
            socket.close();

        } catch (IOException e) {
            System.out.println("Client IOException: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Client ClassNotFoundException: " + e.getMessage());
        }
    }
}