package utils;

import model.Router;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoutingAlgorithm {

    private RoutingAlgorithm() {}

    public static void shortestPathCost(double[][] routersInfoMatrix, int source, Router router) {

        int n = routersInfoMatrix.length-1;
        int[] touch = new int [n+1];
        double[] pathCost = new double[n+1];
        double[] length = new double[n+1];
        int  vNear = source;

        IntStream.rangeClosed(1, n).forEach(i -> {
            touch[i] = source;
            length[i] = routersInfoMatrix[source][i];
        });

        for( int j = 1; j <= n-1; j++ )
        {
            double min = Double.MAX_VALUE;

            for( int i = 1; i <= n; i++ )
                if (min > length[i] && length[i] != 0) {
                    min = length[i];
                    vNear = i;
                }
            length[vNear] = 0;

            for( int i = 1; i <= n; i++ )
                if ((min + routersInfoMatrix[vNear][i]) < length[i] && length[i] != 0) {
                    length[i] = min + routersInfoMatrix[vNear][i];
                    touch[i] = vNear;
                }
            pathCost[vNear] = min;
        }

        updateRoutingTable(source, router, n, touch, pathCost);
        System.out.println();
    }

    private static void updateRoutingTable(int source, Router router, int n, int[] touch, double[] pathCost) {

        Map<String, Map<String, Double>> routingTable = router.getRoutingTable();
        List<String> shortestPath = new ArrayList<>();
        int counter = 0;

        IntStream.rangeClosed(1, n).forEach(i -> updateCostAndNeighbour(source, n, touch, pathCost, routingTable, counter, i, shortestPath));
        router.setRoutingTable(routingTable);
        router.setPath(shortestPath);
    }

    private static void updateCostAndNeighbour(int source, int n, int[] touch, double[] pathCost, Map<String, Map<String, Double>> routingTable,
                                               int counter, int i, List<String> shortestPath) {

        String[] path = new String[n];
        getShortestPath(source, i, touch, path, counter);
        System.out.println();

        String pathString = Arrays.stream(path).filter(Objects::nonNull).collect(Collectors.joining("->"));
        shortestPath.add(pathString);
        System.out.print("R" + source + " to R" + i + " (cost " + pathCost[i] + "):  "+pathString);

        Map<String, Double> newMap = getNeighbourAndCost(pathCost, i, path);
        int index = getDestinationIndex(path);
        routingTable.entrySet().stream().filter(entryKey -> entryKey.getKey().equals(path[index]))
                                        .forEach(stringMapEntry -> stringMapEntry.setValue(newMap));
    }

    private static int getDestinationIndex(String[] path) {

        return IntStream.iterate(path.length - 1, j -> j >= 0, j -> j - 1)
                        .filter(j -> path[j] != null)
                        .findFirst().orElse(-1);
    }

    private static Map<String, Double> getNeighbourAndCost(double[] pathCost, int i, String[] path) {

        Map<String, Double> newMap = new HashMap<>();
        if(path[1] != null)
        newMap.put(path[1], pathCost[i]);
        return newMap;
    }

    public static void getShortestPath(int s, int d, int[] touch, String[] path, int counter)  {

        int m, j = 0 ;
        int[] internal;

        if( s == d )
            path[counter] = "R" + s;
        else
        {
            path[counter++] = "R"+s;

            internal = IntStream.range(0, touch.length).map(i -> -1).toArray();
            m = touch[d];

            while( m != s && j < internal.length )
            {
                internal[j++] = m;
                m = touch[m];
            }

            for( int i = internal.length-1 ; i >= 0 ; i-- )
                if (internal[i] != -1)
                    path[counter++] = "R" + internal[i];
            path[counter] = "R"+d;
        }
    }
}
