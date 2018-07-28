import imageio.SVGImageReader;

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
import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;

public class GameMain extends JFrame {
    // The UI of the game
    MainUI ui;
    // The UI of the main
    JPanel game_panel, statistics;
    // The UI of the start
    JPanel start_ui;
    NetUI net_ui;
    GameData data;
    GameStepper stepper;
    AbstractAction pauseAction, continueAction, stopAction, musicAction, startAction;
    JSlider speed_slider;
    // 龙虎榜记录
    private int[] _records = new int[5];
    // 网络通信
    private ServerSocket _server;
    private Socket _socket;

    public boolean is_server_mode() {
        return _server_mode;
    }

    // The component for chat UI
    JTextArea chat_text;
    JTextField text_input;

    private boolean _server_mode;
    final ArrayList<byte[]> sendBuffer = new ArrayList<>();
    final ServerListener serverListener = new ServerListener(this);
    final ClientListener clientListener = new ClientListener(this);
    SendThread sendThread;
    ReceiveThread receiveThread;


    class StartAction extends AbstractAction {
        StartAction(Image icon) {
            super("Start", new ImageIcon(icon));
        }

        public void actionPerformed(ActionEvent e) {
            connectGame();
        }
    }

    class PauseAction extends AbstractAction {

        PauseAction(Image icon) {
            super("Pause", new ImageIcon(icon));
        }

        public void actionPerformed(ActionEvent e) {
            pauseGame();
        }
    }

    class ContinueAction extends AbstractAction {

        ContinueAction(Image icon) {
            super("Continue", new ImageIcon(icon));
        }

        public void actionPerformed(ActionEvent e) {
            continueGame();
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
                chat_text.append("Server:\n\t" + text + "\n");
            } else {
                clientListener.sendChatData(text);
                chat_text.append("Client:\n\t" + text + "\n");
            }
        }
    }

    class MusicAction extends AbstractAction {
        private JFileChooser _fileChooser;
        private AudioClip _music;

        MusicAction(Image icon) {
            super("Music", new ImageIcon(icon));
            _fileChooser = new JFileChooser(".");
            try {
                _music = new AudioClip(new File("sound/background.mp3").toURI().toURL().toString());
                _music.setCycleCount(AudioClip.INDEFINITE);
                _music.play();
            } catch (MalformedURLException error) {
                System.out.println(error.getMessage());
            }
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
        data = new GameData();
        ui = new MainUI(data);
        stepper = new GameStepper(this);
        initGame();
        loadRecord();

        start_ui = new StartUI(this);
        try {
            startAction = new StartAction(SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/play_option.svg"))));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        JButton start_button = new JButton(startAction);
        start_button.setFocusable(false);
        net_ui = new NetUI();

        start_ui.add(start_button);
        start_ui.add(net_ui);


        game_panel = new JPanel();
        game_panel.setLayout(new BorderLayout());
        game_panel.add(ui, BorderLayout.CENTER);

        // Initialize the toolBar
        JToolBar toolBar = new JToolBar();
        // The slider
        speed_slider = new JSlider(1, 10, 2);
        speed_slider.addChangeListener(stepper);
        toolBar.add(speed_slider);

        // The action buttons
        try {
            pauseAction = new PauseAction(SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/pause.svg"))));
            continueAction = new ContinueAction(SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/stop.svg"))));
            stopAction = new StopAction(SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/home.svg"))));
            musicAction = new MusicAction(SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/music.svg"))));
        } catch (IOException e) {
        }
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
        chat_text.setEditable(false);
        chat_panel.add(chat_text, BorderLayout.CENTER);
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 750);
        setVisible(true);
    }


    void initGame() {
        for (int i = 0; i < GameData.MAP_WIDTH; i++) {
            for (int j = 0; j < GameData.MAP_HEIGHT; j++) {
                data.map.setElementAt(new MapEle(MapEle.EleType.NULL, null), i, j);
            }
        }
        GameLogic.initWalls(data.map, data.walls, 2);
        GameLogic.addFoods(data.map, data.foods, 2);
        GameLogic.initHoles(data.map, data.holes, 2);
        GameLogic.initStones(data.map, data.stones, 5);
        GameLogic.initSnake(0, data);
        GameLogic.initSnake(1, data);
        data.snake_nums[0] = data.snake_nums[1] = 5;
        data.scores[0] = data.scores[1] = 0;
        data.is_lives[0] = data.is_lives[1] = true;
    }

    // todo 暂停和恢复实现在线版本
    void pauseGame() {
        stepper.stepPause();
        ui.is_pause = true;
        continueAction.setEnabled(true);
        pauseAction.setEnabled(false);
        ui.repaint();
    }

    void continueGame() {
        stepper.stepStart();
        ui.is_pause = false;
        continueAction.setEnabled(false);
        pauseAction.setEnabled(true);
        ui.repaint();
    }

    // todo 游戏结束时的断开连接和停止收发
    void gameOver() {
        stepper.stepPause();
        sendThread.exit = true;
        receiveThread.exit = true;
        sendThread.interrupt();
        receiveThread.interrupt();
        try {
            _socket.close();
            if (_server_mode)
                _server.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        saveRecord();
        JOptionPane.showMessageDialog(this, String.format("Game Over\n龙虎榜\n1.%d\n2.%d\n3.%d\n4.%d\n5.%d\n", _records[0], _records[1], _records[2], _records[3], _records[4]));
        setContentPane(start_ui);
        validate();
    }

    // todo 实现等待连接时的画面
    void connectGame() {
        try {
            int port = Integer.parseInt(net_ui.port_field.getText());
            InetAddress addr = InetAddress.getByName(net_ui.ip_field.getText());
            if (net_ui.server_button.isSelected()) {
                _server_mode = true;
            } else {
                _server_mode = false;
            }
            new ConnectThread(addr, port).start();

        } catch (IOException error) {
            JOptionPane.showMessageDialog(this, "该IP地址不存在");
        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(this, "端口号错误");
        }


    }

    void connectError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage() + "\n连接失败，请重试");
    }

    void startGame() {
        chat_text.setText("");
        sendBuffer.clear();
        if (_server_mode) {
            receiveThread = new ReceiveThread(_socket, serverListener);
            sendThread = new SendThread(_socket, sendBuffer, serverListener);
        } else {
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
        stepper.stepStart();
    }

    // 实现龙虎榜功能
    void loadRecord() {
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

    void saveRecord() {
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

    class ConnectThread extends Thread {
        private InetAddress _addr;
        private int _port;

        ConnectThread(InetAddress addr, int port) {
            _addr = addr;
            _port = port;
        }

        public void run() {
            if (_server_mode) {
                try {
                    _server = new ServerSocket(_port, 1, _addr);
                    _socket = _server.accept();
                    startGame();
                } catch (IOException e) {
                    connectError(e);
                }
            } else {
                try {
                    _socket = new Socket();
                    _socket.connect(new InetSocketAddress(_addr, _port), 0);
                    startGame();
                } catch (IOException e) {
                    connectError(e);
                }

            }
        }
    }
}