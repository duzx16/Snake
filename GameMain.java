import gamedata.GameConstants;
import gamedata.GameData;
import gamedata.MapEle;
import gameui.BackgroundPanel;
import gameui.NetUI;
import gameui.StatisticsUI;
import imageio.ImageManager;

import javafx.scene.media.AudioClip;

import socket.ReceiveThread;
import socket.SendThread;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

// The main class
public class GameMain extends JFrame {
    // The UI of the game
    public MainUI ui;
    // The UI of the main
    JPanel game_panel, statistics;
    MaskLayer maskLayer;
    // The UI of the start
    private JPanel start_ui;
    private NetUI net_ui;
    private JLabel connect_label;
    // The setting of the game
    public GameData data;
    GameStepper stepper;
    private AbstractAction pauseAction;
    private AbstractAction continueAction;
    private AbstractAction musicAction;
    private AbstractAction startAction;
    JSlider speed_slider;
    int is_pause = 2;
    // 龙虎榜记录
    private int[] _records = new int[5];
    // 网络通信
    private ServerSocket _server;
    private Socket _socket;
    private boolean _is_connecting = false;
    private ConnectThread connectThread;

    // The component for chat UI
    JTextArea chat_text;
    private JTextField text_input;
    JScrollPane chat_scroll;

    public boolean is_server_mode() {
        return _server_mode;
    }

    private boolean _server_mode;
    final ArrayList<byte[]> sendBuffer = new ArrayList<>();
    final ServerListener serverListener = new ServerListener(this);
    final ClientListener clientListener = new ClientListener(this);
    private SendThread sendThread;
    private ReceiveThread receiveThread;


    // Start Button on the startUI
    class StartAction extends AbstractAction {
        StartAction(Image icon) {
            super("", new ImageIcon(icon));
        }

        public void actionPerformed(ActionEvent e) {
            if (_is_connecting) {
                if (connectThread != null && !connectThread.exit) {
                    connectThread.exit = true;
                    connectThread.interrupt();
                }
                connect_label.setText("当前无连接，点击Play按钮连接");
                _is_connecting = false;
            } else {
                if (connectGame()) {
                    connect_label.setText("正在连接中，点击Play按钮取消");
                    _is_connecting = true;
                }
            }
        }
    }

    // Four Actions on the ToolBar
    class PauseAction extends AbstractAction {

        PauseAction(Image icon) {
            super("Pause", new ImageIcon(icon));
        }

        public void actionPerformed(ActionEvent e) {
            if (is_server_mode()) {
                serverListener.sendPause(0);
                is_pause = 0;
                pauseGame();
            } else {
                clientListener.sendPause();
            }
        }
    }

    class ContinueAction extends AbstractAction {

        ContinueAction(Image icon) {
            super("Continue", new ImageIcon(icon));
        }

        public void actionPerformed(ActionEvent e) {
            if (is_server_mode()) {
                serverListener.sendPause(2);
                is_pause = 2;
                continueGame();
            } else {
                clientListener.sendContinue();
            }
        }
    }

    class StopAction extends AbstractAction {

        StopAction(Image icon) {
            super("Stop", new ImageIcon(icon));
        }

        public void actionPerformed(ActionEvent e) {
            gameOver();
        }
    }

    class ChatAction extends AbstractAction {
        ChatAction() {
            super("发送");
        }

        public void actionPerformed(ActionEvent e) {
            String text = text_input.getText();
            text_input.setText("");
            if (is_server_mode()) {
                serverListener.sendChatData(text);
                chat_text.append("Server:\n  " + text + "\n");
            } else {
                clientListener.sendChatData(text);
                chat_text.append("Client:\n  " + text + "\n");
            }
            SwingUtilities.invokeLater(() -> {
                JScrollBar bar = chat_scroll.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            });
        }
    }

    class MusicAction extends AbstractAction {
        private JFileChooser _fileChooser;
        private AudioClip _music;

        MusicAction(Image icon) {
            super("Music", new ImageIcon(icon));
            _fileChooser = new JFileChooser(".");
            //_music = new AudioClip(new File("sound/background.mp3").toURI().toURL().toString());
            _music = new AudioClip(GameMain.this.getClass().getResource("sound/background.mp3").toString());
            _music.setCycleCount(AudioClip.INDEFINITE);
            _music.play();
        }

        public void actionPerformed(ActionEvent e) {
            if (_fileChooser.showOpenDialog(GameMain.this) == JFileChooser.APPROVE_OPTION) {
                File file = _fileChooser.getSelectedFile();
                if (file != null && file.exists()) {
                    if (_music != null) {
                        _music.stop();
                    }
                    try {
                        _music = new AudioClip(file.toURI().toURL().toString());
                    } catch (MalformedURLException error) {
                        System.out.println(error.getMessage());
                    }
                    _music.setCycleCount(AudioClip.INDEFINITE);
                    _music.play();
                } else {
                    JOptionPane.showMessageDialog(GameMain.this, "The file doesn't exist");
                }
            }
        }
    }

