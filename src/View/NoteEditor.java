package View;

import Domain.Note;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class NoteEditor{
    private Note note;
    @FXML
    private WebView webView;
    private Stage editorStage;

    public void initialize(){ }

    void setNote(Note note){
        this.note = note;
    }

    /**
     * Set editor stage.
     * @param editorStage the editor stage
     *                    Used to pop up file chooser
     */
    void setEditorStage(Stage editorStage){
        this.editorStage = editorStage;
    }

    void displayNoteInEditor(){
        try{
            if(note.getUrl() != null){
                webView.getEngine().load(note.getUrl().toString());
            }else {
                //if is a new note
                File editorFile = new File("editor/editor.html");
                webView.getEngine().load(editorFile.toURI().toURL().toString());
            }
        }catch(MalformedURLException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void handleSave(){
        try{
            //if is a new note
            if(note.getUrl() == null){
                handleExport();
            }else{
                org.w3c.dom.Document w3cDoc = webView.getEngine().getDocument();
                Document jSoupDoc = Jsoup.parse(new W3CDom().asString(w3cDoc));
                String contentH = jSoupDoc.outerHtml();

                FileWriter fileWriter = new FileWriter(new File(note.getUrl().toURI()));
                fileWriter.write(contentH);
                fileWriter.flush();
            }
        }catch(IOException | URISyntaxException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleExport(){
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export to Disk");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML","*.html"));

            File target = fileChooser.showSaveDialog(editorStage);
            if(target != null){
                org.w3c.dom.Document w3cDoc = webView.getEngine().getDocument();
                org.jsoup.nodes.Document jSoupDoc = Jsoup.parse(new W3CDom().asString(w3cDoc));
                String contentH = jSoupDoc.outerHtml();

                FileWriter fileWriter = new FileWriter(target);
                fileWriter.write(contentH);
                fileWriter.flush();

                //if is a new note
                if(note.getUrl() == null){
                    note.setTitle(target.getName());
                    note.setUrl(target.toURI().toURL());
                }
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
     }
}
