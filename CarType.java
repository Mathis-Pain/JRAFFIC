import java.awt.Rectangle;
import java.util.List;
import java.util.ArrayList;

/**
 * Définit le sprite d'un type de véhicule.
 * Chaque Rectangle correspond à la zone découpée dans la spritesheet
 * pour une direction donnée : new Rectangle(x, y, largeur, hauteur).
 */
public class CarType {

    public final String name;

    public final Rectangle north;
    public final Rectangle south;
    public final Rectangle east;
    public final Rectangle west;

    public CarType(String name,
                   Rectangle north, Rectangle south,
                   Rectangle east,  Rectangle west) {
        this.name  = name;
        this.north = north;
        this.south = south;
        this.east  = east;
        this.west  = west;
    }

    /**
     * Retourne le Rectangle correspondant à la direction donnée.
     */
    public Rectangle getSpriteFor(LaneType laneType) {
        switch (laneType) {
            case NORTH: return north;
            case SOUTH: return south;
            case EAST:  return east;
            case WEST:  return west;
            default: throw new IllegalArgumentException("Direction inconnue : " + laneType);
        }
    }

    public static CarType fromDirection(Direction direction) {
    List<CarType> catalogue = createCarList();
    switch (direction) {
        case AHEAD: return catalogue.get(1); // blue
        case LEFT:  return catalogue.get(0); // green
        case RIGHT: return catalogue.get(2); // purple
        default: throw new IllegalArgumentException("Unknown direction: " + direction);
    }
}

    /**
     * Crée et retourne a liste des types de véhicules.
     */
    public static List<CarType> createCarList() {
        List<CarType> list = new ArrayList<>();

        list.add(new CarType(
            "green",
            new Rectangle(167, 40, 24, 40),   // north
            new Rectangle(167,  0, 24, 40),   // south
            new Rectangle(  0,  7, 40, 24),   // east
            new Rectangle(  0, 47, 40, 24)    // west
        ));

        list.add(new CarType(
            "blue",
            new Rectangle(167, 122, 24, 40),  // north
            new Rectangle(167,  82, 24, 40),  // south
            new Rectangle(  0,  87, 40, 24),  // east
            new Rectangle(  0, 127, 40, 24)   // west
        ));

        list.add(new CarType(
            "purple",
            new Rectangle(167, 280, 24, 40),  // north
            new Rectangle(167, 240, 24, 40),  // south
            new Rectangle(  0, 247, 40, 24),  // east
            new Rectangle(  0, 287, 40, 24)   // west
        ));

        return list;
    }
}