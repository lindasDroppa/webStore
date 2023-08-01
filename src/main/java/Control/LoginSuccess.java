package Control;

import org.bson.types.ObjectId;

public class LoginSuccess {
    private String name;
    private String token;
    private String email;

    private String id;


    public LoginSuccess() {
    }

    public LoginSuccess(String name, String token, String email, String  id) {
        this.name = name;
        this.token = token;
        this.email = email;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
