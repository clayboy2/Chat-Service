/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import networking.Client;
import utils.Storage;

/**
 * FXML Controller class
 *
 * @author amclay2
 */
public class ChatBoxController implements Initializable {

    public static Client client;
    private Listener monitor;
    
    @FXML
    private TextArea inbox;
    @FXML
    private TextField input;
    @FXML
    private TextArea userList;

    public void handleInputEvent(ActionEvent event) {
        String toSend = input.getText();
        if (toSend.equalsIgnoreCase("/disconnect"))
        {
            client.send(toSend);
            monitor.stop();
            ((Node) (event.getSource())).getScene().getWindow().hide();
        }
        
        client.send(toSend);
    }
    
    public void enterKey(ActionEvent event)
    {
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inbox.setEditable(false);
        input.setFocusTraversable(true);
        inbox.setFocusTraversable(false);
        userList.setEditable(false);
        userList.setFocusTraversable(false);
        inbox.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override 
            public void handle(MouseEvent e)
            {
                input.requestFocus();
            }
        });
        try {
            client.recieve();
            client.recieve();
        } catch (IOException ex) {

        }
        monitor = new Listener();
        new Thread(monitor).start();
        input.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke)
            {
                switch(ke.getCode())
                {
                    case ENTER:
                        String toSend = input.getText();
                        client.send(toSend);
                        input.setText("");
                }
            }
        });
    }

    public class Listener implements Runnable {
        private volatile boolean isRunning;
        @Override
        public void run() {
            isRunning = true;
            while (isRunning) {
                String response;
                try {
                    response = client.recieve();
                    if (response.equalsIgnoreCase("userlist stream"))
                    {
                        while(!response.equalsIgnoreCase("end stream"))
                        {
                            response = client.recieve();
                            userList.appendText(response+"\n");
                        }
                    }
                    else
                    {
                        inbox.appendText(response + "\n");
                    }
                } catch (IOException ex) {
                    inbox.appendText("ERROR LOADING RESPONSE");
                }

            }
        }
        
        public void stop()
        {
            isRunning = false;
        }

    }

}
