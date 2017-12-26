package App.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * The type Note.
 */
public abstract class Note{
    private StringProperty fileName;
    private StringProperty url;

    private String contentHtml;

    /**
     * Set content html.
     *
     * @param contentHtml the content html
     */
    void setContentHtml(String contentHtml){
        this.contentHtml = contentHtml;
    }

    /**
     * Gets content html.
     *
     * @return the content html
     * @throws URISyntaxException the uri syntax exception
     * @throws IOException        the io exception
     */
    @NotNull
    public String getContentHtml()throws URISyntaxException, IOException{
        if(contentHtml == null){
            updateContentByUrl();
        }
        return contentHtml;
    }

    /**
     * Update content by url.
     *
     * @throws URISyntaxException the uri syntax exception
     * @throws IOException        the io exception
     */
    public abstract void updateContentByUrl() throws URISyntaxException, IOException;

    /**
     * Instantiates a new Note.
     *
     * @param fileName the file name
     * @param url      the url
     */
    Note(String fileName, @Nullable URL url){
        if(url == null){
            this.url = new SimpleStringProperty(null);
        }else {
            this.url = new SimpleStringProperty(url.toString());
        }
        this.fileName = new SimpleStringProperty(fileName);
    }

    /**
     * Set file name.
     *
     * @param fileName the file name
     */
    public void setFileName(String fileName){
        this.fileName.set(fileName);
    }

    /**
     * Get file name string.
     *
     * @return the string
     */
    public String getFileName(){
        return fileName.get();
    }

    /**
     * File name property string property.
     *
     * @return the string property
     */
    public StringProperty fileNameProperty(){
        return fileName;
    }

    /**
     * Get url string.
     *
     * @return the string
     */
    public String getUrl(){
        return url.get();
    }

    /**
     * Sets url.
     *
     * @param url the url
     * @throws NoteUrlException the note url exception
     */
    public void setUrl(@Nullable URL url) throws NoteUrlException{
        if(url == null){
            this.url.set(null);
        }else {
            this.url.set(url.toString());
        }
    }

    /**
     * Url property string property.
     *
     * @return the string property
     */
    public StringProperty urlProperty(){
        return url;
    }
}
