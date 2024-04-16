package com.checkers;

import com.checkers.communicationClientServer.GameInformationDTO;
import com.checkers.communicationClientServer.PieceDTO;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.checkers.GameWindow.HEIGHT_BOARD;
import static com.checkers.GameWindow.WIDTH_BOARD;


public class Game {
    private MoveValidator moveValidator;
    private boolean isPlayerStart;
    private Piece selectedPiece;
    private boolean isPlayerTurn;
    private Tile[][] tiles;
    private List<Piece> lightPieces;
    private List<Piece> darkPieces;
    private ConnectionInfo connectionInfo;
    private boolean isItOnlineGame;

    public Game(boolean isPlayerStart, Tile[][] tiles, List<Piece> lightPieces, List<Piece> darkPieces) {

        this.tiles = tiles;
        this.lightPieces = lightPieces;
        this.darkPieces = darkPieces;
        moveValidator=new MoveValidator(tiles);
        isItOnlineGame = false;

        if(isPlayerStart)
        {
            isPlayerTurn=true;
            for (Piece piece : lightPieces) {
                piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
            }

            for (Piece piece : darkPieces) {
                piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
            }

            for (Tile[] row : tiles) {
                for (Tile tile : row) {
                    tile.setOnMouseClicked(event -> {
                        if (selectedPiece != null && tile.isAccess()) {
                            int newX = tile.getX();
                            int newY = tile.getY();
                            Piece capturedPiece;
                            int oldX = selectedPiece.getX();
                            int oldY = selectedPiece.getY();

                            tiles[selectedPiece.getX()][selectedPiece.getY()].removePiece();
                            tile.setPiece(selectedPiece);
                            selectedPiece.setX(newX);
                            selectedPiece.setY(newY);

                            if(oldX < newX && oldY < newY) {
                                capturedPiece = tiles[newX - 1][newY - 1].getPiece();
                                tiles[newX - 1][newY - 1].removePiece();
                                if (capturedPiece != null) {
                                    capturedPiece.removePieceFromBoard();
                                }
                            }
                            if (oldX < newX && oldY > newY) {
                                capturedPiece = tiles[newX - 1][newY + 1].getPiece();
                                tiles[newX - 1][newY + 1].removePiece();
                                if (capturedPiece != null) {
                                    capturedPiece.removePieceFromBoard();
                                }
                            }
                            if (oldX > newX && oldY > newY) {
                                capturedPiece = tiles[newX + 1][newY + 1].getPiece();
                                tiles[newX + 1][newY + 1].removePiece();
                                if (capturedPiece != null) {
                                    capturedPiece.removePieceFromBoard();
                                }
                            }
                            if (oldX > newX && oldY < newY) {
                                capturedPiece = tiles[newX + 1][newY - 1].getPiece();
                                tiles[newX + 1][newY - 1].removePiece();
                                if (capturedPiece != null) {
                                    capturedPiece.removePieceFromBoard();
                                }
                            }

                            if((Objects.equals(selectedPiece.getColour(), "Light") && selectedPiece.getY()==HEIGHT_BOARD-1)||(Objects.equals(selectedPiece.getColour(), "Dark") &&selectedPiece.getY()==0)) {
                                selectedPiece.makeKing();
                            }

                            selectedPiece = null;
                            isPlayerTurn = !isPlayerTurn;
                            for (Tile[] rowToClear: tiles) {
                                for (Tile tileClear : rowToClear) {
                                    tileClear.removeAccess();
                                }
                            }

                        //sendBoardToServer();
                        }
                    });
                }
            }
        }
    }

