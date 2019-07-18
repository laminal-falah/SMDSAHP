package pd.sahang.mas.palembang.smds.ahp.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pd.sahang.mas.palembang.smds.ahp.models.Users;

public class UserHelper {
    private static final String COLLECTION_NAME = Users.COLLECTION;

    public static CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createUser(String uid, String email, String password, String name, String phone, String level) {
        Users user = new Users(name, email, phone, level, password);
        return UserHelper.getUserCollection().document(uid).set(user);
    }

    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUserCollection().document(uid).get();
    }

    public static Task<Void> updateToken(String uid, String token) {
        return UserHelper.getUserCollection().document(uid).update(Users.FIELD_FIREBASE_TOKEN, token);
    }

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUserCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateIsMentor(String uid, Boolean isMentor) {
        return UserHelper.getUserCollection().document(uid).update("isMentor", isMentor);
    }

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUserCollection().document(uid).delete();
    }
}
