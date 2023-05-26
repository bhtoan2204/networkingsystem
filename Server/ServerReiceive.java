package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerReiceive implements Runnable{
    private Socket socket;
    private ArrayList<Socket> listClient;
    private Vector<String> listNameClient;
    private Map<String, Socket> map;
    public ServerReiceive(Socket s, ArrayList<Socket> lC, Vector<String> nC, Map<String, Socket> map){
        this.socket = s;
        this.listClient = lC;
        this.listNameClient = nC;
        this.map = map;
    }
    @Override
    public void run() {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true){
                String s = br.readLine();
                String[] ss = s.split(",,");
                String info = ss[0];
                String line = ss[1];
                String name = ss[2];
                String path = ss[3];
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                Date curDate = new Date();
                if(info.equals("1")){
                    new ServerSend(listClient, line, "1", "");

                } else if (info.equals("2")) {
                    if(!listNameClient.contains(line)){
                        listNameClient.add(line);
                        MainFrame.map.put(line, socket);
                        MainFrame.mapPath.put(line, path);
                        MainFrame.listUser.setListData(listNameClient);
                        new ServerSend(listClient, name, "2", line);
                        Object[] obj = new Object[]{
                                MainFrame.defaultTableModel.getRowCount() + 1,
                                path,
                                dateFormat.format(curDate),
                                "Connected",
                                line,
                                name
                        };
                        MainFrame.defaultTableModel.addRow(obj);
                        MainFrame.table.setModel(MainFrame.defaultTableModel);
                    }
                    else {
                        listClient.remove(socket);
                        new ServerSend(socket, "", "4", "Server");
                    }
                } else if (info.equals("3")) {

                    Object[] obj = new Object[]{
                            MainFrame.defaultTableModel.getRowCount() + 1, path,
                            dateFormat.format(curDate), "Disconnected",
                            line,
                            name
                    };
                    MainFrame.defaultTableModel.addRow(obj);
                    MainFrame.table.setModel(MainFrame.defaultTableModel);

                    listClient.remove(socket);
                    listNameClient.remove(line);
                    MainFrame.map.remove(line);
                    MainFrame.mapPath.remove(line);
                    MainFrame.listUser.setListData(listNameClient);
                    new ServerSend(listClient, listNameClient, "3", line);
                    socket.close();
                    break;
                } else if (info.equals("10")) {
                    Object[] obj = new Object[] { MainFrame.defaultTableModel.getRowCount() + 1, path,
                            dateFormat.format(curDate), "Created",
                            line,
                            name };
                    MainFrame.defaultTableModel.addRow(obj);
                    MainFrame.table.setModel(MainFrame.defaultTableModel);
                } else if (info.equals("11")) {
                    Object[] obj = new Object[]{
                            MainFrame.defaultTableModel.getRowCount() + 1, path,
                            dateFormat.format(curDate), "Deleted",
                            line,
                            name
                    };
                    MainFrame.defaultTableModel.addRow(obj);
                    MainFrame.table.setModel(MainFrame.defaultTableModel);
                } else if (info.equals("12")) {
                    Object[] obj = new Object[]{
                            MainFrame.defaultTableModel.getRowCount() + 1, path,
                            dateFormat.format(curDate), "Modified",
                            line,
                            name
                    };
                    MainFrame.defaultTableModel.addRow(obj);
                    MainFrame.table.setModel(MainFrame.defaultTableModel);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
