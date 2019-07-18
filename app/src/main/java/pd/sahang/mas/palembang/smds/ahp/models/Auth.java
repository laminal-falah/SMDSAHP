package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

public class Auth {

    private String email;
    private String password;

    public Auth() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NonNull
    @Override
    public String toString() {
        return "Auth{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
