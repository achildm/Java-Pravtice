package com.achldm.chess.client.ui;

import com.achldm.chess.client.network.GameClient;
import com.achldm.chess.common.ChessPiece;
import com.achldm.chess.common.GameMessage;
import com.achldm.chess.game.ChessBoard;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

/**
 * 游戏界面
 */
public class GameFrame extends JFrame {
    private static final int BOARD_SIZE = 640;     // 增大棋盘尺寸
    private static final int CELL_SIZE = 64;       // 调整格子大小
    private static final int PIECE_SIZE = 48;      // 棋子大小，比格子小一些
    private static final int BOARD_OFFSET_X = 32;  // 调整偏移量
    private static final int BOARD_OFFSET_Y = 32;  // 调整偏移量
    
    private GameClient client;
    private ChessBoard chessBoard;
    private boolean isRed;
    private boolean isMyTurn;
    private boolean isFlipped; // 是否翻转视角
    
    // 用户信息
    private String username;
    private ImageIcon userAvatar;
    private int avatarIndex;
    
    private ChessBoardPanel boardPanel;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private Timer gameTimer;
    private int timeLeft = 600; // 10分钟
    
    private int selectedX = -1, selectedY = -1;
    private Image boardImage;
    private Image[] pieceImages;
    private Image selectImage;
    
    public GameFrame(GameClient client, boolean isRed, String username, ImageIcon avatar, int avatarIndex) {
        this.client = client;
        this.isRed = isRed;
        this.isMyTurn = isRed; // 红方先行
        this.isFlipped = !isRed; // 黑方翻转视角
        this.chessBoard = new ChessBoard();
        this.username = username;
        this.userAvatar = avatar;
        this.avatarIndex = avatarIndex;
        
        client.setGameFrame(this);
        
        loadImages();
        initComponents();
        setupLayout();
        setupEventHandlers();
        startTimer();
    }
    
