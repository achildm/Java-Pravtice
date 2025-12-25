package com.achldm.chess.client.ui;

import com.achldm.chess.client.network.GameClient;
import com.achldm.chess.common.GameMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 聊天面板组件
 */
public class ChatPanel extends JPanel {
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendButton;
    private GameClient client;
    private String username;
    
    public ChatPanel(GameClient client, String username) {
        this.client = client;
        this.username = username;
        
        initComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initComponents() {
        chatArea = new JTextArea(8, 25);
        chatArea.setEditable(false);
        chatArea.setFont(new Font("宋体", Font.PLAIN, 12));
        chatArea.setBackground(Color.WHITE);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        chatInput = new JTextField(20);
        chatInput.setFont(new Font("宋体", Font.PLAIN, 12));
        
        sendButton = new JButton("发送");
        sendButton.setFont(new Font("宋体", Font.PLAIN, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("聊天"));
        
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(chatScrollPane, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        add(inputPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        };
        
        chatInput.addActionListener(sendAction);
        sendButton.addActionListener(sendAction);
    }
    
    /**
     * 发送聊天消息
     */
    private void sendMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            GameMessage chatMsg = new GameMessage(GameMessage.MessageType.CHAT);
            chatMsg.setUsername(username);
            chatMsg.setContent(message);
            client.sendMessage(chatMsg);
            
            // 在本地显示自己的消息
            appendMessage(username, message);
            chatInput.setText("");
        }
    }
    
    /**
     * 添加消息到聊天区域
     */
    public void appendMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            chatArea.append(String.format("[%s] %s: %s\n", timestamp, sender, message));
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    /**
     * 添加系统消息
     */
    public void appendSystemMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            chatArea.append(String.format("[%s] 系统: %s\n", timestamp, message));
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    /**
     * 清空聊天记录
     */
    public void clearChat() {
        SwingUtilities.invokeLater(() -> {
            chatArea.setText("");
        });
    }
    
    /**
     * 设置输入框是否可用
     */
    public void setInputEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            chatInput.setEnabled(enabled);
            sendButton.setEnabled(enabled);
        });
    }
}