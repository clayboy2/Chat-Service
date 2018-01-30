/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;


import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import networking.Client;

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

    public void handleInputEvent(ActionEvent event) {
        String toSend = input.getText();
        client.send(toSend);
    }
    
    public void enterKey(ActionEvent event)
    {
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inbox.setDisable(true);
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

        @Override
        public void run() {
            while (true) {
                String response;
                try {
                    response = client.recieve();
                    inbox.appendText(response + "\n");
                } catch (IOException ex) {
                    inbox.appendText("ERROR LOADING RESPONSE");
                }

            }
        }

    }

}
