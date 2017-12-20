package App.Model;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class LocalNote extends Note{
    public LocalNote(String title, URL url)throws NoteUrlException{
        super(title, url);
        if(url != null && !url.getProtocol().equals("file")){
            throw new NoteUrlException("url protocol of local project is not \"file\"");
        }
    }

    @Override
    public void setUrl(URL url) throws NoteUrlException{
        if(url != null && !url.getProtocol().equals("file")){
            throw new NoteUrlException("url protocol of local project is not \"file\"");
        }
        super.setUrl(url);
    }

    @Override
    public void updateContentByUrl() throws URISyntaxException, IOException{
        setContentHtml(Jsoup.parse(new File(new URL(getUrl()).toURI()),"UTF-8").outerHtml());
    }
}
