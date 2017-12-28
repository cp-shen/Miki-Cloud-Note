package App.Client;

import App.Model.GHCredential;
import App.Model.Note;
import javafx.collections.ObservableMap;


/**
 * The type Client.
 * Containing github credentials and the notes.
 */
public class Client{
    private ObservableMap<String, Note> noteMap;
    private GHCredential credential;

    /**
     * Set note map.
     *
     * @param noteMap the note map
     */
    public void setNoteMap(ObservableMap<String, Note> noteMap){
        this.noteMap = noteMap;
    }

    /**
     * Get note map observable map.
     *
     * @return the observable map
     */
    public ObservableMap<String, Note> getNoteMap(){
        return noteMap;
    }

    /**
     * Set credential.
     *
     * @param credential the credential
     */
    public void setCredential(GHCredential credential){
        this.credential = credential;
    }

    /**
     * Get credential gh credential.
     *
     * @return the gh credential
     */
    public GHCredential getCredential(){
        return credential;
    }
}
