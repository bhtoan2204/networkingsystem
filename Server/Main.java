package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    private JFrame frame;
    private JTextField entryPort;
    private JButton btn;

    void init(){
        frame = new JFrame("Server");
        frame.setLayout(null);
        frame.setBounds(200, 200, 450, 200);
        frame.setResizable(false);

        JLabel portLabel = new JLabel("Port: ");
        portLabel.setBounds(20, 60, 80, 30);

        entryPort = new JTextField();
        entryPort.setSize(new Dimension(100, 50));
        entryPort.setBounds(80, 60, 200, 30);

        btn = new JButton("Confirm");
        btn.setBounds(300, 60, 100, 30);

        frame.add(portLabel);
        frame.add(entryPort);
        frame.add(btn);
        frame.setVisible(true);

        entryPort.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = Integer.parseInt(entryPort.getText());
                if (port > 0 && port<10000){
                    MainFrame dashboard = new MainFrame(port);
                    dashboard.frame.setVisible(true);
                    frame.setVisible(false);
                    frame.dispose();
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Invalid port");
                }
            }
        });
    }

    public Main(){init();}

    public static void main(String[] args) {
        new Main();
    }

}
