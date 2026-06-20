
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) {

        TrafficLight northLight = new TrafficLight(LightColor.GREEN);

        TrafficLight southLight = new TrafficLight(LightColor.GREEN);

        TrafficLight eastLight = new TrafficLight(LightColor.RED);

        TrafficLight westLight = new TrafficLight(LightColor.RED);

        Lane northLane = new Lane(500, northLight, LaneType.NORTH);

        Lane southLane = new Lane(500, southLight, LaneType.SOUTH);

        Lane eastLane = new Lane(500, eastLight, LaneType.EAST);

        Lane westLane = new Lane(500, westLight, LaneType.WEST);

        Intersection intersection = new Intersection(
                northLane,
                southLane,
                eastLane,
                westLane);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                update(intersection);
            }
        }, 0, 16);

        System.out.println("Simulation créée");

    }

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

}
