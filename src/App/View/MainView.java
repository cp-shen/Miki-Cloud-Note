package App.View;

import App.Client.Client;
import App.Model.LocalNote;
import App.Model.NoteUrlException;
import App.Model.Note;
import App.Utility.EditorOperator;
import App.Utility.GistUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;


public class MainView{
    @FXML
    private TableView<Note> noteTable;
    @FXML
    private TableColumn<Note,String> titleColumn, urlColumn;
    @FXML
    private WebView displayView;

    private Stage primaryStage;
    private Client client;
    private ObservableList<Note> notes;

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void initialize(){
        titleColumn.setCellValueFactory(param -> param.getValue().titleProperty());
        urlColumn.setCellValueFactory(param -> param.getValue().urlProperty());
        noteTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try{
                if(newValue != null){
                    displayNote(newValue);
                }else {
                    displayNote(null);
                }
            }catch(IOException | URISyntaxException ex){
                ex.printStackTrace();
                noteTable.getSelectionModel().select(oldValue);
            }
        });
    }

    public void setClient(Client client){
        this.client = client;
    }

    public void setNotes(ObservableList<Note> notes){
        this.notes = notes;
        noteTable.setItems(notes);
    }

    @FXML
    private void handleNew(){
        try{
            Note newNote = new LocalNote("untitled",null);
            notes.add(newNote);
            createEditor(newNote);
        }catch(NoteUrlException ex){
            ex.printStackTrace();
        }
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
                Note openedNote = new LocalNote(source.getName(), source.toURI().toURL());
                notes.add(openedNote);
                displayNote(openedNote);
            }
        }catch(NoteUrlException | URISyntaxException | IOException ex){
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

    private void displayNote(@Nullable Note note)throws IOException, URISyntaxException{
        if(note == null){
            displayView.getEngine().loadContent(EditorOperator.getEditorPageHtml("",false));
        }else {
            //a new note
            if(note.getUrl().equals("null")){
                displayView.getEngine().loadContent(EditorOperator.getEditorPageHtml("",false));
            }else {
                displayView.getEngine().loadContent(EditorOperator.getEditorPageHtml(note.getContentHtml(),false));
            }
        }
    }

    @Nullable
    private NoteEditor createEditor(@NotNull Note note){
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
                try{
                    noteEditor.handleSave();
                    noteTable.getSelectionModel().select(note);
                    //refresh
                    displayNote(note);
                }catch(IOException | URISyntaxException ex){
                    ex.printStackTrace();
                }
            });
            editorStage.show();

            return noteEditor;
        }catch(IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleGHLogin(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("LoginDialog.fxml"));

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(fxmlLoader.load()));
            dialogStage.setTitle("Set GitHub Credentials");
            dialogStage.initOwner(primaryStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.show();

            LoginDialog loginDialog = fxmlLoader.getController();
            loginDialog.setDialogStage(dialogStage);
            loginDialog.setClient(client);
            loginDialog.showCredentials();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleFetchByUser(){
        try{
            GistUtil.fetchGistByUser(client);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleFetchById(){
        TextField gistIdField = new TextField();
        Button okButton = new Button("OK");
        HBox hBox = new HBox(10d, gistIdField, okButton);

        Stage dialogStage = new Stage();
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(hBox));
        dialogStage.setTitle("Gist Id Input");
        dialogStage.show();

        okButton.setOnAction(event -> {
            try{
                String gistId = gistIdField.getText();
                if(gistId != null && !gistId.equals("")){
                    GistUtil.fetchGistById(gistId, client);
                }
            }catch(IOException ex){
                ex.printStackTrace();
            }
            dialogStage.close();
        });
    }
}
