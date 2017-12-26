package App.Model;

import App.Utility.GistUtil;
import org.eclipse.egit.github.core.Gist;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * The type Online note.
 */
public class OnlineNote extends Note{
    private String gistId;
    private String fileUrl;

    /**
     * Instantiates a new Online note.
     *
     * @param gist the gist
     * @throws MalformedURLException the malformed url exception
     */
    public OnlineNote(Gist gist)throws MalformedURLException{
        super(GistUtil.getFileNameByGist(gist), new URL(gist.getUrl()));
        this.gistId = gist.getId();
        this.fileUrl = GistUtil.getFileRawUrlByGist(gist);
    }

    /**
     * Get gist id string.
     *
     * @return the string
     */
    public String getGistId(){
        return gistId;
    }

    @Override
    public void updateContentByUrl() throws URISyntaxException, IOException{
        setContentHtml(Jsoup.parse(new URL(fileUrl),3000).outerHtml());
    }
}
