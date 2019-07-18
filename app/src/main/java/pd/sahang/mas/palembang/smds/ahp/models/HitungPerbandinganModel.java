package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

public class HitungPerbandinganModel {
    private String kodeA;
    private String kodeB;
    private String namaPilihanA;
    private String namaPilihanB;

    public HitungPerbandinganModel(String kodeA, String kodeB, String namaPilihanA, String namaPilihanB) {
        this.kodeA = kodeA;
        this.kodeB = kodeB;
        this.namaPilihanA = namaPilihanA;
        this.namaPilihanB = namaPilihanB;
    }

    public String getKodeA() {
        return kodeA;
    }

    public void setKodeA(String kodeA) {
        this.kodeA = kodeA;
    }

    public String getKodeB() {
        return kodeB;
    }

    public void setKodeB(String kodeB) {
        this.kodeB = kodeB;
    }

    public String getNamaPilihanA() {
        return namaPilihanA;
    }

    public void setNamaPilihanA(String namaPilihanA) {
        this.namaPilihanA = namaPilihanA;
    }

    public String getNamaPilihanB() {
        return namaPilihanB;
    }

    public void setNamaPilihanB(String namaPilihanB) {
        this.namaPilihanB = namaPilihanB;
    }

    @NonNull
    @Override
    public String toString() {
        return "HitungPerbandinganModel{" +
                "kodeA='" + kodeA + '\'' +
                ", kodeB='" + kodeB + '\'' +
                ", namaPilihanA='" + namaPilihanA + '\'' +
                ", namaPilihanB='" + namaPilihanB + '\'' +
                '}';
    }
}
