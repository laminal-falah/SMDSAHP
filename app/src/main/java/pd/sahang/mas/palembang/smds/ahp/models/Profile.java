package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

public class Profile {
    private String title;
    private String content;

    public Profile() {
    }

    public Profile(String title, String content) {
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
        return "Profile{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
