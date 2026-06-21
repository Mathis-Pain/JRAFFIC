import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simulation {
    public static void update(Intersection intersection) {
        List<Vehicle> allVehicles = new ArrayList<>();
        allVehicles.addAll(intersection.getNorthLane().getVehicles());
        allVehicles.addAll(intersection.getSouthLane().getVehicles());
        allVehicles.addAll(intersection.getEastLane().getVehicles());
        allVehicles.addAll(intersection.getWestLane().getVehicles());

        Map<Vehicle, Vehicle> aheadByHeading = computeVehicleAhead(allVehicles);

        for (Vehicle vehicle : allVehicles) {
            vehicle.move(aheadByHeading.get(vehicle), intersection);
        }

        // Une fois qu'un vehicule a completement quitte la zone visible, on le retire
        for (Vehicle vehicle : allVehicles) {
            if (hasLeftSimulation(vehicle)) {
                vehicle.getCurrentLane().removeVehicle(vehicle);
            }
        }
    }
    private static final int OFFSCREEN_MARGIN = 50;

    private static boolean hasLeftSimulation(Vehicle vehicle) {
        int x = vehicle.getX();
        int y = vehicle.getY();
        return x < -OFFSCREEN_MARGIN || x > 500 + OFFSCREEN_MARGIN
                || y < -OFFSCREEN_MARGIN || y > 500 + OFFSCREEN_MARGIN;
    }

    // Pour chaque cap (Nord/Sud/Est/Ouest), regroupe les véhicules qui roulent
    // actuellement dans cette direction (qu'ils soient encore sur leur voie
    // d'origine ou déjà en train de sortir après un virage), les trie du plus
    // avancé au moins avancé, puis associe à chacun le véhicule juste devant.
    private static Map<Vehicle, Vehicle> computeVehicleAhead(List<Vehicle> allVehicles) {
        Map<Vehicle, Vehicle> aheadMap = new HashMap<>();

        for (Origin heading : Origin.values()) {
            List<Vehicle> group = new ArrayList<>();
            for (Vehicle vehicle : allVehicles) {
                if (vehicle.getHeading() == heading) {
                    group.add(vehicle);
                }
            }

            // Le plus avancé (celui qui a le moins de chemin restant dans ce
            // cap) en premier ; group.get(0) n'a personne devant lui.
            group.sort((a, b) -> progressAlongHeading(b, heading) - progressAlongHeading(a, heading));

            for (int i = 1; i < group.size(); i++) {
                aheadMap.put(group.get(i), group.get(i - 1));
            }
        }

        return aheadMap;
    }

    // Mesure à quel point un véhicule est avancé sur son cap : plus la valeur
    // est grande, plus il est proche de sa sortie dans cette direction.
    private static int progressAlongHeading(Vehicle vehicle, Origin heading) {
        switch (heading) {
            case North:
                return -vehicle.getY(); // vers le nord : y diminue
            case South:
                return vehicle.getY(); // vers le sud : y augmente
            case East:
                return vehicle.getX(); // vers l'est : x augmente
            case West:
                return -vehicle.getX(); // vers l'ouest : x diminue
            default:
                return 0;
        }
    }

    // Distance minimale qu'il doit y avoir entre le point d'apparition d'un
    // nouveau vehicule et le vehicule le plus proche deja present sur la voie. 
    private static final int SAFETY_GAP = 10;

    public static boolean spawnVehicle(Lane lane, Direction direction) {
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

        if (!canSpawn(lane, x, y)) {
            System.out.println("Spawn ignore : pas assez de place pres du point d'apparition.");
            return false;
        }

        CarType carType = CarType.fromDirection(direction);
        Vehicle v = new Vehicle(x, y, 2, carType, lane, direction, origin);
        lane.addVehicle(v);
        return true;
    }

    // Verifie qu'aucun vehicule deja present sur la voie n'est trop proche du point de spawn
    private static boolean canSpawn(Lane lane, int spawnX, int spawnY) {
        int minSafeDistance = Vehicle.getLENGTH() + SAFETY_GAP;
        for (Vehicle vehicle : lane.getVehicles()) {
            int dx = vehicle.getX() - spawnX;
            int dy = vehicle.getY() - spawnY;
            double distance = Math.sqrt((double) (dx * dx + dy * dy));
            if (distance < minSafeDistance) {
                return false;
            }
        }
        return true;
    }
}