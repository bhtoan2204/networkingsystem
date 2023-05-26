package Server;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ServerHandler implements Runnable{
    private int port;
    public static ArrayList<Socket> listClient = null;
    public static Vector<String> listNameClient = null;
    public static Map<String, Socket> map = null;
    public static ServerSocket ss = null;
    public static boolean flag = true;
    public ServerHandler(int port){
        this.port = port;
    }
    @Override
    public void run() {
        Socket s = null;
        listClient = new ArrayList<Socket>();
        listNameClient = new Vector<String>();
        map = new HashMap<String, Socket>(); // name to socket one on one map

        System.out.println("Máy chủ đã khởi động!");
        try {
            ss = new ServerSocket(port);
            System.out.println(ss.getLocalSocketAddress());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while (flag) {
            try {
                s = ss.accept();
                listClient.add(s);
                new Thread(new ServerReiceive(s, listClient, listNameClient, map)).start();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(MainFrame.frame, "Đóng máy chủ！");
            }
        }
    }
}
