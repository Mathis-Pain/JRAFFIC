import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ---- Option B : réservation de cellules ----
// Découpe la zone du carrefour en une grille 3x3 et n'autorise qu'un seul
// véhicule à la fois dans chaque cellule. Avant d'avancer dans une nouvelle
// cellule, un véhicule doit la "réserver" ; si elle est déjà occupée par un
// autre véhicule, il doit attendre, exactement comme s'il avait un véhicule
// devant lui. Cela règle les conflits entre véhicules qui tournent (gauche,
// droite) et dont les trajectoires se croisent dans l'intersection, en plus
// de ce que gère déjà le feu de circulation.
public class IntersectionGrid {

    // ---- Géométrie de l'intersection ----
    // ATTENTION : ces valeurs doivent correspondre aux coordonnées réelles du
    // centre du carrefour dans ta simulation (fenêtre JavaFX, position des
    // routes, etc.). Adapte CENTER_X / CENTER_Y / CELL_SIZE à ton rendu, ou
    // donne-moi le fichier qui définit la géométrie de la scène pour que je
    // les calibre précisément.
    private static final int CENTER_X = 250;
    private static final int CENTER_Y = 250;
    private static final int CELL_SIZE = 40; // largeur approximative d'une cellule

    // Cellule -> véhicule qui l'occupe actuellement (une seule case réservée à la
    // fois)
    private final Map<Cell, Vehicle> occupied = new HashMap<>();

    // Trajet (liste ordonnée de cellules) correspondant à chaque combinaison
    // origine/direction. Reprend exactement la logique de
    // Intersection.getDestination().
    private static final Map<Origin, Map<Direction, List<Cell>>> PATHS = new HashMap<>();

    static {
        PATHS.put(Origin.North, Map.of(
                Direction.AHEAD, List.of(Cell.N, Cell.C, Cell.S),
                Direction.LEFT, List.of(Cell.N, Cell.C, Cell.E),
                Direction.RIGHT, List.of(Cell.N, Cell.W)));
        PATHS.put(Origin.South, Map.of(
                Direction.AHEAD, List.of(Cell.S, Cell.C, Cell.N),
                Direction.LEFT, List.of(Cell.S, Cell.C, Cell.W),
                Direction.RIGHT, List.of(Cell.S, Cell.E)));
        PATHS.put(Origin.East, Map.of(
                Direction.AHEAD, List.of(Cell.E, Cell.C, Cell.W),
                Direction.LEFT, List.of(Cell.E, Cell.C, Cell.S),
                Direction.RIGHT, List.of(Cell.E, Cell.N)));
        PATHS.put(Origin.West, Map.of(
                Direction.AHEAD, List.of(Cell.W, Cell.C, Cell.E),
                Direction.LEFT, List.of(Cell.W, Cell.C, Cell.N),
                Direction.RIGHT, List.of(Cell.W, Cell.S)));
    }

    // Renvoie la séquence de cellules que doit traverser un véhicule selon son
    // origine et sa direction (utile pour une future logique de
    // virage/anticipation).
    public List<Cell> getPath(Origin origin, Direction direction) {
        return PATHS.get(origin).get(direction);
    }

    // Indique si la position (x, y) se trouve dans la zone du carrefour (grille
    // 3x3)
    public boolean isInsideIntersection(int x, int y) {
        return Math.abs(x - CENTER_X) <= (CELL_SIZE * 3) / 2
                && Math.abs(y - CENTER_Y) <= (CELL_SIZE * 3) / 2;
    }

    // Détermine dans quelle cellule de la grille 3x3 se trouve la position (x, y)
    public Cell getCellAt(int x, int y) {
        int dx = x - CENTER_X;
        int dy = y - CENTER_Y;

        int col = dx < -CELL_SIZE / 2 ? 0 : dx > CELL_SIZE / 2 ? 2 : 1;
        int row = dy < -CELL_SIZE / 2 ? 0 : dy > CELL_SIZE / 2 ? 2 : 1;

        Cell[][] grid = {
                { Cell.NW, Cell.N, Cell.NE },
                { Cell.W, Cell.C, Cell.E },
                { Cell.SW, Cell.S, Cell.SE }
        };
        return grid[row][col];
    }

    // Tente de réserver une cellule pour un véhicule.
    // Renvoie true si la cellule est libre (ou déjà occupée par ce même véhicule),
    // false si elle est occupée par un autre véhicule (le véhicule appelant doit
    // alors attendre).
    public boolean tryEnterCell(Vehicle vehicle, Cell cell) {
        Vehicle current = occupied.get(cell);
        if (current == null || current == vehicle) {
            occupied.put(cell, vehicle);
            return true;
        }
        return false;
    }

    // Libère une cellule précédemment réservée par un véhicule
    public void releaseCell(Vehicle vehicle, Cell cell) {
        occupied.remove(cell, vehicle);
    }

    // Libère toutes les cellules réservées par un véhicule (ex : quand il sort
    // de l'intersection ou qu'il est supprimé de la simulation)
    public void releaseAll(Vehicle vehicle) {
        occupied.values().removeIf(v -> v == vehicle);
    }
}