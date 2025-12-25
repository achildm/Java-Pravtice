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
        // 第0行：黑方主力棋子
        board[0][0] = board[0][8] = ChessPiece.BLACK_CHARIOT;  // 车
        board[0][1] = board[0][7] = ChessPiece.BLACK_HORSE;    // 马
        board[0][2] = board[0][6] = ChessPiece.BLACK_ELEPHANT; // 象
        board[0][3] = board[0][5] = ChessPiece.BLACK_ADVISOR;  // 士
        board[0][4] = ChessPiece.BLACK_KING;                   // 将
        
        // 第2行：黑方炮
        board[2][1] = board[2][7] = ChessPiece.BLACK_CANNON;   // 炮
        
        // 第3行：黑方卒子
        for (int i = 0; i < BOARD_WIDTH; i += 2) {
            board[3][i] = ChessPiece.BLACK_SOLDIER;
        }
        
        // 第4-5行：楚河汉界（空白区域）
        // 这两行保持空白，形成楚河汉界
        
        // 第6行：红方兵
        for (int i = 0; i < BOARD_WIDTH; i += 2) {
            board[6][i] = ChessPiece.RED_SOLDIER;
        }
        
        // 第7行：红方炮
        board[7][1] = board[7][7] = ChessPiece.RED_CANNON;     // 炮
        
        // 第9行：红方主力棋子 - 正确顺序：车马相仕帅仕相马车
        board[9][0] = board[9][8] = ChessPiece.RED_CHARIOT;    // 车
        board[9][1] = board[9][7] = ChessPiece.RED_HORSE;      // 马
        board[9][2] = board[9][6] = ChessPiece.RED_ELEPHANT;   // 相
        board[9][3] = board[9][5] = ChessPiece.RED_ADVISOR;    // 仕
        board[9][4] = ChessPiece.RED_KING;                     // 帅
        
        // 调试信息：打印初始棋盘布局
        System.out.println("棋盘初始化完成：");
        System.out.println("黑方卒子位置（第3行）：" + java.util.Arrays.toString(board[3]));
        System.out.println("楚河汉界（第4行）：" + java.util.Arrays.toString(board[4]));
        System.out.println("楚河汉界（第5行）：" + java.util.Arrays.toString(board[5]));
        System.out.println("红方兵位置（第6行）：" + java.util.Arrays.toString(board[6]));
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
     * 撤销移动（用于悔棋功能）
     */
    public void undoMove(int fromX, int fromY, int toX, int toY, ChessPiece capturedPiece) {
        // 将棋子移回原位置
        ChessPiece piece = getPiece(toX, toY);
        setPiece(fromX, fromY, piece);
        
        // 恢复被吃掉的棋子（如果有的话）
        if (capturedPiece != null && !capturedPiece.isEmpty()) {
            setPiece(toX, toY, capturedPiece);
        } else {
            setPiece(toX, toY, ChessPiece.EMPTY);
        }
        
        // 切换回合
        redTurn = !redTurn;
    }
    
    /**
     * 获取棋盘的深拷贝（用于保存状态）
     */
    public ChessBoard copy() {
        ChessBoard copy = new ChessBoard();
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                copy.board[i][j] = this.board[i][j];
            }
        }
        copy.redTurn = this.redTurn;
        return copy;
    }
    
    /**
     * 从另一个棋盘状态恢复当前棋盘
     */
    public void restoreFrom(ChessBoard other) {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                this.board[i][j] = other.board[i][j];
            }
        }
        this.redTurn = other.redTurn;
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
        int palaceMinY, palaceMaxY;
        
        if (isRed) {
            // 红方帅在第7-9行
            palaceMinY = 7;
            palaceMaxY = 9;
        } else {
            // 黑方将在第0-2行
            palaceMinY = 0;
            palaceMaxY = 2;
        }
        
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
        int palaceMinY, palaceMaxY;
        
        if (isRed) {
            // 红方仕在第7-9行
            palaceMinY = 7;
            palaceMaxY = 9;
        } else {
            // 黑方士在第0-2行
            palaceMinY = 0;
            palaceMaxY = 2;
        }
        
        if (toX < palaceMinX || toX > palaceMaxX || toY < palaceMinY || toY > palaceMaxY) {
            return false;
        }
        
        return Math.abs(toX - fromX) == 1 && Math.abs(toY - fromY) == 1;
    }
    
    private boolean isValidElephantMove(int fromX, int fromY, int toX, int toY, boolean isRed) {
        // 相/象不能过河，斜着走两格，不能被蹩脚
        // 红方相不能越过第4行（楚河汉界），黑方象不能越过第5行
        if (isRed) {
            // 红方相只能在第5-9行活动
            if (toY < 5) {
                return false;
            }
        } else {
            // 黑方象只能在第0-4行活动
            if (toY > 4) {
                return false;
            }
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
        
        // 兵/卒每次只能移动一格
        if (Math.abs(dx) + Math.abs(dy) != 1) {
            return false;
        }
        
        // 兵/卒移动规则
        if (isRed) {
            // 红方兵：初始在第6行
            // 楚河汉界：第4-5行
            if (fromY >= 5) {
                // 在红方区域或楚河汉界（第5-9行），只能向前（y减小）
                return dx == 0 && dy == -1;
            } else {
                // 已过河到黑方区域（第0-4行），可以向前或左右，但不能后退
                return (dx == 0 && dy == -1) || (Math.abs(dx) == 1 && dy == 0);
            }
        } else {
            // 黑方卒：初始在第3行
            // 楚河汉界：第4-5行
            if (fromY <= 4) {
                // 在黑方区域或楚河汉界（第0-4行），只能向前（y增大）
                return dx == 0 && dy == 1;
            } else {
                // 已过河到红方区域（第5-9行），可以向前或左右，但不能后退
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
    
    /**
     * 将棋盘状态序列化为字符串
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        // 添加回合信息
        sb.append(redTurn ? "R" : "B").append("|");
        
        // 添加棋盘状态
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                ChessPiece piece = board[y][x];
                sb.append(piece.getId()).append(",");
            }
        }
        return sb.toString();
    }
    
    /**
     * 从字符串反序列化棋盘状态
     */
    public static ChessBoard deserialize(String data) {
        ChessBoard chessBoard = new ChessBoard();
        String[] parts = data.split("\\|");
        
        // 解析回合信息
        chessBoard.redTurn = "R".equals(parts[0]);
        
        // 解析棋盘状态
        String[] pieceIds = parts[1].split(",");
        int index = 0;
        
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (index < pieceIds.length && !pieceIds[index].isEmpty()) {
                    int pieceId = Integer.parseInt(pieceIds[index]);
                    chessBoard.board[y][x] = ChessPiece.fromId(pieceId);
                }
                index++;
            }
        }
        
        return chessBoard;
    }
}