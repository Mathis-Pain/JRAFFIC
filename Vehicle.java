public class Vehicle {

    private static final int LENGTH = 20;
    private int x, y;
    private int speed;
    private CarType color;
    private Lane currentLane;
    private Origin origin;
    private boolean moving = false;
    private Direction direction;

    // ---- anti-collision dans les virages ----
    // Cellule de la grille de l'intersection actuellement réservée par ce
    // véhicule (null tant qu'il n'est pas entré dans l'intersection).
    private Cell currentCell = null;

    public Vehicle(int x, int y, int speed, CarType color, Lane currentLane, Direction direction, Origin origin) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.color = color;
        this.currentLane = currentLane;
        this.direction = direction;
        this.origin = origin;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public CarType getColor() {
        return color;
    }

    public Lane getCurrentLane() {
        return currentLane;
    }

    public Origin getOrigin() {
        return origin;
    }

    public boolean isMoving() {
        return moving;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public void setCurrentLane(Lane currentLane) {
        this.currentLane = currentLane;
    }

    public static int getLENGTH() {
        return LENGTH;
    }

    private static final int SAFETY_GAP = 10;

    // Le paramètre `grid` permet de vérifier que la prochaine
    // portion de l'intersection que le véhicule va occuper est libre, afin
    // d'éviter les collisions entre véhicules dont les trajectoires (lignes
    // droites, virages à gauche/droite) se croisent.
    public void move(Vehicle vehicleAhead, IntersectionGrid grid) {
        if (currentLane.getTrafficLight().isRed()) {
            return;
        }

        if (vehicleAhead != null) {
            LaneType type = currentLane.getType();
            int distance;
            if (type == LaneType.NORTH || type == LaneType.SOUTH) {
                distance = Math.abs(this.y - vehicleAhead.getY());
            } else {
                distance = Math.abs(this.x - vehicleAhead.getX());
            }
            if (distance < LENGTH + SAFETY_GAP) {
                return;
            }
        }

        // ---- Anti-collision dans les virages (réservation de cellule) ----
        // Tant que le véhicule est dans la zone de l'intersection, il doit
        // réserver la cellule où il se trouve avant d'avancer. Si la cellule
        // est déjà occupée par un autre véhicule, il attend, comme s'il avait
        // un véhicule devant lui (même mécanisme que la distance de sécurité).
        if (grid.isInsideIntersection(x, y)) {
            Cell targetCell = grid.getCellAt(x, y);

            if (targetCell != currentCell) {
                if (!grid.tryEnterCell(this, targetCell)) {
                    return; // cellule occupée par un autre véhicule : on attend
                }
                if (currentCell != null) {
                    grid.releaseCell(this, currentCell);
                }
                currentCell = targetCell;
            }
        } else if (currentCell != null) {
            // Le véhicule a quitté la zone de l'intersection : on libère sa dernière
            // cellule
            grid.releaseCell(this, currentCell);
            currentCell = null;
        }

        switch (currentLane.getType()) {
            case NORTH:
                y -= speed;
                break;
            case SOUTH:
                y += speed;
                break;
            case EAST:
                x -= speed;
                break;
            case WEST:
                x += speed;
                break;
        }
    }
}