import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simulation {

    // NOTE : move() prend maintenant `intersection` en plus du véhicule de
    // devant, pour pouvoir réserver une cellule de la grille (Option B,
    // anti-collision dans les virages) et calculer le côté de sortie via
    // intersection.getDestination().
    public static void update(Intersection intersection) {
        List<Vehicle> allVehicles = new ArrayList<>();
        allVehicles.addAll(intersection.getNorthLane().getVehicles());
        allVehicles.addAll(intersection.getSouthLane().getVehicles());
        allVehicles.addAll(intersection.getEastLane().getVehicles());
        allVehicles.addAll(intersection.getWestLane().getVehicles());

        // Distance de sécurité affinée (post-virage) : le "véhicule devant"
        // n'est plus déduit de la position dans la liste de la voie
        // d'origine, mais du cap réel (Vehicle.getHeading()) de chaque
        // véhicule. Ça couvre le cas où deux véhicules venus de voies
        // d'origine différentes se retrouvent à rouler dans la même
        // direction après avoir tourné (ex: Nord→gauche et Ouest→tout droit
        // qui filent tous les deux vers l'Est).
        Map<Vehicle, Vehicle> aheadByHeading = computeVehicleAhead(allVehicles);

        for (Vehicle vehicle : allVehicles) {
            vehicle.move(aheadByHeading.get(vehicle), intersection);
        }
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