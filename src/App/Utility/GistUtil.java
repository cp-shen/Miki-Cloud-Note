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

/**
 * The type Gist util.
 */
public class GistUtil{
    /**
     * Fetch gist by id.
     *
     * @param gistId the gist id
     * @param client the client
     * @throws IOException the io exception
     */
    public static void fetchGistById(String gistId, Client client) throws IOException{
        GistService gistService = new GistService();
        gistService.getClient().setCredentials(client.getCredential().getUser(), client.getCredential().getPassword());

        Gist gist = gistService.getGist(gistId);
        Note newNote = new OnlineNote(gist);
        client.getNoteMap().put(newNote.getUrl(), newNote);
    }

    /**
     * Fetch gist by user.
     *
     * @param client the client
     * @throws IOException the io exception
     */
    public static void fetchGistByUser(Client client)throws IOException{
        GistService gistService = new GistService();
        gistService.getClient().setCredentials(client.getCredential().getUser(), client.getCredential().getPassword());

        List<Gist> gists = gistService.getGists(client.getCredential().getUser());
        for(Gist gist : gists){
            Note newNote = new OnlineNote(gist);
            client.getNoteMap().put(newNote.getUrl(), newNote);
        }
    }

    /**
     * Create new gist gist.
     *
     * @param client   the client
     * @param fileName the file name
     * @param content  the content
     * @return the gist
     * @throws IOException the io exception
     */
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

    /**
     * Update gist gist.
     *
     * @param client   the client
     * @param fileName the file name
     * @param content  the content
     * @param gistId   the gist id
     * @return the gist
     * @throws IOException the io exception
     */
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

    /**
     * Get file name by gist string.
     *
     * @param gist the gist
     * @return the string
     */
    public static String getFileNameByGist(Gist gist){
        GistFile gistFile = (GistFile)gist.getFiles().values().toArray()[0];
        return gistFile.getFilename();
    }

    /**
     * Get file raw url by gist string.
     *
     * @param gist the gist
     * @return the string
     */
    public static String getFileRawUrlByGist(Gist gist){
        GistFile gistFile = (GistFile)gist.getFiles().values().toArray()[0];
        return gistFile.getRawUrl();
    }
}
