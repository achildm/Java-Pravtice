package com.achldm.chess.client.ui;

import com.achldm.chess.client.network.GameClient;
import com.achldm.chess.common.GameMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * 登录界面
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JTextField serverField;
    private JButton connectButton;
    private JButton resetButton;
    private JButton exitButton;
    private JPanel avatarPanel;
    private JLabel selectedAvatarLabel; // 显示选中头像的标签
    private ImageIcon selectedAvatar;
    private int selectedAvatarIndex = 1;
    private GameClient client;
    
    public LoginFrame() {
        initComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initComponents() {
        setTitle("登录窗口");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // 创建组件
        usernameField = new JTextField(20);
        serverField = new JTextField("localhost", 20); // 改为localhost，更容易测试
        connectButton = new JButton("连接");
        resetButton = new JButton("重置");
        exitButton = new JButton("退出");
        
        // 设置按钮样式
        connectButton.setPreferredSize(new Dimension(80, 30));
        resetButton.setPreferredSize(new Dimension(80, 30));
        exitButton.setPreferredSize(new Dimension(80, 30));
        
        // 创建选中头像显示标签
        selectedAvatarLabel = new JLabel();
        selectedAvatarLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        selectedAvatarLabel.setPreferredSize(new Dimension(40, 40));
        selectedAvatarLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // 创建头像面板
        createAvatarPanel();
    }
    
    private void createAvatarPanel() {
        avatarPanel = new JPanel(new GridLayout(9, 11, 2, 2));
        avatarPanel.setBorder(BorderFactory.createTitledBorder("头像选择"));
        avatarPanel.setPreferredSize(new Dimension(550, 450));
        
        // 加载头像
        for (int i = 1; i <= 99; i++) { // 增加到99个，确保有足够的头像
            try {
                String path = "/face/" + i + "-1.gif";
                java.net.URL imageURL = getClass().getResource(path);
                
                if (imageURL != null) {
                    ImageIcon icon = new ImageIcon(imageURL);
                    if (icon.getIconWidth() > 0) {
                        // 缩放图片到32x32
                        Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                        ImageIcon scaledIcon = new ImageIcon(img);
                        
                        JButton avatarButton = new JButton(scaledIcon);
                        avatarButton.setPreferredSize(new Dimension(40, 40));
                        avatarButton.setBorder(BorderFactory.createRaisedBevelBorder());
                        avatarButton.setToolTipText("头像 " + i);
                        
                        final int avatarIndex = i;
                        final ImageIcon finalIcon = scaledIcon;
                        avatarButton.addActionListener(e -> selectAvatar(avatarIndex, finalIcon, avatarButton));
                        
                        avatarPanel.add(avatarButton);
                        
                        // 默认选择第一个头像
                        if (i == 1) {
                            selectedAvatar = scaledIcon;
                            selectedAvatarIndex = 1;
                            avatarButton.setBorder(BorderFactory.createLoweredBevelBorder());
                            selectedAvatarLabel.setIcon(scaledIcon);
                        }
                        continue;
                    }
                }
                
                // 如果图片加载失败或不存在，添加空按钮
                JButton emptyButton = new JButton("?");
                emptyButton.setPreferredSize(new Dimension(40, 40));
                emptyButton.setEnabled(false);
                avatarPanel.add(emptyButton);
                
            } catch (Exception e) {
                // 如果加载失败，添加空按钮
                JButton emptyButton = new JButton("?");
                emptyButton.setPreferredSize(new Dimension(40, 40));
                emptyButton.setEnabled(false);
                avatarPanel.add(emptyButton);
            }
        }
    }
    
    private void selectAvatar(int index, ImageIcon icon, JButton button) {
        // 重置所有按钮边框
        for (Component comp : avatarPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).isEnabled()) {
                ((JButton) comp).setBorder(BorderFactory.createRaisedBevelBorder());
            }
        }
        
        // 设置选中的按钮边框
        button.setBorder(BorderFactory.createLoweredBevelBorder());
        selectedAvatar = icon;
        selectedAvatarIndex = index;
        
        // 更新显示的头像
        selectedAvatarLabel.setIcon(icon);
        selectedAvatarLabel.repaint();
        
        System.out.println("选择了头像: " + index); // 调试信息
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // 顶部信息面板
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 标题
        JLabel titleLabel = new JLabel("请输入您的个人信息");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        topPanel.add(titleLabel, gbc);
        
        // 用户名
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1;
        topPanel.add(usernameField, gbc);
        
        // 服务器
        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("服务器:"), gbc);
        gbc.gridx = 1;
        topPanel.add(serverField, gbc);
        
        // 头像标签
        gbc.gridx = 0; gbc.gridy = 3;
        topPanel.add(new JLabel("头像:"), gbc);
        gbc.gridx = 1;
        topPanel.add(selectedAvatarLabel, gbc);
        
        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(connectButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(exitButton);
        
        // 主面板布局
        add(topPanel, BorderLayout.NORTH);
        add(avatarPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 设置窗口大小和位置
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupEventHandlers() {
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // 回车键登录
        usernameField.addActionListener(e -> serverField.requestFocus());
        serverField.addActionListener(e -> login());
    }
    
    private void login() {
        String username = usernameField.getText().trim();
        String server = serverField.getText().trim();
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (server.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入服务器地址", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 连接服务器
        try {
            // 解析服务器地址，默认端口8888
            String host = server;
            int port = 8888;
            if (server.contains(":")) {
                String[] parts = server.split(":");
                host = parts[0];
                port = Integer.parseInt(parts[1]);
            }
            
            System.out.println("尝试连接服务器: " + host + ":" + port); // 调试信息
            
            client = new GameClient(host, port);
            client.setLoginFrame(this);
            
            // 发送登录消息
            GameMessage loginMsg = new GameMessage(GameMessage.MessageType.LOGIN);
            loginMsg.setUsername(username);
            loginMsg.setContent(""); // 暂时不需要密码
            client.sendMessage(loginMsg);
            
            // 禁用连接按钮，防止重复点击
            connectButton.setEnabled(false);
            connectButton.setText("连接中...");
            
            System.out.println("登录消息已发送"); // 调试信息
            
        } catch (IOException ex) {
            System.err.println("连接失败: " + ex.getMessage()); // 调试信息
            JOptionPane.showMessageDialog(this, 
                "连接服务器失败: " + ex.getMessage() + "\n\n请确保:\n1. 服务器已启动\n2. 服务器地址正确\n3. 端口8888未被占用", 
                "连接错误", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "服务器端口格式错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reset() {
        usernameField.setText("");
        serverField.setText("localhost");
        
        // 重置头像选择到第一个
        if (avatarPanel.getComponentCount() > 0) {
            Component firstComp = avatarPanel.getComponent(0);
            if (firstComp instanceof JButton && ((JButton) firstComp).isEnabled()) {
                JButton firstButton = (JButton) firstComp;
                Icon icon = firstButton.getIcon();
                if (icon instanceof ImageIcon) {
                    selectAvatar(1, (ImageIcon) icon, firstButton);
                }
            }
        }
    }
    
    /**
     * 登录成功后调用
     */
    public void onLoginSuccess() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("登录成功，打开大厅界面"); // 调试信息
            
            // 打开大厅界面
            LobbyFrame lobbyFrame = new LobbyFrame(client);
            lobbyFrame.setVisible(true);
            
            // 关闭登录界面
            dispose();
        });
    }
    
    /**
     * 登录失败后调用
     */
    public void onLoginFailed(String message) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("登录失败: " + message); // 调试信息
            JOptionPane.showMessageDialog(this, "登录失败: " + message, "错误", JOptionPane.ERROR_MESSAGE);
            connectButton.setEnabled(true);
            connectButton.setText("连接");
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new LoginFrame().setVisible(true);
        });
    }
}