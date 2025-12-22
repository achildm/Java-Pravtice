package com.achldm.chess.game;

import com.achldm.chess.common.ChessPiece;

/**
 * 象棋棋盘类
 */
public class ChessBoard {
    public static final int BOARD_WIDTH = 9;
    public static final int BOARD_HEIGHT = 10;
    
    private ChessPiece[][] board;
    private boolean redTurn = true;  // 红方先行
    
    public ChessBoard() {
        initBoard();
    }
    
    /**
     * 初始化棋盘
     */
    private void initBoard() {
        board = new ChessPiece[BOARD_HEIGHT][BOARD_WIDTH];
        
        // 清空棋盘
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = ChessPiece.EMPTY;
            }
        }
        
        // 放置黑方棋子 - 正确顺序：车马象士将士象马车
        board[0][0] = board[0][8] = ChessPiece.BLACK_CHARIOT;  // 车
        board[0][1] = board[0][7] = ChessPiece.BLACK_HORSE;    // 马
        board[0][2] = board[0][6] = ChessPiece.BLACK_ELEPHANT; // 象
        board[0][3] = board[0][5] = ChessPiece.BLACK_ADVISOR;  // 士
        board[0][4] = ChessPiece.BLACK_KING;                   // 将
        
        board[2][1] = board[2][7] = ChessPiece.BLACK_CANNON;   // 炮
        
        // 黑方卒子
        for (int i = 0; i < BOARD_WIDTH; i += 2) {
            board[3][i] = ChessPiece.BLACK_SOLDIER;
        }
        
        // 放置红方棋子 - 正确顺序：车马相仕帅仕相马车
        board[9][0] = board[9][8] = ChessPiece.RED_CHARIOT;    // 车
        board[9][1] = board[9][7] = ChessPiece.RED_HORSE;      // 马
        board[9][2] = board[9][6] = ChessPiece.RED_ELEPHANT;   // 相
        board[9][3] = board[9][5] = ChessPiece.RED_ADVISOR;    // 仕
        board[9][4] = ChessPiece.RED_KING;                     // 帅
        
        board[7][1] = board[7][7] = ChessPiece.RED_CANNON;     // 炮
        
        // 红方兵
        for (int i = 0; i < BOARD_WIDTH; i += 2) {
            board[6][i] = ChessPiece.RED_SOLDIER;
        }
    }
    
    /**
     * 获取指定位置的棋子
     */
    public ChessPiece getPiece(int x, int y) {
        if (isValidPosition(x, y)) {
            return board[y][x];
        }
        return ChessPiece.EMPTY;
    }
    
    /**
     * 设置指定位置的棋子
     */
    public void setPiece(int x, int y, ChessPiece piece) {
        if (isValidPosition(x, y)) {
            board[y][x] = piece;
        }
    }
    
    /**
     * 移动棋子
     */
    public boolean movePiece(int fromX, int fromY, int toX, int toY) {
        if (!isValidMove(fromX, fromY, toX, toY)) {
            return false;
        }
        
        ChessPiece piece = getPiece(fromX, fromY);
        setPiece(toX, toY, piece);
        setPiece(fromX, fromY, ChessPiece.EMPTY);
        
        redTurn = !redTurn;
        return true;
    }
    
    /**
     * 检查移动是否合法
     */
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        if (!isValidPosition(fromX, fromY) || !isValidPosition(toX, toY)) {
            return false;
        }
        
        ChessPiece piece = getPiece(fromX, fromY);
        ChessPiece target = getPiece(toX, toY);
        
        // 不能移动空位
        if (piece.isEmpty()) {
            return false;
        }
        
        // 不能移动对方棋子
        if (piece.isRed() != redTurn) {
            return false;
        }
        
        // 不能吃自己的棋子
        if (!target.isEmpty() && target.isRed() == piece.isRed()) {
            return false;
        }
        
        // 检查具体棋子的移动规则
        return isValidPieceMove(piece, fromX, fromY, toX, toY);
    }
    
    /**
     * 检查具体棋子的移动规则
     */
    private boolean isValidPieceMove(ChessPiece piece, int fromX, int fromY, int toX, int toY) {
        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);
        
        switch (piece) {
            case RED_KING:
            case BLACK_KING:
                return isValidKingMove(fromX, fromY, toX, toY, piece.isRed());
                
            case RED_ADVISOR:
            case BLACK_ADVISOR:
                return isValidAdvisorMove(fromX, fromY, toX, toY, piece.isRed());
                
            case RED_ELEPHANT:
            case BLACK_ELEPHANT:
                return isValidElephantMove(fromX, fromY, toX, toY, piece.isRed());
                
            case RED_HORSE:
            case BLACK_HORSE:
                return isValidHorseMove(fromX, fromY, toX, toY);
                
            case RED_CHARIOT:
            case BLACK_CHARIOT:
                return isValidChariotMove(fromX, fromY, toX, toY);
                
            case RED_CANNON:
            case BLACK_CANNON:
                return isValidCannonMove(fromX, fromY, toX, toY);
                
            case RED_SOLDIER:
            case BLACK_SOLDIER:
                return isValidSoldierMove(fromX, fromY, toX, toY, piece.isRed());
                
            default:
                return false;
        }
    }
    
    // 各种棋子的移动规则检查方法
    private boolean isValidKingMove(int fromX, int fromY, int toX, int toY, boolean isRed) {
        // 帅/将只能在九宫格内移动，每次只能移动一格
        int palaceMinX = 3, palaceMaxX = 5;
        int palaceMinY = isRed ? 7 : 0;
        int palaceMaxY = isRed ? 9 : 2;
        
        if (toX < palaceMinX || toX > palaceMaxX || toY < palaceMinY || toY > palaceMaxY) {
            return false;
        }
        
        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }
    
    private boolean isValidAdvisorMove(int fromX, int fromY, int toX, int toY, boolean isRed) {
        // 仕/士只能在九宫格内斜着移动
        int palaceMinX = 3, palaceMaxX = 5;
        int palaceMinY = isRed ? 7 : 0;
        int palaceMaxY = isRed ? 9 : 2;
        
        if (toX < palaceMinX || toX > palaceMaxX || toY < palaceMinY || toY > palaceMaxY) {
            return false;
        }
        
        return Math.abs(toX - fromX) == 1 && Math.abs(toY - fromY) == 1;
    }
    
    private boolean isValidElephantMove(int fromX, int fromY, int toX, int toY, boolean isRed) {
        // 相/象不能过河，斜着走两格，不能被蹩脚
        int riverY = isRed ? 4 : 5;
        if ((isRed && toY <= riverY) || (!isRed && toY >= riverY)) {
            return false;
        }
        
        if (Math.abs(toX - fromX) != 2 || Math.abs(toY - fromY) != 2) {
            return false;
        }
        
        // 检查蹩脚点
        int midX = (fromX + toX) / 2;
        int midY = (fromY + toY) / 2;
        return getPiece(midX, midY).isEmpty();
    }
    
    private boolean isValidHorseMove(int fromX, int fromY, int toX, int toY) {
        // 马走日字，不能被蹩脚
        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);
        
        if (!((dx == 2 && dy == 1) || (dx == 1 && dy == 2))) {
            return false;
        }
        
        // 检查蹩脚点
        int blockX = fromX, blockY = fromY;
        if (dx == 2) {
            blockX += (toX > fromX) ? 1 : -1;
        } else {
            blockY += (toY > fromY) ? 1 : -1;
        }
        
        return getPiece(blockX, blockY).isEmpty();
    }
    
    private boolean isValidChariotMove(int fromX, int fromY, int toX, int toY) {
        // 车走直线，路径上不能有棋子
        if (fromX != toX && fromY != toY) {
            return false;
        }
        
        return isPathClear(fromX, fromY, toX, toY);
    }
    
    private boolean isValidCannonMove(int fromX, int fromY, int toX, int toY) {
        // 炮走直线，吃子时中间必须有一个棋子
        if (fromX != toX && fromY != toY) {
            return false;
        }
        
        ChessPiece target = getPiece(toX, toY);
        if (target.isEmpty()) {
            // 不吃子时路径必须清空
            return isPathClear(fromX, fromY, toX, toY);
        } else {
            // 吃子时中间必须有且仅有一个棋子
            return countPiecesInPath(fromX, fromY, toX, toY) == 1;
        }
    }
    
    private boolean isValidSoldierMove(int fromX, int fromY, int toX, int toY, boolean isRed) {
        int dx = toX - fromX;
        int dy = toY - fromY;
        
        // 兵/卒只能向前走，过河后可以左右走
        if (isRed) {
            if (fromY > 4) {
                // 未过河，只能向前
                return dx == 0 && dy == -1;
            } else {
                // 已过河，可以向前或左右
                return (dx == 0 && dy == -1) || (Math.abs(dx) == 1 && dy == 0);
            }
        } else {
            if (fromY < 5) {
                // 未过河，只能向前
                return dx == 0 && dy == 1;
            } else {
                // 已过河，可以向前或左右
                return (dx == 0 && dy == 1) || (Math.abs(dx) == 1 && dy == 0);
            }
        }
    }
    
    /**
     * 检查路径是否清空
     */
    private boolean isPathClear(int fromX, int fromY, int toX, int toY) {
        return countPiecesInPath(fromX, fromY, toX, toY) == 0;
    }
    
    /**
     * 计算路径上的棋子数量
     */
    private int countPiecesInPath(int fromX, int fromY, int toX, int toY) {
        int count = 0;
        int stepX = Integer.compare(toX, fromX);
        int stepY = Integer.compare(toY, fromY);
        
        int x = fromX + stepX;
        int y = fromY + stepY;
        
        while (x != toX || y != toY) {
            if (!getPiece(x, y).isEmpty()) {
                count++;
            }
            x += stepX;
            y += stepY;
        }
        
        return count;
    }
    
    /**
     * 检查位置是否有效
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT;
    }
    
    /**
     * 检查游戏是否结束
     */
    public boolean isGameOver() {
        boolean redKingExists = false;
        boolean blackKingExists = false;
        
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                ChessPiece piece = board[i][j];
                if (piece == ChessPiece.RED_KING) {
                    redKingExists = true;
                } else if (piece == ChessPiece.BLACK_KING) {
                    blackKingExists = true;
                }
            }
        }
        
        return !redKingExists || !blackKingExists;
    }
    
    public boolean isRedTurn() {
        return redTurn;
    }
    
    public void setRedTurn(boolean redTurn) {
        this.redTurn = redTurn;
    }
}