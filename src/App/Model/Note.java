package App.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class Note{
    private StringProperty fileName;
    private StringProperty url;

    private String contentHtml;

    void setContentHtml(String contentHtml){
        this.contentHtml = contentHtml;
    }

    @NotNull
    public String getContentHtml()throws URISyntaxException, IOException{
        if(contentHtml == null){
            updateContentByUrl();
        }
        return contentHtml;
    }

    public abstract void updateContentByUrl() throws URISyntaxException, IOException;

    Note(String fileName, @Nullable URL url){
        if(url == null){
            this.url = new SimpleStringProperty(null);
        }else {
            this.url = new SimpleStringProperty(url.toString());
        }
        this.fileName = new SimpleStringProperty(fileName);
    }

    public void setFileName(String fileName){
        this.fileName.set(fileName);
    }

    public String getFileName(){
        return fileName.get();
    }

    public StringProperty fileNameProperty(){
        return fileName;
    }

    public String getUrl(){
        return url.get();
    }

    public void setUrl(@Nullable URL url) throws NoteUrlException{
        if(url == null){
            this.url.set(null);
        }else {
            this.url.set(url.toString());
        }
    }

    public StringProperty urlProperty(){
        return url;
    }
}
