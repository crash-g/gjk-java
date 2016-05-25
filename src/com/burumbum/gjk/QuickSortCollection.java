package com.burumbum.gjk;

import java.util.AbstractList;

/**
 * A utility that sorts collections using quicksort
 * @param <T> the collection to be sorted
 * @param <E> the elements of the collection
 */
public class QuickSortCollection<T extends AbstractList<E>, E extends Number & Comparable<E>> {

    public void order(T v) {
        if(v != null) {
            order(v,0,v.size());
        }
    }
    void order(T v, int l, int r) {
        if (r - l > 1) {
            int i = partition(v, l, r);

            order(v, l, i);
            order(v, i+1, r);
        }
    }
    private int partition(T v, int l, int r) {
        E x=v.get(r-1);
        int i=l;

        for (int j=l; j<r-1; j++) {
            if(v.get(j).compareTo(x) < 0) {
                E t = v.get(j);
                v.set(j,v.get(i));
                v.set(i,t);
                i++;
            }
        }
        v.set(r-1, v.get(i));
        v.set(i,x);
        return i;
    }
}
