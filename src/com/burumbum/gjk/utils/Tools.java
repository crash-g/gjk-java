package com.burumbum.gjk.utils;

public class Tools {// A small quantity. Anything smaller is treated as zero.
    static final float EPSILON = 0.00001f;

    public static boolean equal(float f1, float f2) {
        return Math.abs(f1 - f2) < EPSILON;
    }

    public static int compare(float f1, float f2) {
        float diff = f1 - f2;
        if(Math.abs(diff) < EPSILON) {
            return 0;
        }
        return diff > 0 ? 1 : -1;
    }

    public static boolean isZero(float f) {
        return f < EPSILON;
    }
}
