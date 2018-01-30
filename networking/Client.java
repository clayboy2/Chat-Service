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
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author amclay2
 */
public class Client {

    private PrintWriter out;
    private BufferedReader in;
    private Socket mySock;
    private Listener monitor;
    private Scanner keys;

    private void init(String host, int port) {
        this.keys = new Scanner(System.in);
        this.monitor = new Listener();
        try {
            this.mySock = new Socket(host, port);
            this.out = new PrintWriter(mySock.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(mySock.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error has occoured");
            e.printStackTrace();
        }
    }
    
    public String getHost()
    {
        return mySock.getInetAddress().toString();
    }
    public Client() {
        init("localhost", 1120);
    }

    public Client(String host, int port) {
        init(host, port);
    }

    public void run() {
        new Thread(monitor).start();
        System.out.println("Send a response: ");
        String response;
        while (true) {
            response = keys.nextLine();
            if (response.equalsIgnoreCase("disconnect")) {
                monitor.stop();
                send(response);
                break;
            }
            send(response);
        }
    }

    public boolean isGoodConnection()
    {
        return mySock.isBound();
    }
            
    public void send(String message) {
        out.println(message);
    }

    public String recieve() throws IOException {
        return in.readLine();
    }

    private class Listener implements Runnable {

        private volatile boolean shouldStop = false;

        @Override
        public void run() {
            try {
                while (!shouldStop) {
                    System.out.println(recieve());
                }
            } catch (IOException e) {
                stop();
            }
        }

        public void stop() {
            this.shouldStop = true;
        }
    }
}