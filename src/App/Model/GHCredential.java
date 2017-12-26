package App.Model;

/**
 * The type Gh credential.
 */
public class GHCredential{
    private String user;
    private String password;

    /**
     * Instantiates a new Gh credential.
     *
     * @param user     the user
     * @param password the password
     */
    public GHCredential(String user, String password){
        this.user = user;
        this.password = password;
    }

    /**
     * Get user string.
     *
     * @return the string
     */
    public String getUser(){
        return user;
    }

    /**
     * Get password string.
     *
     * @return the string
     */
    public String getPassword(){
        return password;
    }
}
