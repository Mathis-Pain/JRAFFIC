public class Vehicle {

    private static final int LENGTH = 20;
    private int x, y;
    private int speed;
    private CarType color;
    private Lane currentLane;
    private final Origin origin;
    private boolean moving = false;
    private final Direction direction;

    // ---- Option A : anti-collision dans les virages ----
    // true tant que ce vehicule est engage dans le carrefour (entre le moment
    // ou il entre dans la zone et celui ou il en ressort).
    private boolean entered = false;

    // true des que ce vehicule a fini de traverser le carrefour une premiere
    // fois. Une fois sorti, son feu d'origine ne doit plus jamais l'arreter :
    // sinon, si ce feu repasse au rouge apres son passage, le vehicule se
    // figerait pour toujours sur la route de sortie alors qu'il n'a plus
    // rien a voir avec ce feu.
    private boolean passedIntersection = false;

    // true une fois que le vehicule a negocie son virage (LEFT/RIGHT) au point
    // de pivot ; reste toujours false pour un AHEAD (pas de changement d'axe).
    private boolean turned = false;

    // Cote vers lequel le vehicule se dirige une fois qu'il a tourne, calcule
    // une seule fois au moment du virage via Intersection.getDestination().
    private Origin exitSide = null;

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

    // Vrai tant que le vehicule n'a pas encore ete autorise a entrer dans le
    // carrefour : c'est seulement dans cet etat qu'il compte comme "en
    // attente" dans la file de sa voie pour le calcul de congestion.
    public boolean isQueued() {
        return !entered && !passedIntersection;
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

    // Le parametre `intersection` permet de verifier les conflits de
    // trajectoire (Option A, anti-collision dans les virages) ainsi que de
    // calculer la destination du vehicule (Intersection.getDestination()).
    public void move(Vehicle vehicleAhead, Intersection intersection) {
        boolean insideZone = intersection.isInsideIntersection(x, y);

        // Un vehicule deja engage dans l'intersection doit pouvoir terminer
        // sa traversee meme si le feu passe au rouge entre-temps, sinon il
        // resterait bloque au milieu du carrefour. Et un vehicule qui a deja
        // fini de traverser (passedIntersection) ne doit plus jamais etre
        // arrete par le feu de sa voie d'origine : ce feu ne controle que
        // l'entree dans le carrefour, pas la route apres la sortie.
        if (currentLane.getTrafficLight().isRed() && !insideZone && !passedIntersection) {
            return;
        }

        // ---- Distance de securite (affinee pour le post-virage) ----
        // On compare desormais sur l'axe du cap REEL et ACTUEL du vehicule
        // (getHeading()), qui change automatiquement une fois le virage
        // negocie, plutot que sur l'axe fige de sa voie d'origine. Couple au
        // fait que `vehicleAhead` est maintenant calcule par Simulation en
        // fonction de ce meme cap (voir computeVehicleAhead), ce check reste
        // valable meme quand le vehicule devant vient d'une autre voie
        // d'origine (ex: un "Nord -> gauche" qui suit un "Ouest -> tout droit"
        // une fois que les deux roulent vers l'Est).
        if (vehicleAhead != null) {
            Origin heading = getHeading();
            int distance;
            if (heading == Origin.North || heading == Origin.South) {
                distance = Math.abs(this.y - vehicleAhead.getY());
            } else {
                distance = Math.abs(this.x - vehicleAhead.getX());
            }
            if (distance < LENGTH + SAFETY_GAP) {
                return;
            }
        }

        // ---- Anti-collision dans les virages (Option A) ----
        // A l'entree de la zone du carrefour, le vehicule demande
        // l'autorisation a l'intersection : s'il existe un vehicule en
        // conflit deja engage (origine opposee + virage a gauche), il
        // attend, comme s'il avait un vehicule devant lui.
        if (insideZone) {
            if (!entered) {
                if (!intersection.canEnter(this)) {
                    return; // un vehicule en conflit est deja dans le carrefour : on attend
                }
                intersection.enter(this);
                entered = true;
            }

            // ---- Detection du point de virage ----
            // Un AHEAD continue tout droit, donc rien a faire. Pour un
            // LEFT/RIGHT, le virage a lieu au point de pivot, proche du
            // centre du carrefour.
            if (!turned && direction != Direction.AHEAD && intersection.isAtPivot(x, y)) {
                exitSide = intersection.getDestination(origin, direction);
                turned = true;
            }
        } else if (entered) {
            // Le vehicule a quitte la zone du carrefour : il libere sa place
            // et n'est plus jamais concerne par le feu de sa voie d'origine.
            intersection.leave(this);
            entered = false;
            passedIntersection = true;
        }

        // ---- Deplacement ----
        // Unifie sur getHeading() : avant le virage c'est le cap d'approche
        // (equivalent a l'axe de la voie d'origine), apres le virage c'est le
        // cote de sortie reel. Un seul switch, donc plus aucun risque que
        // deplacement et distance de securite utilisent des axes differents.
        switch (getHeading()) {
            case North:
                y -= speed;
                break;
            case South:
                y += speed;
                break;
            case East:
                x += speed;
                break;
            case West:
                x -= speed;
                break;
        }
    }

    // Cap actuel du vehicule, exprime comme le cote vers lequel il roule.
    // Avant le virage : cap theorique "AHEAD" de son origine (donc le meme
    // axe que sa voie d'origine -- North / South, East / West).
    // Apres le virage (LEFT/RIGHT negocie) : cote de sortie reel, calcule une
    // seule fois via Intersection.getDestination() au moment du pivot.
    public Origin getHeading() {
        return turned ? exitSide : oppositeOf(origin);
    }

    private static Origin oppositeOf(Origin origin) {
        switch (origin) {
            case North:
                return Origin.South;
            case South:
                return Origin.North;
            case East:
                return Origin.West;
            case West:
                return Origin.East;
        }
        throw new IllegalArgumentException("Invalid origin");
    }
}