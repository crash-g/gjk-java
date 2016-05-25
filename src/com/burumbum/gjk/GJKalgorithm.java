package com.burumbum.gjk;

import com.burumbum.gjk.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main class of the implementation of GJK.
 */
public class GJKalgorithm {
    static final int DIM = 3;

    //Debug variables
    // the maximum number of cycles (if the solution is still not optimal, just outputs the most recent calculation)
    private static final int BIG_NUMBER = 100;
    // the number of calls to gjkDistance during the lifetime of the program
    private static int calls = 0;
    // average cycles per call
    private static float averageCycles = 0;

    //witnessVertices is a vector containing size two int arrays: the i-th array contains the indexes of the vertices whose difference gives the i-th simplex vertex
    private static List<int[]> witnessVertices = new ArrayList<>();
    private static Point witness;

    /**
     * The main loop that computes the distance.
     *
     * @param cs1 a convex shape
     * @param cs2 another convex shape
     * @return the distance between the two shapes or zero if they intersect.
     */
    public static float gjkDistance(Shape cs1, Shape cs2) {
        float[] supportDirection;
        Simplex s = new Simplex();

        ++calls;

        if (witnessVertices.size() > 0) {
            System.out.println("Using previously calculated simplex.");
            for (int[] witnessVertex : witnessVertices) {
                float[] v1 = cs1.vertexCoord(witnessVertex[0]);
                float[] v2 = cs2.vertexCoord(witnessVertex[1]);
                v1 = VecOperation.difference(v1, v2);
                s.addVertex(new Point(v1));
            }
        } else {
            supportDirection = VecOperation.difference(cs1.vertexCoord(cs1.firstVertex()), cs2.vertexCoord(cs2.firstVertex()));
            s.addVertex(new Point(supportDirection));
            int[] t = {cs1.firstVertex(), cs2.firstVertex()};
            witnessVertices.add(t);
            float[] supportVertex = gjkSupport(cs1, cs2, supportDirection);
            float c = checkIfCloser(supportVertex, supportDirection);
            if (c >= 0f) {
                return c;
            }
            s.addVertex(new Point(supportVertex));
        }
        for (int k = 0; k < BIG_NUMBER; k++) {
            witness = s.computeClosestToOrigin();
            updateWitnessVertices(s);
            if (Point.equals(witness, Point.ORIGIN)) {
                averageCycles = ((calls - 1) * averageCycles + k + 1) / calls;
                return 0f;
            }
            supportDirection = Point.computeVector(Point.ORIGIN, witness);
            float[] supportVertex = gjkSupport(cs1, cs2, supportDirection);
            float c = checkIfCloser(supportVertex, supportDirection);
            if (c >= 0f) {
                averageCycles = ((calls - 1) * averageCycles + k + 1) / calls;
                return c;
            }
            s.addVertex(new Point(supportVertex));
        }
        System.out.println("Could not find exact solution, outputting approximate solution.");
        averageCycles = ((calls - 1) * averageCycles + BIG_NUMBER) / calls;
        return (float) Math.sqrt(Point.squaredDistance(witness, Point.ORIGIN));
    }

    private static void updateWitnessVertices(Simplex s) {
        List<Integer> toKeep = s.getVerticesToKeep();
        witnessVertices = toKeep.stream().map(index -> witnessVertices.get(index)).collect(Collectors.toList());
    }

    /**
     * Finds the support vertex along direction d.
     */
    private static float[] gjkSupport(Shape cs1, Shape cs2, float[] d) {
        int[] sv = new int[2];
        sv[0] = cs1.supportVertex(d);
        sv[1] = cs2.supportVertex(VecOperation.reverse(d));
        witnessVertices.add(sv);
        float[] v1 = cs1.vertexCoord(sv[0]);
        float[] v2 = cs2.vertexCoord(sv[1]);
        return VecOperation.difference(v1, v2);
    }

    /**
     * Should be called right after gjkSupport, to check if the point that is found (on the Minkowski sum) is closer to the origin along the support direction.
     *
     * @param supportVertex    the support vertex computed by gjkSupport
     * @param supportDirection the previous witness vertex
     * @return the distance from the previous witness vertex to the origin or -1f if the supportVertex is closer to the origin
     */
    private static float checkIfCloser(float[] supportVertex, float[] supportDirection) {
        float dot = VecOperation.dot(supportVertex, supportDirection);
        float squaredWitness = VecOperation.dot(supportDirection, supportDirection);
        if (Tools.compare(dot,squaredWitness) < 0) {
            witnessVertices.remove(witnessVertices.size() - 1);
            return (float) Math.sqrt(squaredWitness);
        }
        return -1f;
    }

    public static void setWitnessVertices(List<int[]> v) {
        witnessVertices = v;
    }

    public static List<int[]> getWitnessVertices() {
        return witnessVertices;
    }

    public static float getAverageCycles() {
        return averageCycles;
    }
}
