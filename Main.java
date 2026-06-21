
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
// simulation spawn d'un vehicule
        Simulation.spawnVehicle(northLane, Direction.AHEAD);
// timer pour faire avancer les vehicules 
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Simulation.update(intersection);
            }
        }, 0, 16);

        System.out.println("Simulation créée");

    }

}
