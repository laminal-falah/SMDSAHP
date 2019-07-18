package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DetailLaporanItem {

    public static final String COLLECTION = "item";
    public static final String FIELD_NAMA_KRITERIA = "namaKriteria";
    public static final String FIELD_NAMA_SUB_KRITERIA = "namaSubKriteria";
    public static final String FIELD_NILAI_SUB_KRITERIA = "nilaiSubKriteria";

    private String namaKriteria;
    private String namaSubKriteria;
    private double nilaiSubKriteria;

    public DetailLaporanItem(String namaKriteria, String namaSubKriteria, double nilaiSubKriteria) {
        this.namaKriteria = namaKriteria;
        this.namaSubKriteria = namaSubKriteria;
        this.nilaiSubKriteria = nilaiSubKriteria;
    }

    public String getNamaKriteria() {
        return namaKriteria;
    }

    public void setNamaKriteria(String namaKriteria) {
        this.namaKriteria = namaKriteria;
    }

    public String getNamaSubKriteria() {
        return namaSubKriteria;
    }

    public void setNamaSubKriteria(String namaSubKriteria) {
        this.namaSubKriteria = namaSubKriteria;
    }

    public double getNilaiSubKriteria() {
        return nilaiSubKriteria;
    }

    public void setNilaiSubKriteria(double nilaiSubKriteria) {
        this.nilaiSubKriteria = nilaiSubKriteria;
    }

    @NonNull
    @Override
    public String toString() {
        return "DetailLaporanItem{" +
                "namaKriteria='" + namaKriteria + '\'' +
                ", namaSubKriteria='" + namaSubKriteria + '\'' +
                ", nilaiSubKriteria=" + nilaiSubKriteria +
                '}';
    }
}
