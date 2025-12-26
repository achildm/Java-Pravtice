package com.achldm.chess.client.ui;

import com.achldm.chess.client.network.GameClient;
import com.achldm.chess.common.GameMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 游戏控制面板组件
 */
public class GameControlPanel extends JPanel {
    private JButton undoButton;
    private JButton drawButton;
    private JButton surrenderButton;
    private GameClient client;
    private String username;
    private ChatPanel chatPanel;
    
    public GameControlPanel(GameClient client, String username, ChatPanel chatPanel) {
        this.client = client;
        this.username = username;
        this.chatPanel = chatPanel;
        
        initComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initComponents() {
        undoButton = new JButton("悔棋");
        undoButton.setFont(new Font("宋体", Font.PLAIN, 12));
        undoButton.setToolTipText("请求悔棋");
        
        drawButton = new JButton("求和");
        drawButton.setFont(new Font("宋体", Font.PLAIN, 12));
        drawButton.setToolTipText("请求求和");
        
        surrenderButton = new JButton("认输");
        surrenderButton.setFont(new Font("宋体", Font.PLAIN, 12));
        surrenderButton.setBackground(new Color(255, 100, 100));
        surrenderButton.setToolTipText("认输投降");
    }
    
    private void setupLayout() {
        setLayout(new GridLayout(3, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder("游戏操作"));
        
        add(undoButton);
        add(drawButton);
        add(surrenderButton);
    }
    
    private void setupEventHandlers() {
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestUndo();
            }
        });
        
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestDraw();
            }
        });
        
        surrenderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                surrender();
            }
        });
    }
    
    /**
     * 请求悔棋
     */
    private void requestUndo() {
        // 防止重复点击
        if (!undoButton.isEnabled()) {
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "确定要请求悔棋吗？", 
            "悔棋请求", 
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            // 立即禁用按钮，防止重复点击
            undoButton.setEnabled(false);
            undoButton.setText("悔棋中...");
            
            // 在后台线程发送消息，避免阻塞UI
            SwingUtilities.invokeLater(() -> {
                try {
                    GameMessage undoMsg = new GameMessage(GameMessage.MessageType.UNDO_REQUEST);
                    client.sendMessage(undoMsg);
                    chatPanel.appendSystemMessage("已发送悔棋请求，等待对手回应...");
                    
                    // 设置超时恢复按钮状态（防止网络问题导致按钮永久禁用）
                    Timer timeoutTimer = new Timer(10000, e -> {
                        if (!undoButton.isEnabled()) {
                            undoButton.setEnabled(true);
                            undoButton.setText("悔棋");
                            chatPanel.appendSystemMessage("悔棋请求超时，请重试");
                        }
                    });
                    timeoutTimer.setRepeats(false);
                    timeoutTimer.start();
                    
                } catch (Exception e) {
                    // 发送失败时恢复按钮状态
                    undoButton.setEnabled(true);
                    undoButton.setText("悔棋");
                    chatPanel.appendSystemMessage("悔棋请求发送失败，请重试");
                    e.printStackTrace();
                }
            });
        }
    }
    
    /**
     * 请求求和
     */
    private void requestDraw() {
        // 防止重复点击
        if (!drawButton.isEnabled()) {
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "确定要请求求和吗？", 
            "求和请求", 
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            // 立即禁用按钮，防止重复点击
            drawButton.setEnabled(false);
            drawButton.setText("求和中...");
            
            // 在后台线程发送消息，避免阻塞UI
            SwingUtilities.invokeLater(() -> {
                try {
                    GameMessage drawMsg = new GameMessage(GameMessage.MessageType.DRAW_REQUEST);
                    client.sendMessage(drawMsg);
                    chatPanel.appendSystemMessage("已发送求和请求，等待对手回应...");
                    
                    // 设置超时恢复按钮状态（防止网络问题导致按钮永久禁用）
                    Timer timeoutTimer = new Timer(10000, e -> {
                        if (!drawButton.isEnabled()) {
                            drawButton.setEnabled(true);
                            drawButton.setText("求和");
                            chatPanel.appendSystemMessage("求和请求超时，请重试");
                        }
                    });
                    timeoutTimer.setRepeats(false);
                    timeoutTimer.start();
                    
                } catch (Exception e) {
                    // 发送失败时恢复按钮状态
                    drawButton.setEnabled(true);
                    drawButton.setText("求和");
                    chatPanel.appendSystemMessage("求和请求发送失败，请重试");
                    e.printStackTrace();
                }
            });
        }
    }
    
    /**
     * 认输
     */
    private void surrender() {
        // 防止重复点击
        if (!surrenderButton.isEnabled()) {
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "确定要认输吗？认输后游戏将立即结束。", 
            "认输确认", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (option == JOptionPane.YES_OPTION) {
            // 立即禁用按钮，防止重复点击
            surrenderButton.setEnabled(false);
            surrenderButton.setText("认输中...");
            
            // 在后台线程发送消息，避免阻塞UI
            SwingUtilities.invokeLater(() -> {
                try {
                    GameMessage surrenderMsg = new GameMessage(GameMessage.MessageType.SURRENDER);
                    client.sendMessage(surrenderMsg);
                    chatPanel.appendSystemMessage("已发送认输请求...");
                    
                    // 设置超时恢复按钮状态（防止网络问题导致按钮永久禁用）
                    Timer timeoutTimer = new Timer(5000, e -> {
                        if (!surrenderButton.isEnabled()) {
                            surrenderButton.setEnabled(true);
                            surrenderButton.setText("认输");
                            chatPanel.appendSystemMessage("认输请求可能未成功发送，请重试");
                        }
                    });
                    timeoutTimer.setRepeats(false);
                    timeoutTimer.start();
                    
                } catch (Exception e) {
                    // 发送失败时恢复按钮状态
                    surrenderButton.setEnabled(true);
                    surrenderButton.setText("认输");
                    chatPanel.appendSystemMessage("认输请求发送失败，请重试");
                    e.printStackTrace();
                }
            });
        }
    }
    
    /**
     * 设置悔棋按钮是否可用
     */
    public void setUndoEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            undoButton.setEnabled(enabled);
            if (enabled) {
                undoButton.setText("悔棋");
            }
        });
    }
    
    /**
     * 设置求和按钮是否可用
     */
    public void setDrawEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            drawButton.setEnabled(enabled);
            if (enabled) {
                drawButton.setText("求和");
            }
        });
    }
    
    /**
     * 设置认输按钮是否可用
     */
    public void setSurrenderEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            surrenderButton.setEnabled(enabled);
            if (enabled) {
                surrenderButton.setText("认输");
            }
        });
    }
    
    /**
     * 设置所有按钮是否可用
     */
    public void setAllButtonsEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            undoButton.setEnabled(enabled);
            drawButton.setEnabled(enabled);
            surrenderButton.setEnabled(enabled);
            if (enabled) {
                undoButton.setText("悔棋");
                drawButton.setText("求和");
                surrenderButton.setText("认输");
            }
        });
    }
}