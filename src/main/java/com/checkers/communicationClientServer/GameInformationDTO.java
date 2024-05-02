package com.checkers.communicationClientServer;

import com.checkers.Game;

import java.io.Serializable;

public record GameInformationDTO(boolean playerTurn, PieceDTO[] board, int[] movedPieceStartPos, int[] movedPieceEndPos) implements Serializable {
    public GameInformationDTO(boolean playerTurn) {
        this(playerTurn, null, null, null);
    }
}
