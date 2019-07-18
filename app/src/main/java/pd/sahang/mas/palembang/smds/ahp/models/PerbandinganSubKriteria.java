package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PerbandinganSubKriteria {

    public static final String COLLECTION = "perbandingan_sub_kriteria";

    public static final String FIELD_SUB_KODE_KRITERIA = "subKodeKriteria";
    public static final String FIELD_SUB_NAMA_KRITERIA = "subNamaKriteria";
    public static final String FIELD_TIPE_NILAI = "subTipeNilaiKriteria";
    public static final String FIELD_NILAI_MIN_SUB_KRITERIA = "subNilaiMinKriteria";
    public static final String FIELD_NILAI_MAX_SUB_KRITERIA = "subNilaiMaxKriteria";
    public static final String FIELD_SUB_NILAI_KRITERIA = "subNilaiEigenVektorKriteria";

    private String subKodeKriteria;
    private String subNamaKriteria;
    private int subTipeNilaiKriteria;
    private double subNilaiMinKriteria;
    private double subNilaiMaxKriteria;
    private double subNilaiEigenVektorKriteria;

    public PerbandinganSubKriteria() {
    }

    public PerbandinganSubKriteria(String subKodeKriteria, double subNilaiEigenVektorKriteria) {
        this.subKodeKriteria = subKodeKriteria;
        this.subNilaiEigenVektorKriteria = subNilaiEigenVektorKriteria;
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

    public double getSubNilaiEigenVektorKriteria() {
        return subNilaiEigenVektorKriteria;
    }

    public void setSubNilaiEigenVektorKriteria(double subNilaiEigenVektorKriteria) {
        this.subNilaiEigenVektorKriteria = subNilaiEigenVektorKriteria;
    }

    @NonNull
    @Override
    public String toString() {
        return "PerbandinganSubKriteria{" +
                "subKodeKriteria='" + subKodeKriteria + '\'' +
                ", subNamaKriteria='" + subNamaKriteria + '\'' +
                ", subTipeNilaiKriteria=" + subTipeNilaiKriteria +
                ", subNilaiMinKriteria=" + subNilaiMinKriteria +
                ", subNilaiMaxKriteria=" + subNilaiMaxKriteria +
                ", subNilaiEigenVektorKriteria=" + subNilaiEigenVektorKriteria +
                '}';
    }
}
