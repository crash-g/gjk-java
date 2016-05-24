package com.burumbum.gjk;

/**
 * useful operations with vectors
 */
public class VecOperation {
    public final static float[] ORIGIN = new float[Point.DIM];
    // cross product only works with three dimensional vectors and it does not check that the input is correct
    public static float[] cross(float[] a, float[] b) {
        return new float[]{a[1]*b[2]-a[2]*b[1],a[2]*b[0]-a[0]*b[2],a[0]*b[1]-a[1]*b[0]};
    }

    public static float dot(float[] a, float[] b) {
        float result = 0;
        int min = Math.min(a.length,b.length);
        for(int i=0; i<min; i++) {
            result += a[i]*b[i];
        }
        return result;
    }

    public static float[] reverse(float[] a) {
        float[] result = copy(a);
        for(int i=0; i<result.length; i++) {
            result[i]=-result[i];
        }
        return result;
    }
    public static float[] copy(float[] v) {
        float[] result = new float[v.length];
        for(int i=0; i<v.length; i++) {
            result[i] = v[i];
        }
        return result;
    }
    //NOTE: Returns null if there is a problem
    //TODO: Check it!
    public static float[] matrixVectorProduct(float[][] m, float[] v) {
        if(m != null && v != null && m.length > 0 && m[0].length == v.length) {
            float[] result = new float[m.length];
            for(int i=0; i<m.length; i++) {
                for(int j=0; j<m[i].length; j++) {
                    result[i] += m[i][j]*v[j];
                }
            }
            return result;
        }
        return null;
    }
    public static Point pointLineDistance(Point p, Point a, Point b) {
        float[] ab = Point.computeVector(a,b);
        float[] ap = Point.computeVector(a,p);
        float t = dot(ab,ap)/dot(ab,ab);
        float[] result = new float[Point.DIM];
        for(int i=0; i<Point.DIM; i++) {
            result[i] = a.get(i)+t*ab[i];
        }
        return new Point(result);
    }
    public static Point pointPlaneDistance(Point p, Point a, float[] n) {
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
    public static float[] difference(float[] v, float[] w) {
        float[] result = new float[v.length];
        for(int i=0; i<v.length; i++) {
            result[i] = v[i]-w[i];
        }
        return result;
    }
}
