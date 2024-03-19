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
- Stworzyć 2 wątki, pierwszy do kolejkowania graczy, drugi do zarządzania partiami
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

            while (!Thread.interrupted()) {
                Socket clientSocket = serverSocket.accept();

                PlayerToken playerToken = new PlayerToken();
                playersQueue.add(playerToken);

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
