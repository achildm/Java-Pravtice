package com.achldm.chess.server;

import com.achldm.chess.common.GameMessage;
import com.achldm.chess.common.ChessPiece;
import com.achldm.chess.game.ChessBoard;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏房间类
 */
public class GameRoom {
    private String roomId;
    private ClientHandler redPlayer;
    private ClientHandler blackPlayer;
    private ChessBoard chessBoard;
    private boolean gameStarted = false;
    
    // 游戏历史记录，用于悔棋功能
    private List<GameMove> moveHistory = new ArrayList<>();
    private List<ChessBoard> boardHistory = new ArrayList<>(); // 保存棋盘状态历史
    
    // 内部类：游戏移动记录
    private static class GameMove {
        int fromX, fromY, toX, toY;
        ChessPiece capturedPiece; // 被吃掉的棋子（如果有的话）
        boolean wasRedTurn; // 移动时是否是红方回合
        
        GameMove(int fromX, int fromY, int toX, int toY, ChessPiece capturedPiece, boolean wasRedTurn) {
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
            this.capturedPiece = capturedPiece;
            this.wasRedTurn = wasRedTurn;
        }
    }
    
    public GameRoom(String roomId, ClientHandler player1, ClientHandler player2) {
        this.roomId = roomId;
        this.redPlayer = player1;  // 第一个玩家是红方
        this.blackPlayer = player2; // 第二个玩家是黑方
        this.chessBoard = new ChessBoard();
        
        // 保存初始棋盘状态
        boardHistory.add(chessBoard.copy());
        
        startGame();
    }
    
    /**
     * 开始游戏
     */
    private void startGame() {
        gameStarted = true;
        
        GameMessage startMsg = new GameMessage(GameMessage.MessageType.GAME_START);
        redPlayer.sendMessage(startMsg);
        blackPlayer.sendMessage(startMsg);
        
        System.out.println("游戏开始 - 房间: " + roomId);
    }
    
    /**
     * 处理棋子移动
     */
    public void handleMove(GameMessage moveMessage, ClientHandler sender) {
        if (!gameStarted) return;
        
        // 验证是否轮到该玩家
        boolean isRedTurn = chessBoard.isRedTurn();
        boolean senderIsRed = (sender == redPlayer);
        
        if (isRedTurn != senderIsRed) {
            // 不是该玩家的回合
            return;
        }
        
        // 验证移动是否合法
        int fromX = moveMessage.getFromX();
        int fromY = moveMessage.getFromY();
        int toX = moveMessage.getToX();
        int toY = moveMessage.getToY();
        
        if (chessBoard.isValidMove(fromX, fromY, toX, toY)) {
            // 记录被吃掉的棋子（如果有的话）
            ChessPiece capturedPiece = chessBoard.getPiece(toX, toY);
            boolean wasRedTurn = chessBoard.isRedTurn();
            
            // 执行移动
            chessBoard.movePiece(fromX, fromY, toX, toY);
            
            // 保存移动后的棋盘状态（用于悔棋恢复）
            ChessBoard afterMove = chessBoard.copy();
            
            // 记录移动历史
            moveHistory.add(new GameMove(fromX, fromY, toX, toY, capturedPiece, wasRedTurn));
            boardHistory.add(afterMove);
            
            // 转发移动消息给对手
            ClientHandler opponent = senderIsRed ? blackPlayer : redPlayer;
            opponent.sendMessage(moveMessage);
            
            // 检查游戏是否结束
            if (chessBoard.isGameOver()) {
                handleGameOver(senderIsRed);
            }
        }
    }
    
    /**
     * 处理游戏结束
     */
    private void handleGameOver(boolean redWin) {
        gameStarted = false;
        
        GameMessage gameOverMsg = new GameMessage(GameMessage.MessageType.GAME_OVER);
        gameOverMsg.setRed(redWin);
        
        redPlayer.sendMessage(gameOverMsg);
        blackPlayer.sendMessage(gameOverMsg);
        
        System.out.println("游戏结束 - 房间: " + roomId + ", 获胜方: " + (redWin ? "红方" : "黑方"));
    }
    
