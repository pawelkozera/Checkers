package com.checkers.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
TODO
- Stworzyć klasę Game z 2 polami PlayerToken, będzie przechowywać informację o rozgrywce
- Określenie który gracz się rusza, kolor gracza, prawdopodobnie przechowywać tę informację w klasie PlayerToken
- Przechowywanie informacji o obu szachownicach oraz następnym ruchu i sprawdzanie czy danych ruch jest prawidłowy?
 */

public class Server {
    LinkedList<PlayerToken> playersQueue = new LinkedList<>();
    HashMap<PlayerToken, PlayerToken> playersGames = new HashMap<>();

    public void start() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            final int SERVER_PORT = 1025;
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            executorService.submit(() -> handleQueue());

            while (!Thread.interrupted()) {
                Socket clientSocket = serverSocket.accept();

                PlayerToken playerToken = new PlayerToken(clientSocket);
                playersQueue.add(playerToken);

                executorService.submit(() -> handleClient(playerToken));
            }
        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    private void handleQueue() {
        boolean queuePlayers = true;
        try {
            while (queuePlayers) {
                Thread.sleep(1000);
                matchPlayers();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClient(PlayerToken playerToken) {
        try {
            ObjectInputStream input = new ObjectInputStream(playerToken.getClientSocket().getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(playerToken.getClientSocket().getOutputStream());

            String messageFromClient = (String) input.readObject();
            System.out.println("Received from client: " + messageFromClient);

            input.close();
            output.close();
            playerToken.getClientSocket().close();

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
        PlayerToken firstPlayer, secondPlayer;

        while (playersQueue.size() > 1) {
            firstPlayer = playersQueue.removeFirst();
            secondPlayer = playersQueue.removeFirst();
            playersGames.put(firstPlayer, secondPlayer);
        }
        System.out.println(playersGames);
        System.out.println(playersQueue);
    }
}
