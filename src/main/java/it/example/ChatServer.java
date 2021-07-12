package it.example;

import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * ChatServer is responsible of managing connection and thread opened for each client.
 * 
 * @author dpescatore
 */
public class ChatServer {

    private int port; 
    private Set<ChatThread> chatThreads = Collections.synchronizedSet(new HashSet<ChatThread>());

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer(10000);
        server.execute();

    }

    public ChatServer(int port) {
        this.port=port;
    }

    /**
     * Instantiate Socket, accept new incoming connections and instantiate ChatTread objects
     */
    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("ChatServer is listening new connections on " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                ChatThread newUser = new ChatThread(socket, this);
                chatThreads.add(newUser);
                newUser.start();

            }

        } catch (Exception ex) {
            System.out.println("ChatServer: ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     * 
     * @param message     the message to send to other users
     * @param excludeUser the sender of the message to be exluded from receivers
     */
    void broadcast(String message, ChatThread excludeUser) {
        chatThreads.forEach(aUser -> {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        });
    }

    /**
     * Removes the associated ChatThread
     * 
     * @param client the thread to remove from list
     */
    void removeClient(ChatThread client) {
        chatThreads.remove(client);
        System.out.println("A client is quitted");
    }

}
