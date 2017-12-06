package View;

import Domain.Note;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class View{
    @FXML
    private TableView<Note> noteTable;
    @FXML
    private TableColumn<Note,String> noteTitleColumn;
    @FXML
    private WebView displayView;



    private Stage primaryStage;
    private ObservableList<Note> notes;
    private List<NoteEditor> noteEditors;

    List<NoteEditor> getNoteEditors(){
        return noteEditors;
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void initialize(){
        noteTitleColumn.setCellValueFactory(param -> param.getValue().titleProperty());
        noteEditors = new ArrayList<NoteEditor>();

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

        NoteEditor noteEditor = new NoteEditor(this,newNote);
        noteEditors.add(noteEditor);

        tabPane.getTabs().add(noteEditor.getTab());
        tabPane.getSelectionModel().select(noteEditor.getTab());
    }

    @FXML
    private void handleSave(){
        try{
            Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
            NoteEditor currentEditor = NoteEditor.selectByTab(noteEditors, currentTab);

            if(currentEditor != null){
                //a new note
                if(currentEditor.getNote().getUrl() == null){
                    handleExport();
                }else{
                    Document w3cDoc = currentEditor.getWebView().getEngine().getDocument();
                    org.jsoup.nodes.Document jSoupDoc = Jsoup.parse(new W3CDom().asString(w3cDoc));
                    String contentH = jSoupDoc.outerHtml();

                    FileWriter fileWriter = new FileWriter(new File(currentEditor.getNote().getUrl().toURI()));
                    fileWriter.write(contentH);
                    fileWriter.flush();
                }
            }
        }catch(IOException | URISyntaxException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleExport(){
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        NoteEditor currentEditor = NoteEditor.selectByTab(noteEditors, currentTab);

        if(currentEditor != null){
            try{
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Export to Disk");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML","*.html"));

                File target = fileChooser.showSaveDialog(primaryStage);
                if(target != null){
                    Document w3cDoc = currentEditor.getWebView().getEngine().getDocument();
                    org.jsoup.nodes.Document jSoupDoc = Jsoup.parse(new W3CDom().asString(w3cDoc));
                    String contentH = jSoupDoc.outerHtml();

                    FileWriter fileWriter = new FileWriter(target);
                    fileWriter.write(contentH);
                    fileWriter.flush();

                    //a new note
                    if(currentEditor.getNote().getUrl() == null){
                        currentEditor.getNote().setTitle(target.getName());
                        currentEditor.getNote().setUrl(target.toURI().toURL());
                    }
                }
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void handleOpen(){
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Local File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML","*.html"));

            File source = fileChooser.showOpenDialog(primaryStage);
            if(source != null){
                Note openedNote = new Note(source.getName(), source.toURI().toURL());
                notes.add(openedNote);

                NoteEditor noteEditor = new NoteEditor(this, openedNote);
                noteEditors.add(noteEditor);

                tabPane.getTabs().add(noteEditor.getTab());
                tabPane.getSelectionModel().select(noteEditor.getTab());
            }
        }catch(MalformedURLException ex){
            ex.printStackTrace();
        }
    }

    private void displayNote(Note note){
        displayView.getEngine().load(note.getUrl().toString());
        displayView.getEngine().setJavaScriptEnabled(false);
    }

}
