/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testlibrary;

import gui.ChatBoxController;
import gui.NameController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import networking.Client;
import networking.Server;
import utils.Storage;

/**
 *
 * @author amclay2
 */
public class LoginController implements Initializable {

    private String host;
    private int port;
    @FXML
    private TextField hostBox;
    @FXML
    private TextField portBox;
    @FXML
    private Label responder;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        host = hostBox.getText();
        if (host.equals("")) {
            host = "localhost";
        }
        if (portBox.getText().equals("")) {
            port = 1120;
        } else {
            port = Integer.parseInt(portBox.getText());
        }
        Client c = new Client(host, port);
        NameController.c = c;
        ChatBoxController.client = c;
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/gui/NameSelector.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Name selector");
            stage.setScene(new Scene(root));
            stage.show();
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            System.out.println("Error Launcing name screen");
            e.printStackTrace();
        }
    }

    @FXML
    public void startServer(ActionEvent event)
    {
        Storage.server = new Server();
        new Thread(Storage.server).start();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
