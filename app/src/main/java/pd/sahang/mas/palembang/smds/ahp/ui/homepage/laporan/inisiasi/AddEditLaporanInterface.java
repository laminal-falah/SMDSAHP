package pd.sahang.mas.palembang.smds.ahp.ui.homepage.laporan.inisiasi;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import pd.sahang.mas.palembang.smds.ahp.models.DetailLaporan;

public interface AddEditLaporanInterface {
    void onAuthenticationFirebase();
    void onInitFirestore();
    void onDetailLaporan(DetailLaporan detailLaporan);
    boolean validate();
    void onResetForm();
    void dispatchTakePictureIntent();
    File createImageFile() throws IOException;
    void onRecyclerView();
    void getKriteria(Task<QuerySnapshot> task);
    double getPvk(String kodeKriteria);
    void getPvsk(String kode, String kodeSubKriteria);
    void onGetKriteria(String kode, String nilai);
    void simpan(String idNyo);
    void setSuccessful(int count, boolean successful);
    void setErrorFirebase(Exception e);
    void sendNotification(String user, String idLaporan, String namaLaporan);
    void send(@NonNull JSONObject jsonObject);
}
