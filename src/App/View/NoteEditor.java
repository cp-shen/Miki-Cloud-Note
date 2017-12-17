package App.View;

import App.Model.Note;
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

    Stage getEditorStage(){
        return editorStage;
    }

    void displayNoteInEditor(){
        try{
            if(note.getUrl() != null){
                String contentHtml = Jsoup.parse(new File(note.getUrl().toURI()),"UTF-8").outerHtml();
                webView.getEngine().loadContent(EditorOperator.editorInit(contentHtml,true));
            }else {
                //if is a new note
                webView.getEngine().loadContent(EditorOperator.editorInit("",true));
            }
        }catch(URISyntaxException | IOException ex){
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
                String contentH = EditorOperator.getContentHtml(webView.getEngine().getDocument());

                FileWriter fileWriter = new FileWriter(new File(note.getUrl().toURI()));
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
                String contentH = EditorOperator.getContentHtml(webView.getEngine().getDocument());

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
            String contentHtml = EditorOperator.getContentHtml(webView.getEngine().getDocument());
            pageDom.getElementsByTag("div").html(contentHtml);
            String pageHtml = pageDom.outerHtml();

            PdfMaker.makePdf(pageHtml, target.getAbsolutePath());
        }
    }
}
