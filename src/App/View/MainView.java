package App.View;

import App.Model.Note;
import App.Utility.EditorOperator;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


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
        noteTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                displayNote(newValue);
            }
        });
    }

    public void setNotes(ObservableList<Note> notes){
        this.notes = notes;
        noteTable.setItems(notes);
    }

    @FXML
    private void handleNew(){
        Note newNote = new Note("untitled",null);
        notes.add(newNote);
        createEditor(newNote);
    }

    @FXML
    private void handleRemove(){
        Note selectedNote = noteTable.getSelectionModel().getSelectedItem();
        if(selectedNote != null){
            notes.remove(selectedNote);
        }
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

    @FXML @Nullable
    private NoteEditor handleShowEditor(){
        Note selectedNote = noteTable.getSelectionModel().getSelectedItem();
        if(selectedNote != null){
            return createEditor(selectedNote);
        }else {
            return null;
        }
    }

    @FXML
    private void handleExport(){
        NoteEditor noteEditor = handleShowEditor();
        if(noteEditor != null){
            noteEditor.handleExport();
            noteEditor.getEditorStage().close();
        }
    }

    @FXML
    private void handleExportPdf(){
        NoteEditor noteEditor = handleShowEditor();
        if(noteEditor != null){
            noteEditor.handleToPdf();
            noteEditor.getEditorStage().close();
        }
    }

    private void displayNote(Note note){
        try{
            if(note.getUrl() != null){
                File noteFile = new File(note.getUrl().toURI());
                Document jSoupDoc = Jsoup.parse(noteFile,"UTF-8");

                String contentHtml = jSoupDoc.outerHtml();

                displayView.getEngine().loadContent(EditorOperator.editorInit(contentHtml,false));
            }else {
                displayView.getEngine().loadContent(EditorOperator.editorInit("",false));
            }
        }catch(IOException | URISyntaxException ex){
            ex.printStackTrace();
        }
    }

    @Nullable
    private NoteEditor createEditor(Note note){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("NoteEditor.fxml"));

            Stage editorStage = new Stage();
            editorStage.setTitle("Editor : " + note.getTitle());
            editorStage.setScene(new Scene(fxmlLoader.load()));

            NoteEditor noteEditor = fxmlLoader.getController();
            noteEditor.setEditorStage(editorStage);
            noteEditor.setNote(note);
            noteEditor.displayNoteInEditor();

            editorStage.setOnCloseRequest(event -> {
                noteEditor.handleSave();
                noteTable.getSelectionModel().select(note);
                displayNote(note);
            });
            editorStage.show();

            return noteEditor;
        }catch(IOException ex){
            ex.printStackTrace();
            return null;
        }
    }
}
