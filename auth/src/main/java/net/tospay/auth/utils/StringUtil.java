package net.tospay.auth.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class StringUtil {

    public static String formatAmount(String amount) {
        if (amount != null) {
            return NumberFormat.getInstance(Locale.getDefault()).format(Double.parseDouble(amount));
        }
        return "0.00";
    }

    public static String formatAmount(double amount) {
        return NumberFormat.getInstance(Locale.getDefault()).format(amount);
    }

    public static String formatAmount(int amount) {
        return NumberFormat.getInstance(Locale.getDefault()).format(amount);
    }

    public static String formatAmount(float amount) {
        return NumberFormat.getInstance(Locale.getDefault()).format(amount);
    }
}
