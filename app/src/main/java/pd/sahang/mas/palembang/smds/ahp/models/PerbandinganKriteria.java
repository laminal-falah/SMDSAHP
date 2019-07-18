package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PerbandinganKriteria {
    public static final String COLLECTION = "perbandingan_kriteria";

    public static final String FIELD_KODE_KRITERIA = "kodeKriteria";
    public static final String FIELD_NAMA_KRITERIA = "namaKriteria";
    public static final String FIELD_NILAI_KRITERIA = "nilaiEigenVektorKriteria";

    private String kodeKriteria;
    private String namaKriteria;
    private double nilaiEigenVektorKriteria;

    public PerbandinganKriteria() {
    }

    public PerbandinganKriteria(String kodeKriteria, String namaKriteria, double nilaiEigenVektorKriteria) {
        this.kodeKriteria = kodeKriteria;
        this.namaKriteria = namaKriteria;
        this.nilaiEigenVektorKriteria = nilaiEigenVektorKriteria;
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

    public double getNilaiEigenVektorKriteria() {
        return nilaiEigenVektorKriteria;
    }

    public void setNilaiEigenVektorKriteria(double nilaiEigenVektorKriteria) {
        this.nilaiEigenVektorKriteria = nilaiEigenVektorKriteria;
    }

    @NonNull
    @Override
    public String toString() {
        return "PerbandinganKriteria{" +
                "kodeKriteria='" + kodeKriteria + '\'' +
                ", namaKriteria='" + namaKriteria + '\'' +
                ", nilaiEigenVektorKriteria=" + nilaiEigenVektorKriteria +
                '}';
    }
}
