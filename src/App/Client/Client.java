package App.Client;

import App.Model.GHCredential;
import App.Model.Note;
import javafx.collections.ObservableMap;



public class Client{
    private ObservableMap<String, Note> noteMap;
    private GHCredential credential;

    public void setNoteMap(ObservableMap<String, Note> noteMap){
        this.noteMap = noteMap;
    }

    public ObservableMap<String, Note> getNoteMap(){
        return noteMap;
    }

    public void setCredential(GHCredential credential){
        this.credential = credential;
    }

    public GHCredential getCredential(){
        return credential;
    }
}
