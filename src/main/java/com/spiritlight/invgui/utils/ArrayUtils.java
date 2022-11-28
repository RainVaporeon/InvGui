package com.spiritlight.invgui.utils;

import com.spiritlight.invgui.exceptions.ProcessException;

public class ArrayUtils {

    public static double[] multiply(double[] src, double[] scalar) throws ProcessException {
        if(src.length != scalar.length) throw new ProcessException("src does not have same length as scalar!");
        double[] ret = new double[src.length];
        for(int i=0; i<src.length; i++) {
            ret[i] = src[i] * scalar[i];
        }
        return ret;
    }

    public static <T> String getString(T[] src) {
        StringBuilder ret = new StringBuilder("[");
        // for enclosing
        for(int i=0; i<src.length-1; i++) {
            ret.append(src[i].toString()).append(", ");
        }
        ret.append(src[src.length-1].toString()).append("]");
        return ret.toString();
    }

    public static double[] toPrimitive(Double[] src) {
        double[] ret = new double[src.length];
        for (int i = 0; i < src.length; i++) {
            ret[i] = src[i];
        }
        return ret;
    }

    public static Double[] fromPrimitive(double[] src) {
        Double[] ret = new Double[src.length];
        for (int i = 0; i < src.length; i++) {
            ret[i] = src[i];
        }
        return ret;
    }
}
