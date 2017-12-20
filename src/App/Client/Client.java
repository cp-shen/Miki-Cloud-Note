package App.Client;

import App.Model.GHCredential;
import App.Model.Note;
import javafx.collections.ObservableList;

public class Client{
    private ObservableList<Note> notes;
    private GHCredential credential;

    public void setNotes(ObservableList<Note> notes){
        this.notes = notes;
    }

    public ObservableList<Note> getNotes(){
        return notes;
    }

    public void setCredential(GHCredential credential){
        this.credential = credential;
    }

    public GHCredential getCredential(){
        return credential;
    }
}
