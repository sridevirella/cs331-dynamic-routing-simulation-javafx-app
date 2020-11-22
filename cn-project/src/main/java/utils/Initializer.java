package utils;

import model.Router;

import static utils.FileReadWrite.getOutPath;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Initializer {

    private static List<String> neighborsList;
    private static List<Double> costList;
    private static List<String> destinationList;
    private static String label;
    private Initializer() {}

    public static List<Router> getRoutersList(String fileName) throws IOException {

        BufferedReader br = Files.newBufferedReader(getOutPath(fileName));
        int routersCount = br.lines().limit(1).map(Integer::parseInt).collect(Collectors.toList()).get(0);
        List<Router> routerList = new ArrayList<>();

        br.lines().forEach(line -> { parseAndCreateRouter(line, routersCount, routerList); });
        return routerList;
    }

    private static void parseAndCreateRouter(String line, int routersCount, List<Router> routerList) {

        label = line.split(":")[0].trim();
        neighborsList = getNeighborsList(line);
        costList = getCost(line);
        destinationList = getDestinations(routersCount);
        Map<String, Map<String, Double>> routingTable = initRoutingTable();
        routerList.add(new Router(label, neighborsList,costList , getDestinations(routersCount), routingTable));
    }

    private static Map<String, Map<String, Double>> initRoutingTable() {


       Map<String, Double> neighbourCostList = IntStream.range(0, neighborsList.size())
                                                        .boxed()
                                                        .collect(Collectors.toMap(i -> neighborsList.get(i), i -> costList.get(i)));

       return IntStream.range(0, destinationList.size())
                       .boxed()
                       .filter(i -> !destinationList.get(i).equals(label))
                       .collect(Collectors.toMap(i -> destinationList.get(i), i -> (neighbourCostList.containsKey(destinationList.get(i))) ?
                                Collections.singletonMap(destinationList.get(i), neighbourCostList.get(destinationList.get(i)) ) :
                                Collections.singletonMap("No", -1.0 )
                        ));
    }

    private static List<Double> getCost(String line) {

        return Arrays.stream(line.split(":")[1].split(";"))
                                  .map( valuePairStr -> { String costString = valuePairStr.split(", ")[1].trim();
                                                           return costString.substring(0, costString.indexOf(')')); })
                                  .map(Double::parseDouble)
                                  .collect(Collectors.toList());
    }

    private static List<String> getNeighborsList(String line) {

        return Arrays.stream(line.split(":")[1].split(";"))
                                       .map(valuePairStr -> { return valuePairStr.split(", ")[0].trim().substring(1); })
                                       .collect(Collectors.toList());
    }

    private static List<String> getDestinations(int routersCount) {

        return IntStream.rangeClosed(1, routersCount)
                        .mapToObj(i -> "R" + i)
                        .collect(Collectors.toList());
    }
}
