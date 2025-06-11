package SuperAdmin;

public class User {//用户实体类
    private String username;
    private String password;
    private String role;
    private String account;


    public User(String username,  String account,String role, String password) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getAccount() {
        return account;
    }
}