package App.Utility;

import App.Client.Client;
import App.Model.Note;
import App.Model.OnlineNote;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;

import java.io.IOException;
import java.util.List;

public class GistUtil{
    public static void fetchGistById(String gistId, Client client) throws IOException{
        GistService gistService = new GistService();
        gistService.getClient().setCredentials(client.getCredential().getUser(), client.getCredential().getPassword());

        Gist gist = gistService.getGist(gistId);
        Note newNote = new OnlineNote(gist);
        client.getNoteMap().put(newNote.getUrl(), newNote);
    }

    public static void fetchGistByUser(Client client)throws IOException{
        GistService gistService = new GistService();
        gistService.getClient().setCredentials(client.getCredential().getUser(), client.getCredential().getPassword());

        List<Gist> gists = gistService.getGists(client.getCredential().getUser());
        for(Gist gist : gists){
            Note newNote = new OnlineNote(gist);
            client.getNoteMap().put(newNote.getUrl(), newNote);
        }
    }

    public static String getFileNameByGist(Gist gist){
        GistFile gistFile = (GistFile)gist.getFiles().values().toArray()[0];
        return gistFile.getFilename();
    }

    public static String getFileRawUrlByGist(Gist gist){
        GistFile gistFile = (GistFile)gist.getFiles().values().toArray()[0];
        return gistFile.getRawUrl();
    }
}
