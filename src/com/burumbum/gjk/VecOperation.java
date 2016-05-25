package com.burumbum.gjk;

/**
 * Useful operations with vectors.
 */
class VecOperation {
    // cross product only works with three dimensional vectors
    static float[] cross(float[] a, float[] b) {
        assert Point.DIM == a.length;
        assert Point.DIM == b.length;
        return new float[]{a[1]*b[2]-a[2]*b[1],a[2]*b[0]-a[0]*b[2],a[0]*b[1]-a[1]*b[0]};
    }

    static float dot(float[] a, float[] b) {
        assert a.length == b.length;
        float result = 0;
        for(int i=0; i<a.length; i++) {
            result += a[i]*b[i];
        }
        return result;
    }

    static float[] reverse(float[] a) {
        float[] result = new float[a.length];
        for(int i=0; i<result.length; i++) {
            result[i]=-a[i];
        }
        return result;
    }

    static Point pointLineDistance(Point p, Point a, Point b) {
        float[] ab = Point.computeVector(a,b);
        float[] ap = Point.computeVector(a,p);
        float t = dot(ab,ap)/dot(ab,ab);
        float[] result = new float[Point.DIM];
        for(int i=0; i<Point.DIM; i++) {
            result[i] = a.get(i)+t*ab[i];
        }
        return new Point(result);
    }

    static Point pointPlaneDistance(Point p, Point a, float[] n) {
        float[] pa = Point.computeVector(p,a);
        float t = dot(n,pa)/dot(n,n);
        float[] result = new float[Point.DIM];
        for(int i=0; i<Point.DIM; i++) {
            result[i] = p.get(i)+t*n[i];
        }
        return new Point(result);
    }

    public static boolean equal(float[] v, float[] w) {
        if(v.length != w.length) {
            return false;
        }
        for(int i=0; i<v.length; i++) {
            if(v[i] != w[i]) {
                return false;
            }
        }
        return true;
    }
    public static float[] sum(float[] v, float[] w) {
        float[] result = new float[v.length];
        for(int i=0; i<v.length; i++) {
            result[i] = v[i]+w[i];
        }
        return result;
    }

    static float[] difference(float[] v, float[] w) {
        assert v.length == w.length;
        float[] result = new float[v.length];
        for(int i=0; i<v.length; i++) {
            result[i] = v[i]-w[i];
        }
        return result;
    }
}
