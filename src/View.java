import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import java.net.URL;

public class View{
    @FXML
    private WebView webView;

    public void initialize(){
    }

    public void render(){
        URL url = this.getClass().getResource("editor.html");
        webView.getEngine().load(url.toString());
    }
}
