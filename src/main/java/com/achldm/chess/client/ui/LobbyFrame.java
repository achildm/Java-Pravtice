package com.achldm.chess.client.ui;

import com.achldm.chess.client.network.GameClient;
import com.achldm.chess.common.GameMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 游戏大厅界面
 */
public class LobbyFrame extends JFrame {
    private GameClient client;
    private JPanel roomPanel;
    private JPanel userInfoPanel;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendButton;
    private JButton joinButton;
    private JButton exitButton;
    private boolean isMatching = false;
    
    // 用户信息
    private String username;
    private ImageIcon userAvatar;
    private int avatarIndex;
    
    public LobbyFrame(GameClient client, String username, ImageIcon avatar, int avatarIndex) {
        this.client = client;
        this.username = username;
        this.userAvatar = avatar;
        this.avatarIndex = avatarIndex;
        client.setLobbyFrame(this);
        initComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initComponents() {
        setTitle("游戏大厅");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        
        // 创建房间面板
        createRoomPanel();
        
        // 创建用户信息面板
        createUserInfoPanel();
        
        // 创建聊天面板组件
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(Color.WHITE);
        
        chatInput = new JTextField();
        sendButton = new JButton("发送");
        
        joinButton = new JButton("加入");
        exitButton = new JButton("退出");
    }
    
    private void createRoomPanel() {
        roomPanel = new JPanel(new GridLayout(5, 3, 10, 10));
        roomPanel.setBorder(BorderFactory.createTitledBorder("象棋游戏"));
        roomPanel.setBackground(new Color(100, 149, 237)); // 蓝色背景
        
        // 创建15个房间
        for (int i = 1; i <= 15; i++) {
            JPanel room = createRoomButton(i);
            roomPanel.add(room);
        }
    }
    
    private JPanel createRoomButton(int roomNumber) {
        JPanel roomPanel = new JPanel(new BorderLayout());
        roomPanel.setPreferredSize(new Dimension(180, 120));
        roomPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        roomPanel.setBackground(Color.LIGHT_GRAY);
        
        // 房间号标签
        JLabel roomLabel = new JLabel("- " + roomNumber + " -", JLabel.CENTER);
        roomLabel.setFont(new Font("宋体", Font.BOLD, 14));
        roomLabel.setForeground(Color.YELLOW);
        
        // 中间区域 - 显示玩家信息或问号
        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        centerPanel.setOpaque(false);
        
        // 左侧玩家位置
        JLabel leftPlayer = new JLabel("?", JLabel.CENTER);
        leftPlayer.setFont(new Font("宋体", Font.BOLD, 24));
        leftPlayer.setForeground(Color.WHITE);
        
        // 中间棋盘图标
        JLabel boardIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/xqnoone.gif"));
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                boardIcon.setIcon(new ImageIcon(img));
            } else {
                boardIcon.setText("棋");
                boardIcon.setFont(new Font("宋体", Font.BOLD, 20));
                boardIcon.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            boardIcon.setText("棋");
            boardIcon.setFont(new Font("宋体", Font.BOLD, 20));
            boardIcon.setForeground(Color.WHITE);
        }
        boardIcon.setHorizontalAlignment(JLabel.CENTER);
        
        // 右侧玩家位置
        JLabel rightPlayer = new JLabel("?", JLabel.CENTER);
        rightPlayer.setFont(new Font("宋体", Font.BOLD, 24));
        rightPlayer.setForeground(Color.WHITE);
        
        centerPanel.add(leftPlayer);
        centerPanel.add(boardIcon);
        centerPanel.add(rightPlayer);
        
        roomPanel.add(roomLabel, BorderLayout.NORTH);
        roomPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 添加点击事件
        roomPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                joinRoom(roomNumber);
            }
        });
        
        return roomPanel;
    }
    
    private void createUserInfoPanel() {
        userInfoPanel = new JPanel(new BorderLayout());
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("个人信息"));
        userInfoPanel.setPreferredSize(new Dimension(200, 300));
        userInfoPanel.setBackground(Color.WHITE);
        
        // 用户头像和信息
        JPanel userPanel = new JPanel(new BorderLayout());
        
        // 头像
        JLabel avatarLabel = new JLabel();
        if (userAvatar != null) {
            // 缩放头像到80x80
            Image img = userAvatar.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(img));
        } else {
            // 如果没有头像，尝试加载默认头像
            try {
                String path = "/face/" + avatarIndex + "-1.gif";
                java.net.URL imageURL = getClass().getResource(path);
                if (imageURL != null) {
                    ImageIcon icon = new ImageIcon(imageURL);
                    Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    avatarLabel.setIcon(new ImageIcon(img));
                }
            } catch (Exception e) {
                avatarLabel.setText("头像");
            }
        }
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // 用户信息
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.add(new JLabel("用户: " + (username != null ? username : "玩家"), JLabel.CENTER));
        infoPanel.add(new JLabel("等级: 1", JLabel.CENTER));
        infoPanel.add(new JLabel("胜率: 0%", JLabel.CENTER));
        
        userPanel.add(avatarLabel, BorderLayout.CENTER);
        userPanel.add(infoPanel, BorderLayout.SOUTH);
        
        userInfoPanel.add(userPanel, BorderLayout.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(5, 5));
        
        // 顶部工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolBar.setBackground(Color.DARK_GRAY);
        toolBar.add(new JLabel("<<<"));
        toolBar.add(new JLabel("象棋游戏"));
        toolBar.add(new JLabel(">>>>"));
        
        JPanel rightToolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightToolBar.setOpaque(false);
        rightToolBar.add(joinButton);
        rightToolBar.add(exitButton);
        toolBar.add(rightToolBar);
        
        // 主要内容区域
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 左侧房间面板
        JScrollPane roomScrollPane = new JScrollPane(roomPanel);
        roomScrollPane.setPreferredSize(new Dimension(600, 500));
        
        // 右侧面板
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(280, 500));
        
        // 用户信息面板
        rightPanel.add(userInfoPanel, BorderLayout.NORTH);
        
        // 聊天面板
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("聊天室"));
        
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(0, 200));
        
        JPanel chatInputPanel = new JPanel(new BorderLayout(5, 5));
        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);
        
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);
        
        rightPanel.add(chatPanel, BorderLayout.CENTER);
        
        // 添加到主面板
        mainPanel.add(roomScrollPane, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(toolBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
    }
    
    private void setupEventHandlers() {
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isMatching) {
                    startMatching();
                } else {
                    cancelMatching();
                }
            }
        });
        
        exitButton.addActionListener(e -> System.exit(0));
        
        sendButton.addActionListener(e -> sendChatMessage());
        chatInput.addActionListener(e -> sendChatMessage());
    }
    
    private void joinRoom(int roomNumber) {
        int option = JOptionPane.showConfirmDialog(this, 
            "是否加入房间 " + roomNumber + "？", 
            "加入房间", 
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            startMatching();
        }
    }
    
    private void startMatching() {
        isMatching = true;
        joinButton.setText("取消");
        
        // 发送匹配请求
        GameMessage matchMsg = new GameMessage(GameMessage.MessageType.MATCH_REQUEST);
        client.sendMessage(matchMsg);
        
        // 在聊天区显示匹配信息
        chatArea.append("系统: 正在寻找对手...\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private void cancelMatching() {
        isMatching = false;
        joinButton.setText("加入");
        
        chatArea.append("系统: 已取消匹配\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private void sendChatMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            GameMessage chatMsg = new GameMessage(GameMessage.MessageType.CHAT);
            chatMsg.setContent(message);
            client.sendMessage(chatMsg);
            
            // 在本地显示自己的消息
            chatArea.append("我: " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            
            chatInput.setText("");
        }
    }
    
    /**
     * 接收聊天消息
     */
    public void onChatMessage(String username, String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(username + ": " + message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    /**
     * 匹配成功
     */
    public void onMatchFound(boolean isRed) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("系统: 匹配成功！准备开始游戏...\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            
            // 延迟一秒后打开游戏界面
            Timer timer = new Timer(1000, e -> {
                GameFrame gameFrame = new GameFrame(client, isRed, username, userAvatar, avatarIndex);
                gameFrame.setVisible(true);
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
    
    /**
     * 更新状态
     */
    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("系统: " + status + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
}