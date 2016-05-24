package com.burumbum.gjk;

/**
 * A point in a space with DIM dimensions.
 */
public class Point {
    public static final int DIM = 3;
    private static final float[] ZEROES = new float[DIM];

    public static final Point ORIGIN = new Point(ZEROES);

    private float[] coord = new float[DIM];

    public Point(float[] v) {
        assert DIM == v.length;
        for(int i=0; i<DIM; i++) {
            coord[i] = v[i];
        }
    }
    public Point(Point a) {
        for(int i=0; i<DIM; i++) {
            coord[i] = a.get(i);
        }
    }
    public static boolean equal(Point a, Point b) {
        for(int i=0; i<DIM; i++) {
            if(a.get(i) != b.get(i)) {
                return false;
            }
        }
        return true;
    }
    public static float squaredDistance(Point a, Point b) {
        float result = 0;
        for(int i=0; i<DIM; i++) {
            result += (a.get(i)-b.get(i))*(a.get(i)-b.get(i));
        }
        return result;
    }
    public float get(int i) {
        return coord[i];
    }
    public float[] toVector() {
        return VecOperation.copy(coord);
    }
    public static float[] computeVector(Point a, Point b) {
        float[] v = new float[DIM];
        for(int i=0; i<DIM; i++) {
            v[i] = b.get(i)-a.get(i);
        }
        return v;
    }
}
