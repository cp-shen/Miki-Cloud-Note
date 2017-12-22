package App.Model;

import App.Utility.GistUtil;
import org.eclipse.egit.github.core.Gist;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;



public class OnlineNote extends Note{
    private String gistId;
    private String fileUrl;

    public OnlineNote(Gist gist)throws MalformedURLException{
        super(GistUtil.getFileNameByGist(gist), new URL(gist.getUrl()));
        this.gistId = gist.getId();
        this.fileUrl = GistUtil.getFileRawUrlByGist(gist);
    }

    public String getGistId(){
        return gistId;
    }

    @Override
    public void updateContentByUrl() throws URISyntaxException, IOException{
        setContentHtml(Jsoup.parse(new URL(fileUrl),3000).outerHtml());
    }
}
