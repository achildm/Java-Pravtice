package com.achldm.chess.test;

import com.achldm.chess.game.ChessBoard;
import com.achldm.chess.common.ChessPiece;

/**
 * 棋盘测试类
 */
public class ChessBoardTest {
    public static void main(String[] args) {
        System.out.println("=== 象棋棋盘兵卒移动规则测试 ===");
        
        ChessBoard board = new ChessBoard();
        
        // 测试1：打印初始棋盘状态
        System.out.println("\n1. 初始棋盘状态：");
        printBoard(board);
        
        // 测试2：红方兵移动测试
        System.out.println("\n2. 红方兵移动测试：");
        testRedSoldierMove(board);
        
        // 测试3：黑方卒移动测试
        System.out.println("\n3. 黑方卒移动测试：");
        testBlackSoldierMove(board);
        
        // 测试4：兵卒对战测试
        System.out.println("\n4. 兵卒对战测试：");
        testSoldierBattle(board);
    }
    
    private static void printBoard(ChessBoard board) {
        System.out.println("   0 1 2 3 4 5 6 7 8");
        for (int y = 0; y < 10; y++) {
            System.out.print(y + ": ");
            for (int x = 0; x < 9; x++) {
                ChessPiece piece = board.getPiece(x, y);
                if (piece.isEmpty()) {
                    System.out.print("· ");
                } else {
                    System.out.print(piece.getName() + " ");
                }
            }
            System.out.println();
        }
    }
    
    private static void testRedSoldierMove(ChessBoard board) {
        // 创建新棋盘进行红方测试
        ChessBoard testBoard = new ChessBoard();
        
        // 红方兵初始在第6行
        System.out.println("红方兵初始位置：第6行");
        
        // 测试红方兵向前移动一步（从第6行到第5行）
        boolean canMove1 = testBoard.isValidMove(0, 6, 0, 5);
        System.out.println("红方兵从(0,6)移动到(0,5)：" + (canMove1 ? "合法" : "不合法"));
        
        // 测试红方兵直接跳到第4行（楚河汉界）
        boolean canMove2 = testBoard.isValidMove(0, 6, 0, 4);
        System.out.println("红方兵从(0,6)直接移动到(0,4)：" + (canMove2 ? "合法" : "不合法"));
        
        // 手动设置红方兵在第5行进行测试
        ChessBoard testBoard2 = new ChessBoard();
        testBoard2.setPiece(0, 5, ChessPiece.RED_SOLDIER);
        testBoard2.setPiece(0, 6, ChessPiece.EMPTY);
        testBoard2.setRedTurn(true); // 确保是红方回合
        
        System.out.println("红方兵移动到(0,5)");
        
        // 测试从第5行继续向前移动到第4行
        boolean canMove3 = testBoard2.isValidMove(0, 5, 0, 4);
        System.out.println("红方兵从(0,5)移动到(0,4)：" + (canMove3 ? "合法" : "不合法"));
        
        if (canMove3) {
            // 手动设置红方兵在第4行进行测试
            ChessBoard testBoard3 = new ChessBoard();
            testBoard3.setPiece(0, 4, ChessPiece.RED_SOLDIER);
            testBoard3.setRedTurn(true);
            
            System.out.println("红方兵移动到(0,4) - 已过河");
            
            // 测试过河后的移动
            boolean canMove4 = testBoard3.isValidMove(0, 4, 0, 3);
            System.out.println("红方兵从(0,4)移动到(0,3)：" + (canMove4 ? "合法" : "不合法"));
            
            boolean canMove5 = testBoard3.isValidMove(0, 4, 1, 4);
            System.out.println("红方兵从(0,4)移动到(1,4)：" + (canMove5 ? "合法" : "不合法"));
        }
    }
    
    private static void testBlackSoldierMove(ChessBoard board) {
        // 创建新棋盘进行黑方测试
        ChessBoard testBoard = new ChessBoard();
        testBoard.setRedTurn(false); // 切换到黑方回合
        
        // 黑方卒初始在第3行
        System.out.println("黑方卒初始位置：第3行");
        
        // 测试黑方卒向前移动一步（从第3行到第4行）
        boolean canMove1 = testBoard.isValidMove(0, 3, 0, 4);
        System.out.println("黑方卒从(0,3)移动到(0,4)：" + (canMove1 ? "合法" : "不合法"));
        
        // 测试黑方卒直接跳到第5行（楚河汉界）
        boolean canMove2 = testBoard.isValidMove(0, 3, 0, 5);
        System.out.println("黑方卒从(0,3)直接移动到(0,5)：" + (canMove2 ? "合法" : "不合法"));
        
        // 手动设置黑方卒在第4行进行测试
        ChessBoard testBoard2 = new ChessBoard();
        testBoard2.setPiece(0, 4, ChessPiece.BLACK_SOLDIER);
        testBoard2.setPiece(0, 3, ChessPiece.EMPTY);
        testBoard2.setRedTurn(false); // 确保是黑方回合
        
        System.out.println("黑方卒移动到(0,4)");
        
        // 测试从第4行继续向前移动到第5行
        boolean canMove3 = testBoard2.isValidMove(0, 4, 0, 5);
        System.out.println("黑方卒从(0,4)移动到(0,5)：" + (canMove3 ? "合法" : "不合法"));
        
        if (canMove3) {
            // 手动设置黑方卒在第5行进行测试
            ChessBoard testBoard3 = new ChessBoard();
            testBoard3.setPiece(0, 5, ChessPiece.BLACK_SOLDIER);
            testBoard3.setRedTurn(false);
            
            System.out.println("黑方卒移动到(0,5) - 已过河");
            
            // 测试过河后的移动
            boolean canMove4 = testBoard3.isValidMove(0, 5, 0, 6);
            System.out.println("黑方卒从(0,5)移动到(0,6)：" + (canMove4 ? "合法" : "不合法"));
            
            boolean canMove5 = testBoard3.isValidMove(0, 5, 1, 5);
            System.out.println("黑方卒从(0,5)移动到(1,5)：" + (canMove5 ? "合法" : "不合法"));
        }
    }
    
    private static void testSoldierBattle(ChessBoard board) {
        // 创建新棋盘进行测试
        ChessBoard testBoard = new ChessBoard();
        
        // 手动设置兵卒位置进行测试
        testBoard.setPiece(4, 5, ChessPiece.RED_SOLDIER);   // 红方兵在第5行
        testBoard.setPiece(4, 4, ChessPiece.BLACK_SOLDIER); // 黑方卒在第4行
        
        System.out.println("设置测试场景：红方兵在(4,5)，黑方卒在(4,4)");
        
        // 测试红方兵能否直接吃黑方卒（红方回合）
        testBoard.setRedTurn(true);
        boolean canEat1 = testBoard.isValidMove(4, 5, 4, 4);
        System.out.println("红方兵从(4,5)吃黑方卒(4,4)：" + (canEat1 ? "可以" : "不可以"));
        
        // 测试黑方卒能否直接吃红方兵（黑方回合）
        testBoard.setRedTurn(false);
        boolean canEat2 = testBoard.isValidMove(4, 4, 4, 5);
        System.out.println("黑方卒从(4,4)吃红方兵(4,5)：" + (canEat2 ? "可以" : "不可以"));
        
        System.out.println("结论：兵卒之间隔着楚河汉界，需要各自再移动一步才能相遇");
    }
}