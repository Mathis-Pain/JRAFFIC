public class Intersection {
    private static final int SAFETY_GAP = 10;

    private Lane northLane;
    private Lane southLane;
    private Lane eastLane;
    private Lane westLane;

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

    public void setInterval(int interval) {
        this.switchInterval = interval;
    }

    public Lane getRandomLane() {
    Lane[] lanes = {northLane, southLane, eastLane, westLane};
    return lanes[(int)(Math.random() * lanes.length)];
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
    public Lane getNorthLane(){
        return northLane;
    }
    public Lane getSouthLane(){
        return southLane;
    }
    public Lane getEastLane(){
        return  eastLane;
    }
    public Lane getWestLane(){
        return westLane;
    }
}