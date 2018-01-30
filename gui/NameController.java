/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import networking.Client;
import utils.Storage;

/**
 * FXML Controller class
 *
 * @author amclay2
 */
public class NameController implements Initializable {

    public static Client c;

    @FXML
    private TextField nameBox;
    @FXML
    private Label response;
    @FXML
    private Label salutation;

    public void onButtonPush(ActionEvent event) {
        String name = nameBox.getText();
        response = new Label();
        System.out.println("Selected name: " + name);
        if (name.equals("")) {
            response.setText("Invalid Name");
        } else if (Storage.names.contains(name)) {
            response.setText("Name in use already, please select another");
        } else {
            try {
                c.send(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Storage.names.add(name);
            //Launch chat service
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("/gui/ChatBox.fxml"));
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
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                switch (ke.getCode()) {
                    case ENTER:
                        String name = nameBox.getText();
                        response = new Label();
                        System.out.println("Selected name: " + name);
                        if (name.equals("")) {
                            response.setText("Invalid Name");
                        } else if (Storage.names.contains(name)) {
                            response.setText("Name in use already, please select another");
                        } else {
                            try {
                                c.send(name);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Storage.names.add(name);
                            //Launch chat service
                            Parent root;
                            try {
                                root = FXMLLoader.load(getClass().getResource("/gui/ChatBox.fxml"));
                                Stage stage = new Stage();
                                stage.setTitle("Name selector");
                                stage.setScene(new Scene(root));
                                stage.show();
                                ((Node) (ke.getSource())).getScene().getWindow().hide();
                            } catch (IOException e) {
                                System.out.println("Error Launcing name screen");
                                e.printStackTrace();
                            }

                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
