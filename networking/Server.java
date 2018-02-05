/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amclay2
 */
public class Server implements Runnable{

    private ArrayList<Connection> connections;
    private final String salutation = "Welcome to my Chat Server! Please enter a name: ";
    private ArrayList<String> names;
    private final String password = "marshmello";
    private volatile boolean serverRunning;
    
    @Override
    public void run() {
        try {
            connections = new ArrayList<>();
            names = new ArrayList<>();
            ServerSocket server = new ServerSocket(1120);
            serverRunning = true;
            while (serverRunning) {
                System.out.println("Waiting for connections...");
                Socket client = server.accept();
                if (!serverRunning)
                {
                    break;
                }
                System.out.println("Accepted connection from "+client.getInetAddress());
                new PrintWriter(client.getOutputStream(),true).println(salutation);
                ConnectionManager cm = new ConnectionManager(client);
                new Thread(cm).start();
            }
        } catch (IOException e) {
            System.out.println("Error running server");
        }
    }
    
    private void stopServer()
    {
        serverRunning = false;
    }
    
    private class ConnectionManager implements Runnable
    {
        private final Socket s;
        public ConnectionManager(Socket s)
        {
            this.s = s;
        }
        
        @Override
        public void run()
        {
            BufferedReader in;
            PrintWriter out;
            try {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(),true);
            } catch (IOException ex) {
                return;
            }
            while(true)
            {
                boolean goodName = true;
                out.println("Please enter a name: ");
                String response = "";
                try {
                    response = in.readLine();
                } catch (IOException ex) {
                    
                }
                if (names.contains(response.toLowerCase()))
                {
                    out.println("Name in use already. Pick a new name");
                    goodName = false;
                }
                if (response.equals(""))
                {
                    out.println("Invalid name choice. Please try again.");
                    goodName = false;
                }
                if (goodName)
                {
                    out.println("good name");
                    names.add(response);
                    connections.add(new Connection(s, out, in, response));
                    break;
                }
            }
        }
    }
    
    public boolean enterPassword(String userPassword)
    {
        return (userPassword.equals(password));
    }

    private class Connection {

        private final Socket connection;
        private final PrintWriter out;
        private final BufferedReader in;
        private final Listener monitor;
        private final String name;

        private void init()
        {
            try {
                recieve();
            } catch (IOException ex) {
            }
            sendAll("userlist stream");
            names.forEach((n) -> {
                sendAll(n);
            });
            sendAll("end stream");
            new Thread(monitor).start();
        }
        
        public Connection(Socket s, PrintWriter out, BufferedReader in, String name) {
            this.connection = s;
            this.out = out;
            this.in = in;
            this.name = name;
            monitor = new Listener();
            init();
        }

        public class Listener implements Runnable {

            private volatile boolean shouldStop = false;

            @Override
            public void run() {
                try {
                    while (!shouldStop) {
                        String response = recieve();
                        if (response.equals(""))
                        {
                            continue;
                        }
                        if (response.equalsIgnoreCase("/disconnect")) {
                            sendAll(name + " has left the chat");
                            stop();
                            removeConnection(getConnection());
                            names.remove(name);
                            sendNames();
                            break;
                        }
                        else if (response.charAt(0) == '/') {
                            response = response.substring(1);
                            switch(response)
                            {
                                case "refresh users":
                                    sendNames();
                                    break;
                                case "help":
                                    send("Available server commands ('help','disconnect', refresh users')");
                                    break;
                                case "stop server":
                                    send("Please enter server password: ");
                                    if (enterPassword(recieve()))
                                    {
                                        stopServer();
                                    }
                                default:
                                    send("Invalid server command");
                                    break;
                            }
                        } else {
                            response = name + ": " + response;
                            sendAll(response);
                            System.out.println(response);
                        }
                    }
                } catch (IOException e) {
                    stop();
                }
            }

            public void stop() {
                this.shouldStop = true;
            }
            
            private void sendNames()
            {
                sendAll("userlist stream");
                for(String name : names)
                {
                    System.out.println(name);
                    sendAll(name);
                }
                sendAll("end stream");
            }
        }

        public void send(String message) {
            out.println(message);
        }

        public void sendAll(String message) {
            for (Connection c : connections) {
                c.send(message);
            }
        }

        public String recieve() throws IOException {
            return in.readLine();
        }

        private void removeConnection(Connection c) {
            connections.remove(c);
        }

        private Connection getConnection() {
            return this;
        }
    }
}