    private void loadImages() {
        try {
            // 加载棋盘图片
            URL boardUrl = getClass().getResource("/qizi/xqboard.gif");
            if (boardUrl != null) {
                boardImage = new ImageIcon(boardUrl).getImage();
            }
            
            // 加载选中框图片
            URL selectUrl = getClass().getResource("/qizi/select.gif");
            if (selectUrl != null) {
                selectImage = new ImageIcon(selectUrl).getImage();
            }
            
            // 初始化棋子图片数组
            pieceImages = new Image[33];
            
            // 加载棋子图片 - 使用中文文件名
            loadPieceImage(ChessPiece.RED_KING, "/qizi/红帅.gif");
            loadPieceImage(ChessPiece.RED_ADVISOR, "/qizi/红士1.gif");
            loadPieceImage(ChessPiece.RED_ELEPHANT, "/qizi/红象1.gif");
            loadPieceImage(ChessPiece.RED_HORSE, "/qizi/红马1.gif");
            loadPieceImage(ChessPiece.RED_CHARIOT, "/qizi/红车1.gif");
            loadPieceImage(ChessPiece.RED_CANNON, "/qizi/红炮1.gif");
            loadPieceImage(ChessPiece.RED_SOLDIER, "/qizi/红兵1.gif");
            
            loadPieceImage(ChessPiece.BLACK_KING, "/qizi/黑将.gif");
            loadPieceImage(ChessPiece.BLACK_ADVISOR, "/qizi/黑士1.gif");
            loadPieceImage(ChessPiece.BLACK_ELEPHANT, "/qizi/黑象1.gif");
            loadPieceImage(ChessPiece.BLACK_HORSE, "/qizi/黑马1.gif");
            loadPieceImage(ChessPiece.BLACK_CHARIOT, "/qizi/黑车1.gif");
            loadPieceImage(ChessPiece.BLACK_CANNON, "/qizi/黑炮1.gif");
            loadPieceImage(ChessPiece.BLACK_SOLDIER, "/qizi/黑兵1.gif");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadPieceImage(ChessPiece piece, String path) {
        try {
            URL pieceUrl = getClass().getResource(path);
            if (pieceUrl != null) {
                pieceImages[piece.getId()] = new ImageIcon(pieceUrl).getImage();
                System.out.println("加载棋子图片: " + piece.getName() + " -> " + path);
            } else {
                System.err.println("找不到棋子图片: " + path);
            }
        } catch (Exception e) {
            System.err.println("加载棋子图片失败: " + path + " - " + e.getMessage());
        }
    }
    
    private void initComponents() {
        setTitle("象棋游戏 - " + (isRed ? "红方" : "黑方") + " - " + (username != null ? username : "玩家"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        boardPanel = new ChessBoardPanel();
        boardPanel.setPreferredSize(new Dimension(BOARD_SIZE + 80, BOARD_SIZE + PIECE_SIZE + 80)); // 增加背景图高度和边距
        
        statusLabel = new JLabel("游戏开始 - " + (isMyTurn ? "轮到你了" : "等待对手"), JLabel.CENTER);
        statusLabel.setFont(new Font("宋体", Font.BOLD, 16));
        
        timeLabel = new JLabel("剩余时间: 10:00", JLabel.CENTER);
        timeLabel.setFont(new Font("宋体", Font.PLAIN, 14));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部信息面板
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(statusLabel);
        topPanel.add(timeLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupEventHandlers() {
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isMyTurn) return;
                
                // 调整点击坐标计算，考虑棋子的偏移
                // 减去偏移量，然后根据视角调整
                int x, y;
                
                if (isFlipped) {
                    // 黑方视角：向右偏移一格
                    x = (e.getX() - BOARD_OFFSET_X - CELL_SIZE + (CELL_SIZE / 2)) / CELL_SIZE;
                    y = (e.getY() - BOARD_OFFSET_Y + (CELL_SIZE / 2)) / CELL_SIZE;
                } else {
                    // 红方视角：向右偏移一格，向下偏移一格
                    x = (e.getX() - BOARD_OFFSET_X - CELL_SIZE + (CELL_SIZE / 2)) / CELL_SIZE;
                    y = (e.getY() - BOARD_OFFSET_Y - CELL_SIZE + (CELL_SIZE / 2)) / CELL_SIZE;
                }
                
                if (x >= 0 && x < ChessBoard.BOARD_WIDTH && y >= 0 && y < ChessBoard.BOARD_HEIGHT) {
                    handleBoardClick(x, y);
                }
            }
        });
    }
    
    private void handleBoardClick(int x, int y) {
        // 如果视角翻转，需要转换坐标
        int actualX = x;
        int actualY = y;
        if (isFlipped) {
            actualX = ChessBoard.BOARD_WIDTH - 1 - x;
            actualY = ChessBoard.BOARD_HEIGHT - 1 - y;
        }
        
        ChessPiece piece = chessBoard.getPiece(actualX, actualY);
        
        if (selectedX == -1 && selectedY == -1) {
            // 选择棋子
            if (!piece.isEmpty() && piece.isRed() == isRed) {
                selectedX = actualX;
                selectedY = actualY;
                boardPanel.repaint();
                playSound("select");
            }
        } else {
            // 移动棋子
            if (actualX == selectedX && actualY == selectedY) {
                // 取消选择
                selectedX = selectedY = -1;
                boardPanel.repaint();
            } else if (chessBoard.isValidMove(selectedX, selectedY, actualX, actualY)) {
                // 发送移动消息
                GameMessage moveMsg = new GameMessage(GameMessage.MessageType.MOVE);
                moveMsg.setFromX(selectedX);
                moveMsg.setFromY(selectedY);
                moveMsg.setToX(actualX);
                moveMsg.setToY(actualY);
                client.sendMessage(moveMsg);
                
                // 本地移动
                chessBoard.movePiece(selectedX, selectedY, actualX, actualY);
                selectedX = selectedY = -1;
                isMyTurn = false;
                
                updateStatus();
                boardPanel.repaint();
                playSound("go");
                
                // 检查游戏是否结束
                if (chessBoard.isGameOver()) {
                    onGameOver(isRed);
                }
            } else {
                // 重新选择
                if (!piece.isEmpty() && piece.isRed() == isRed) {
                    selectedX = actualX;
                    selectedY = actualY;
                    boardPanel.repaint();
                }
            }
        }
    }
    
    private void startTimer() {
        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            updateTimeDisplay();
            
            if (timeLeft <= 0) {
                gameTimer.stop();
                onGameOver(!isRed); // 超时则对方获胜
            }
        });
        gameTimer.start();
    }
    
