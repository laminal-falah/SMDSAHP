package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class DetailLaporan {

    public static final String COLLECTION = "detail_laporan";
    public static final String TIMESTAMPS = "timestamps";

    public static final String FIELD_ID_USER = "idUser";
    public static final String FIELD_NAMA_KARUNG = "namaKarung";
    public static final String FIELD_GAMBAR_KARUNG = "gambarKarung";
    public static final String FIELD_GRADE_KARUNG = "gradeKarung";
    public static final String FIELD_NILAI_GRADE_KARUNG = "nilaiGradeKarung";

    private String idUser;
    private String namaKarung;
    private String gambarKarung;
    private String gradeKarung;
    private double nilaiGradeKarung;
    private @ServerTimestamp Date timestamps;

    public DetailLaporan() {
    }

    public DetailLaporan(String idUser, String namaKarung, String gambarKarung) {
        this.idUser = idUser;
        this.namaKarung = namaKarung;
        this.gambarKarung = gambarKarung;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNamaKarung() {
        return namaKarung;
    }

    public void setNamaKarung(String namaKarung) {
        this.namaKarung = namaKarung;
    }

    public String getGambarKarung() {
        return gambarKarung;
    }

    public void setGambarKarung(String gambarKarung) {
        this.gambarKarung = gambarKarung;
    }

    public String getGradeKarung() {
        return gradeKarung;
    }

    public void setGradeKarung(String gradeKarung) {
        this.gradeKarung = gradeKarung;
    }

    public double getNilaiGradeKarung() {
        return nilaiGradeKarung;
    }

    public void setNilaiGradeKarung(double nilaiGradeKarung) {
        this.nilaiGradeKarung = nilaiGradeKarung;
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
        return "DetailLaporan{" +
                "idUser='" + idUser + '\'' +
                ", namaKarung='" + namaKarung + '\'' +
                ", gambarKarung='" + gambarKarung + '\'' +
                ", timestamps=" + timestamps +
                '}';
    }
}