    GameMain() {
        super("Snake");

        // Initialize the Game
        ImageManager.initImage(this.getClass());
        data = new GameData();
        ui = new MainUI(data);
        stepper = new GameStepper(this);
        initGame();
        loadRecord();

        // Set the start UI
        start_ui = new BackgroundPanel(ImageManager.start_background);
        startAction = new StartAction(ImageManager.play_option);
        JButton start_button = new JButton(startAction);
        start_button.setFocusable(false);
        start_button.setContentAreaFilled(false);
        start_button.setBorderPainted(false);

        net_ui = new NetUI();
        net_ui.setOpaque(false);
        start_ui.setLayout(new GridLayout(3, 2));
        for (int i = 0; i < 2; ++i) {
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            start_ui.add(panel);
        }
        connect_label = new JLabel("", SwingConstants.CENTER);
        connect_label.setHorizontalAlignment(SwingConstants.CENTER);
        connect_label.setVerticalAlignment(SwingConstants.CENTER);
        connect_label.setOpaque(false);
        connect_label.setForeground(Color.WHITE);
        connect_label.setFont(new Font("Yuanti SC", Font.BOLD, 20));
        connect_label.setText("当前无连接，点击Play按钮连接");
        start_ui.add(connect_label);
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        start_ui.add(panel);
        start_ui.add(start_button);
        start_ui.add(net_ui);

        //Set the UI for game
        game_panel = new JPanel();
        game_panel.setLayout(new BorderLayout());
        ui.setLayout(new BorderLayout());
        maskLayer = new MaskLayer(this);
        maskLayer.setOpaque(false);
        GameLogic._parent = this;
        ui.add(maskLayer, BorderLayout.CENTER);
        game_panel.add(ui, BorderLayout.CENTER);

        // Initialize the toolBar
        JToolBar toolBar = new JToolBar();
        // The slider
        speed_slider = new JSlider(GameConstants.min_speed, GameConstants.max_speed, GameConstants.default_speed);
        speed_slider.addChangeListener(stepper);
        toolBar.add(speed_slider);

        // The action buttons
        pauseAction = new PauseAction(ImageManager.pause_button);
        continueAction = new ContinueAction(ImageManager.play_button);
        AbstractAction stopAction = new StopAction(ImageManager.home_button);
        musicAction = new MusicAction(ImageManager.music_button);

        toolBar.add(pauseAction).setFocusable(false);
        toolBar.add(continueAction).setFocusable(false);
        pauseAction.setEnabled(true);
        continueAction.setEnabled(false);
        toolBar.add(stopAction).setFocusable(false);
        toolBar.add(musicAction).setFocusable(false);
        toolBar.setFocusable(false);
        game_panel.add(toolBar, BorderLayout.SOUTH);

        // The statistics for scores and lives
        statistics = new StatisticsUI(data);
        game_panel.add(statistics, BorderLayout.NORTH);

        // The chat panel
        JPanel chat_panel = new JPanel();
        chat_panel.setLayout(new BorderLayout());
        text_input = new JTextField(10);
        chat_text = new JTextArea();
        chat_scroll = new JScrollPane(chat_text);
        chat_text.setEditable(false);
        chat_text.setLineWrap(true);
        chat_text.setWrapStyleWord(true);
        chat_scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chat_panel.add(chat_scroll, BorderLayout.CENTER);
        JPanel input_panel = new JPanel();
        input_panel.add(text_input);
        input_panel.add(new JButton(new ChatAction()));
        chat_panel.add(input_panel, BorderLayout.SOUTH);
        game_panel.add(chat_panel, BorderLayout.EAST);

        // Set the game
        setContentPane(start_ui);
        KeyboardController key_controller = new KeyboardController(this);
        addKeyListener(key_controller);
        game_panel.addKeyListener(key_controller);
        game_panel.setFocusable(true);
        ui.setFocusable(true);
        ui.addKeyListener(key_controller);
        ui.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                GameMain.super.requestFocus();
            }
        });
        toolBar.addKeyListener(key_controller);
        speed_slider.addKeyListener(key_controller);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(750, 750);
        setMinimumSize(new Dimension(400, 400));
        setVisible(true);
    }

    // 初始化游戏数据
    private void initGame() {
        for (int i = 0; i < GameConstants.map_width; i++) {
            for (int j = 0; j < GameConstants.map_height; j++) {
                data.map.setElementAt(new MapEle(MapEle.EleType.NULL, null, false, false), i, j);
            }
        }
        GameLogic.initWalls(data.map, data.walls);
        GameLogic.addFoods(data.map, data.foods);
        GameLogic.initHoles(data.map, data.holes);
        GameLogic.initStones(data.map, data.stones);
        GameLogic.initSnake(0, data);
        GameLogic.initSnake(1, data);
        data.snake_nums[0] = data.snake_nums[1] = GameConstants.init_snakes;
        data.scores[0] = data.scores[1] = 0;
        data.is_lives[0] = data.is_lives[1] = true;
    }

    void pauseGame() {
        stepper.stepPause();
        continueAction.setEnabled(true);
        pauseAction.setEnabled(false);
        ui.repaint();
    }

    void continueGame() {
        stepper.stepStart();
        continueAction.setEnabled(false);
        pauseAction.setEnabled(true);
        ui.repaint();
    }

    // 结束游戏
    void gameOver() {
        // 防止还有未发送完成的消息导致客户端没有顺利结束
        synchronized (sendBuffer) {
            if (sendBuffer.isEmpty()) {
                disconnectGame();
            }
        }
        stepper.stepPause();
        saveRecord();
        String result;
        if (data.is_lives[0] ^ data.is_lives[1]) {
            if (is_server_mode() ^ data.is_lives[0]) {
                result = "对方获胜\n";
            } else {
                result = "你获胜了\n";
            }
        } else {
            result = "平局\n";
        }
        JOptionPane.showMessageDialog(this, String.format(result + "龙虎榜\n1.%d\n2.%d\n3.%d\n4.%d\n5.%d\n", _records[0], _records[1], _records[2], _records[3], _records[4]));
        setContentPane(start_ui);
        validate();
    }

    // 关闭收发线程
    private void stopCommunicate() {
        synchronized (sendBuffer) {
            sendBuffer.clear();
        }
        if (sendThread != null) {
            sendThread.exit = true;
            sendThread.interrupt();
        }
        if (receiveThread != null) {
            receiveThread.exit = true;
            receiveThread.interrupt();
        }
    }

    // 断开连接
    void disconnectGame() {
        stopCommunicate();
        if (_socket != null) {
            try {
                _socket.close();
                if (_server != null)
                    _server.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // 尝试进行连接
    private boolean connectGame() {
        try {
            disconnectGame();
            int port = Integer.parseInt(net_ui.port_field.getText());
            InetAddress addr = InetAddress.getByName(net_ui.ip_field.getText());
            if (net_ui.server_button.isSelected()) {
                _server_mode = true;
            } else {
                _server_mode = false;
            }
            connectThread = new ConnectThread(addr, port);
            connectThread.start();
            return true;

        } catch (IOException error) {
            JOptionPane.showMessageDialog(this, "该IP地址不存在");
            return false;
        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(this, "端口号错误");
            return false;
        }
    }

    // 连接时发生错误
    private void connectError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage() + "\n连接失败，请重试");
        connect_label.setText("当前无连接，点击Play按钮连接");
        _is_connecting = false;
    }

    // 连接成功，开始进入游戏
    private void startGame() {
        _is_connecting = false;
        connect_label.setText("当前无连接，点击Play按钮连接");
        chat_text.setText("");
        sendBuffer.clear();
        if (_server_mode) {
            serverListener.listening = true;
            receiveThread = new ReceiveThread(_socket, serverListener);
            sendThread = new SendThread(_socket, sendBuffer, serverListener);
        } else {
            clientListener.listening = true;
            receiveThread = new ReceiveThread(_socket, clientListener);
            sendThread = new SendThread(_socket, sendBuffer, clientListener);
        }
        receiveThread.start();
        sendThread.start();
        if (_server_mode) {
            initGame();
            serverListener.sendInitData();
            setContentPane(game_panel);
            validate();
        } else {

        }
        stepper._count.clear();
        stepper.connect_counter.clear();
        stepper.stepStart();
    }

    // 实现龙虎榜功能
    private void loadRecord() {
        File file = new File("record.txt");
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                for (int i = 0; i < 5; i++) {
                    _records[i] = Integer.parseInt(reader.readLine());
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("The file format incorrect");
            }
        } else {
            for (int i = 0; i < 5; i++) {
                _records[i] = 0;
            }
        }
    }

    private void saveRecord() {
        // update the record
        int my_record = data.scores[0];
        for (int i = 0; i < 5; i++) {
            if (my_record > _records[i]) {
                int temp = _records[i];
                _records[i] = my_record;
                my_record = temp;
            }
        }
        try {
            File file = new File("record.txt");
            if (!file.exists())
                file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (int i = 0; i < 5; i++) {
                writer.write(String.valueOf(_records[i]));
                writer.write('\n');
            }
            writer.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    // 用于进行连接的线程
    class ConnectThread extends Thread {
        public volatile boolean exit = false;
        private InetAddress _addr;
        private int _port;

        ConnectThread(InetAddress addr, int port) {
            _addr = addr;
            _port = port;
        }

        public void run() {
            if (_server_mode) {
                try {
                    if (_server != null) {
                        try {
                            _server.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    _server = new ServerSocket(_port);
                    _socket = _server.accept();
                    SwingUtilities.invokeLater(GameMain.this::startGame);
                } catch (IOException e) {
                    if (!exit) {
                        exit = true;
                        connectError(e);
                    }
                }
            } else {
                try {
                    _socket = new Socket();
                    _socket.connect(new InetSocketAddress(_addr, _port), 0);
                    SwingUtilities.invokeLater(GameMain.this::startGame);
                } catch (IOException e) {
                    if (!exit) {
                        exit = true;
                        connectError(e);
                    }
                }

            }
        }
    }
}