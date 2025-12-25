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
        int option = JOptionPane.showConfirmDialog(this, 
            "确定要请求悔棋吗？", 
            "悔棋请求", 
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            GameMessage undoMsg = new GameMessage(GameMessage.MessageType.UNDO_REQUEST);
            client.sendMessage(undoMsg);
            undoButton.setEnabled(false);
            chatPanel.appendSystemMessage("已发送悔棋请求，等待对手回应...");
        }
    }
    
    /**
     * 请求求和
     */
    private void requestDraw() {
        int option = JOptionPane.showConfirmDialog(this, 
            "确定要请求求和吗？", 
            "求和请求", 
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            GameMessage drawMsg = new GameMessage(GameMessage.MessageType.DRAW_REQUEST);
            client.sendMessage(drawMsg);
            drawButton.setEnabled(false);
            chatPanel.appendSystemMessage("已发送求和请求，等待对手回应...");
        }
    }
    
    /**
     * 认输
     */
    private void surrender() {
        int option = JOptionPane.showConfirmDialog(this, 
            "确定要认输吗？认输后游戏将立即结束。", 
            "认输确认", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (option == JOptionPane.YES_OPTION) {
            GameMessage surrenderMsg = new GameMessage(GameMessage.MessageType.SURRENDER);
            client.sendMessage(surrenderMsg);
        }
    }
    
    /**
     * 设置悔棋按钮是否可用
     */
    public void setUndoEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            undoButton.setEnabled(enabled);
        });
    }
    
    /**
     * 设置求和按钮是否可用
     */
    public void setDrawEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            drawButton.setEnabled(enabled);
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
        });
    }
}