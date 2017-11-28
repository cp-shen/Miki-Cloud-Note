import javafx.application.Application;
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
            fxmlLoader.setLocation(getClass().getResource("View.fxml"));

            primaryStage.setScene(new Scene(fxmlLoader.load()));

            View view = fxmlLoader.getController();
            view.render();

            primaryStage.show();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
