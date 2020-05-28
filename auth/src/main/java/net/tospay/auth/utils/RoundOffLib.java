package net.tospay.auth.utils;

public class RoundOffLib {
    public static double roundOffValue(double value){
        return Math.round(value * 100.0) / 100.0;
    }
}
