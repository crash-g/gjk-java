package com.burumbum.gjk.utils;

public class Number implements Comparable<Number> {
    public static final float ZERO = 0.00001f;

    private float number;

    public Number() {
        number = 0f;
    }
    public Number(float f) {
        number = f;
    }

    public int compareTo(Number n) {
        float diff = this.getNumber() - n.getNumber();
        if(Math.abs(diff) < ZERO) {
            return 0;
        }
        return diff > 0 ? 1 : -1;
    }
    
    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }
}