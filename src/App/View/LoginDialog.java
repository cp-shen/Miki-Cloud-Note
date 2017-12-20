package App.View;

import App.Client.Client;
import App.Model.GHCredential;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginDialog{
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;

    private Stage dialogStage;

    private Client client;

    void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }

    public void setClient(Client client){
        this.client = client;
    }

    public void initialize(){
    }

    void showCredentials(){
        if(client.getCredential() != null){
            userField.setText(client.getCredential().getUser());
            passwordField.setText(client.getCredential().getPassword());
        }
    }

    @FXML
    private void handleOK(){
        client.setCredential(new GHCredential(userField.getText(), passwordField.getText()));
        dialogStage.close();
    }

    @FXML
    private void handleCancel(){
        dialogStage.close();
    }

}
