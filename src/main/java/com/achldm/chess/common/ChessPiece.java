package com.achldm.chess.common;

/**
 * 象棋棋子类型枚举
 */
public enum ChessPiece {
    // 红方棋子
    RED_KING(1, "帅", true),
    RED_ADVISOR(2, "仕", true),
    RED_ELEPHANT(3, "相", true),
    RED_HORSE(4, "马", true),
    RED_CHARIOT(5, "车", true),
    RED_CANNON(6, "炮", true),
    RED_SOLDIER(7, "兵", true),
    
    // 黑方棋子
    BLACK_KING(17, "将", false),
    BLACK_ADVISOR(18, "士", false),
    BLACK_ELEPHANT(19, "象", false),
    BLACK_HORSE(20, "马", false),
    BLACK_CHARIOT(21, "车", false),
    BLACK_CANNON(22, "炮", false),
    BLACK_SOLDIER(23, "卒", false),
    
    // 空位
    EMPTY(0, "", false);
    
    private final int id;
    private final String name;
    private final boolean isRed;
    
    ChessPiece(int id, String name, boolean isRed) {
        this.id = id;
        this.name = name;
        this.isRed = isRed;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isRed() {
        return isRed;
    }
    
    public boolean isEmpty() {
        return this == EMPTY;
    }
    
    public static ChessPiece fromId(int id) {
        for (ChessPiece piece : values()) {
            if (piece.id == id) {
                return piece;
            }
        }
        return EMPTY;
    }
}