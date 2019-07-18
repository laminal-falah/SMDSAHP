package pd.sahang.mas.palembang.smds.ahp.models;

import androidx.annotation.NonNull;

public class AhpModelHelper {
    private int value;
    private int kepentingan;

    public AhpModelHelper(int value, int kepentingan) {
        this.value = value;
        this.kepentingan = kepentingan;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getKepentingan() {
        return kepentingan;
    }

    public void setKepentingan(int kepentingan) {
        this.kepentingan = kepentingan;
    }

    @NonNull
    @Override
    public String toString() {
        return "AhpModelHelper{" +
                "value=" + value +
                ", kepentingan=" + kepentingan +
                '}';
    }
}
