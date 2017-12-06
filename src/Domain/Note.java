package Domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.URL;

public class Note{
    private StringProperty title;
    private URL url;

    public Note(String title,URL url){
        this.url = url;
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

    public URL getUrl(){
        return url;
    }

    public void setUrl(URL url){
        this.url = url;
    }
}
