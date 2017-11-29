import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

    @FXML
    private void handleSave(){
        Document document = webView.getEngine().getDocument();
        Element editorElement = document.getElementById("div2");
        System.out.println(editorElement.getTextContent());
    }
}
