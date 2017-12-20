package App.Model;

import App.Utility.GistUtil;
import org.eclipse.egit.github.core.Gist;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;



public class OnlineNote extends Note{
    private Gist gist;

    public OnlineNote(Gist gist)throws MalformedURLException{
        super(GistUtil.getFileNameByGist(gist), new URL(GistUtil.getFileRawUrlByGist(gist)));
        this.gist = gist;
    }

    public Gist getGist(){
        return gist;
    }

    @Override
    public void updateContentByUrl() throws URISyntaxException, IOException{
        setContentHtml(Jsoup.parse(new URL(getUrl()),3000).outerHtml());
    }
}
