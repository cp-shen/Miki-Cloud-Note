package App.Utility;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class WindowUtil{
    public static void showNotifyDialog(Window owner, String notification){
        Label notifyLabel = new Label(notification);

        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(notifyLabel));
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.show();
    }

    public static void showConfirmDialog(Window owner, String msg,
                    EventHandler<ActionEvent> okClickedHandler, EventHandler<ActionEvent> cancelClickedHandler){

        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        Label msgLabel = new Label(msg);
        HBox hbox = new HBox(10d, msgLabel, okButton, cancelButton);
        Stage dialogStage = new Stage();

        okButton.setOnAction(event -> {
            okClickedHandler.handle(event);
            dialogStage.close();
        });
        cancelButton.setOnAction(clicked -> {
            cancelClickedHandler.handle(clicked);
            dialogStage.close();
        });

        dialogStage.setScene(new Scene(hbox));
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.show();
    }

    public static void showInputDialog(Window owner, String initText, String title, TextHandler textHandler){
        TextField textField = new TextField(initText);
        Button okButton = new Button("OK");
        HBox hBox = new HBox(10d, textField, okButton);

        Stage dialogStage = new Stage();
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(hBox));
        dialogStage.setTitle(title);
        dialogStage.show();

        okButton.setOnAction(event -> {
            String input = textField.getText();
            textHandler.handle(input);
            dialogStage.close();
        });
    }

}
