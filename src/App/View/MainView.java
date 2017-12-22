package App.View;

import App.Client.Client;
import App.Model.LocalNote;
import App.Model.NoteUrlException;
import App.Model.Note;
import App.Utility.EditorOperator;
import App.Utility.GistUtil;
import App.Utility.WindowUtil;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
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
    private TableColumn<Note, String> titleColumn, urlColumn;
    @FXML
    private WebView displayView;

    private Stage primaryStage;
    private Client client;

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
        client.getNoteMap().addListener(new MapChangeListener<String, Note>(){
            @Override
            public void onChanged(Change<? extends String, ? extends Note> change){
                noteTable.setItems(FXCollections.observableArrayList(client.getNoteMap().values()));
            }
        });
    }

    @FXML
    private void handleNew(){
        try{
            Note newNote = new LocalNote("untitled",null);
            createEditor(newNote);
        }catch(NoteUrlException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleRemove(){
        Note selectedNote = noteTable.getSelectionModel().getSelectedItem();
        if(selectedNote != null){
            client.getNoteMap().remove(selectedNote.getUrl());
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
                client.getNoteMap().put(openedNote.getUrl(), openedNote);
                noteTable.getSelectionModel().select(openedNote);
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
            if(note.getUrl() == null){
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
            noteEditor.setClient(client);
            noteEditor.setNote(note);
            noteEditor.displayNoteInEditor();

            editorStage.setOnCloseRequest(closeEvent -> {
                    noteEditor.handleSave();
                    if(note.getUrl() == null){
                        closeEvent.consume();
                        WindowUtil.showConfirmDialog(editorStage, "Do you want to duplicate the content?",
                            okClicked -> {
                                //close the window
                                editorStage.close();
                            },
                            cancelClicked -> {
                                //do not close the window
                            });
                    }else {
                        //refresh content in mainView
                        noteTable.getSelectionModel().select(note);
                        try{
                            displayNote(note);
                        }catch(IOException | URISyntaxException ex){
                            ex.printStackTrace();
                        }
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
            if(client.getCredential() == null){
                //show notification
                WindowUtil.showNotifyDialog(primaryStage, "GitHub Credential Not Set");
            }else {
                GistUtil.fetchGistByUser(client);
            }
        }catch(IOException ex){
            ex.printStackTrace();
            WindowUtil.showNotifyDialog(primaryStage, "Failed to Fetch Gists");
        }
    }

    @FXML
    private void handleFetchById(){
        if(client.getCredential() == null){
            //show notification
            WindowUtil.showNotifyDialog(primaryStage, "GitHub Credential Not Set");
        }else {
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
                    WindowUtil.showNotifyDialog(primaryStage, "Failed to Fetch Gists");
                }
                dialogStage.close();
            });
        }
    }
}
