package it.example;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Responsible of some tests for different scenarios, due to not predictable
 * concurrency interleaving, single testing should be preferred over complete
 * scenario testing.
 */
public class ChatTest {

    @Test
    @DisplayName("Connect to server")
    public void testConnect() throws IOException {

        int port = 10000;
        String host = "localhost";
        Thread t = StartServer(port);
        Socket client = new Socket(host, port);
        assertTrue(client.isConnected());
        client.close();
        StopServer(t);
    }

    @Test
    @DisplayName("Connect to same server multiple clients")
    public void testMultipleConnect() throws IOException {

        int port = 10001;
        String host = "localhost";
        Thread t = StartServer(port);
        Socket client1 = new Socket(host, port);
        Socket client2 = new Socket(host, port);
        Socket client3 = new Socket(host, port);
        assertTrue(client1.isConnected() && client2.isConnected() && client3.isConnected());
        client1.close();
        client2.close();
        client3.close();
        StopServer(t);
    }

    @Test
    @DisplayName("Connect and read from another client")
    public void testConnectAndRead() throws IOException {
        int port = 10002;
        String host = "localhost";
        Thread t = StartServer(port);
        // Connect client 1
        Socket client1 = new Socket(host, port);
        OutputStream output1 = client1.getOutputStream();
        PrintWriter writer1 = new PrintWriter(output1, true);

        // Connect client 2
        Socket client2 = new Socket(host, port);
        OutputStream output2 = client2.getOutputStream();
        PrintWriter writer2 = new PrintWriter(output2, true);

        // After connection each client send a representative string
        writer1.println("client1");
        writer2.println("client2");

        // Read two lines from client1 (first line is sent by server as connection
        // check)
        BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        String userInput1 = in1.readLine();
        userInput1 = in1.readLine();

        // Read two lines from client2 (first line is sent by server as connection
        // check)
        BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        String userInput2 = in2.readLine();
        userInput2 = in2.readLine();

        // Check if client1 has received "client2" and client2 has receved "client1"
        assertTrue(userInput1.contains("client2") && userInput2.contains("client1"));
        writer1.println(":q");
        writer2.println(":q");
        client1.close();
        client2.close();
        StopServer(t);
    }

    @Test
    @DisplayName("Connect and read same string with three clients")
    public void testConnectAndBroadcastRead() throws IOException {

        int port = 10005;
        String host = "localhost";
        Thread t = StartServer(port);
        // Connect client 1
        Socket client1 = new Socket(host, port);
        OutputStream output1 = client1.getOutputStream();
        PrintWriter writer1 = new PrintWriter(output1, true);

        // Connect client 2
        Socket client2 = new Socket(host, port);

        // Connect client 3
        Socket client3 = new Socket(host, port);

        // After connection write a representative string
        writer1.println("client1");

        // Read two lines from client2 (first line is sent by server as connection
        // check)
        BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        String userInput2 = in2.readLine();
        userInput2 = in2.readLine();

        // Read two lines from client2 (first line is sent by server as connection
        // check)
        BufferedReader in3 = new BufferedReader(new InputStreamReader(client3.getInputStream()));
        String userInput3 = in3.readLine();
        userInput3 = in3.readLine();

        // Check if both client2 and client3 has received "client1"
        assertTrue(userInput2.contains("client1") && userInput3.contains("client1"));
        client1.close();
        client2.close();
        client3.close();
        StopServer(t);
    }

    private Thread StartServer(int port) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                ChatServer server = new ChatServer(port);
                server.execute();
            }
        });
        t.start();
        return t;
    }

    private void StopServer(Thread t) {
        t.interrupt();
    }
}
