package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerSend {
    ServerSend(ArrayList<Socket> lC, Object msg, String info, String name) throws IOException {
        String messages = info + "." + msg + "." + name;
        PrintWriter pw = null;
        for (Socket s : lC) {
            pw = new PrintWriter(s.getOutputStream(), true);
            pw.println(messages);
        }
    }
    ServerSend(Socket s, Object message, String info, String name) throws IOException {
        String messages = info + "." + message + "." + name;
        PrintWriter pwOut = new PrintWriter(s.getOutputStream(), true);
        pwOut.println(messages);
    }
}
