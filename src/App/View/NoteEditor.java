package App.View;

import App.Model.LocalNote;
import App.Model.Note;
import App.Model.NoteUrlException;
import App.Utility.EditorOperator;
import App.Utility.PdfMaker;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class NoteEditor{
    @FXML
    private WebView webView;

    private Stage editorStage;

    private Note note;


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

    Stage getEditorStage(){
        return editorStage;
    }

    void displayNoteInEditor(){
        try{
            //if is a new note
            if(note.getUrl().equals("null")){
                webView.getEngine().loadContent(EditorOperator.getEditorPageHtml("",true));
            }else {
                webView.getEngine().loadContent(EditorOperator.getEditorPageHtml(note.getContentHtml(),true));
            }
        }catch(URISyntaxException | IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void handleSave(){
        try{
            //if is a new note
            if(note.getUrl().equals("null")){
                handleExport();
            }else if(note instanceof LocalNote){
                String contentH = EditorOperator.retrieveContentHtml(webView.getEngine().getDocument());

                FileWriter fileWriter = new FileWriter(new File(new URL(note.getUrl()).toURI()));
                fileWriter.write(contentH);
                fileWriter.flush();
            }
        }catch(IOException | URISyntaxException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void handleExport(){
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export to Disk");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML","*.html"));

            File target = fileChooser.showSaveDialog(editorStage);
            if(target != null){
                String contentH = EditorOperator.retrieveContentHtml(webView.getEngine().getDocument());

                FileWriter fileWriter = new FileWriter(target);
                fileWriter.write(contentH);
                fileWriter.flush();

                //if is a new note
                if(note.getUrl().equals("null")){
                    note.setTitle(target.getName());
                    note.setUrl(target.toURI().toURL());
                }
            }
        }catch(IOException | NoteUrlException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void handleToPdf(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export as PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF","*.pdf"));

        File target = fileChooser.showSaveDialog(editorStage);
        if(target != null){
            Document pageDom = Jsoup.parse
                    ("<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<meta charset=\"UTF-8\">\n" + "<title>new note</title>\n" +
                            "</head>\n" + "<body>\n" + "<div>\n" + "</div>\n" + "</body>\n" + "</html>\n");
            String contentHtml = EditorOperator.retrieveContentHtml(webView.getEngine().getDocument());
            pageDom.getElementsByTag("div").html(contentHtml);
            String pageHtml = pageDom.outerHtml();

            PdfMaker.makePdf(pageHtml, target.getAbsolutePath());
        }
    }
}
