package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

public class DetailUser {
    private String title;
    private String content;

    public DetailUser() {
    }

    public DetailUser(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return "DetailUser{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
