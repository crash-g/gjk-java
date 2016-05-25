package com.burumbum.gjk;

import java.util.ArrayList;
import java.util.List;

/**
 * Main utility of GJK. If a simplex that contains the origin can be constructed there is an intersection.
 * All the code relative to normalVectors or relying on it only works for up to 4 points (3D case).
 */
class Simplex {
    // list of vertices forming the simplex. The size of this lis is at most DIM + 1
    private final List<Point> vertices = new ArrayList<>();
    //pointVectors is an upper-left triangular matrix which contains vectors from simplex vertices to other simplex vertices, or to the origin.
    // (i,j), with 0 < j < vertices.size()-i, contains the vector from vertex i to vertex j+i, while (i,0) contains the vertex from i to the origin.
    private final List<List<float[]>> pointVectors = new ArrayList<>();
    //normalVector is an array which contains vectors perpendicular to the faces of the simplex, one for each face. The faces are 012, 013, 023, 123.
    // The sums of the digits are 3, 4, 5, 6 respectively, so face xyz is in position x+y+z - 3. Note that this would not work in dimension four or higher.
    private List<float[]> normalVectors = new ArrayList<>();
    private final List<Integer> verticesToKeep = new ArrayList<>();

    void addVertex(Point p) {
        vertices.add(p);
        updateVectorArrays();
    }
    /** used to update pointVectors and normalVectors after adding a vertex to the simplex */
    private void updateVectorArrays() {
        //updating the last element for every row in pointVectors
        for(int i=0; i < vertices.size()-1; i++) {
            pointVectors.get(i).add(Point.computeVector(vertices.get(i),vertices.get(vertices.size()-1)));
        }
        pointVectors.add(new ArrayList<>());
        pointVectors.get(vertices.size()-1).add(Point.computeVector(vertices.get(vertices.size() - 1), Point.ORIGIN));

        //updating normalVectors: different situation if there is one face or there are four
        if(3 == vertices.size()) {
            normalVectors.add(VecOperation.cross(pointVectors.get(0).get(1),pointVectors.get(0).get(2)));
        } else if(4 == vertices.size()) {
            for(int j1=0; j1 < 2; j1++) {
                for(int j2=j1+1; j2 < 3; j2++) {
                    normalVectors.add(VecOperation.cross(pointVectors.get(j1).get(j2),pointVectors.get(j1).get(3)));
                }
            }
            //ensure that the normal vectors are pointing outward
            for(int i=0; i<normalVectors.size()-1; i++) {
                if(VecOperation.dot(normalVectors.get(i),pointVectors.get(0).get(3-i)) > 0) {
                    normalVectors.set(i,VecOperation.reverse(normalVectors.get(i)));
                }
            }
            if(VecOperation.dot(normalVectors.get(3),pointVectors.get(0).get(1)) < 0) {
                normalVectors.set(3,VecOperation.reverse(normalVectors.get(3)));
            }
        }
    }

    /** used to update pointVectors and normalVectors after removing a vertex from the simplex (must be called every time) */
    private void updateVectorArrays(int removedIndex) {
        //removing the entry relative to the removed vertex for every row with index strictly less than removedIndex. Then removing the whole row relative to the removed vertex
        for(int i=0; i < removedIndex; i++) {
            pointVectors.get(i).remove(removedIndex-i);
        }
        pointVectors.remove(removedIndex);

        //clearing if there are no faces in the new simplex, otherwise keeping only the normal vector relative to the surviving face
        if(vertices.size() < 3) {
            normalVectors.clear();
        } else {  // this means that vertices.size() == 3
            int faceLeftIndex = 6 - removedIndex - 3;
            List<float[]> faceLeft = new ArrayList<>();
            faceLeft.add(normalVectors.get(faceLeftIndex));
            normalVectors = faceLeft;
        }
    }

