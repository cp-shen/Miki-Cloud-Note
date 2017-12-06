package View;

import Domain.Note;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

public class NoteEditor{

    private Note note;
    private WebView webView;

    WebView getWebView(){
        return webView;
    }

    Note getNote(){
        return note;
    }

    NoteEditor(View mainView, Note note){
        try{
            webView = new WebView();
            tab = new Tab();
            tab.textProperty().bindBidirectional(note.titleProperty());
            this.note = note;

            if(note.getUrl() != null){
                webView.getEngine().load(note.getUrl().toString());
            }else {
                //a new note
                File editorFile = new File("editor/editor.html");
                webView.getEngine().load(editorFile.toURI().toURL().toString());
            }

            tab.setContent(webView);
            tab.setOnClosed(event -> mainView.getNoteEditors().remove(this));
        }catch(MalformedURLException ex){
            ex.printStackTrace();
        }
    }

    static NoteEditor selectByTab(List<NoteEditor> noteEditors, Tab tab){
        for(NoteEditor noteEditor : noteEditors){
            if(noteEditor.tab.equals(tab)){
                return noteEditor;
            }
        }
        return null;
    }
}
