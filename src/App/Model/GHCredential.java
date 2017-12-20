package App.Model;

public class GHCredential{
    private String user;
    private String password;

    public GHCredential(String user, String password){
        this.user = user;
        this.password = password;
    }

    public String getUser(){
        return user;
    }

    public String getPassword(){
        return password;
    }
}
