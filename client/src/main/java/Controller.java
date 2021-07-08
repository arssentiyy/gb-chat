import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Controller {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;



    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private HBox authPanel;

    public Controller(){
        connection();
    }

    private void connection() {
        try {
            this.socket = new Socket("localhost", 8189);
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {

        }
    }

    public void sendAuth(ActionEvent actionEvent) {
    }
}
