import java.util.ArrayList;
import java.util.List;

public class Intersection {
    private static final int SAFETY_GAP = 10;

    private Lane northLane;
    private Lane southLane;
    private Lane eastLane;
    private Lane westLane;

    // ---- Option A : anti-collision dans les virages ----
    // Coordonnées du centre du carrefour et rayon de sa zone, calibrés sur
    // Simulation.spawnVehicle() (spawns à 250px du centre (250,250)).
    private static final int CENTER_X = 250;
    private static final int CENTER_Y = 250;
    private static final int INTERSECTION_RADIUS = 60; // zone où la règle de conflit s'applique

    // Véhicules actuellement engagés dans le carrefour (entrés mais pas encore
    // sortis)
    private final List<Vehicle> insideIntersection = new ArrayList<>();

    private boolean nsGreen;

    private int timer;
    private static final int NORMAL_INTERVAL = 120;
    private static final int EXTENDED_INTERVAL = 200;
    private static final int REDUCED_INTERVAL = 60;
    private int switchInterval = NORMAL_INTERVAL;

    public Intersection(
            Lane northLane,
            Lane southLane,
            Lane eastLane,
            Lane westLane) {
        this.northLane = northLane;
        this.southLane = southLane;
        this.eastLane = eastLane;
        this.westLane = westLane;

        this.nsGreen = true;
        northLane.getTrafficLight().setColor(LightColor.GREEN);
        southLane.getTrafficLight().setColor(LightColor.GREEN);

        eastLane.getTrafficLight().setColor(LightColor.RED);
        westLane.getTrafficLight().setColor(LightColor.RED);
    }

    // ---- Getters & Setters ----
    public int getInterval() {
        return this.switchInterval;
    }

    // Vrai si (x, y) est dans la zone circulaire du carrefour (rayon INTERSECTION_RADIUS).
    public boolean isInsideIntersection(int x, int y) {
        int dx = x - CENTER_X;
        int dy = y - CENTER_Y;
        return dx * dx + dy * dy <= INTERSECTION_RADIUS * INTERSECTION_RADIUS;
    }

    // Pivot au coordonnée exacte de la voie de destination pour que le véhicule
    // se retrouve sur la bonne voie après son virage.
    // Voies : southbound x=220, northbound x=270, westbound y=220, eastbound y=270.
    public boolean isAtPivot(int x, int y, Origin origin, Direction direction) {
        final int TOL = 3;
        switch (origin) {
            case North: return (direction == Direction.LEFT) ? y >= 270 - TOL : y >= 220 - TOL;
            case South: return (direction == Direction.LEFT) ? y <= 220 + TOL : y <= 270 + TOL;
            case East:  return (direction == Direction.LEFT) ? x <= 220 + TOL : x <= 270 + TOL;
            case West:  return (direction == Direction.LEFT) ? x >= 270 - TOL : x >= 220 - TOL;
        }
        return false;
    }

    // Un véhicule peut entrer dans le carrefour si aucun véhicule déjà engagé
    // n'est en conflit avec sa trajectoire.
    public boolean canEnter(Vehicle vehicle) {
        for (Vehicle other : insideIntersection) {
            if (conflicts(vehicle, other)) {
                return false;
            }
        }
        return true;
    }

    public void enter(Vehicle vehicle) {
        if (!insideIntersection.contains(vehicle)) {
            insideIntersection.add(vehicle);
        }
    }

    public void leave(Vehicle vehicle) {
        insideIntersection.remove(vehicle);
    }

    // ---- Règle de conflit ----
    // Deux véhicules sont en conflit dans deux cas :
    // 1. Axes perpendiculaires (N/S vs E/W) : toujours bloqués, car un véhicule
    //    de la phase précédente peut encore être dans la zone quand le feu change.
    // 2. Même axe, origines opposées, et exactement l'un des deux tourne à GAUCHE :
    //    c'est le seul virage dont la trajectoire traverse celle du véhicule d'en face.
    private boolean conflicts(Vehicle a, Vehicle b) {
        // Axes perpendiculaires (N/S vs E/W) : toujours en conflit.
        // Garantit qu'un vehicule E/W attend qu'un N/S ait libere l'intersection
        // apres un changement de feu (et vice-versa).
        boolean aIsNS = (a.getOrigin() == Origin.North || a.getOrigin() == Origin.South);
        boolean bIsNS = (b.getOrigin() == Origin.North || b.getOrigin() == Origin.South);
        if (aIsNS != bIsNS) {
            return true;
        }

        // Meme axe, origines opposees.
        boolean oppositeOrigins = (a.getOrigin() == Origin.North && b.getOrigin() == Origin.South)
                || (a.getOrigin() == Origin.South && b.getOrigin() == Origin.North)
                || (a.getOrigin() == Origin.East && b.getOrigin() == Origin.West)
                || (a.getOrigin() == Origin.West && b.getOrigin() == Origin.East);

        if (!oppositeOrigins) return false;

        // Virage a droite : ne croise jamais les trajectoires adverses.
        if (a.getDirection() == Direction.RIGHT) return false;

        // Exactement un des deux tourne a gauche : leurs trajectoires se croisent.
        // AHEAD vs AHEAD : pas de conflit (voies x=220 / x=270 separees).
        // LEFT vs LEFT  : pas de conflit (NL→East y=270, SL→West y=220 ne se croisent pas).
        boolean aLeft = a.getDirection() == Direction.LEFT;
        boolean bLeft = b.getDirection() == Direction.LEFT;
        return aLeft != bLeft;
    }

