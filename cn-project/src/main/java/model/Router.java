package model;

import java.util.List;
import java.util.Map;

public class Router {

    private String label;
    private List<String> neighbors;
    private List<Double> cost;
    private List<String> destinations;
    private Map<String, Map<String, Double>> routingTable; //Map<destinations, Map<neighbor, shortestPathCost>>
    private List<String> path;

    public Router(String label, List<String> neighbors, List<Double> cost, List<String> destinations, Map<String, Map<String, Double>> routingTable, List<String> path) {

        this.label = label;
        this.neighbors = neighbors;
        this.cost = cost;
        this.destinations = destinations;
        this.routingTable = routingTable;
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getNeighbors() {
        return neighbors;
    }

    public List<Double> getCost() {
        return cost;
    }

    public List<String> getDestinations() {
        return destinations;
    }

    public Map<String, Map<String, Double>> getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(Map<String, Map<String, Double>> routingTable) {
        this.routingTable = routingTable;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }
}
