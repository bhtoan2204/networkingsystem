package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    public static JFrame frame;
    public static JTextField entryPort, entryIP, entryName;
    public static JButton btn;
    public void init(){
        frame = new JFrame("Client");
        frame.setLayout(null);
        frame.setBounds(300, 200, 330, 300);
        frame.setResizable(false);

        JLabel ipLabel = new JLabel("IP: ");
        JLabel portLabel = new JLabel("Port: ");
        JLabel nameLabel = new JLabel("Name: ");

        ipLabel.setBounds(20, 80, 80, 30);
        portLabel.setBounds(20, 120, 80, 30);
        nameLabel.setBounds(20, 160, 80, 30);

        entryIP = new JTextField();
        entryPort = new JTextField();
        entryName = new JTextField();

        entryIP.setSize(new Dimension(100, 50));
        entryPort.setSize(new Dimension(100, 50));
        entryName.setSize(new Dimension(100, 50));

        entryIP.setBounds(80, 80, 200, 30);
        entryPort.setBounds(80, 120, 200, 30);
        entryName.setBounds(80, 160, 200, 30);

        btn = new JButton("Connect");
        btn.setBounds(100, 200, 100, 30);

        frame.add(ipLabel);
        frame.add(portLabel);
        frame.add(nameLabel);

        frame.add(entryIP);
        frame.add(entryPort);
        frame.add(entryName);
        frame.add(btn);

        frame.setVisible(true);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = Integer.parseInt(entryPort.getText());
                String ip = entryIP.getText();
                String name = entryName.getText();
                if(port > 0 && port < 10000){
                    ClientHandler clientHandler = new ClientHandler(ip, port, name);
                    clientHandler.frame.setVisible(true);
                    frame.setVisible(false);
                    frame.dispose();
                }
                else {
                    JOptionPane.showMessageDialog(frame, "Invalid information!!");
                }
            }
        });
        entryPort.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });
    }

    public Main(){ init();}

    public static void main(String[] args) {
        new Main();
    }
}
