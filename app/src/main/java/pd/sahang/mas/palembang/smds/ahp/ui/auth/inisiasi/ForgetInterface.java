package pd.sahang.mas.palembang.smds.ahp.ui.auth.inisiasi;

import com.google.android.gms.tasks.Task;

import pd.sahang.mas.palembang.smds.ahp.models.Auth;

public interface ForgetInterface {
    boolean validate();
    boolean isEmailValid(String email);
    Auth setForget();
    void running(Auth auth);
    void taskForget(Task<Void> task);
    void errorEmail();
}
