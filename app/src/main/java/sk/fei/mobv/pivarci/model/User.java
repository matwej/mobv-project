package sk.fei.mobv.pivarci.model;


import android.os.Parcel;
import android.os.Parcelable;

public class User {

    private String first_name;
    private String last_name;
    private String session_token;
    private int user_id;

    public User() {
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getSession_token() {
        return session_token;
    }

    public void setSession_token(String session_token) {
        this.session_token = session_token;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFullName() {
        return String.format("%s, %s",getLast_name(), getFirst_name());
    }
}
