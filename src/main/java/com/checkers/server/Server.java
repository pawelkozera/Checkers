package com.checkers.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public void start() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            final int port = 1025;
            ServerSocket serverSocket = new ServerSocket(port);

            while (!Thread.interrupted()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket.getRemoteSocketAddress());
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());

            String responseToClient = "Server response to client: ";
            output.writeObject(responseToClient);

            String messageFromClient = (String) input.readObject();
            System.out.println("Received from client: " + messageFromClient);

            input.close();
            output.close();
            clientSocket.close();

        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException: " + e.getMessage());
        }
    }
}
