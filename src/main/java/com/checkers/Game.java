package com.checkers;

import com.checkers.communicationClientServer.GameInformationDTO;
import com.checkers.communicationClientServer.PieceDTO;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.checkers.GameWindow.HEIGHT_BOARD;
import static com.checkers.GameWindow.WIDTH_BOARD;


public class Game {
    private MoveValidator moveValidator;
    private boolean isPlayerStart;
    private Piece selectedPiece;
    private volatile boolean isPlayerTurn;
    private Tile[][] tiles;
    private List<Piece> lightPieces;
    private List<Piece> darkPieces;
    private ConnectionInfo connectionInfo;
    private boolean isItOnlineGame;
    LongestTakingSequence longestTakingSequence;
    boolean longestSequenceChecked;
    List<Piece> allPiecesWithPossibleTakings = new ArrayList<>();
    boolean possibleTakingsChecked;
    boolean isPlayerWhite;

    public Game(boolean isPlayerStart, Tile[][] tiles, List<Piece> lightPieces, List<Piece> darkPieces) {

        this.tiles = tiles;
        this.lightPieces = lightPieces;
        this.darkPieces = darkPieces;
        moveValidator=new MoveValidator(tiles);
        isItOnlineGame = false;
        longestSequenceChecked = false;
        possibleTakingsChecked = false;

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
                    tile.setOnMouseClicked(event -> makeMoveLan(tile));
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
        longestSequenceChecked = false;

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
            isPlayerWhite = true;
            for (Piece piece : lightPieces) {
                piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
            }
        }
        else {
            isPlayerWhite = false;
            for (Piece piece : darkPieces) {
                piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
            }
            gameBoard.setRotate(180);
        }

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                tile.setOnMouseClicked(event -> makeMoveOnlineAndComputer(tile));
            }
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(this::handleOnlineGameFlow);
    }

    private void handlePieceClick(Piece piece) {
        if (isItOnlineGame && isPlayerTurn) {
            handleSelectedPiece(piece);
        }
        else if ((isPlayerTurn && lightPieces.contains(piece)) || (!isPlayerTurn && darkPieces.contains(piece))) {
            handleSelectedPiece(piece);
        }
    }

    private void makeMoveLan(Tile tile){
        makeMove(tile);
    }

    private void makeMoveOnlineAndComputer(Tile tile) {
        if (isPlayerTurn) {
            makeMove(tile);
        }
    }

    private void makeMove(Tile tile) {
        if (selectedPiece != null && tile.isAccess()) {
            int newX = tile.getX();
            int newY = tile.getY();

            tiles[selectedPiece.getX()][selectedPiece.getY()].removePiece();
            tile.setPiece(selectedPiece);
            selectedPiece.setX(newX);
            selectedPiece.setY(newY);

            List<LongestTakingSequenceInformation> takingInformation = longestTakingSequence.getLongestTakingSequenceInformations();
            if (takingInformation.size() > 0) {
                int i = 0;
                int j = 0;

                int[][] boardForSwapping = new int[WIDTH_BOARD][HEIGHT_BOARD];
                for (LongestTakingSequenceInformation information : takingInformation) {
                    if (information.x() == newX && information.y() == newY) {
                        boardForSwapping = information.board();
                    }
                }

                for (int[] takingBoardRow : boardForSwapping) {
                    for (int pieceAfterTaking : takingBoardRow) {
                        Piece piece = tiles[i][j].getPiece();
                        if (piece != null) {
                            String pieceColour = piece.getColour();
                            if ((pieceColour.equals("Light") && pieceAfterTaking != 1) || (pieceColour.equals("Dark") && pieceAfterTaking != 2)) {
                                piece.removePieceFromBoard();
                                tiles[i][j].removePiece();

                                if(Objects.equals(piece.getColour(), "Dark")) //Usuwanie pionków z tablic
                                    darkPieces.remove(piece);
                                else
                                    lightPieces.remove(piece);
                            }
                        }
                        j += 1;
                    }
                    i += 1;
                    j = 0;
                }
            }

            if ((Objects.equals(selectedPiece.getColour(), "Light") && selectedPiece.getY() == HEIGHT_BOARD - 1) || (Objects.equals(selectedPiece.getColour(), "Dark") && selectedPiece.getY() == 0)) {
                selectedPiece.makeKing();
            }

            selectedPiece = null;
            isPlayerTurn = !isPlayerTurn;
            for (Tile[] rowToClear : tiles) {
                for (Tile tileClear : rowToClear) {
                    tileClear.removeAccess();
                    tileClear.removeMarking();
                }
            }

            if (isItOnlineGame) {
                sendBoardToServer();
            }
            else {
                markPossibleCapture();
            }

            possibleTakingsChecked = false;
        }
    }

    private void handleSelectedPiece(Piece piece) {
        if (!possibleTakingsChecked) {
            findAllPiecesWithLongestTakings();
            possibleTakingsChecked = true;
        }

        longestTakingSequence = findLongestTaking(piece);
        List<LongestTakingSequenceInformation> takingInformation = longestTakingSequence.getLongestTakingSequenceInformations();

        if (takingInformation.size() > 0 && allPiecesWithPossibleTakings.contains(piece)) {
            List<Point2D> possibleMoves = new ArrayList<>();
            for (LongestTakingSequenceInformation information : takingInformation) {
                possibleMoves.add(new Point2D(information.x(), information.y()));
            }

            markPossibleMoves(possibleMoves);
            selectedPiece = piece;
        }
        else if (allPiecesWithPossibleTakings.size() == 0) {
            markPossibleMoves(moveValidator.getPossibleMoves(piece));
            selectedPiece = piece;
        }
        else {
            for (Tile[] tileRow : tiles) {
                for (Tile tile : tileRow) {
                    tile.removeAccess();
                }
            }
            selectedPiece = null;
        }
    }

    private void findAllPiecesWithLongestTakings() {
        allPiecesWithPossibleTakings.clear();
        List<Piece> playerPieces;
        if (!isItOnlineGame) {
            playerPieces = isPlayerTurn ? lightPieces : darkPieces;
        }
        else {
            playerPieces = isPlayerWhite ? lightPieces : darkPieces;
        }

        Map<Integer, List<Piece>> piecesByMaxTakingSequenceLength = new HashMap<>();
        int maxTakingSequenceLength = 0;

        for (Piece piece : playerPieces) {
            LongestTakingSequence longestTakingSequence = findLongestTaking(piece);
            List<LongestTakingSequenceInformation> takingInformation = longestTakingSequence.getLongestTakingSequenceInformations();
            int sequenceLength = longestTakingSequence.getSequenceLength();
            if (takingInformation.size() > 0 && sequenceLength > maxTakingSequenceLength) {
                maxTakingSequenceLength = sequenceLength;
                piecesByMaxTakingSequenceLength.clear();
            }
            if (sequenceLength == maxTakingSequenceLength) {
                piecesByMaxTakingSequenceLength.computeIfAbsent(sequenceLength, k -> new ArrayList<>()).add(piece);
            }
        }

        if (maxTakingSequenceLength > 0) {
            allPiecesWithPossibleTakings.addAll(piecesByMaxTakingSequenceLength.get(maxTakingSequenceLength));
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

    private void markPossibleCapture()
    {
        List<Piece> currentPlayerPieces;
        if (!isItOnlineGame) {
            currentPlayerPieces = isPlayerTurn ? lightPieces : darkPieces;
        }
        else {
            currentPlayerPieces = isPlayerWhite ? lightPieces : darkPieces;
        }
        System.out.println(currentPlayerPieces.size());
        int max=0;

        for(Piece piece:currentPlayerPieces)
        {
            longestTakingSequence = findLongestTaking(piece);
            List<LongestTakingSequenceInformation> takingInformation = longestTakingSequence.getLongestTakingSequenceInformations();
            if (takingInformation.size() > 0) {
                int sequenceLong=longestTakingSequence.getSequenceLength();
                if(sequenceLong>=max) {
                    max=sequenceLong;
                }
            }
        }

        for(Piece pieceWithCapture:currentPlayerPieces)
        {
            longestTakingSequence = findLongestTaking(pieceWithCapture);
            List<LongestTakingSequenceInformation> takingInformation = longestTakingSequence.getLongestTakingSequenceInformations();
            if (takingInformation.size() > 0) {
                int sequenceLong=longestTakingSequence.getSequenceLength();
                if(sequenceLong==max) {
                   tiles[pieceWithCapture.getX()][pieceWithCapture.getY()].setMarking();
                }
            }
        }

    }

    private LongestTakingSequence findLongestTaking(Piece piece) {
        LongestTakingSequence longestSequence = new LongestTakingSequence();
        int pieceColor;
        if (piece.getColour().equals("Light")) {
            pieceColor = 1;
        }
        else {
            pieceColor = 2;
        }

        longestSequence.findLongestSequence(createBoard(), pieceColor, piece.getX(), piece.getY(), 0);

        return longestSequence;
    }

    private int[][] createBoard() {
        int[][] board = new int[WIDTH_BOARD][HEIGHT_BOARD];
        int i = 0;
        int j = 0;
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                Piece piece = tile.getPiece();

                if (piece != null) {
                    String pieceColour = piece.getColour();

                    if (pieceColour.equals("Light")) {
                        board[i][j] = 1;
                    } else if (pieceColour.equals("Dark")) {
                        board[i][j] = 2;
                    }
                }
                else {
                    board[i][j] = 0;
                }
                j += 1;
            }
            i += 1;
            j = 0;
        }

        return board;
    }

    private void handleOnlineGameFlow() {
        boolean gameRunning = true;
        while (gameRunning) {
            if (!isPlayerTurn) {
                try {
                    GameInformationDTO serverResponse = (GameInformationDTO) connectionInfo.getInputStream().readObject();
                    PieceDTO[] board = serverResponse.board();
                    updateUIAfterServerResponse(board);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void updateUIAfterServerResponse(PieceDTO[] board) {
        Platform.runLater(() -> {
            int indexLight = 0;
            int indexDark = 0;
            Piece[] piecesLight = new Piece[lightPieces.size()];
            Piece[] piecesDark = new Piece[darkPieces.size()];

            for (Tile[] row : tiles) {
                for (Tile tile : row) {
                    if (tile.getPiece() != null) {
                        if (tile.getPiece().getColour().equals("Light")) {
                            piecesLight[indexLight] = tile.getPiece();
                            indexLight += 1;
                        }
                        else {
                            piecesDark[indexDark] = tile.getPiece();
                            indexDark += 1;
                        }
                        tile.getPiece().removePieceFromBoard();
                        tile.removePiece();
                    }
                }
            }

            indexLight = 0;
            indexDark = 0;
            Piece piece = null;
            for (PieceDTO pieceDTO : board) {
                if (pieceDTO != null) {
                    if (pieceDTO.color().equals("Light")) {
                        piece = piecesLight[indexLight];
                        indexLight += 1;
                    } else {
                        piece = piecesDark[indexDark];
                        indexDark += 1;
                    }

                    if (pieceDTO.isKing()) {
                        piece.makeKing();
                    }

                    tiles[pieceDTO.x()][pieceDTO.y()].setPiece(piece);
                    piece.setX(pieceDTO.x());
                    piece.setY(pieceDTO.y());
                }
            }

            isPlayerTurn = true;
            markPossibleCapture();
        });
    }

    private void sendBoardToServer() {
        try {
            PieceDTO[] pieces = createPieceDTO();
            GameInformationDTO gameInformationDTO = new GameInformationDTO(isPlayerTurn, pieces);
            connectionInfo.getOutputStream().writeObject(gameInformationDTO);
            isPlayerTurn = false;
        } catch (IOException e) {
            System.out.println("Error sending tiles to server: " + e.getMessage());
        }
    }

    private PieceDTO[] createPieceDTO() {
        PieceDTO[] pieces = new PieceDTO[lightPieces.size() + darkPieces.size()];
        int index = 0;

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile.getPiece() != null) {
                    PieceDTO pieceDTO = new PieceDTO(tile.getX(), tile.getY(), tile.getPiece().isKing, tile.getPiece().getColour());

                    pieces[index++] = pieceDTO;
                }
            }
        }

        return pieces;
    }
}