import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.*;

public class Board_PVP_Channel{

    public static void main(String [] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java Board_PVP_Channel <port>");
            System.exit(1);
        }
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        Board_PVP_Channel server = new Board_PVP_Channel(serverSocket);
        server.startServer();
    }

    private ServerSocket serverSock;
    
    public Board_PVP_Channel(ServerSocket serverSocket){
            this.serverSock = serverSocket;
    }
    public void startServer() {
        try {
            while (!serverSock.isClosed()){
                Socket clientSock = serverSock.accept();
                ClientHandler handler = new ClientHandler(clientSock);
                if (!handler.approve().equals("neither")) {
                    continue;
                }
                else {
                    System.out.println("A client has just connected.");
                    Thread thread = new Thread(handler);
                    thread.start();
                }
            }
        } catch(IOException e){} 
    }
    public void closeServerSocket() {
        try {
           if (serverSock != null) {
                serverSock.close();
           }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> ClientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter bufferedWriter;
    private String clientUsername;
    private String string_approval = "neither";

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.bufferedWriter = new PrintWriter(socket.getOutputStream(), true);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String msg = bufferedReader.readLine();
                if (msg.equals("SECRET")) {
                    msg = bufferedReader.readLine();
                    if (msg.equals("3c3c4ac618656ae32b7f3431e75f7b26b1a14a87")){
                        continue;
                    }
                    else {
                        System.err.println("SECRET wrong.");
                        this.socket.close();
                    }
                } 
                else if (msg.equals("LOGIN_VALIDATION")) {
                    String name = bufferedReader.readLine();
                    for (ClientHandler clientHandler: ClientHandlers){
                        if (clientHandler.clientUsername.equals(name)){
                            bufferedWriter.println("UNAPPROVED_VALIDATION");
                            bufferedWriter.flush();
                            string_approval = "false";
                            return;
                        }
                    }
                    bufferedWriter.println("APPROVED_VALIDATION");
                    bufferedWriter.flush();
                    string_approval = "true";
                    return;
                }
                if (msg.equals("NAME")) {
                    msg = bufferedReader.readLine();  
                    this.clientUsername = msg;
                    break;
                }
                
            }
            ClientHandlers.add(this);
            int i = 0;
            StringBuilder str = new StringBuilder();
            for (ClientHandler clientHandler: ClientHandlers){
                if (i == 0 || i == 1) {
                    str.append(clientHandler.clientUsername + " (Player)\n");
                }
                else {
                    str.append(clientHandler.clientUsername + "\n");
                }
                i += 1;
            }
            bufferedWriter.println("START_CLIENT_LIST\n" + str + "END_CLIENT_LIST");
            bufferedWriter.flush(); 
            broadcastMessageWithName("SERVER: " + clientUsername + " has entered the chat!");
            broadcastMessage("SERVER_LOG: " + clientUsername + " has entered the chat!");
            bufferedWriter.flush();
            
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

    @Override
    public void run(){
        String messageFromClient;
        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient.equals("FNAJDSIFNIJNSDIFNISDNIFGUBIFDAISFIOT")) {
                    String name = bufferedReader.readLine();
                    for (ClientHandler clientHandler: ClientHandlers)
                    {
                        if (clientHandler.clientUsername.equals(name)){
                            ClientHandlers.remove(clientHandler);
                            broadcastMessageWithName("SERVER: " + name + " has left the chat!"); 
                            broadcastMessage("SERVER_LOG: " + clientUsername + " has left the chat!");
                            bufferedWriter.flush();
                            break;
                        }
                    }
                    int i = 0;
                    StringBuilder str = new StringBuilder();
                    for (ClientHandler ch: ClientHandlers){
                        if (i == 0 || i == 1) {
                            str.append(ch.clientUsername + " (Player)\n");
                        }
                        else {
                            str.append(ch.clientUsername + "\n");
                        }
                        i += 1;
                    }
                    //bufferedWriter.println("START_CLIENT_LIST\n" + str + "END_CLIENT_LIST");
                    broadcastMessage("START_CLIENT_LIST\n" + str + "END_CLIENT_LIST");
                    bufferedWriter.flush();  
                    break;
                }

                else if (messageFromClient.equals("FNAJDSIFNIJNSDIFNISDNIFGUBIFDAISFIOTE")) { 
                    String name = bufferedReader.readLine();

                    for (ClientHandler clientHandler: ClientHandlers)
                    {
                        if (clientHandler.clientUsername.equals(name)){
                            ClientHandlers.remove(clientHandler);
                            broadcastMessageWithName("SERVER: " + name + " has left the chat!"); 
                            broadcastMessage("SERVER_LOG: " + clientUsername + " has left the chat!");
                            bufferedWriter.flush();
                            break;
                        }
                    }
                    break;
                }

                else if (messageFromClient.equals("INPUT_MOVE")) {
                    String i_row_col = bufferedReader.readLine();
                    String color_update = bufferedReader.readLine();
                    broadcastMessage("SENDBACK_KEY_COLOR\n" + i_row_col + " " + color_update);
                    bufferedWriter.flush();
                    continue;
                }
                else if (messageFromClient.equals("UPDATE_SPECTATE_COLOR")) {
                    StringBuilder str = new StringBuilder();
                    for (int i = 0; i < 9; i++){
                        if (i != 8) {
                            str.append(bufferedReader.readLine() + "\n");
                        }
                        else {
                            str.append(bufferedReader.readLine());
                        }
                    }
                    broadcastMessage("SENDBACK_UPDATE_COLOR\n" + str);
                    bufferedWriter.flush();
                    continue;
                }
                else if (messageFromClient.equals("RESET_GAME")) {
                    broadcastMessage("SENDBACK_RESET");
                    bufferedWriter.flush();
                    continue;
                }
                else if (messageFromClient.equals("WIN_MESSAGE")) {
                    String msg = bufferedReader.readLine();
                    massbroadcastMessage(msg);
                    continue;
                }
                else if (messageFromClient.equals("REORDER_CLIENT")) {
                    String clientToBeReorder = bufferedReader.readLine();
                    for (ClientHandler clientHandler: ClientHandlers) {
                        if (clientHandler.clientUsername.equals(clientToBeReorder)){
                            ClientHandler client_added = clientHandler;
                            ClientHandlers.remove(clientHandler);
                            massbroadcastMessageWithName("SERVER: " + clientHandler.clientUsername +  " has left the chat!"); 
                            bufferedWriter.flush();

                            ClientHandlers.add(client_added);
                            massbroadcastMessageWithName("SERVER: " + clientHandler.clientUsername +  " has entered the chat!");
                            bufferedWriter.flush();
                            break;
                        }
                    }
                    int i = 0;
                    StringBuilder str = new StringBuilder();
                    for (ClientHandler ch: ClientHandlers){
                        if (i == 0 || i == 1) {
                            str.append(ch.clientUsername + " (Player)\n");
                        }
                        else {
                            str.append(ch.clientUsername + "\n");
                        }
                        i += 1;
                    }
                    massbroadcastMessage("START_CLIENT_LIST\n" + str + "END_CLIENT_LIST");
                    bufferedWriter.flush();  
                    continue;
                }
                else if (messageFromClient.equals("UPDATE_LIST")) {
                    int i = 0;
                    StringBuilder str = new StringBuilder();
                    for (ClientHandler ch: ClientHandlers){
                        if (i == 0 || i == 1) {
                            str.append(ch.clientUsername + " (Player)\n");
                        }
                        else {
                            str.append(ch.clientUsername + "\n");
                        }
                        i += 1;
                    }
                    massbroadcastMessage("START_CLIENT_LIST\n" + str + "END_CLIENT_LIST");
                    bufferedWriter.flush();  
                    continue;
                }
                broadcastMessage(messageFromClient);

            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            } 

        }  
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler: ClientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.println(messageToSend);
                    clientHandler.bufferedWriter.flush();
                }
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void massbroadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler: ClientHandlers) {
            try {
                clientHandler.bufferedWriter.println(messageToSend);
                clientHandler.bufferedWriter.flush();
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void broadcastMessageWithName(String messageToSend) {
        for (ClientHandler clientHandler: ClientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)){
                        clientHandler.bufferedWriter.println(messageToSend);
                        clientHandler.bufferedWriter.flush();
                        clientHandler.bufferedWriter.println(clientUsername);
                        clientHandler.bufferedWriter.flush();
                    }
                } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void  massbroadcastMessageWithName(String messageToSend) {
        for (ClientHandler clientHandler: ClientHandlers) {
            try {
                clientHandler.bufferedWriter.println(messageToSend);
                clientHandler.bufferedWriter.println(clientHandler.clientUsername);
                clientHandler.bufferedWriter.flush();
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void removeClientHandler() {
        ClientHandlers.remove(this);
        broadcastMessageWithName("SERVER: " + clientUsername + " has left the chat!");
        bufferedWriter.flush();
        int i = 0;
        StringBuilder str = new StringBuilder();
        for (ClientHandler clientHandler: ClientHandlers){
            if (i == 0 || i == 1) {
                str.append(clientHandler.clientUsername + " (Player)\n");
            }
            else {
                str.append(clientHandler.clientUsername + "\n");
            }
            i += 1;
        }
        bufferedWriter.println("START_CLIENT_LIST\n" + str + "END_CLIENT_LIST");
        bufferedWriter.flush(); 
    }
    public String approve(){
        return this.string_approval;
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, PrintWriter bufferedWriter){
        removeClientHandler(); 
        try {
            if (bufferedReader != null)
            {
                bufferedReader.close();
            }
            if (bufferedWriter != null)
            {
                bufferedWriter.close();
            }
            if (socket != null)
            {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}  