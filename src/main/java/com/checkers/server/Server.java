package com.checkers.server;

import com.checkers.communicationClientServer.GameInformationDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
                executorService.submit(() -> checkClientConnection(playerToken));
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

        boolean[] results = pairPlayersInQueue(playerToken);
        boolean playerTurn = results[0];
        boolean isPlayer1 = results[1];

        GameInformation gameInformation = playersGames.get(playerToken);

        GameInformationDTO gameInformationDTO = new GameInformationDTO(playerTurn);
        sendInitialGameInformationDTOToClient(output, gameInformationDTO);

        ObjectOutputStream outputSecondPlayer = findOutputStreamOfSecondPlayer(isPlayer1, gameInformation);

        boolean playGame = true;
        while (playGame) {
            if (playerTurn) {
                handleTransferingGameInformationBetweenClients(input, outputSecondPlayer, gameInformation, isPlayer1);
                playerTurn = false;
            }

            gameInformation = playersGames.get(playerToken);
            boolean isPlayerTurn = (isPlayer1 && gameInformation.isPlayer1IsMoving()) || (!isPlayer1 && !gameInformation.isPlayer1IsMoving());
            if (isPlayerTurn) {
                playerTurn = true;
            }
        }

        try {
            input.close();
            output.close();
            playerToken.getClientSocket().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkClientConnection(PlayerToken playerToken) {
        try {
            ObjectOutputStream output = playerToken.getOutputStream();
            ObjectInputStream input = playerToken.getInputStream();
            while (true) {
                output.writeObject("SEND_BEAT");
                Thread.sleep(1000);
                //Object received = input.readObject();
                //if (received instanceof String && ((String) received).equals("RECEIVE_BEAT")) {
                 //   System.out.println("RECEIVE_BEAT");
                //}
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean[] pairPlayersInQueue(PlayerToken playerToken) {
        boolean searchForMatch = true;
        while (searchForMatch) {
            if (playersGames.containsKey(playerToken)) {
                searchForMatch = false;
            }
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

        return new boolean[] { playerTurn, isPlayer1 };
    }

    private void sendInitialGameInformationDTOToClient(ObjectOutputStream output, GameInformationDTO gameInformationDTO) {
        try {
            output.writeObject(gameInformationDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectOutputStream findOutputStreamOfSecondPlayer(boolean isPlayer1, GameInformation gameInformation) {
        ObjectOutputStream outputSecondPlayer;

        if (isPlayer1) {
            outputSecondPlayer = gameInformation.getPlayer2().getOutputStream();
        }
        else {
            outputSecondPlayer = gameInformation.getPlayer1().getOutputStream();
        }

        return outputSecondPlayer;
    }

    private void handleTransferingGameInformationBetweenClients(ObjectInputStream input, ObjectOutputStream outputSecondPlayer, GameInformation gameInformation, boolean isPlayer1) {
        GameInformationDTO receivedGameInformation = null;
        try {
            Object receivedObject = input.readObject();
            if (receivedObject instanceof GameInformationDTO) {
                receivedGameInformation = (GameInformationDTO) receivedObject;
                GameInformationDTO gameInformationDTO = new GameInformationDTO(true, receivedGameInformation.board());
                outputSecondPlayer.writeObject(gameInformationDTO);
                gameInformation.setPlayer1IsMoving(!isPlayer1);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        GameInformationDTO gameInformationDTO = new GameInformationDTO(true, receivedGameInformation.board());
        try {
            outputSecondPlayer.writeObject(gameInformationDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gameInformation.setPlayer1IsMoving(!isPlayer1);
    }

    private void matchPlayers() {
        //System.out.println(playersQueue);
        PlayerToken firstPlayer, secondPlayer;

        while (playersQueue.size() > 1) {
            firstPlayer = playersQueue.removeFirst();
            secondPlayer = playersQueue.removeFirst();

            GameInformation gameInformation = new GameInformation(firstPlayer, secondPlayer);

            playersGames.put(firstPlayer, gameInformation);
            playersGames.put(secondPlayer, gameInformation);
        }
        //System.out.println(playersGames);
        //System.out.println(playersQueue);
    }
}
