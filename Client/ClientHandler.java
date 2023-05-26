package Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler {
    public static JFrame frame;
    public static DefaultTableModel defaultTableModel;
    public static JTable table;
    public static JButton disconnectBtn;
    public static Socket socket = null;
    public static String nickName;
    public static String pathDirectory = "C:\\";
    public static String confirmedIP;
    public static int confirmedPort;

    public static JLabel labelPath;

    public ClientHandler(String ip, int port, String name){
        if(socket!=null&&socket.isConnected()){
            JOptionPane.showMessageDialog(frame, "Connected!");
        }
        else {
            try{
                socket = new Socket(ip, port);
                nickName = name;
                confirmedPort = port;
                confirmedIP = ip;
                new ClientSend(socket, name, "2", "Connected", pathDirectory);
                new Thread(new ClientReceive(socket)).start();

            }
            catch (Exception e){
                JOptionPane.showMessageDialog(frame, "Invalid information, try again");
            }
        }
        init();
        new Thread(new WatchFolder(socket)).start();

    }
    public void init(){
        frame = new JFrame(nickName);
        frame.setLayout(null);
        frame.setBounds(400, 400, 1000, 480);
        frame.setResizable(false);
        frame.setVisible(true);

        JLabel nameLabel = new JLabel("Name: "+ nickName);
        nameLabel.setBounds(300, 28, 100, 30);

        disconnectBtn = new JButton("Disconnect");
        disconnectBtn.setBounds(10, 10, 150, 30);

        labelPath = new JLabel("Path: " + pathDirectory);
        labelPath.setBounds(600, 28, 600, 30);

        defaultTableModel = new DefaultTableModel(
                new String[] { "No", "Monitoring directory", "Time", "Action", "Name Client", "Description" }, 0) {
            public Class getColumnClass(int column) {
                Class returnValue;
                if ((column >= 0) && (column < getColumnCount())) {
                    returnValue = getValueAt(0, column).getClass();
                } else {
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
        table = new JTable();
        table.setModel(defaultTableModel);
        table.setAutoCreateRowSorter(true);
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(defaultTableModel);
        table.setRowSorter(sorter);
        table.setBounds(10, 120, 1160, 300);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(400);

        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setBounds(10, 100, 1030, 320);

        frame.add(tablePane);
        frame.add(nameLabel);
        frame.add(disconnectBtn);
        frame.add(labelPath);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, nickName, "3", "Disconnected", pathDirectory);
                        WatchFolder.watchService.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
        disconnectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                Date curDate = new Date();
                if(socket==null){
                    try{
                        socket = new Socket(confirmedIP, confirmedPort);
                        disconnectBtn.setText("Disconnect");

                        new ClientSend(socket, nickName, "2", "Connected", pathDirectory);
                        new Thread(new ClientReceive(socket)).start();
                        new Thread(new WatchFolder(socket)).start();

                        Object[] obj = new Object[] { defaultTableModel.getRowCount() + 1, pathDirectory,
                                dateFormat.format(curDate), "Connected",
                                nickName,
                                nickName + " connected to server!" };
                        defaultTableModel.addRow(obj);
                        table.setModel(defaultTableModel);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else if(socket!=null&&socket.isConnected()){
                    try {
                        new ClientSend(socket, nickName, "3", "Disconnected", pathDirectory);
                        disconnectBtn.setText("Connect");
                        WatchFolder.watchService.close();
                        socket.close();
                        socket = null;
                        Object[] obj = new Object[] { defaultTableModel.getRowCount() + 1, pathDirectory,
                                dateFormat.format(curDate), "Disconnected",
                                nickName,
                                "(Notification) " + nickName + " disconnected to server!" };
                        defaultTableModel.addRow(obj);
                        table.setModel(defaultTableModel);
                    }
                    catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }
}
