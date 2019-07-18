package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Laporan {

    public static final String COLLECTION = "laporan";
    public static final String TIMESTAMPS = "timestramps";

    public static final String FIELD_KODE_LAPORAN = "kodeLaporan";
    public static final String FIELD_NAMA_LAPORAN = "namaLaporan";

    public static final String DEFAULT_NAMA = "Kualitas Kopi";

    private String kodeLaporan;
    private String namaLaporan;
    private @ServerTimestamp Date timestamps;

    public Laporan() {
    }

    public Laporan(String kodeLaporan, String namaLaporan) {
        this.kodeLaporan = kodeLaporan;
        this.namaLaporan = namaLaporan;
    }

    public String getKodeLaporan() {
        return kodeLaporan;
    }

    public void setKodeLaporan(String kodeLaporan) {
        this.kodeLaporan = kodeLaporan;
    }

    public String getNamaLaporan() {
        return namaLaporan;
    }

    public void setNamaLaporan(String namaLaporan) {
        this.namaLaporan = namaLaporan;
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
        return "Laporan{" +
                "kodeLaporan='" + kodeLaporan + '\'' +
                ", namaLaporan='" + namaLaporan + '\'' +
                ", timestamps=" + timestamps +
                '}';
    }
}
