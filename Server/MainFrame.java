package Server;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainFrame {
    public static JFrame frame;
    public static JList<String> listUser;
    public static Map<String, String> mapPath = new HashMap<String, String>();
    public static Map<String, Socket> map = new HashMap<String, Socket>();
    public static String address;
    public static DefaultTableModel defaultTableModel;
    public static JTable table;
    public static String defaultPath = "D:";
    public static JButton disconBtn;

    public static JTextField message;

    public void init(int port){
        frame = new JFrame("Monitoring System");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setBounds(200, 200, 1200, 480);

        JLabel tittle = new JLabel("Server Monitoring System");
        tittle.setFont(new Font("Serif", Font.PLAIN, 50));
        tittle.setBounds(400, 0, 800 , 100);
        frame.add(tittle);

        JLabel note = new JLabel("Double click name of each client to change their monitoring path");
        note.setBounds(10, 10, 700, 100);

        System.out.println("IP: "+ String.valueOf(address));
        System.out.println("Port: "+ port);

        JLabel label_text = new JLabel("List client");
        label_text.setBounds(10, 80, 100, 30);

        listUser = new JList<String>();
        JScrollPane pane = new JScrollPane(listUser);
        pane.setBounds(10, 110, 130, 320);

        message = new JTextField();
        message.setBounds(0,0,0,0);

        disconBtn = new JButton("Disconnect");
        disconBtn.setBounds(0,0,0,0);

        defaultTableModel = new DefaultTableModel( new String[] {"No", "Directory","Time", "Type action", "Name","Detail" }, 0) {
            public Class getColumnClass(int column) {
                Class result = Object.class;
                if ((column < getColumnCount()) && (column >= 0)) {
                    result = getValueAt(0, column).getClass();
                }
                return result;
            }
        };
        table = new JTable();
        table.setModel(defaultTableModel);
        table.setAutoCreateRowSorter(true);
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(defaultTableModel);
        table.setRowSorter(sorter);
        table.setBounds(145, 110, 1030, 320);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(300);

        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setBounds(145, 110, 1030, 320);

        frame.add(message);
        frame.add(disconBtn);
        frame.add(label_text);
        frame.add(pane);
        frame.add(tablePane);
        frame.add(note);

        listUser.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()){
                    JList src = (JList) e.getSource();
                    String selected = src.getSelectedValue().toString();
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Select Folder");
                    if (Files.isDirectory(Paths.get(mapPath.get(selected))))
                        fileChooser.setCurrentDirectory(new File(mapPath.get(selected)));
                    int findRes = fileChooser.showOpenDialog(frame);
                    if(findRes == fileChooser.APPROVE_OPTION){
                        String pathClient = fileChooser.getCurrentDirectory().getAbsolutePath();
                        try{
                            new ServerSend(map.get(selected), pathClient, "13", "Server");
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                            Date curDate = new Date();
                            Object[] obj = new Object[]{defaultTableModel.getRowCount() + 1, pathClient, dateFormat.format(curDate), "Change path", selected, "Change path system"};
                            defaultTableModel.addRow(obj);
                            table.setModel(defaultTableModel);

                        }
                        catch (IOException ee){
                            ee.printStackTrace();
                        }
                    }
                }
            }
        });
        triggerEvent();
    }

    public void triggerEvent(){
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (ServerHandler.listClient != null && ServerHandler.listClient.size() != 0) {
                    try {
                        new ServerSend(ServerHandler.listClient, "Server die", "5", "Server");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
        disconBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ServerHandler.ss == null || ServerHandler.ss.isClosed()){
                    JOptionPane.showMessageDialog(frame, "Client was closed");
                }
                else {
                    if(ServerHandler.listClient != null && ServerHandler.listClient.size()!=0){
                        try{
                            new ServerSend(ServerHandler.listClient, "", "5", "");
                        }
                        catch (IOException eee){
                            eee.printStackTrace();
                        }
                    }
                    try{
                        ServerHandler.ss.close();
                        ServerHandler.ss = null;
                        ServerHandler.listClient = null;
                        ServerHandler.flag = false;
                    }
                    catch (IOException eeee){
                        eeee.printStackTrace();
                    }
                }
            }
        });
    }

    public MainFrame(int port){
        if(ServerHandler.ss != null && !ServerHandler.ss.isClosed()){
            JOptionPane.showMessageDialog(frame, "Server is running!");
        }
        else {
            try {
                ServerHandler.flag = true;
                address = InetAddress.getLocalHost().getHostAddress();
                new Thread(new ServerHandler(port)).start();
                defaultPath = Paths.get(".").normalize().toAbsolutePath().toString();
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(frame, "Unavailable start server");
            }
        }
        init(port);
    }

}
