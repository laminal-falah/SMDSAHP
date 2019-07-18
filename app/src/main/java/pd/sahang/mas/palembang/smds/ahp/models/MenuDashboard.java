package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class MenuDashboard {
    private String tag;
    private Fragment fragment;
    private int position;
    private String title;

    public MenuDashboard(String tag, Fragment fragment, int position, String title) {
        this.tag = tag;
        this.fragment = fragment;
        this.position = position;
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return "MenuDashboard{" +
                "tag='" + tag + '\'' +
                ", fragment=" + fragment +
                ", position=" + position +
                ", title='" + title + '\'' +
                '}';
    }
}
