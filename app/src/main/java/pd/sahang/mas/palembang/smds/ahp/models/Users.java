package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Users {
    public static final String COLLECTION = "users";
    public static final String TIMESTAMPS = "timestamps";

    public static final String FIELD_FULLNAME = "fullname";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_LEVEL = "level";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_FIREBASE_TOKEN = "firebase_token";

    private String fullname;
    private String email;
    private String phone;
    private String level;
    private String password;
    private String firebase_token;

    private @ServerTimestamp Date timestamps;

    public Users() {
    }

    public Users(String fullname, String email, String phone, String level, String password) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.level = level;
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirebase_token() {
        return firebase_token;
    }

    public void setFirebase_token(String firebase_token) {
        this.firebase_token = firebase_token;
    }

    public Date getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Date timestamps) {
        this.timestamps = timestamps;
    }

    @NonNull
    @Override
    public String toString() {
        return "Users{" +
                "fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", level='" + level + '\'' +
                ", password='" + password + '\'' +
                ", firebase_token='" + firebase_token + '\'' +
                ", timestamps=" + timestamps +
                '}';
    }
}
