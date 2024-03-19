package com.checkers.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    LinkedList<SocketAddress> playersQueue = new LinkedList<>();
    HashMap<SocketAddress, SocketAddress> playersGames = new HashMap<>();

    public void start() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            final int SERVER_PORT = 1025;
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            while (!Thread.interrupted()) {
                Socket clientSocket = serverSocket.accept();
                playersQueue.add(clientSocket.getRemoteSocketAddress());
                executorService.submit(() -> handleClient(clientSocket));

                matchPlayers();
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

    private void matchPlayers() {
        System.out.println(playersQueue);
        SocketAddress firstPlayer, secondPlayer;

        while (playersQueue.size() > 1) {
            firstPlayer = playersQueue.removeFirst();
            secondPlayer = playersQueue.removeFirst();

            playersGames.put(firstPlayer, secondPlayer);
        }
        System.out.println(playersGames);
        System.out.println(playersQueue);
    }
}
