package pd.sahang.mas.palembang.smds.ahp.ui.auth.inisiasi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import pd.sahang.mas.palembang.smds.ahp.models.Auth;

public interface LoginInterface {
    boolean validate();
    boolean isEmailValid(String email);
    boolean isPasswordValid(String password);
    Auth setLogin();
    void running(Auth auth);
    void taskLogin(Task<AuthResult> task);
    void onSuccessLogin(DocumentSnapshot snapshot);
    void checkLevel();
    void errorEmail();
    void errorPassword();
}
