package App.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class Note{
    private StringProperty title;
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

    Note(String title, @Nullable URL url){
        if(url == null){
            this.url = new SimpleStringProperty(null);
        }else {
            this.url = new SimpleStringProperty(url.toString());
        }
        this.title = new SimpleStringProperty(title);
    }

    public void setTitle(String title){
        this.title.set(title);
    }

    public String getTitle(){
        return title.get();
    }

    public StringProperty titleProperty(){
        return title;
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
