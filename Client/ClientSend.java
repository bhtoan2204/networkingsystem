package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSend {
    public ClientSend(Socket s, Object message, String info, String name, String pathDirectory) throws IOException {
        String messages = info + ",," + message + ",," + name + ",," + pathDirectory;
        PrintWriter pwOut = new PrintWriter(s.getOutputStream(), true);
        pwOut.println(messages);
    }
}
