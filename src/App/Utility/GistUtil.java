package App.Utility;

import App.Client.Client;
import App.Model.Note;
import App.Model.OnlineNote;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;

import java.io.IOException;
import java.util.Collections;
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

    public static Gist createNewGist(Client client, String fileName, String content)throws IOException{
        GistService gistService = new GistService();
        gistService.getClient().setCredentials(client.getCredential().getUser(), client.getCredential().getPassword());

        Gist localGist = new Gist();
        GistFile localGistFile = new GistFile();

        localGistFile.setContent(content);
        localGistFile.setFilename(fileName);
        localGist.setPublic(false);
        localGist.setDescription("Created By Miki Cloud Note");
        localGist.setFiles(Collections.singletonMap(fileName, localGistFile));

        return gistService.createGist(localGist);
    }

    public static Gist updateGist(Client client, String fileName, String content, String gistId)throws IOException{
        GistService gistService = new GistService();
        gistService.getClient().setCredentials(client.getCredential().getUser(), client.getCredential().getPassword());

        Gist localGist = new Gist();
        GistFile localGistFile = new GistFile();

        localGistFile.setContent(content);
        localGistFile.setFilename(fileName);
        localGist.setPublic(false);
        localGist.setId(gistId);
        localGist.setDescription("Created By Miki Cloud Note");
        localGist.setFiles(Collections.singletonMap(fileName, localGistFile));

        return gistService.updateGist(localGist);
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
