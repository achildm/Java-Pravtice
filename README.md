# Java 网络象棋游戏

这是一个基于Java Swing和Socket网络编程技术开发的象棋游戏，支持多人在线对战。

## 功能特性

- **登录界面**: 支持用户登录，85个头像选择网格，服务器地址配置
- **游戏大厅**: 15个房间展示，用户信息面板，聊天功能，匹配系统
- **象棋对战**: 完整的象棋规则实现，实时对战，正确的棋子布局
- **音效支持**: 移动、选择、将军等音效
- **计时功能**: 每局游戏10分钟倒计时

## 界面改进

### 登录界面
- 仿QQ游戏风格的头像选择网格（9x11布局）
- 服务器地址输入框（默认192.168.0.100）
- 连接、重置、退出三个功能按钮
- 选中头像高亮显示

### 游戏大厅  
- 15个房间网格布局（5x3）
- 每个房间显示房间号和棋盘图标
- 右侧用户信息面板显示头像和个人信息
- 底部聊天室支持实时聊天
- 蓝色主题背景，仿QQ游戏大厅风格

### 象棋布局修正
- 修正了棋子初始位置：车马象士将士象马车
- 兵和炮的位置已正确放置
- 完整实现中国象棋标准布局

## 技术栈

- Java 17
- Swing GUI
- Socket网络编程
- 多线程技术
- 音频播放 (Java Sound API)

## 项目结构

```
src/main/java/com/achldm/chess/
├── ChessGameMain.java          # 主启动类
├── client/                     # 客户端
│   ├── ui/                    # 用户界面
│   │   ├── LoginFrame.java    # 登录界面
│   │   ├── LobbyFrame.java    # 大厅界面
│   │   └── GameFrame.java     # 游戏界面
│   └── network/               # 网络通信
│       └── GameClient.java    # 客户端网络处理
├── server/                    # 服务器端
│   ├── GameServer.java        # 游戏服务器
│   ├── ClientHandler.java     # 客户端处理器
│   └── GameRoom.java          # 游戏房间
├── game/                      # 游戏逻辑
│   └── ChessBoard.java        # 象棋棋盘和规则
└── common/                    # 公共类
    ├── ChessPiece.java        # 棋子枚举
    └── GameMessage.java       # 网络消息类
```

## 资源文件

- `src/main/resources/qizi/`: 棋子和棋盘图片
- `src/main/resources/face/`: 用户头像图片
- `src/main/resources/audio/`: 游戏音效文件
- `src/main/resources/img/`: 其他界面图片

## 运行说明

### 1. 编译项目

```bash
mvn clean compile
```

### 2. 启动服务器

```bash
mvn exec:java -Dexec.mainClass="com.achldm.chess.ChessGameMain" -Dexec.args="server"
```

或者直接运行：
```bash
java -cp target/classes com.achldm.chess.ChessGameMain server
```

### 3. 启动客户端

```bash
mvn exec:java -Dexec.mainClass="com.achldm.chess.ChessGameMain"
```

或者直接运行：
```bash
java -cp target/classes com.achldm.chess.ChessGameMain
```

### 4. 游戏流程

1. 启动服务器（默认端口8888）
2. 启动客户端，在登录界面选择头像，输入用户名
3. 设置服务器地址（默认192.168.0.100:8888），点击连接
4. 在大厅中点击房间或点击"加入"按钮开始匹配
5. 需要两个客户端都进行匹配才能开始游戏
6. 匹配成功后自动进入游戏界面，红方先行

## 象棋规则

游戏实现了完整的中国象棋规则：

- **帅/将**: 只能在九宫格内移动，每次一格
- **仕/士**: 只能在九宫格内斜着移动
- **相/象**: 斜着走两格，不能过河，不能被蹩脚
- **马**: 走日字，不能被蹩脚
- **车**: 走直线，路径不能有棋子
- **炮**: 走直线，吃子时中间必须有一个棋子
- **兵/卒**: 只能向前，过河后可以左右移动

## 网络协议

客户端和服务器通过序列化的GameMessage对象进行通信，支持以下消息类型：

- LOGIN: 登录请求
- MATCH_REQUEST: 匹配请求  
- MOVE: 棋子移动
- CHAT: 聊天消息
- GAME_OVER: 游戏结束
- 等等...

## 开发说明

- 服务器支持多个客户端同时连接
- 使用线程池处理客户端连接
- 游戏房间管理多个对战
- 支持断线重连（基础版本）

## 注意事项

1. 确保服务器先启动，客户端才能连接
2. 默认连接localhost:8888，可在代码中修改
3. 需要Java 17或更高版本
4. 资源文件路径要正确，否则可能显示异常

## 扩展功能

可以进一步添加的功能：
- 用户注册和数据库存储
- 游戏回放功能
- 排行榜系统
- 更丰富的聊天功能
- 观战模式
- 悔棋功能