    private void updateTimeDisplay() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timeLabel.setText(String.format("剩余时间: %d:%02d", minutes, seconds));
    }
    
    private void updateStatus() {
        String status = isMyTurn ? "轮到你了" : "等待对手";
        statusLabel.setText("游戏进行中 - " + status);
    }
    
    /**
     * 接收对手移动
     */
    public void onOpponentMove(int fromX, int fromY, int toX, int toY) {
        SwingUtilities.invokeLater(() -> {
            chessBoard.movePiece(fromX, fromY, toX, toY);
            isMyTurn = true;
            updateStatus();
            boardPanel.repaint();
            playSound("go");
            
            // 检查游戏是否结束
            if (chessBoard.isGameOver()) {
                onGameOver(!isRed);
            }
        });
    }
    
    /**
     * 游戏结束
     */
    public void onGameOver(boolean redWin) {
        SwingUtilities.invokeLater(() -> {
            gameTimer.stop();
            
            String message;
            if ((redWin && isRed) || (!redWin && !isRed)) {
                message = "恭喜你获胜！";
                playSound("jiang");
            } else {
                message = "很遗憾，你败了！";
            }
            
            statusLabel.setText("游戏结束 - " + message);
            
            int option = JOptionPane.showConfirmDialog(this, 
                message + "\n是否返回大厅？", 
                "游戏结束", 
                JOptionPane.YES_NO_OPTION);
                
            if (option == JOptionPane.YES_OPTION) {
                LobbyFrame lobbyFrame = new LobbyFrame(client, username, userAvatar, avatarIndex);
                lobbyFrame.setVisible(true);
                dispose();
            }
        });
    }
    
    private void playSound(String soundName) {
        try {
            URL soundUrl = getClass().getResource("/audio/" + soundName + ".wav");
            if (soundUrl != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundUrl);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            }
        } catch (Exception e) {
            // 忽略音效播放错误
        }
    }
    
    /**
     * 棋盘绘制面板
     */
    private class ChessBoardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制棋盘背景
            if (boardImage != null) {
                // 增加背景图高度，大概一个棋子的高度
                int boardHeight = BOARD_SIZE + PIECE_SIZE;
                
                // 黑方视角下，背景图位置往上移动一格（一个棋子的高度）
                int backgroundY = BOARD_OFFSET_Y;
                if (isFlipped) {
                    backgroundY = BOARD_OFFSET_Y - PIECE_SIZE;
                }
                
                g2d.drawImage(boardImage, BOARD_OFFSET_X, backgroundY, BOARD_SIZE, boardHeight, this);
            } else {
                // 如果没有背景图，绘制简单的棋盘
                drawSimpleBoard(g2d);
            }
            
            // 绘制选中框
            if (selectedX >= 0 && selectedY >= 0) {
                int displayX = selectedX;
                int displayY = selectedY;
                
                // 如果视角翻转，转换显示坐标
                if (isFlipped) {
                    displayX = ChessBoard.BOARD_WIDTH - 1 - selectedX;
                    displayY = ChessBoard.BOARD_HEIGHT - 1 - selectedY;
                }
                
                // 计算选中框绘制位置，与棋子位置保持一致
                int drawX, drawY;
                
                if (isFlipped) {
                    // 黑方视角：向右移动一格
                    drawX = BOARD_OFFSET_X + (displayX + 1) * CELL_SIZE - (PIECE_SIZE / 2);
                    drawY = BOARD_OFFSET_Y + displayY * CELL_SIZE - (PIECE_SIZE / 2);
                } else {
                    // 红方视角：向右移动一格，向下移动一格
                    drawX = BOARD_OFFSET_X + (displayX + 1) * CELL_SIZE - (PIECE_SIZE / 2);
                    drawY = BOARD_OFFSET_Y + (displayY + 1) * CELL_SIZE - (PIECE_SIZE / 2);
                }
                
                if (selectImage != null) {
                    g2d.drawImage(selectImage, drawX, drawY, PIECE_SIZE, PIECE_SIZE, this);
                } else {
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRect(drawX, drawY, PIECE_SIZE, PIECE_SIZE);
                }
            }
            
            // 绘制棋子
            for (int y = 0; y < ChessBoard.BOARD_HEIGHT; y++) {
                for (int x = 0; x < ChessBoard.BOARD_WIDTH; x++) {
                    ChessPiece piece = chessBoard.getPiece(x, y);
                    if (!piece.isEmpty()) {
                        drawPiece(g2d, piece, x, y);
                    }
                }
            }
        }
        
        private void drawSimpleBoard(Graphics2D g2d) {
            g2d.setColor(new Color(255, 206, 84));
            g2d.fillRect(BOARD_OFFSET_X, BOARD_OFFSET_Y, BOARD_SIZE, BOARD_SIZE);
            
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            
            // 绘制网格线
            for (int i = 0; i < ChessBoard.BOARD_WIDTH; i++) {
                int x = BOARD_OFFSET_X + i * CELL_SIZE;
                g2d.drawLine(x, BOARD_OFFSET_Y, x, BOARD_OFFSET_Y + (ChessBoard.BOARD_HEIGHT - 1) * CELL_SIZE);
            }
            
            for (int i = 0; i < ChessBoard.BOARD_HEIGHT; i++) {
                int y = BOARD_OFFSET_Y + i * CELL_SIZE;
                g2d.drawLine(BOARD_OFFSET_X, y, BOARD_OFFSET_X + (ChessBoard.BOARD_WIDTH - 1) * CELL_SIZE, y);
            }
            
            // 绘制九宫格斜线
            g2d.setStroke(new BasicStroke(1));
            
            // 红方九宫格
            int redPalaceX = BOARD_OFFSET_X + 3 * CELL_SIZE;
            int redPalaceY = BOARD_OFFSET_Y + 7 * CELL_SIZE;
            g2d.drawLine(redPalaceX, redPalaceY, redPalaceX + 2 * CELL_SIZE, redPalaceY + 2 * CELL_SIZE);
            g2d.drawLine(redPalaceX + 2 * CELL_SIZE, redPalaceY, redPalaceX, redPalaceY + 2 * CELL_SIZE);
            
            // 黑方九宫格
            int blackPalaceX = BOARD_OFFSET_X + 3 * CELL_SIZE;
            int blackPalaceY = BOARD_OFFSET_Y;
            g2d.drawLine(blackPalaceX, blackPalaceY, blackPalaceX + 2 * CELL_SIZE, blackPalaceY + 2 * CELL_SIZE);
            g2d.drawLine(blackPalaceX + 2 * CELL_SIZE, blackPalaceY, blackPalaceX, blackPalaceY + 2 * CELL_SIZE);
        }
        
        private void drawPiece(Graphics2D g2d, ChessPiece piece, int x, int y) {
            int displayX = x;
            int displayY = y;
            
            // 如果视角翻转，转换显示坐标
            if (isFlipped) {
                displayX = ChessBoard.BOARD_WIDTH - 1 - x;
                displayY = ChessBoard.BOARD_HEIGHT - 1 - y;
            }
            
            // 计算棋子绘制位置，根据视角调整偏移
            int drawX, drawY;
            
            if (isFlipped) {
                // 黑方视角：向右移动一格
                drawX = BOARD_OFFSET_X + (displayX + 1) * CELL_SIZE - (PIECE_SIZE / 2);
                drawY = BOARD_OFFSET_Y + displayY * CELL_SIZE - (PIECE_SIZE / 2);
            } else {
                // 红方视角：向右移动一格，向下移动一格
                drawX = BOARD_OFFSET_X + (displayX + 1) * CELL_SIZE - (PIECE_SIZE / 2);
                drawY = BOARD_OFFSET_Y + (displayY + 1) * CELL_SIZE - (PIECE_SIZE / 2);
            }
            
            if (pieceImages != null && piece.getId() < pieceImages.length && pieceImages[piece.getId()] != null) {
                g2d.drawImage(pieceImages[piece.getId()], drawX, drawY, PIECE_SIZE, PIECE_SIZE, this);
            } else {
                // 如果没有图片，绘制文字
                g2d.setColor(piece.isRed() ? Color.RED : Color.BLACK);
                g2d.fillOval(drawX + 2, drawY + 2, PIECE_SIZE - 4, PIECE_SIZE - 4);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("宋体", Font.BOLD, 18));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = drawX + (PIECE_SIZE - fm.stringWidth(piece.getName())) / 2;
                int textY = drawY + (PIECE_SIZE + fm.getAscent()) / 2;
                g2d.drawString(piece.getName(), textX, textY);
            }
        }
    }
}