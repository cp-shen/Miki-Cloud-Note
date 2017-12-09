package View;

import Domain.Note;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;


public class MainView{
    @FXML
    private TableView<Note> noteTable;
    @FXML
    private TableColumn<Note,String> noteTitleColumn;
    @FXML
    private WebView displayView;

    private Stage primaryStage;
    private ObservableList<Note> notes;

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void initialize(){
        noteTitleColumn.setCellValueFactory(param -> param.getValue().titleProperty());
        noteTable.getSelectionModel().selectedItemProperty().addListener
                ((observable, oldValue, newValue) -> displayNote(newValue));
    }

    public void setNotes(ObservableList<Note> notes){
        this.notes = notes;
        noteTable.setItems(notes);
    }

    @FXML
    public void handleNew(){
        Note newNote = new Note("untitled",null);
        notes.add(newNote);
        createEditor(newNote);
    }


    @FXML
    private void handleOpen(){
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Local File");
            fileChooser.getExtensionFilters().add (new FileChooser.ExtensionFilter("HTML","*.html"));
            File source = fileChooser.showOpenDialog(primaryStage);
            if(source != null){
                Note openedNote = new Note(source.getName(), source.toURI().toURL());
                notes.add(openedNote);
                displayNote(openedNote);
            }
        }catch(MalformedURLException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleShowEditor(){
        Note selectedNote = noteTable.getSelectionModel().getSelectedItem();
        if(selectedNote != null){
            createEditor(selectedNote);
        }
    }

    private void displayNote(Note note){
        displayView.getEngine().load(note.getUrl().toString());
        displayView.getEngine().setJavaScriptEnabled(false);
    }

    private void createEditor(Note note){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("NoteEditor.fxml"));

            Stage editorStage = new Stage();
            editorStage.setTitle("Editor: " + note.getTitle());
            editorStage.setScene(new Scene(fxmlLoader.load()));

            NoteEditor noteEditor = fxmlLoader.getController();
            noteEditor.setEditorStage(editorStage);
            noteEditor.setNote(note);
            noteEditor.displayNoteInEditor();

            editorStage.setOnCloseRequest(event -> noteEditor.handleSave());
            editorStage.show();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
