
public class Vehicle {

    private static final int LENGTH = 20;
    private int x, y;
    private int speed;
    private String color;
    private Lane currentLane;
    private Origin origin;
    private boolean moving = false;
    private Direction direction;

    public Vehicle(int x, int y, int speed, String color, Lane currentLane, Direction direction, Origin origin) {
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

    public String getColor() {
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

    public void move(Vehicle vehicleAhead, Intersection intersection) {
        if (currentLane.getTrafficLight().isRed()) {
            return;
        }
// change de direction au croisement de l'intersection en focntion de la direction choisie
        LaneType type = currentLane.getType();
        if ((type == LaneType.NORTH && y >= 250)
                || (type == LaneType.SOUTH && y <= 250)
                || (type == LaneType.EAST && x <= 250)
                || (type == LaneType.WEST && x >= 250)) {
            if (type == LaneType.NORTH) {
                if (direction == Direction.LEFT) {
                    currentLane = intersection.getEastLane();
                }
                if (direction == Direction.RIGHT) {
                    currentLane = intersection.getWestLane();
                }
            }
            if (type == LaneType.SOUTH) {
                if (direction == Direction.LEFT) {
                    currentLane = intersection.getWestLane();
                }
                if (direction == Direction.RIGHT) {
                    currentLane = intersection.getEastLane();
                }
            }
            if (type == LaneType.EAST) {
                if (direction == Direction.LEFT) {
                    currentLane = intersection.getSouthLane();
                }
                if (direction == Direction.RIGHT) {
                    currentLane = intersection.getNorthLane();
                }
            }
            if (type == LaneType.WEST) {
                if (direction == Direction.LEFT) {
                    currentLane = intersection.getNorthLane();
                }
                if (direction == Direction.RIGHT) {
                    currentLane = intersection.getSouthLane();
                }
            }

        }
// verifie si le vehicule a la place d'avancer
        if (vehicleAhead != null) {
            type = currentLane.getType();
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
// fait avancer le vehicule suivant la lane a laquelle il appartient
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
        // supression des vehicules de la liste quand ils sortent du cadre 500*500
        if (x > 500 || x < 0 || y > 500 || y < 0) {
            currentLane.getVehicles().remove(this);
        }
    }
}
