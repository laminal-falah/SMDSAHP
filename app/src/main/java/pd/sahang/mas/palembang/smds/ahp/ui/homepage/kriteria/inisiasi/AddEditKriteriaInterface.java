package pd.sahang.mas.palembang.smds.ahp.ui.homepage.kriteria.inisiasi;

import pd.sahang.mas.palembang.smds.ahp.models.Kriteria;

public interface AddEditKriteriaInterface {
    void onAuthenticationFirebase();
    void onInitFirestore();
    void setKriteria(Kriteria kriteria);
    boolean validate();
    void onResetForm();
}
