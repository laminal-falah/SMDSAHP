package pd.sahang.mas.palembang.smds.ahp.ui.homepage.profile.inisiasi;

public interface PasswordFragmentInterface {
    boolean validate();
    boolean isPasswordValid(String password);
    void reAuthentication();
    void updatePassword();
    void updateCollectionPassword();
    void errorPasswordOld();
}