    public void setInterval(int interval) {
        this.switchInterval = interval;
    }

    public Lane getRandomLane() {
        Lane[] lanes = { northLane, southLane, eastLane, westLane };
        return lanes[(int) (Math.random() * lanes.length)];
    }

    // ---- Gestion des feux ----
    public void switchLights() {
        nsGreen = !nsGreen;

        if (nsGreen) {
            northLane.getTrafficLight().setColor(LightColor.GREEN);
            southLane.getTrafficLight().setColor(LightColor.GREEN);

            eastLane.getTrafficLight().setColor(LightColor.RED);
            westLane.getTrafficLight().setColor(LightColor.RED);
        } else {
            northLane.getTrafficLight().setColor(LightColor.RED);
            southLane.getTrafficLight().setColor(LightColor.RED);

            eastLane.getTrafficLight().setColor(LightColor.GREEN);
            westLane.getTrafficLight().setColor(LightColor.GREEN);
        }
    }

    private void updateSwitchInterval() {

        boolean greenSideCongested;
        boolean redSideCongested;

        if (nsGreen) {
            greenSideCongested = isCongested(northLane) || isCongested(southLane);
            redSideCongested = isCongested(eastLane) || isCongested(westLane);
        } else {
            greenSideCongested = isCongested(eastLane) || isCongested(westLane);
            redSideCongested = isCongested(northLane) || isCongested(southLane);
        }

        if (greenSideCongested) {
            // la voie verte a besoin de plus de temps pour s'écouler
            setInterval(EXTENDED_INTERVAL);
        } else if (redSideCongested) {
            // la voie rouge sature : on écourte la phase verte actuelle
            setInterval(REDUCED_INTERVAL);
        } else {
            setInterval(NORMAL_INTERVAL);
        }
    }

    public void updateTrafficLights() {
        timer++;

        if (timer >= getInterval()) {
            switchLights();
            timer = 0;
        }
    }

    public boolean wouldEnterIntersection(int x, int y, Origin heading, int speed) {
        int nx = x, ny = y;
        switch (heading) {
            case South: ny += speed; break;
            case North: ny -= speed; break;
            case West:  nx -= speed; break;
            case East:  nx += speed; break;
        }
        return isInsideIntersection(nx, ny);
    }

    // ---- Autorisation de mouvement des véhicules ----
    public boolean canMove(Lane lane) {

        if (lane == northLane || lane == southLane) {
            return nsGreen;
        } else {
            return !nsGreen;
        }
    }

    private void updateLane(Lane lane) {

        boolean canMove = canMove(lane);

        for (Vehicle vehicle : lane.getVehicles()) {
            vehicle.setMoving(canMove);
        }
    }

    // --- Gestion des embouteillages ----
    private int calculateCapacity(Lane lane) {
        return lane.getLength()
                / (Vehicle.getLENGTH() + SAFETY_GAP);
    }

    public boolean isCongested(Lane lane) {
        return lane.getVehicles().size() >= calculateCapacity(lane);
    }

    // ---- Calcul de la destination en fonction de l'origine et de la destination
    // ----
    public Origin getDestination(Origin origin, Direction direction) {
        switch (origin) {
            case North:
                switch (direction) {
                    case AHEAD:
                        return Origin.South;
                    case LEFT:
                        return Origin.East;
                    case RIGHT:
                        return Origin.West;
                }
                break;

            case South:
                switch (direction) {
                    case AHEAD:
                        return Origin.North;
                    case LEFT:
                        return Origin.West;
                    case RIGHT:
                        return Origin.East;
                }
                break;

            case East:
                switch (direction) {
                    case AHEAD:
                        return Origin.West;
                    case LEFT:
                        return Origin.South;
                    case RIGHT:
                        return Origin.North;
                }
                break;

            case West:
                switch (direction) {
                    case AHEAD:
                        return Origin.East;
                    case LEFT:
                        return Origin.North;
                    case RIGHT:
                        return Origin.South;
                }
                break;
        }

        throw new IllegalArgumentException("Invalid origin or direction");
    }

    // ---- Cerveau de l'intersection ----
    public void update() {
        updateSwitchInterval();
        updateTrafficLights();

        updateLane(northLane);
        updateLane(southLane);
        updateLane(westLane);
        updateLane(eastLane);

    }

    public Lane getNorthLane() {
        return northLane;
    }

    public Lane getSouthLane() {
        return southLane;
    }

    public Lane getEastLane() {
        return eastLane;
    }

    public Lane getWestLane() {
        return westLane;
    }
}