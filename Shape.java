package com.burumbum.gjk;

/** a minimal interface for a shape */
public interface Shape {
    public final static int FIRSTVERTEX = 0;
    public final static int INVALIDVERTEX = -1;

    /** should return FIRSTVERTEX */
    int firstVertex();
    /** returns an array with the coordinates of the vertex v */
    float[] vertexCoord(int v);
    /** returns the dot product of v and d */
    float supportValue(int v, float[] d);
    /** returns the support vertex along the direction d */
    int supportVertex(float[] d);
}
