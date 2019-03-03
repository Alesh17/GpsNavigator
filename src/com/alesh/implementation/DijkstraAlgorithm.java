package com.alesh.implementation;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static java.util.Arrays.fill;

public class DijkstraAlgorithm {

    private final int INF = Integer.MAX_VALUE / 2; // infinity
    private final int vNum;
    private MultiList graph;
    private String[] cities;

    public List<String> path;
    public int cost;

    public DijkstraAlgorithm(int vNum, int eNum, int[][] roads, String[] cities) {
        this.vNum = vNum;
        this.cities = cities;

        graph = new MultiList(this.vNum, eNum);
        graph.add(roads);
    }

    public void computePathsAndCosts(String pointA, String pointB) throws Exception{

        int start = Arrays.asList(cities).indexOf(pointA);
        int end = Arrays.asList(cities).indexOf(pointB);

        if(start == -1 || end == -1) throw new Exception("Такой точки не найдено!");

        boolean[] used = new boolean[vNum]; // markup array
        int[] prev = new int[vNum]; // ancestral array
        int[] dist = new int[vNum]; // array of distances
        RMQ rmq = new RMQ(vNum); // RMQ

        // initialization
        fill(prev, -1);
        fill(dist, INF);
        rmq.set(start, dist[start] = 0);

        for (;;) {
            int v = rmq.minIndex(); // choose the nearest vertex
            if (v == -1 || v == end) break; // if it is not found, or is finite, then we exit

            used[v] = true; // mark selected vertex
            rmq.set(v, INF); // put its value in RMQ

            for (int i = graph.head[v]; i != 0; i = graph.next[i]) { // go to adjacent vertex
                int nv = graph.vert[i];
                int cost = graph.cost[i];
                if (!used[nv] && dist[nv] > dist[v] + cost) { // if we can improve the distance estimate
                    rmq.set(nv, dist[nv] = dist[v] + cost); // improve it
                    prev[nv] = v; // mark ancestor
                }
            }
        }

        // restore path
        Stack<Integer> stack = new Stack();
        for (int v = end; v != -1; v = prev[v]) stack.push(v);
        int[] sp = new int[stack.size()];
        for (int i = 0; i < sp.length; i++) sp[i] = stack.pop() + 1;

        String[] str = new String[sp.length];
        for (int i = 0; i <  str.length; i++) {
            str[i] = cities[sp[i]-1];
        }

        cost = dist[end];
        path = Arrays.asList(str);
    }

    // the class that stores the edges and the cost of the graph
    private class MultiList {

        int[] head;
        int[] next;
        int[] vert;
        int[] cost;
        int cnt = 1;

        MultiList(int vNum, int eNum) {
            head = new int[vNum];
            next = new int[eNum + 1];
            vert = new int[eNum + 1];
            cost = new int[eNum + 1];
        }

        void add(int[][] ints) {
            for (int[] ints1: ints) {
                next[cnt] = head[ints1[0]];
                vert[cnt] = ints1[1];
                cost[cnt] = ints1[2];
                head[ints1[0]] = cnt++;
            }
        }
    }

    // the class that stores range minimum query
    private class RMQ {

        int n;
        int[] val, ind;

        RMQ(int size) {
            n = size;
            val = new int[2 * n];
            ind = new int[2 * n];
            fill(val, INF);
            for (int i = 0; i < n; i++)
                ind[n + i] = i;
        }

        void set(int index, int value) {
            val[n + index] = value;
            for (int v = (n + index) / 2; v > 0; v /= 2) {
                int l = 2 * v;
                int r = l + 1;
                if (val[l] <= val[r]) {
                    val[v] = val[l];
                    ind[v] = ind[l];
                } else {
                    val[v] = val[r];
                    ind[v] = ind[r];
                }
            }
        }

        int minIndex() {
            return val[1] < INF ? ind[1] : -1;
        }
    }
}
