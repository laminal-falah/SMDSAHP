package pd.sahang.mas.palembang.smds.ahp.ui.homepage.pengguna.inisiasi;

import pd.sahang.mas.palembang.smds.ahp.models.Users;

public interface AddEditUserInterface {
    void onAuthenticationFirebase();
    void onInitFirestore();
    void setUser(Users users);
    void onSpinnerLevel();
    boolean validate();
    void onResetForm();
}
