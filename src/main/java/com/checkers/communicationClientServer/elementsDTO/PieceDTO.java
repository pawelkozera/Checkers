package com.checkers.communicationClientServer.elementsDTO;

import java.io.Serializable;

public record PieceDTO(int x, int y, boolean isKing, String color) implements Serializable {
}
