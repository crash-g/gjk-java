package com.burumbum.gjk;

import com.burumbum.gjk.utils.Tools;

/**
 * A point in a space with DIM dimensions.
 */
class Point {
    static final int DIM = GJKalgorithm.DIM;
    private static final float[] ZEROES = new float[DIM];

    static final Point ORIGIN = new Point(ZEROES);

    private final float[] coord = new float[DIM];

    Point(float[] v) {
        assert DIM == v.length;
        System.arraycopy(v, 0, coord, 0, DIM);
    }
    Point(Point a) {
        for(int i=0; i<DIM; i++) {
            coord[i] = a.get(i);
        }
    }
    static boolean equals(Point a, Point b) {
        for(int i=0; i<DIM; i++) {
            if(!Tools.equal(a.get(i), b.get(i))) {
                return false;
            }
        }
        return true;
    }
    static float squaredDistance(Point a, Point b) {
        float result = 0f;
        for(int i=0; i<DIM; i++) {
            result += (a.get(i)-b.get(i))*(a.get(i)-b.get(i));
        }
        return result;
    }
    float get(int i) {
        return coord[i];
    }
    static float[] computeVector(Point a, Point b) {
        float[] v = new float[DIM];
        for(int i=0; i<DIM; i++) {
            v[i] = b.get(i)-a.get(i);
        }
        return v;
    }
}
