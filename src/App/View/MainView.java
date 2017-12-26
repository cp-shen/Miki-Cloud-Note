package App.View;

import App.Client.Client;
import App.Model.LocalNote;
import App.Model.NoteUrlException;
import App.Model.Note;
import App.Utility.EditorUtil;
import App.Utility.GistUtil;
import App.Utility.WindowUtil;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;


/**
 * The type Main view.
 */
public class MainView{
    @FXML
    private TableView<Note> noteTable;
    @FXML
    private TableColumn<Note, String> fileNameColumn, urlColumn;
    @FXML
    private WebView displayView;

    private Stage primaryStage;
    private Client client;

    /**
     * Set primary stage.
     *
     * @param primaryStage the primary stage
     */
    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    /**
     * Initialize.
     */
    public void initialize(){

        fileNameColumn.setCellValueFactory(param -> param.getValue().fileNameProperty());

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

    /**
     * Set client.
     *
     * @param client the client
     */
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
    private EditorView handleShowEditor(){
        Note selectedNote = noteTable.getSelectionModel().getSelectedItem();
        if(selectedNote != null){
            return createEditor(selectedNote);
        }else {
            return null;
        }
    }

    @FXML
    private void handleExport(){
        EditorView editorView = handleShowEditor();
        if(editorView != null){
            editorView.handleExport();
            editorView.getEditorStage().close();
        }
    }

    @FXML
    private void handleExportPdf(){
        EditorView editorView = handleShowEditor();
        if(editorView != null){
            editorView.handleToPdf();
            editorView.getEditorStage().close();
        }
    }

    private void displayNote(@Nullable Note note)throws IOException, URISyntaxException{
        if(note == null){
            displayView.getEngine().loadContent(EditorUtil.generateEditorPageHtml("",false));
        }else {
            //a new note
            if(note.getUrl() == null){
                displayView.getEngine().loadContent(EditorUtil.generateEditorPageHtml("",false));
            }else {
                displayView.getEngine().loadContent(EditorUtil.generateEditorPageHtml(note.getContentHtml(),false));
            }
        }
    }

    @Nullable
    private EditorView createEditor(@NotNull Note note){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("EditorView.fxml"));

            Stage editorStage = new Stage();
            editorStage.titleProperty().bind(note.urlProperty());
            editorStage.setScene(new Scene(fxmlLoader.load()));

            EditorView editorView = fxmlLoader.getController();
            editorView.setEditorStage(editorStage);
            editorView.setClient(client);
            editorView.setNote(note);
            editorView.displayNoteInEditor();

            editorStage.setOnCloseRequest(closeEvent -> {
                    if(note.getUrl() == null){
                        closeEvent.consume();
                        WindowUtil.showConfirmDialog(editorStage, "Do you want to drop the content?",
                            okClicked -> {
                                //close the window
                                editorStage.close();
                            },
                            cancelClicked -> {
                                //do not close the window
                            });
                    }else {
                        editorView.handleSave();
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

            return editorView;
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
            WindowUtil.showInputDialog(primaryStage, "", "Gist ID",
                    gistId -> {
                        try{
                            if(gistId != null && !gistId.equals("")){
                                GistUtil.fetchGistById(gistId, client);
                            }
                        }catch(IOException ex){
                            ex.printStackTrace();
                            WindowUtil.showNotifyDialog(primaryStage, "Failed to Fetch Gists");
                        }
                    });
        }
    }
}
