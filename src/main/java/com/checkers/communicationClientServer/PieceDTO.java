package com.checkers.communicationClientServer;

import java.io.Serializable;

public record PieceDTO(int x, int y, boolean isKing, String color) implements Serializable {
}
