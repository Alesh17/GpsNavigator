package com.alesh;

import com.alesh.api.GpsNavigator;
import com.alesh.api.Path;
import com.alesh.implementation.DijkstraAlgorithm;

import java.sql.Time;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import java.io.File;
import java.io.FileInputStream;

public class Main {

    public static void main(String[] args) {

        final GpsNavigator navigator = new StubGpsNavigator();
        navigator.readData("roads.txt");

        final Path path = navigator.findPath("F", "B");
        if(path != null) System.out.println(path);
    }

    private static class StubGpsNavigator implements GpsNavigator {

        String regex = "^(\\S*?)+\\s{1}+(\\S*?)+\\s{1}+[0-9]{1,5}+\\s{1}+[0-9]{1,5}$"; // regex for validate text format
        String rawTextFromFile = "";
        String[] points;
        
        int countOfRibs;
        int countOfCities;
        int[][] graph;

        @Override
        public void readData(String filePath) {

            String[][] arrayOfRoads;

            try {

                // read text from file
                if(new File(filePath).exists()) {
                    FileInputStream fin = new FileInputStream(filePath);
                    int i;
                    while((i = fin.read()) != -1)
                        rawTextFromFile = rawTextFromFile.concat(String.valueOf((char)i));
                } else throw new Exception("Файла не существует!");

                // split raw text for strings
                String[] ribs = rawTextFromFile.replace("\r","").split("\n");
                arrayOfRoads = new String[ribs.length][];
                countOfRibs = ribs.length;

                // validation file format
                for (int i = 0; i < ribs.length; i++) {
                    if(ribs[i].matches(regex)) arrayOfRoads[i] = ribs[i].split(" ");
                    else throw new Exception("Неверный формат файла!");
                }

                // get names of points
                String[] strings1 = new String[arrayOfRoads.length*2];
                int iterator = 0;

                for (int i = 0; i < arrayOfRoads.length; i++) {
                    for (int j = 0; j < arrayOfRoads[i].length; j++) {
                        if(j == 0 || j == 1) {
                            strings1[iterator] = arrayOfRoads[i][j];
                            iterator++;
                        }
                    }
                }

                // get count of cities
                Set<String> set = new HashSet<>(Arrays.asList(strings1));
                points = set.toArray(new String[set.size()]);
                countOfCities = points.length;

                // get route graph
                graph = new int[arrayOfRoads.length][3];

                for (int i = 0; i < arrayOfRoads.length; i++) {
                    for (int j = 0; j < 3; j++) {
                        if(j==0 || j==1) graph[i][j] = Arrays.asList(points).indexOf(arrayOfRoads[i][j]);
                        else if(j==2) graph[i][j] = Integer.valueOf(arrayOfRoads[i][2]) * Integer.valueOf(arrayOfRoads[i][3]);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public Path findPath(String pointA, String pointB) {

            try {
                DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(countOfCities, countOfRibs, graph, points);
                dijkstraAlgorithm.computePathsAndCosts(pointA, pointB);

                if(dijkstraAlgorithm.cost > Integer.MAX_VALUE / 3) throw new Exception("Путь не найден!");
                else return new Path(dijkstraAlgorithm.path, dijkstraAlgorithm.cost);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }

        }
    }
}
