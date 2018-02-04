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
import utils.Storage;

/**
 *
 * @author amclay2
 */
public class Server implements Runnable{

    private ArrayList<Connection> connections;
    private final String salutation = "Welcome to my Chat Server! Please enter a name: ";
    private ArrayList<String> names;
    
    @Override
    public void run() {
        try {
            connections = new ArrayList<>();
            names = new ArrayList<>();
            ServerSocket server = new ServerSocket(1120);
            while (true) {
                System.out.println("Waiting for connections...");
                Socket client = server.accept();
                System.out.println("Accepted connection from "+client.getInetAddress());
                new PrintWriter(client.getOutputStream(),true).println(salutation);
                ConnectionManager cm = new ConnectionManager(client);
                new Thread(cm).start();
            }
        } catch (IOException e) {
            System.out.println("Error running server");
        }
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
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(),true);
            } catch (IOException ex) {
                
            }
            if (in==null||out==null)
            {
                System.exit(0);
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
                if (Storage.names.contains(response.toLowerCase()))
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
                    connections.add(new Connection(s, out, in, response));
                    break;
                }
            }
        }
    }

    private class Connection {

        private final Socket connection;
        private final PrintWriter out;
        private final BufferedReader in;
        private final Listener monitor;
        private final String name;

        public Connection(Socket s, PrintWriter out, BufferedReader in, String name) {
            this.connection = s;
            this.out = out;
            this.in = in;
            this.name = name;
            monitor = new Listener();
            new Thread(monitor).start();
            sendAll("userlist stream");
            for (String n : names)
            {
                sendAll(n);
            }
            sendAll("end stream");
        }

        public class Listener implements Runnable {

            private volatile boolean shouldStop = false;

            @Override
            public void run() {
                try {
                    while (!shouldStop) {
                        String response = recieve();
                        if (response.equalsIgnoreCase("/disconnect")) {
                            sendAll(name + " has left the chat");
                            stop();
                            removeConnection(getConnection());
                            Storage.names.remove(name);
                            sendAll("userlist stream");
                            for(String name : names)
                            {
                                sendAll(name);
                            }
                            sendAll("end stream");
                            break;
                        }
                        if (response.charAt(0) == '/') {
                            //Do a command...
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