    public Game(Tile[][] tiles, List<Piece> lightPieces, List<Piece> darkPieces, ConnectionInfo connectionInfo, BorderPane gameBoard) {
        this.tiles = tiles;
        this.lightPieces = lightPieces;
        this.darkPieces = darkPieces;
        moveValidator = new MoveValidator(tiles);
        this.connectionInfo = connectionInfo;
        isItOnlineGame = true;

        try {
            connectionInfo.openConnection();
        } catch (IOException e) {
            System.out.println("Error opening connection: " + e.getMessage());
        }

        try {
            GameInformationDTO gameInformationDTO = (GameInformationDTO) connectionInfo.getInputStream().readObject();
            isPlayerTurn = gameInformationDTO.playerTurn();
            System.out.println(isPlayerTurn);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (isPlayerTurn) {
            for (Piece piece : lightPieces) {
                piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
            }
        }
        else {
            for (Piece piece : darkPieces) {
                piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
            }
            gameBoard.setRotate(180);
        }

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                tile.setOnMouseClicked(event -> makeMove(tile));
            }
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(this::handleOnlineGameFlow);
    }

    private void handlePieceClick(Piece piece) {
        if ((isPlayerTurn && lightPieces.contains(piece)) || (!isPlayerTurn && darkPieces.contains(piece))) {
            selectedPiece = piece;
            markPossibleMoves(moveValidator.getPossibleMoves(piece));
        }
    }
    private void markPossibleMoves(List<Point2D> possibleMoves) {
        for (int y = 0; y < HEIGHT_BOARD; y++) {
            for (int x = 0; x < WIDTH_BOARD; x++) {
                Tile tile = this.tiles[x][y];
                if (possibleMoves.contains(new Point2D(x, y))) {
                    tile.setAccess();
                } else {
                    tile.removeAccess();
                }
            }
        }
    }

    private void makeMove(Tile tile) {
        if (isPlayerTurn) {
            if (selectedPiece != null && tile.isAccess()) {
                int newX = tile.getX();
                int newY = tile.getY();
                tiles[selectedPiece.getX()][selectedPiece.getY()].removePiece();
                tile.setPiece(selectedPiece);
                selectedPiece.setX(newX);
                selectedPiece.setY(newY);

                if ((Objects.equals(selectedPiece.getColour(), "Light") && selectedPiece.getY() == HEIGHT_BOARD - 1) || (Objects.equals(selectedPiece.getColour(), "Dark") && selectedPiece.getY() == 0)) {
                    selectedPiece.makeKing();
                }

                selectedPiece = null;
                isPlayerTurn = !isPlayerTurn;
                for (Tile[] rowToClear : tiles) {
                    for (Tile tileClear : rowToClear) {
                        tileClear.removeAccess();
                    }
                }

                if (isItOnlineGame) {
                    sendBoardToServer();
                }
            }
        }
    }

    private void handleOnlineGameFlow() {
        boolean gameRunning = true;
        while (gameRunning) {
            if (!isPlayerTurn) {
                try {
                    GameInformationDTO serverResponse = (GameInformationDTO) connectionInfo.getInputStream().readObject();
                    isPlayerTurn = true;
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void sendBoardToServer() {
        try {
            String serverAddress = "localhost";
            int serverPort = 1025;

            Socket socket = new Socket(serverAddress, serverPort);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            PieceDTO[] pieces = createPieceDTO();

            outputStream.writeObject(pieces);

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            String serverResponse = (String) inputStream.readObject();
            System.out.println("Server response: " + serverResponse);

            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error sending tiles to server: " + e.getMessage());
        }
    }

    private void updateUIAfterServerResponse(PieceDTO[] serverResponse) {
        Platform.runLater(() -> {
            // updatePiecesPositions(serverResponse.getPieces());
            // isPlayerTurn = serverResponse.isPlayerTurn();
        });
    }

    private PieceDTO[] createPieceDTO() {
        PieceDTO[] pieces = new PieceDTO[WIDTH_BOARD * HEIGHT_BOARD];
        int index = 0;

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile.getPiece() != null) {
                    PieceDTO pieceDTO = new PieceDTO(tile.getX(), tile.getY(), tile.getPiece().isKing);

                    pieces[index++] = pieceDTO;
                }
            }
        }

        return pieces;
    }

}