    /**
     * Computes the closest point to the origin in the simplex.
     * @return  the closest point. If it is the origin, than the origin is either inside or on the surface of the simplex
     */
    Point computeClosestToOrigin() {
        verticesToKeep.clear();

        // checking if the closest point is a vertex
        for(int i=0; i<vertices.size(); i++) {
            if(closestToVertex(i)) {
                verticesToKeep.add(i);
                for(int j = vertices.size()-1; j >= 0; j--) {
                    if(j != i) {
                        vertices.remove(j);
                        updateVectorArrays(j);
                    }
                }
                return new Point(vertices.get(0));
            }
        }

        // checking if the closest point is on an edge
        for(int i=0; i<vertices.size(); i++) {
            for(int j=i+1; j<vertices.size(); j++) {
                if(closestToEdge(i,j)) {
                    verticesToKeep.add(i);
                    verticesToKeep.add(j);
                    for(int k = vertices.size()-1; k >= 0; k--) {
                        if(k != i && k != j) {
                            vertices.remove(k);
                            updateVectorArrays(k);
                        }
                    }
                    return VecOperation.pointLineDistance(Point.ORIGIN,vertices.get(0),vertices.get(1));
                }
            }
        }

        // checking if closest point is on a face
        if(normalVectors.size() > 1) {
            for(int i=0; i<normalVectors.size()-1; i++) {
                if(VecOperation.dot(pointVectors.get(0).get(0),normalVectors.get(i))>0) {
                    for(int j = vertices.size()-1; j >= 0; j--) {
                        if(j == 3-i) {
                            vertices.remove(3-i);
                            updateVectorArrays(3-i);
                        } else {
                            verticesToKeep.add(0,j);
                        }
                    }
                    return VecOperation.pointPlaneDistance(Point.ORIGIN,vertices.get(0),normalVectors.get(0));
                }
            }
            if(VecOperation.dot(pointVectors.get(1).get(0),normalVectors.get(3))>0) {
                for(int j = 1; j < vertices.size(); j++) {
                    verticesToKeep.add(j);
                }
                vertices.remove(0);
                updateVectorArrays(0);
                return VecOperation.pointPlaneDistance(Point.ORIGIN,vertices.get(0),normalVectors.get(0));
            }
        } else {
            for(int i=0; i < vertices.size(); i++) {
                verticesToKeep.add(i);
            }
            return VecOperation.pointPlaneDistance(Point.ORIGIN,vertices.get(0),normalVectors.get(0));
        }

        // the origin is in the simplex (or on its surface)
        for(int i=0; i < vertices.size(); i++) {
            verticesToKeep.add(i);
        }
        return Point.ORIGIN;
    }

    /** check if closer to vertex i */
    private boolean closestToVertex(int i) {
        for(int j=0; j<vertices.size(); j++) {
            if(i<j) {
                if(VecOperation.dot(pointVectors.get(i).get(0),pointVectors.get(i).get(j-i))>0) {
                    return false;
                }
            }
            if(i>j) {
                if(VecOperation.dot(pointVectors.get(i).get(0),pointVectors.get(j).get(i-j))<0) {
                    return false;
                }
            }
        }
        return true;
    }

    /** check if closer to edge ij */
    private boolean closestToEdge(int i, int j) {
        assert i < j;

        if(VecOperation.dot(pointVectors.get(i).get(0),pointVectors.get(i).get(j-i))<0) {
            return false;
        }
        if(VecOperation.dot(pointVectors.get(j).get(0),pointVectors.get(i).get(j-i))>0) {
            return false;
        }
        for(int k=0; k<vertices.size(); k++) {
            if(k != j) {
                if(i < k) {
                    // v is the vector perpendicular to the edge ij, parallel to the face ijk and pointing outward
                    float[] v = VecOperation.cross(normalVectors.get(i+j+k-3),pointVectors.get(i).get(j-i));
                    if(VecOperation.dot(v,pointVectors.get(i).get(k-i))>0) {
                        v = VecOperation.reverse(v);
                    }
                    if(VecOperation.dot(pointVectors.get(i).get(0),v)<0) {
                        return false;
                    }
                } else if(i > k) {
                    // v is the vector perpendicular to the edge ij, parallel to the face ijk and pointing outward
                    float[] v = VecOperation.cross(normalVectors.get(i+j+k-3),pointVectors.get(i).get(j-i));
                    if(VecOperation.dot(v,pointVectors.get(k).get(i-k))<0) {
                        v = VecOperation.reverse(v);
                    }
                    if(VecOperation.dot(pointVectors.get(i).get(0),v)<0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    List<Integer> getVerticesToKeep() {
        return verticesToKeep;
    }
}
