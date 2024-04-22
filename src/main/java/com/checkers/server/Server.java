package com.checkers.server;

import com.checkers.Game;
import com.checkers.communicationClientServer.GameInformationDTO;
import com.checkers.communicationClientServer.PieceDTO;

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


public class Server {
    LinkedList<PlayerToken> playersQueue = new LinkedList<>();
    HashMap<PlayerToken, GameInformation> playersGames = new HashMap<>();

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
        ObjectOutputStream output = playerToken.getOutputStream();
        ObjectInputStream input = playerToken.getInputStream();
        try {
            boolean searchForMatch = true;
            while (searchForMatch) {
                if (playersGames.containsKey(playerToken)) {
                    searchForMatch = false;
                }
                Thread.sleep(1000);
            }

            GameInformation gameInformation = playersGames.get(playerToken);

            boolean playerTurn;
            boolean isPlayer1;

            if (gameInformation.getPlayer1() == playerToken) {
                playerTurn = gameInformation.isPlayer1IsMoving();
                isPlayer1 = true;
            } else {
                playerTurn = !gameInformation.isPlayer1IsMoving();
                isPlayer1 = false;
            }

            GameInformationDTO gameInformationDTO = new GameInformationDTO(playerTurn);
            output.writeObject(gameInformationDTO);

            ObjectOutputStream outputSecondPlayer;
            if (isPlayer1) {
                outputSecondPlayer = gameInformation.getPlayer2().getOutputStream();
            }
            else {
                outputSecondPlayer = gameInformation.getPlayer1().getOutputStream();
            }

            boolean playGame = true;
            while (playGame) {
                if (playerTurn) {
                    System.out.println("Serwer odbieranie");
                    GameInformationDTO receivedGameInformation = (GameInformationDTO) input.readObject();

                    gameInformationDTO = new GameInformationDTO(true, receivedGameInformation.board());
                    outputSecondPlayer.writeObject(gameInformationDTO);
                    gameInformation.setPlayer1IsMoving(!isPlayer1);

                    playerTurn = false;
                }

                gameInformation = playersGames.get(playerToken);
                if ((isPlayer1 && gameInformation.isPlayer1IsMoving()) || (!isPlayer1 && !gameInformation.isPlayer1IsMoving())) {
                    System.out.println("Zmiana " + isPlayer1);
                    playerTurn = true;
                }
            }

            input.close();
            output.close();
            playerToken.getClientSocket().close();
        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } catch (InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void matchPlayers() {
        System.out.println(playersQueue);
        PlayerToken firstPlayer, secondPlayer;

        while (playersQueue.size() > 1) {
            firstPlayer = playersQueue.removeFirst();
            secondPlayer = playersQueue.removeFirst();

            GameInformation gameInformation = new GameInformation(firstPlayer, secondPlayer);

            playersGames.put(firstPlayer, gameInformation);
            playersGames.put(secondPlayer, gameInformation);
        }
        System.out.println(playersGames);
        System.out.println(playersQueue);
    }
}
