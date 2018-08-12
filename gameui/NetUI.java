package gameui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUI extends JPanel implements ActionListener {
    public JRadioButton server_button, client_button;
    public JTextField ip_field, port_field;

    public NetUI() {
        GridLayout layout = new GridLayout(4, 1);
        setLayout(layout);

        server_button = new JRadioButton("以服务器模式运行");
        server_button.setForeground(Color.WHITE);
        server_button.setOpaque(false);
        client_button = new JRadioButton("以客户端模式运行");
        client_button.setForeground(Color.WHITE);
        client_button.setOpaque(false);
        server_button.addActionListener(this);
        client_button.addActionListener(this);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(server_button);
        buttonGroup.add(client_button);

        JPanel ip_panel = new JPanel();
        ip_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        ip_panel.setOpaque(false);
        JLabel ip_label = new JLabel("IP地址");
        ip_label.setForeground(Color.WHITE);
        ip_panel.add(ip_label);
        ip_field = new JTextField(10);
        ip_panel.add(ip_field);

        JPanel port_panel = new JPanel();
        port_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        port_panel.setOpaque(false);
        JLabel port_label = new JLabel("端口号");
        port_label.setForeground(Color.WHITE);
        port_panel.add(port_label);
        port_field = new JTextField(4);
        port_panel.add(port_field);

        add(server_button);
        add(client_button);
        add(ip_panel);
        add(port_panel);

    }

    public void actionPerformed(ActionEvent e) {
        if (server_button.isSelected()) {
            try {
                InetAddress addr = InetAddress.getLocalHost();
                ip_field.setText(addr.getHostAddress());
                ip_field.setEditable(false);
            } catch (UnknownHostException error) {
                System.out.println(error.getMessage());
            }
        } else {
            ip_field.setEditable(true);
        }
    }
}
