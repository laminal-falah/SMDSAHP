package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Kriteria {

    public static final String COLLECTION = "kriteria";
    public static final String TIMESTAMPS = "timestamps";

    public static final String FIELD_KODE_KRITERIA = "kodeKriteria";
    public static final String FIELD_NAMA_KRITERIA = "namaKriteria";

    private String kodeKriteria;
    private String namaKriteria;
    private @ServerTimestamp Date timestamps;

    public Kriteria() {
    }

    public Kriteria(String kodeKriteria, String namaKriteria) {
        this.kodeKriteria = kodeKriteria;
        this.namaKriteria = namaKriteria;
    }

    public String getKodeKriteria() {
        return kodeKriteria;
    }

    public void setKodeKriteria(String kodeKriteria) {
        this.kodeKriteria = kodeKriteria;
    }

    public String getNamaKriteria() {
        return namaKriteria;
    }

    public void setNamaKriteria(String namaKriteria) {
        this.namaKriteria = namaKriteria;
    }

    public Date getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Date timestamps) {
        this.timestamps = timestamps;
    }

    @NonNull
    @Override
    public String toString() {
        return "Kriteria{" +
                "kodeKriteria='" + kodeKriteria + '\'' +
                ", namaKriteria='" + namaKriteria + '\'' +
                '}';
    }
}
