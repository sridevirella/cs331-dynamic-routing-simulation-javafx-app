package handler;

import model.Router;
import org.json.JSONObject;
import utils.RoutingAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class RouterHandler {

    private static double[][] shortestDistance;
    private static int routersCount = 0;
    private static int inf = Integer.MAX_VALUE - 100000;
    private static List<Router> routersList = new ArrayList<>();

    private Router router;

    public RouterHandler(Router router) {

        this.router = router;
        routersList.add(router);
    }

    public void builder() {

        String packet = packetBuilder();
        router.getDestinations().forEach(destination -> listener(packet));
    }

    private String packetBuilder() {

        JSONObject packet = new JSONObject();
        packet.put("label", router.getLabel());
        packet.put("neighbours", router.getNeighbors());
        packet.put("cost", router.getCost());
        return packet.toString();
    }

    public void listener(String packet) {

        JSONObject receivedPacket = new JSONObject(packet);
        int row = Integer.parseInt(receivedPacket.getString("label").substring(1));
        double[] costArray = getCostsFromJson(receivedPacket);
        double[] neighbourCostArray = getNeighbourCostArray();

        calculateRoutingPath(receivedPacket, row, costArray, neighbourCostArray);
    }

    public void changeCost(int neighborIndex, double newCost) {

        router.getCost().set(neighborIndex, newCost);
        builder();
    }

    public static StringBuilder displayTable(Router router) {

        Map<String, Map<String, Double>> routingTable = router.getRoutingTable();
        StringBuilder routingTableData = new StringBuilder();
        routingTableData.append("\n").append(router.getLabel())
                .append(" Routing table\n=========================================")
                .append("\nDestinations" +"\t\t"+"Neighbour"+"\t\t"+"Cost"+"\t\t\t"+"Path\n\n");

        StringBuilder mapData = new StringBuilder();
        routingTable.forEach((key1, value1) -> value1.forEach((key, value) -> mapData.append(key1).append("\t\t\t\t")
                                                                                      .append(key).append("\t\t\t\t")
                                                                                      .append(value).append("\t\t")
                                                                                      .append((router.getPath() != null ? router.getPath().get(Integer.parseInt(key1.substring(1))) : ""))
                                                                                      .append("\n")));
        routingTableData.append(mapData);
        return routingTableData;
    }

    public static void setRoutersCount(int routerCount) {

        routersCount = routerCount;
        shortestDistance = new double[routerCount+1][routerCount+1];
    }

    private void calculateRoutingPath(JSONObject receivedPacket, int row, double[] costArray, double[] neighbourCostArray) {

        createNeighbourCostArray(receivedPacket, costArray, neighbourCostArray);
        constructMatrix(neighbourCostArray, row);
        findShortestRoutingPath(row);
    }

    private void findShortestRoutingPath(int row) {

        if(shortestDistance[0][0] == (double) (routersCount * routersCount))
            routersList.forEach(eachRouter -> RoutingAlgorithm.shortestPathCost(shortestDistance, Integer.parseInt(eachRouter.getLabel().substring(1)), eachRouter));

        else if(shortestDistance[0][0] > (double) (routersCount * routersCount))
            RoutingAlgorithm.shortestPathCost(shortestDistance, row, router);
    }

    private double[] getNeighbourCostArray() {

        return IntStream.range(0, routersCount + 1)
                                               .mapToDouble(i -> inf)
                                               .toArray();
    }

    private void createNeighbourCostArray(JSONObject receivedPacket, double[] costArray, double[] neighbourCostArray) {

        AtomicInteger count = new AtomicInteger(0);
        receivedPacket.getJSONArray("neighbours").toList().stream()
                                                    .mapToInt(value -> Integer.parseInt(value.toString().substring(1)))
                                                    .forEach(intValue -> neighbourCostArray[intValue] = costArray[count.getAndIncrement()]);
    }

    private double[] getCostsFromJson(JSONObject receivedPacket) {

        return receivedPacket.getJSONArray("cost").toList()
                                  .stream()
                                  .mapToDouble(value -> Double.parseDouble(value.toString()))
                                  .toArray();
    }

    private void constructMatrix(double[] forwardPacket, int row) {

        IntStream.range(1, forwardPacket.length)
                 .filter(i -> i != row)
                 .forEach(i -> shortestDistance[row][i] = forwardPacket[i]);
       shortestDistance[0][0]++;
    }
}
