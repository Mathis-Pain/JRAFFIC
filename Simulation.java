
import java.util.List;

public class Simulation {

    public static void update(Intersection intersection) {
        Lane northLane = intersection.getNorthLane();
        Lane southLane = intersection.getSouthLane();
        Lane eastLane = intersection.getEastLane();
        Lane westLane = intersection.getWestLane();

        List<Vehicle> northLaneVehicles = northLane.getVehicles();
        for (int i = 0; i < northLaneVehicles.size(); i++) {
            Vehicle ahead;
            if (i == 0) {
                ahead = null;
            } else {
                ahead = northLaneVehicles.get(i - 1);
            }
            northLaneVehicles.get(i).move(ahead);
        }

        List<Vehicle> southLaneVehicles = southLane.getVehicles();
        for (int i = 0; i < southLaneVehicles.size(); i++) {
            Vehicle ahead;
            if (i == 0) {
                ahead = null;
            } else {
                ahead = southLaneVehicles.get(i - 1);
            }
            southLaneVehicles.get(i).move(ahead);
        }

        List<Vehicle> eastLaneVehicles = eastLane.getVehicles();
        for (int i = 0; i < eastLaneVehicles.size(); i++) {
            Vehicle ahead;
            if (i == 0) {
                ahead = null;
            } else {
                ahead = eastLaneVehicles.get(i - 1);
            }
            eastLaneVehicles.get(i).move(ahead);
        }

        List<Vehicle> westLaneVehicles = westLane.getVehicles();
        for (int i = 0; i < westLaneVehicles.size(); i++) {
            Vehicle ahead;
            if (i == 0) {
                ahead = null;
            } else {
                ahead = westLaneVehicles.get(i - 1);
            }
            westLaneVehicles.get(i).move(ahead);
        }
    }

    public static void spawnVehicle(Lane lane, Direction direction) {
        int x = 0;
        int y = 0;
        Origin origin = null;
        switch (lane.getType()) {
            case NORTH:
                x = 250;
                y = 0;
                origin = Origin.North;
                break;
            case SOUTH:
                x = 250;
                y = 500;
                origin = Origin.South;
                break;
            case EAST:
                x = 500;
                y = 250;
                origin = Origin.East;
                break;
            case WEST:
                x = 0;
                y = 250;
                origin = Origin.West;
                break;
        }

       CarType carType = CarType.fromDirection(direction);
        Vehicle v = new Vehicle(x, y, 2, carType, lane, direction, origin);
        lane.addVehicle(v);

    }
}
