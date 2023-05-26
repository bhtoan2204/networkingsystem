package Client;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WatchFolder implements Runnable {
    public static WatchService watchService;
    private Socket s;
    public WatchFolder(Socket s){this.s = s;}

    @Override
    public void run() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path directory = Path.of(ClientHandler.pathDirectory);
            WatchKey watchKey = directory.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            while (true){
                for(WatchEvent<?> event: watchKey.pollEvents()){
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path fileName = pathEvent.context();
                    WatchEvent.Kind<?> kind = event.kind();
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    Date curDate = new Date();
                    if(kind==StandardWatchEventKinds.ENTRY_CREATE){
                        Object[] obj = new Object[] { ClientHandler.defaultTableModel.getRowCount() + 1,
                                ClientHandler.pathDirectory,
                                dateFormat.format(curDate), "Created",
                                ClientHandler.nickName,
                                "New file had been created : " + fileName };
                        ClientHandler.defaultTableModel.addRow(obj);
                        ClientHandler.table.setModel(ClientHandler.defaultTableModel);
                        new ClientSend(s, ClientHandler.nickName, "10", "A new file is created : " + fileName,
                                ClientHandler.pathDirectory);
                    }
                    else if(kind==StandardWatchEventKinds.ENTRY_DELETE){
                        Object[] obj = new Object[] { ClientHandler.defaultTableModel.getRowCount() + 1,
                                ClientHandler.pathDirectory,
                                dateFormat.format(curDate), "Deleted",
                                ClientHandler.nickName,
                                "File had been deleted : " + fileName };
                        ClientHandler.defaultTableModel.addRow(obj);
                        ClientHandler.table.setModel(ClientHandler.defaultTableModel);
                        new ClientSend(s, ClientHandler.nickName, "11", "A file has been deleted : " + fileName,
                                ClientHandler.pathDirectory);
                    }
                    else if(kind == StandardWatchEventKinds.ENTRY_MODIFY){
                        Object[] obj = new Object[] { ClientHandler.defaultTableModel.getRowCount() + 1,
                                ClientHandler.pathDirectory,
                                dateFormat.format(curDate), "Modified",
                                ClientHandler.nickName,
                                "File had been modified : " + fileName };
                        ClientHandler.defaultTableModel.addRow(obj);
                        ClientHandler.table.setModel(ClientHandler.defaultTableModel);
                        new ClientSend(s, ClientHandler.nickName, "12", "A file has been modified : " + fileName,
                                ClientHandler.pathDirectory);
                    }
                }
                if(!watchKey.reset()) break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