    /**
     * 处理玩家断开连接
     */
    public void handlePlayerDisconnect(ClientHandler disconnectedPlayer) {
        if (!gameStarted) return;
        
        gameStarted = false;
        
        // 通知另一个玩家对手已断开连接
        ClientHandler otherPlayer = (disconnectedPlayer == redPlayer) ? blackPlayer : redPlayer;
        if (otherPlayer != null) {
            GameMessage disconnectMsg = new GameMessage(GameMessage.MessageType.DISCONNECT);
            disconnectMsg.setContent("对手已断开连接");
            otherPlayer.sendMessage(disconnectMsg);
        }
        
        System.out.println("玩家断开连接 - 房间: " + roomId);
    }
    
    /**
     * 检查房间是否包含指定玩家
     */
    public boolean containsPlayer(ClientHandler player) {
        return player == redPlayer || player == blackPlayer;
    }
    
    /**
     * 处理聊天消息
     */
    public void handleChatMessage(GameMessage chatMessage, ClientHandler sender) {
        if (!gameStarted) return;
        
        // 转发聊天消息给对手
        ClientHandler opponent = (sender == redPlayer) ? blackPlayer : redPlayer;
        if (opponent != null) {
            opponent.sendMessage(chatMessage);
        }
    }
    
    /**
     * 处理悔棋请求
     */
    public void handleUndoRequest(ClientHandler sender) {
        if (!gameStarted || moveHistory.isEmpty()) {
            // 游戏未开始或没有可悔棋的步数
            GameMessage response = new GameMessage(GameMessage.MessageType.UNDO_RESPONSE);
            response.setAccepted(false);
            response.setReason("当前无法悔棋");
            sender.sendMessage(response);
            return;
        }
        
        // 检查是否是刚下完棋的一方请求悔棋
        GameMove lastMove = moveHistory.get(moveHistory.size() - 1);
        boolean senderIsRed = (sender == redPlayer);
        
        // 只有刚下完棋的一方才能请求悔棋
        if (lastMove.wasRedTurn != senderIsRed) {
            GameMessage response = new GameMessage(GameMessage.MessageType.UNDO_RESPONSE);
            response.setAccepted(false);
            response.setReason("只有刚下完棋的一方才能请求悔棋");
            sender.sendMessage(response);
            return;
        }
        
        // 转发悔棋请求给对手
        ClientHandler opponent = (sender == redPlayer) ? blackPlayer : redPlayer;
        if (opponent != null) {
            GameMessage undoRequest = new GameMessage(GameMessage.MessageType.UNDO_REQUEST);
            undoRequest.setUsername(sender.getUsername());
            opponent.sendMessage(undoRequest);
        }
    }
    
    /**
     * 处理悔棋回应
     */
    public void handleUndoResponse(GameMessage response, ClientHandler sender) {
        if (!gameStarted) return;
        
        ClientHandler opponent = (sender == redPlayer) ? blackPlayer : redPlayer;
        if (opponent == null) return;
        
        if (response.isAccepted() && !moveHistory.isEmpty() && boardHistory.size() > 1) {
            // 同意悔棋，撤销最后一步
            GameMove lastMove = moveHistory.remove(moveHistory.size() - 1);
            boardHistory.remove(boardHistory.size() - 1);
            
            // 恢复到悔棋前的状态
            // boardHistory现在的最后一个状态就是悔棋请求方移动前的状态
            ChessBoard previousState = boardHistory.get(boardHistory.size() - 1);
            chessBoard.restoreFrom(previousState);
            
            System.out.println("悔棋详情 - 撤销移动: (" + lastMove.fromX + "," + lastMove.fromY + 
                             ") -> (" + lastMove.toX + "," + lastMove.toY + ")");
            System.out.println("悔棋后轮到: " + (chessBoard.isRedTurn() ? "红方" : "黑方"));
            
            // 发送棋盘刷新消息给双方
            GameMessage undoRefreshMsg = new GameMessage(GameMessage.MessageType.UNDO_REFRESH);
            undoRefreshMsg.setRed(chessBoard.isRedTurn()); // 告诉客户端现在轮到谁
            undoRefreshMsg.setBoardState(chessBoard.serialize()); // 发送完整棋盘状态
            
            redPlayer.sendMessage(undoRefreshMsg);
            blackPlayer.sendMessage(undoRefreshMsg);
            
            // 通知双方悔棋成功
            GameMessage undoSuccess = new GameMessage(GameMessage.MessageType.UNDO_RESPONSE);
            undoSuccess.setAccepted(true);
            undoSuccess.setContent("悔棋成功");
            
            redPlayer.sendMessage(undoSuccess);
            blackPlayer.sendMessage(undoSuccess);
            
            System.out.println("悔棋成功 - 房间: " + roomId + ", 恢复到之前的状态");
        } else {
            // 拒绝悔棋
            GameMessage undoRejected = new GameMessage(GameMessage.MessageType.UNDO_RESPONSE);
            undoRejected.setAccepted(false);
            undoRejected.setReason(response.getReason() != null ? response.getReason() : "对手拒绝了悔棋请求");
            opponent.sendMessage(undoRejected);
        }
    }
    
