package App.View;

import App.Client.Client;
import App.Model.LocalNote;
import App.Model.Note;
import App.Model.NoteUrlException;
import App.Model.OnlineNote;
import App.Utility.EditorUtil;
import App.Utility.GistUtil;
import App.Utility.PdfMaker;
import App.Utility.WindowUtil;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.egit.github.core.Gist;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

public class EditorView{
    @FXML
    private WebView webView;

    private Stage editorStage;

    private Note note;

    private Client client;

    public void initialize(){ }

    void setClient(Client client){
        this.client = client;
    }

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
            if(note.getUrl() == null){
                webView.getEngine().loadContent(EditorUtil.generateEditorPageHtml("",true));
            }else {
                webView.getEngine().loadContent(EditorUtil.generateEditorPageHtml(note.getContentHtml(),true));
            }
        }catch(URISyntaxException | IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void handleSave(){
        try{
            //if is a new local note
            if(note.getUrl() == null){
                handleExport();
            }else if(note instanceof OnlineNote){
                WindowUtil.showNotifyDialog(editorStage, "This is not a local note.");
            }else {
                String contentH = EditorUtil.retrieveContentHtml(webView.getEngine().getDocument());

                FileOutputStream fos = new FileOutputStream(new File(new URL(note.getUrl()).toURI()));
                OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
                writer.write(contentH);
                writer.flush();

                //update content in main memory
                note.updateContentByUrl();
                WindowUtil.showNotifyDialog(editorStage, "Saved changes to disk");
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
                String contentH = EditorUtil.retrieveContentHtml(webView.getEngine().getDocument());

                FileOutputStream fos = new FileOutputStream(target);
                OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
                writer.write(contentH);
                writer.flush();

                //if is a new note
                if(note.getUrl() == null){
                    note.setFileName(target.getName());
                    note.setUrl(target.toURI().toURL());
                }

                //open the exported note
                Note openedNote = new LocalNote(target.getName(), target.toURI().toURL());
                client.getNoteMap().put(openedNote.getUrl(), openedNote);
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
            String contentHtml = EditorUtil.retrieveContentHtml(webView.getEngine().getDocument());
            pageDom.getElementsByTag("div").html(contentHtml);
            String pageHtml = pageDom.outerHtml();

            try{
                PdfMaker.makePdf(pageHtml, target.getAbsolutePath());
            }catch(IOException | InterruptedException ex){
                WindowUtil.showNotifyDialog(editorStage, "Failed to Build PDF File");
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void handleNewGist(){
        if(client.getCredential() == null){
            //show notification
            WindowUtil.showNotifyDialog(editorStage, "GitHub Credential Not Set");
        }else {
            WindowUtil.showInputDialog(editorStage, note.getFileName(), "GistFileName",
                fileName -> {
                    try{
                        Gist newGist = GistUtil.createNewGist(client, fileName, EditorUtil.retrieveContentHtml(webView.getEngine().getDocument()));
                        WindowUtil.showNotifyDialog(editorStage, "New Gist Created at\r\n" + newGist.getUrl());
                        GistUtil.fetchGistById(newGist.getId(), client);
                    }catch(IOException ex){
                        ex.printStackTrace();
                        WindowUtil.showNotifyDialog(editorStage, "Failed to Create New Gist");
                    }
                });
        }
    }

    @FXML
    private void handleSubmit(){
        if(note instanceof LocalNote){
            WindowUtil.showNotifyDialog(editorStage, "This is not an online note.");
        }else {
            try{
                String contentHtml = EditorUtil.retrieveContentHtml(webView.getEngine().getDocument());
                String gistId = ((OnlineNote)note).getGistId();

                Gist updatedGist = GistUtil.updateGist(client, note.getFileName(), contentHtml, gistId);
                Note updatedNote = new OnlineNote(updatedGist);
                client.getNoteMap().put(updatedNote.getUrl(), updatedNote);

                WindowUtil.showNotifyDialog(editorStage, "Gist Content Updated");
            }catch(IOException ex){
                ex.printStackTrace();
                WindowUtil.showNotifyDialog(editorStage, "Failed to Update");
            }
        }
    }
}
