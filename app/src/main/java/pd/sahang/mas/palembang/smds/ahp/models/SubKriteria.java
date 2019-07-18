package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class SubKriteria {

    public static final String COLLECTION = "subkriteria";
    public static final String TIMESTAMPS = "timestamps";

    public static final String FIELD_KODE_SUB_KRITERIA = "subKodeKriteria";
    public static final String FIELD_NAMA_SUB_KRITERIA = "subNamaKriteria";
    public static final String FIELD_TIPE_NILAI = "subTipeNilaiKriteria";
    public static final String FIELD_NILAI_MIN_SUB_KRITERIA = "subNilaiMinKriteria";
    public static final String FIELD_NILAI_MAX_SUB_KRITERIA = "subNilaiMaxKriteria";

    private String subKodeKriteria;
    private String subNamaKriteria;
    private int subTipeNilaiKriteria;
    private double subNilaiMinKriteria;
    private double subNilaiMaxKriteria;
    private @ServerTimestamp Date timestamps;

    public SubKriteria() {
    }

    public SubKriteria(String subKodeKriteria, String subNamaKriteria, int subTipeNilaiKriteria,
                       double subNilaiMinKriteria, double subNilaiMaxKriteria) {
        this.subKodeKriteria = subKodeKriteria;
        this.subNamaKriteria = subNamaKriteria;
        this.subTipeNilaiKriteria = subTipeNilaiKriteria;
        this.subNilaiMinKriteria = subNilaiMinKriteria;
        this.subNilaiMaxKriteria = subNilaiMaxKriteria;
    }

    public String getSubKodeKriteria() {
        return subKodeKriteria;
    }

    public void setSubKodeKriteria(String subKodeKriteria) {
        this.subKodeKriteria = subKodeKriteria;
    }

    public String getSubNamaKriteria() {
        return subNamaKriteria;
    }

    public void setSubNamaKriteria(String subNamaKriteria) {
        this.subNamaKriteria = subNamaKriteria;
    }

    public int getSubTipeNilaiKriteria() {
        return subTipeNilaiKriteria;
    }

    public void setSubTipeNilaiKriteria(int subTipeNilaiKriteria) {
        this.subTipeNilaiKriteria = subTipeNilaiKriteria;
    }

    public double getSubNilaiMinKriteria() {
        return subNilaiMinKriteria;
    }

    public void setSubNilaiMinKriteria(double subNilaiMinKriteria) {
        this.subNilaiMinKriteria = subNilaiMinKriteria;
    }

    public double getSubNilaiMaxKriteria() {
        return subNilaiMaxKriteria;
    }

    public void setSubNilaiMaxKriteria(double subNilaiMaxKriteria) {
        this.subNilaiMaxKriteria = subNilaiMaxKriteria;
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
        return "SubKriteria{" +
                "subKodeKriteria='" + subKodeKriteria + '\'' +
                ", subNamaKriteria='" + subNamaKriteria + '\'' +
                ", subTipeNilaiKriteria=" + subTipeNilaiKriteria +
                ", subNilaiMinKriteria=" + subNilaiMinKriteria +
                ", subNilaiMaxKriteria=" + subNilaiMaxKriteria +
                ", timestamps=" + timestamps +
                '}';
    }
}