    /**
     * 处理求和请求
     */
    public void handleDrawRequest(ClientHandler sender) {
        if (!gameStarted) return;
        
        // 转发求和请求给对手
        ClientHandler opponent = (sender == redPlayer) ? blackPlayer : redPlayer;
        if (opponent != null) {
            GameMessage drawRequest = new GameMessage(GameMessage.MessageType.DRAW_REQUEST);
            drawRequest.setUsername(sender.getUsername());
            opponent.sendMessage(drawRequest);
        }
    }
    
    /**
     * 处理求和回应
     */
    public void handleDrawResponse(GameMessage response, ClientHandler sender) {
        if (!gameStarted) return;
        
        ClientHandler opponent = (sender == redPlayer) ? blackPlayer : redPlayer;
        if (opponent == null) return;
        
        if (response.isAccepted()) {
            // 同意求和，游戏结束
            gameStarted = false;
            
            GameMessage drawAccepted = new GameMessage(GameMessage.MessageType.DRAW_RESPONSE);
            drawAccepted.setAccepted(true);
            drawAccepted.setContent("双方同意求和");
            
            redPlayer.sendMessage(drawAccepted);
            blackPlayer.sendMessage(drawAccepted);
            
            System.out.println("游戏求和结束 - 房间: " + roomId);
        } else {
            // 拒绝求和
            GameMessage drawRejected = new GameMessage(GameMessage.MessageType.DRAW_RESPONSE);
            drawRejected.setAccepted(false);
            drawRejected.setReason(response.getReason() != null ? response.getReason() : "对手拒绝了求和请求");
            opponent.sendMessage(drawRejected);
        }
    }
    
    /**
     * 处理认输
     */
    public void handleSurrender(ClientHandler sender) {
        if (!gameStarted) return;
        
        gameStarted = false;
        boolean senderIsRed = (sender == redPlayer);
        
        // 通知双方认输结果
        GameMessage surrenderMsg = new GameMessage(GameMessage.MessageType.SURRENDER);
        surrenderMsg.setUsername(sender.getUsername());
        surrenderMsg.setRed(!senderIsRed); // 对方获胜
        
        redPlayer.sendMessage(surrenderMsg);
        blackPlayer.sendMessage(surrenderMsg);
        
        System.out.println("玩家认输 - 房间: " + roomId + ", 认输方: " + (senderIsRed ? "红方" : "黑方"));
    }
    
    /**
     * 从历史记录重建棋盘状态（备用方法）
     */
    private void rebuildBoardFromHistory() {
        // 重新初始化棋盘
        chessBoard = new ChessBoard();
        
        // 重新执行所有历史移动
        for (GameMove move : moveHistory) {
            chessBoard.movePiece(move.fromX, move.fromY, move.toX, move.toY);
        }
    }
    
    // Getters
    public String getRoomId() {
        return roomId;
    }
    
    public ClientHandler getRedPlayer() {
        return redPlayer;
    }
    
    public ClientHandler getBlackPlayer() {
        return blackPlayer;
    }
    
    public boolean isGameStarted() {
        return gameStarted;
    }
}