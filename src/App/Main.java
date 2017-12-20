package App;

import App.Client.Client;
import App.View.MainView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("View/MainView.fxml"));

            primaryStage.setScene(new Scene(fxmlLoader.load()));
            primaryStage.setTitle("Miki Cloud Note");

            Client client = new Client();
            client.setNotes(FXCollections.observableArrayList());

            MainView mainView = fxmlLoader.getController();
            mainView.setPrimaryStage(primaryStage);
            mainView.setNotes(client.getNotes());
            mainView.setClient(client);

            primaryStage.show();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
