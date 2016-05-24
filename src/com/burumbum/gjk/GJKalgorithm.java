package com.burumbum.gjk;

import java.util.Vector;

/**
 * Main class of the implementation of GJK.
 */
public class GJKalgorithm {
    // A small quantity. Anything smaller is treated as zero.
    private static final float EPSILON = 0.00001f;

    //Debug variables
    private static final int BIG_NUMBER = 100;
    private static int calls = 0;
    private static float averageCycles = 0;

    //witnessVertices is a vector containing size two int arrays: the i-th array contains the indexes of the vertices whose difference gives the i-th simplex vertex
    private static Vector<int[]> witnessVertices = new Vector<>();
    private static Point witness;

    /**
     * contains the main loop that computes the distance.
     * @param cs1   a convex shape
     * @param cs2   another convex shape
     * @return      the distance between the two shapes or zero if they intersect.
     */
    public static float gjkDistance(Shape cs1, Shape cs2) {
        float[] supportDirection;
        Simplex s = new Simplex();

        ++calls;

        if(witnessVertices.size() > 0) {
            System.out.println("Using previously calculated simplex.");
            for(int i=0; i < witnessVertices.size(); i++) {
                float[] v = cs1.vertexCoord(witnessVertices.get(i)[0]);
                float[] v1 = cs2.vertexCoord(witnessVertices.get(i)[1]);
                v = VecOperation.difference(v,v1);
                s.addVertex(new Point(v));
            }
        } else {
            supportDirection = VecOperation.difference(cs1.vertexCoord(cs1.firstVertex()),cs2.vertexCoord(cs2.firstVertex()));
            s.addVertex(new Point(supportDirection));
            int[] t = {cs1.firstVertex(),cs2.firstVertex()};
            witnessVertices.add(t);
            float[] supportVertex = gjkSupport(cs1, cs2, supportDirection);
            float c = checkIfCloser(supportVertex,supportDirection);
            if(c != -1) {
                return c;
            }
            s.addVertex(new Point(supportVertex));
        }
        for(int k = 0; k < BIG_NUMBER; k++) {
            witness = s.computeClosestToOrigin();
            updateWitnessVertices(s);
            if(Point.equal(witness,Point.ORIGIN)) {
                averageCycles = ((calls-1)*averageCycles + k + 1)/calls;
                return 0f;
            }
            supportDirection = Point.computeVector(Point.ORIGIN, witness);
            float[] supportVertex = gjkSupport(cs1, cs2, supportDirection);
            float c = checkIfCloser(supportVertex,supportDirection);
            if(c >= 0f) {
                averageCycles = ((calls-1)*averageCycles + k + 1)/calls;
                return c;
            }
            s.addVertex(new Point(supportVertex));
        }
        System.out.println("Could not find exact solution, outputting approximate solution.");
        averageCycles = ((calls-1)*averageCycles + BIG_NUMBER)/calls;
        return (float) Math.sqrt(Point.squaredDistance(witness,Point.ORIGIN));
    }
    private static void updateWitnessVertices(Simplex s) {
        Vector<Integer> toKeep = s.getVerticesToKeep();
        Vector<int[]> vec = new Vector<>();
        for(int i=0; i<toKeep.size(); i++) {
            vec.add(witnessVertices.get(toKeep.get(i)));
        }
        witnessVertices = vec;
    }
    /** finds the support vertex along direction d */
    private static float[] gjkSupport(Shape cs1, Shape cs2, float[] d) {
        int[] sv = new int[2];
        sv[0] = cs1.supportVertex(d);
        sv[1] = cs2.supportVertex(VecOperation.reverse(d));
        witnessVertices.add(sv);
        float[] v = cs1.vertexCoord(sv[0]);
        float[] v1 = cs2.vertexCoord(sv[1]);
        return VecOperation.difference(v,v1);
    }
    /**
     * should be called right after gjkSupport, to check if the point that is found is closer to the origin along the support direction
     * @param supportVertex         the support vertex computed by gjkSupport
     * @param supportDirection      the previous witness vertex
     * @return                      the distance from the previous witness vertex to the origin or -1f if the supportVertex is closer to the origin
     */
    // DEBUG: possible problem, as I take the square of a quantity, the supportVertex may be much further away than along the supportDirection but still
    // the function returns -1
    private static float checkIfCloser(float[] supportVertex, float[] supportDirection) {
        float dot = VecOperation.dot(supportVertex, supportDirection);
        float squaredWitness = VecOperation.dot(supportDirection, supportDirection);
        dot = dot/squaredWitness-1;    // this is a lazy approach, maybe I should define a new variable and not reuse dot
        if(dot*dot*squaredWitness < EPSILON) {
            witnessVertices.remove(witnessVertices.lastElement());
            return (float) Math.sqrt(squaredWitness);
        }
        return -1f;
    }
    public static void setWitnessVertices(Vector<int[]> v) {
        witnessVertices = v;
    }
    public static Vector<int[]> getWitnessVertices() {
        return witnessVertices;
    }
    public static float getAverageCycles() {return averageCycles;}
}
