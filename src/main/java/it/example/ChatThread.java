package it.example;
import java.io.*;
import java.net.*;
 
/**
 * The thread is responsible to manage a single connection from client to server and is opened by server on each connection.
 *
 * @author dpescatore
 */
public class ChatThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;
 
    public ChatThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }
 
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
 
            String clientCode = output.hashCode()+"";
            writer.println("Connected as: "+clientCode);
            
            String clientMessage=null;
            
            do {
                try {
                    clientMessage = reader.readLine();
                    String toSent= "[" + clientCode + "]: " + clientMessage;
                    server.broadcast(toSent, this);
 
                } catch (SocketException e) {
                    //This exception may occur when client force disconnect
                    clientMessage=null;
                }
                
            } while (clientMessage!=null && !clientMessage.equals(":q"));
 
            server.removeClient(this);
            socket.close();
 
 
        } catch (Exception ex) {
            System.out.println("ChatThread: ERROR " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    /**
     * Sends a message to the client.
     * @param message
     */
    public void sendMessage(String message) {
        if (writer!=null)
            writer.println(message);
    }
}