package pd.sahang.mas.palembang.smds.ahp.utils;

import android.text.TextUtils;

public class Validation {

    public static boolean isEmptyString(String s) {
        return TextUtils.isEmpty(s);
    }

    public static boolean isLengthNama(String s, int first, int last) {
        return s.length() < first || s.length() > last;
    }

    public static boolean isLengthKode(String s, int first, int last) {
        return s.length() < first || s.length() > last;
    }

    public static boolean isRegexKodeKriteria(String s) {
        return s.matches("^(K\\W{0})((0[0-9]{1})|(1[0-5]))$");
    }

    public static boolean isRegexKodeSubKriteria(String s) {
        return s.matches("^(SK\\W{0})((0[0-9]{1})|(1[0-5]))((0[0-9]{1})|(1[0-5]))$");
    }

    public static boolean isNumberInteger(String s) {
        return s.matches("^([+-]?[1-9]\\d*|0)$");
    }

    public static boolean isNumberDouble(String s) {
        return s.matches("^([0-9]*\\.?[0-9]+)$");
    }

    /*
    back up regex 0 - 1;
    public static boolean isNumberDouble(String s) {
        return s.matches("(^(([0-1]\\d{0})|(0\\.[0-9]{1,5})|(1\\.0))$)");
    }
    */
}
