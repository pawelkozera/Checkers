package com.checkers;

import com.checkers.communicationClientServer.GameInformationDTO;
import com.checkers.communicationClientServer.PieceDTO;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.layout.BorderPane;

import java.io.*;
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
    private List<Piece> constLightPieces;
    private List<Piece> constDarkPieces;
    private List<Piece> lightPieces;
    private List<Piece> darkPieces;
    private ConnectionInfo connectionInfo;
    private boolean isItOnlineGame;
    LongestTakingSequence longestTakingSequence;
    boolean longestSequenceChecked;
    List<Piece> allPiecesWithPossibleTakings = new ArrayList<>();
    boolean possibleTakingsChecked;
    boolean isPlayerWhite;
    private final GameInfoScreen gameInfoScreen;
    private  GameOverScreen gameOverScreen;


    public Game(GameInfoScreen gameInfoScreen,GameOverScreen gameOverScreen, Tile[][] tiles, List<Piece> lightPieces, List<Piece> darkPieces) {

        this.tiles = tiles;
        this.lightPieces = lightPieces;
        this.darkPieces = darkPieces;
        this.constDarkPieces=clonePieceList(darkPieces);
        this.constLightPieces=clonePieceList(lightPieces);
        moveValidator=new MoveValidator(tiles);
        isItOnlineGame = false;
        longestSequenceChecked = false;
        possibleTakingsChecked = false;
        isPlayerTurn=true;
        this.gameOverScreen=gameOverScreen;
        this.gameInfoScreen=gameInfoScreen;
        this.gameInfoScreen.setDisable(false);
        this.gameInfoScreen.setVisible(true);
        this.gameInfoScreen.setUpScreen(isPlayerTurn);

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
        gameOverScreen.getRestartButton().setOnMouseClicked(event->{
            restartGame();
        });
        gameInfoScreen.getRestartButton().setOnMouseClicked(event->{
            restartGame();
        });

    }

    public Game(GameInfoScreen gameInfoScreen, GameOverScreen gameOverScreen, Tile[][] tiles, List<Piece> lightPieces, List<Piece> darkPieces, ConnectionInfo connectionInfo, BorderPane gameBoard) {
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
            Object received = null;
            while (true) {
                if (received == null) {
                    received = connectionInfo.getInputStream().readObject();
                }
                else {
                    if (received instanceof GameInformationDTO) {
                        GameInformationDTO gameInformationDTO = (GameInformationDTO) received;
                        isPlayerTurn = gameInformationDTO.playerTurn();
                        System.out.println(isPlayerTurn);
                        break;
                    }
                    received = null;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.gameOverScreen=gameOverScreen;
        this.gameInfoScreen=gameInfoScreen;
        this.gameInfoScreen.setDisable(false);
        this.gameInfoScreen.setVisible(true);
        this.gameInfoScreen.setUpScreen(isPlayerTurn);

        if (isPlayerTurn) {
            isPlayerWhite = true;
            for (Piece piece : lightPieces) {
                piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
            }
        }
        else {
            isPlayerWhite = false;
            gameInfoScreen.refreshGameInfoScreen(12-lightPieces.size(),12-darkPieces.size(), true);
            for (Piece piece : darkPieces) {
                piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
            }
            gameBoard.setRotate(180);
            rotatePieces(); //Dodanie obracania pionków
        }

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                tile.setOnMouseClicked(event -> makeMoveOnlineAndComputer(tile));
            }
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(this::listenForServerResponse);
    }

    private void listenForServerResponse() {
        try {
            while (true) {
                Object received = connectionInfo.getInputStream().readObject();
                handleReceivedObject(received);
            }
        }
        catch (EOFException e) {
            System.out.println("EFO");
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleReceivedObject(Object received) {
        if (received instanceof GameInformationDTO) {
            GameInformationDTO serverResponse = (GameInformationDTO) received;
            updateUIAfterServerResponse(serverResponse);
        }
        else if (received instanceof String && received.equals("SEND_BEAT")) {
            //System.out.println("SEND_BEAT PRINTLN");
            replyToServer();
        }
        else if (received instanceof String && received.equals("GAME_WON")) {
            changeToEndOfGameScreen(true);
        }
    }

    private void changeToEndOfGameScreen(boolean playerWon) {
        gameOverScreen.setDisable(false);
        gameOverScreen.setVisible(true);

        if ((isPlayerWhite && playerWon) || (!isPlayerWhite && !playerWon)) {
            gameOverScreen.setUpScreen("light");
        }
        else {
            gameOverScreen.setUpScreen("dark");
        }
        gameInfoScreen.setEndGameStyle();
    }

    private void replyToServer() {
        try {
            ObjectOutputStream output = connectionInfo.getOutputStream();
            output.writeObject("RECEIVE_BEAT");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        checkWinner();
    }

    private void makeMoveOnlineAndComputer(Tile tile) {
        if (isPlayerTurn) {
            makeMove(tile);
        }
    }

    private void makeMove(Tile tile) {
        if (selectedPiece != null && tile.isAccess()) {

            gameSound.playMoveSound();
            int oldX = selectedPiece.getX();
            int oldY = selectedPiece.getY();
            int newX = tile.getX();
            int newY = tile.getY();

            movePiece(tile, newX, newY);
            takePieces(newX, newY);
            promotePieceToKing();
            removeMarking();
            updatePlayerTurnAndSetFlags();

            if (isItOnlineGame) {
                int[] pieceMovedStartPos = {oldX, oldY};
                int[] pieceMovedEndPos = {newX, newY};
                sendBoardToServer(pieceMovedStartPos, pieceMovedEndPos);
            }
            else {
                markPossibleCapture();
            }
        }
    }

    private void movePiece(Tile tile, int newX, int newY) {
        tiles[selectedPiece.getX()][selectedPiece.getY()].removePiece();
        tile.setPiece(selectedPiece);
        selectedPiece.setX(newX);
        selectedPiece.setY(newY);
    }

    private void takePieces(int newX, int newY) {
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
    }

    private void promotePieceToKing() {
        boolean isWhiteKing = Objects.equals(selectedPiece.getColour(), "Light") && selectedPiece.getY() == HEIGHT_BOARD - 1;
        boolean isBlackKing = Objects.equals(selectedPiece.getColour(), "Dark") && selectedPiece.getY() == 0;

        if (isWhiteKing || isBlackKing) {
            selectedPiece.makeKing();
        }
    }

    private void removeMarking() {
        for (Tile[] rowToClear : tiles) {
            for (Tile tileClear : rowToClear) {
                tileClear.removeAccess();
                tileClear.removeMarking();
            }
        }
    }

    private void updatePlayerTurnAndSetFlags() {
        selectedPiece = null;
        isPlayerTurn = !isPlayerTurn;
        if (isItOnlineGame) {
            gameInfoScreen.refreshGameInfoScreen(12 - lightPieces.size(), 12 - darkPieces.size(), !isPlayerWhite);
        }
        else {
            gameInfoScreen.refreshGameInfoScreen(12 - lightPieces.size(), 12 - darkPieces.size(), isPlayerTurn); //Dodane do zarządania ekranem
        }
        possibleTakingsChecked = false;
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

        boolean forKing = piece.isKing;
        longestSequence.findLongestSequence(createBoard(), pieceColor, piece.getX(), piece.getY(), 0, forKing);

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

    private void updateUIAfterServerResponse(GameInformationDTO gameInformationDTO) {
        Platform.runLater(() -> {
            int movePieceStartPosX = gameInformationDTO.movedPieceStartPos()[0];
            int movePieceStartPosY = gameInformationDTO.movedPieceStartPos()[1];
            int movePieceEndPosX = gameInformationDTO.movedPieceEndPos()[0];
            int movePieceEndPosY = gameInformationDTO.movedPieceEndPos()[1];
            Tile tileWithPieceToMove = tiles[movePieceStartPosX][movePieceStartPosY];
            Tile whereToMovePiece = tiles[movePieceEndPosX][movePieceEndPosY];

            if (tileWithPieceToMove.getPiece() != null && whereToMovePiece.getPiece() == null) {
                selectedPiece = tileWithPieceToMove.getPiece();
                movePiece(whereToMovePiece, movePieceEndPosX, movePieceEndPosY);
                selectedPiece = null;
            }

            for (Tile[] row : tiles) {
                for (Tile tile : row) {
                    if (tile.getPiece() != null) {
                        boolean pieceWasTaken = true;
                        for (PieceDTO pieceDTO : gameInformationDTO.board()) {
                            if (pieceDTO.x() == tile.getPiece().getX() && pieceDTO.y() == tile.getPiece().getY()) {
                                pieceWasTaken = false;
                                if (pieceDTO.isKing()) {
                                    tile.getPiece().makeKing();
                                }
                                else {
                                    tile.getPiece().makePawn();
                                }
                                break;
                            }
                        }
                        if (pieceWasTaken) {
                            Piece pieceTaken = tile.getPiece();
                            pieceTaken.removePieceFromBoard();
                            tile.removePiece();

                            if(Objects.equals(pieceTaken.getColour(), "Dark"))
                                darkPieces.remove(pieceTaken);
                            else
                                lightPieces.remove(pieceTaken);
                        }
                    }
                }
            }

            isPlayerTurn = true;
            markPossibleCapture();
            gameInfoScreen.refreshGameInfoScreen(12 - lightPieces.size(), 12 - darkPieces.size(), isPlayerWhite);
            gameSound.playMoveSound();
        });
    }

    private void sendBoardToServer(int[] pieceMovedStartPos, int[] pieceMovedEndPos) {
        try {
            PieceDTO[] pieces = createPieceDTO();
            GameInformationDTO gameInformationDTO = new GameInformationDTO(isPlayerTurn, pieces, pieceMovedStartPos, pieceMovedEndPos);
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

    private void rotatePieces()
    {
        for (Piece piece: lightPieces)
            piece.setRotate(180);
        for (Piece piece: darkPieces)
            piece.setRotate(180);
    }

    private void checkWinner()
    {
        if(!moveValidator.isPossibleMovesForThePlayer(lightPieces) && !moveValidator.isPossibleMovesForThePlayer(darkPieces)) {
            gameOverScreen.setUpScreen("draw");
            gameInfoScreen.setEndGameStyle();
        }
        else if(lightPieces.isEmpty() || !moveValidator.isPossibleMovesForThePlayer(lightPieces)) {  //todo sprawdzać czyja jest tura
            gameOverScreen.setUpScreen("dark");
            gameInfoScreen.setEndGameStyle();
        }
        else if (darkPieces.isEmpty() || !moveValidator.isPossibleMovesForThePlayer(darkPieces)) {
            gameOverScreen.setUpScreen("light");
            gameInfoScreen.setEndGameStyle();
        }

    }
    public void restartGame() {

        for (int y = 0; y < HEIGHT_BOARD; y++) {
            for (int x = 0; x < WIDTH_BOARD; x++) {
                tiles[x][y].removePiece();
                tiles[x][y].removeMarking();
            }
        }
        for (Piece piece : lightPieces) {
            piece.removePieceFromBoard();
        }
        for (Piece piece : darkPieces) {
            piece.removePieceFromBoard();
        }
        lightPieces.clear();
        darkPieces.clear();
        System.gc();

        lightPieces = clonePieceList(constLightPieces);
        darkPieces = clonePieceList(constDarkPieces);

        for (Piece piece : lightPieces) {
            Tile tile = getTile(piece.getX(), piece.getY());
            if (tile != null) {
                tile.setPiece(piece);
            }
        }
        for (Piece piece : darkPieces) {
            Tile tile = getTile(piece.getX(), piece.getY());
            if (tile != null) {
                tile.setPiece(piece);
            }
        }
        for (Piece piece : lightPieces) {
            piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
        }

        for (Piece piece : darkPieces) {
            piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
        }
        isPlayerTurn = true;
        gameOverScreen.setDisable(true);
        gameOverScreen.setVisible(false);
        gameInfoScreen.restart();
        gameInfoScreen.setUpScreen(isPlayerTurn);
        gameInfoScreen.getRestartButton().setOnMouseClicked(event->{
            restartGame();
        });
        gameSound.playGameStartSound();
    }

    public void sendEndGameButtonToServer() {
        if (isItOnlineGame) {
            try {
                ObjectOutputStream output = connectionInfo.getOutputStream();
                output.writeObject("END_GAME");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Tile getTile(int x, int y) {
        for (int i = 0; i < HEIGHT_BOARD; i++) {
            for (int j = 0; j < WIDTH_BOARD; j++) {
                Tile tile = tiles[i][j];
                if (tile.getX() == x && tile.getY() == y) {
                    return tile;
                }
            }
        }
        return null;
    }

    private List<Piece> clonePieceList(List<Piece> original) {
        List<Piece> copy = new ArrayList<>();
        for (Piece piece : original) {
            copy.add(piece.clone());
        }
        return copy;
    }
}