package com.achldm.chess.test;

import com.achldm.chess.game.ChessBoard;
import com.achldm.chess.common.ChessPiece;

/**
 * 兵卒移动规则调试测试
 */
public class SoldierMoveDebugTest {
    public static void main(String[] args) {
        System.out.println("=== 兵卒移动规则调试测试 ===");
        
        ChessBoard board = new ChessBoard();
        
        // 测试红方兵的各种移动情况
        System.out.println("\n红方兵移动测试：");
        testRedSoldierMoves(board);
        
        // 测试黑方卒的各种移动情况
        System.out.println("\n黑方卒移动测试：");
        testBlackSoldierMoves(board);
    }
    
    private static void testRedSoldierMoves(ChessBoard board) {
        // 创建一个新的棋盘用于测试
        ChessBoard testBoard = new ChessBoard();
        
        // 测试红方兵在不同位置的移动
        System.out.println("测试红方兵在第6行（初始位置）：");
        testMove(testBoard, 0, 6, 0, 5, "红方兵从(0,6)到(0,5)");
        testMove(testBoard, 0, 6, 1, 6, "红方兵从(0,6)到(1,6)");
        testMove(testBoard, 0, 6, 0, 7, "红方兵从(0,6)到(0,7)");
        
        // 手动设置红方兵在第5行
        testBoard.setPiece(0, 5, ChessPiece.RED_SOLDIER);
        testBoard.setPiece(0, 6, ChessPiece.EMPTY);
        System.out.println("\n测试红方兵在第5行（楚河汉界边缘）：");
        testMove(testBoard, 0, 5, 0, 4, "红方兵从(0,5)到(0,4)");
        testMove(testBoard, 0, 5, 1, 5, "红方兵从(0,5)到(1,5)");
        testMove(testBoard, 0, 5, 0, 6, "红方兵从(0,5)到(0,6)");
        
        // 手动设置红方兵在第4行
        testBoard.setPiece(0, 4, ChessPiece.RED_SOLDIER);
        testBoard.setPiece(0, 5, ChessPiece.EMPTY);
        System.out.println("\n测试红方兵在第4行（楚河汉界）：");
        testMove(testBoard, 0, 4, 0, 3, "红方兵从(0,4)到(0,3)");
        testMove(testBoard, 0, 4, 1, 4, "红方兵从(0,4)到(1,4)");
        testMove(testBoard, 0, 4, 0, 5, "红方兵从(0,4)到(0,5)");
        
        // 手动设置红方兵在第3行
        testBoard.setPiece(0, 3, ChessPiece.RED_SOLDIER);
        testBoard.setPiece(0, 4, ChessPiece.EMPTY);
        System.out.println("\n测试红方兵在第3行（已过河）：");
        testMove(testBoard, 0, 3, 0, 2, "红方兵从(0,3)到(0,2)");
        testMove(testBoard, 0, 3, 1, 3, "红方兵从(0,3)到(1,3)");
        testMove(testBoard, 0, 3, 0, 4, "红方兵从(0,3)到(0,4)");
    }
    
    private static void testBlackSoldierMoves(ChessBoard board) {
        // 创建一个新的棋盘用于测试
        ChessBoard testBoard = new ChessBoard();
        testBoard.setRedTurn(false); // 切换到黑方回合
        
        // 测试黑方卒在不同位置的移动
        System.out.println("测试黑方卒在第3行（初始位置）：");
        testMove(testBoard, 0, 3, 0, 4, "黑方卒从(0,3)到(0,4)");
        testMove(testBoard, 0, 3, 1, 3, "黑方卒从(0,3)到(1,3)");
        testMove(testBoard, 0, 3, 0, 2, "黑方卒从(0,3)到(0,2)");
        
        // 手动设置黑方卒在第4行
        testBoard.setPiece(0, 4, ChessPiece.BLACK_SOLDIER);
        testBoard.setPiece(0, 3, ChessPiece.EMPTY);
        System.out.println("\n测试黑方卒在第4行（楚河汉界边缘）：");
        testMove(testBoard, 0, 4, 0, 5, "黑方卒从(0,4)到(0,5)");
        testMove(testBoard, 0, 4, 1, 4, "黑方卒从(0,4)到(1,4)");
        testMove(testBoard, 0, 4, 0, 3, "黑方卒从(0,4)到(0,3)");
        
        // 手动设置黑方卒在第5行
        testBoard.setPiece(0, 5, ChessPiece.BLACK_SOLDIER);
        testBoard.setPiece(0, 4, ChessPiece.EMPTY);
        System.out.println("\n测试黑方卒在第5行（楚河汉界）：");
        testMove(testBoard, 0, 5, 0, 6, "黑方卒从(0,5)到(0,6)");
        testMove(testBoard, 0, 5, 1, 5, "黑方卒从(0,5)到(1,5)");
        testMove(testBoard, 0, 5, 0, 4, "黑方卒从(0,5)到(0,4)");
        
        // 手动设置黑方卒在第6行
        testBoard.setPiece(0, 6, ChessPiece.BLACK_SOLDIER);
        testBoard.setPiece(0, 5, ChessPiece.EMPTY);
        System.out.println("\n测试黑方卒在第6行（已过河）：");
        testMove(testBoard, 0, 6, 0, 7, "黑方卒从(0,6)到(0,7)");
        testMove(testBoard, 0, 6, 1, 6, "黑方卒从(0,6)到(1,6)");
        testMove(testBoard, 0, 6, 0, 5, "黑方卒从(0,6)到(0,5)");
    }
    
    private static void testMove(ChessBoard board, int fromX, int fromY, int toX, int toY, String description) {
        boolean isValid = board.isValidMove(fromX, fromY, toX, toY);
        System.out.println(description + "：" + (isValid ? "合法" : "不合法"));
    }
}