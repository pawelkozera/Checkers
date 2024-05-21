package com.checkers.server;

import com.checkers.communicationClientServer.GameInformationDTO;
import com.checkers.communicationClientServer.PieceDTO;

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

import static com.checkers.GameWindow.HEIGHT_BOARD;
import static com.checkers.GameWindow.WIDTH_BOARD;


public class Server {
    LinkedList<PlayerToken> playersQueue = new LinkedList<>();
    HashMap<PlayerToken, GameInformation> playersGames = new HashMap<>();

    public void start() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            final int SERVER_PORT = 1025;
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            executorService.submit(this::handleQueue);

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

    private void checkClientConnection(PlayerToken playerToken) {
        try {
            ObjectOutputStream output = playerToken.getOutputStream();
            while (playerToken.getClientSocket().isConnected()) {
                output.writeObject("SEND_BEAT");
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(PlayerToken playerToken) {
        ObjectOutputStream output = playerToken.getOutputStream();
        ObjectInputStream input = playerToken.getInputStream();

        boolean[] results = pairPlayersInQueue(playerToken);
        boolean playerTurn = results[0];
        boolean isPlayer1 = results[1];

        GameInformationDTO gameInformationDTO = new GameInformationDTO(playerTurn);
        sendInitialGameInformationDTOToClient(output, gameInformationDTO);

        listenForClientResponse(playerToken, isPlayer1);

        try {
            input.close();
            output.close();
            playerToken.getClientSocket().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private void listenForClientResponse(PlayerToken playerToken, boolean isPlayer1) {
        try {
            ObjectInputStream input = playerToken.getInputStream();
            GameInformation gameInformation = playersGames.get(playerToken);
            ObjectOutputStream outputSecondPlayer = findOutputStreamOfSecondPlayer(isPlayer1, gameInformation);
            GameValidation gameValidation = new GameValidation();

            boolean runGame = true;
            while (runGame) {
                Object received = input.readObject();
                runGame = handleReceivedObject(received, gameInformation, outputSecondPlayer, isPlayer1);

                if (gameValidation.endGame(gameInformation.getBoard())) {
                    playersGames.remove(playerToken);
                    break;
                }
            }
            System.out.println("Game end");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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

    private boolean handleReceivedObject(Object received, GameInformation gameInformation, ObjectOutputStream outputSecondPlayer, boolean isPlayer1) {
        if (received instanceof GameInformationDTO) {
            GameInformationDTO serverResponse = (GameInformationDTO) received;
            handleTransferingGameInformationBetweenClients(gameInformation, outputSecondPlayer, serverResponse, isPlayer1);

            int[][] newBoard = changePieceDTOboardToIntegerBoard(serverResponse.board());
            gameInformation.setBoard(newBoard);
        }
        else if (received instanceof String && received.equals("RECEIVE_BEAT")) {
            //System.out.println("RECEIVE_BEAT");
        }
        else if (received instanceof String && received.equals("END_GAME")) {
            sendInformationAboutWonGameToSecondPlayer(outputSecondPlayer);
            endGame(gameInformation.getPlayer1());
            endGame(gameInformation.getPlayer2());
            return false;
        }

        return true;
    }

    private void handleTransferingGameInformationBetweenClients(GameInformation gameInformation, ObjectOutputStream outputSecondPlayer, GameInformationDTO gameInformationDTO, boolean isPlayer1) {
        GameInformationDTO gameInfoDTO = new GameInformationDTO(true, gameInformationDTO.board(), gameInformationDTO.movedPieceStartPos(), gameInformationDTO.movedPieceEndPos());
        try {
            outputSecondPlayer.writeObject(gameInfoDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gameInformation.setPlayer1IsMoving(!isPlayer1);
    }

    private void sendInformationAboutWonGameToSecondPlayer(ObjectOutputStream outputSecondPlayer) {
        try {
            outputSecondPlayer.writeObject("GAME_WON");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[][] changePieceDTOboardToIntegerBoard(PieceDTO[] pieceDTOboard) {
        int[][] board = new int[WIDTH_BOARD][HEIGHT_BOARD];

        for (int i = 0; i < WIDTH_BOARD; i++) {
            for (int j = 0; j < HEIGHT_BOARD; j++) {
                board[i][j] = 0;
            }
        }

        for (PieceDTO pieceDTO : pieceDTOboard) {
            if (pieceDTO != null) {
                int x = pieceDTO.x();
                int y = pieceDTO.y();
                if (pieceDTO.color().equals("Light")) {
                    board[x][y] = 1;
                }
                else {
                    board[x][y] = 2;
                }
            }
        }

        return board;
    }

    private void endGame(PlayerToken playerToken) {
        playersGames.remove(playerToken);

        try {
            playerToken.getClientSocket().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        System.out.println(playersGames);
        //System.out.println(playersQueue);
    }
}
