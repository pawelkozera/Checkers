package com.checkers.communicationClientServer.elementsDTO;

import java.io.Serializable;

public record GameInformationDTO(boolean playerTurn, PieceDTO[] board, int[] movedPieceStartPos, int[] movedPieceEndPos) implements Serializable {
    public GameInformationDTO(boolean playerTurn) {
        this(playerTurn, null, null, null);
    }
}
