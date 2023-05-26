package Client;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientReceive implements Runnable {
    private Socket socket;
    public ClientReceive(Socket s){this.socket = s;}
    public void run(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread.sleep(450);
            while (true){
                String s = br.readLine();
                String[] ss = s.split("\\.");
                String info = ss[0];
                String line = ss[1];
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                Date curDate = new Date();
                if (info.equals("2")){
                    Object[] obj = new Object[] {
                            ClientHandler.defaultTableModel.getRowCount() + 1,
                            ClientHandler.pathDirectory,
                            dateFormat.format(curDate),
                            "Connected",
                            ClientHandler.nickName,
                            ClientHandler.nickName + " connected to server!" };
                    ClientHandler.defaultTableModel.addRow(obj);
                    ClientHandler.table.setModel(ClientHandler.defaultTableModel);
                }
                else if(info.equals("3")){

                    Object[] obj = new Object[] { ClientHandler.defaultTableModel.getRowCount() + 1,
                            ClientHandler.pathDirectory,
                            dateFormat.format(curDate), "Disconnected",
                            ClientHandler.nickName,
                            ClientHandler.nickName + " disconnected to server!" };
                    ClientHandler.defaultTableModel.addRow(obj);
                    ClientHandler.table.setModel(ClientHandler.defaultTableModel);
                }
                else if(info.equals("4")){
                    ClientHandler.disconnectBtn.setText("Log-in");
                    ClientHandler.socket.close();
                    ClientHandler.socket = null;
                    JOptionPane.showMessageDialog(ClientHandler.frame, "Your name has already in used, try again with new window");
                    ClientHandler.frame.dispatchEvent(new WindowEvent(ClientHandler.frame, WindowEvent.WINDOW_CLOSING));
                    break;
                }
                else if(info.equals("5")){
                    Object[] obj = new Object[] { ClientHandler.defaultTableModel.getRowCount() + 1,
                            ClientHandler.pathDirectory,
                            dateFormat.format(curDate), "Server die",
                            ClientHandler.nickName,
                            "Server has been closed" };
                    ClientHandler.defaultTableModel.addRow(obj);
                    ClientHandler.table.setModel(ClientHandler.defaultTableModel);
                    ClientHandler.disconnectBtn.setText("Connect");
                    JOptionPane.showMessageDialog(ClientHandler.frame, "Server disconnect, try again");
                    WatchFolder.watchService.close();
                    ClientHandler.socket.close();
                    ClientHandler.socket = null;
                    break;
                }
                else if(info.equals("13")){
                    ClientHandler.pathDirectory = line + "\\";
                    ClientHandler.labelPath.setText("Path: "+ line);
                    Object[] obj = new Object[] { ClientHandler.defaultTableModel.getRowCount() + 1,
                            ClientHandler.pathDirectory,
                            dateFormat.format(curDate), "Change path",
                            ClientHandler.nickName,
                            "Server send change path" };
                    ClientHandler.defaultTableModel.addRow(obj);
                    ClientHandler.table.setModel(ClientHandler.defaultTableModel);
                    WatchFolder.watchService.close();
                    new Thread(new WatchFolder(this.socket)).start();
                    break;
                }
            }
        }
        catch (IOException e){
            JOptionPane.showMessageDialog(ClientHandler.frame, "This user has left!!!");
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
