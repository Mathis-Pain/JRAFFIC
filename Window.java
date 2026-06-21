import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.JPanel;
import java.awt.Dimension;

public class Window extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private static final int MARGIN = 25;
    private static final int SIM_SIZE = 500;
    private static final int Width = SIM_SIZE + 2 * MARGIN;
    private static final int Height = SIM_SIZE + 2 * MARGIN;

    private static final int CENTER_X = 250;
    private static final int CENTER_Y = 250;
    private static final int ROAD_HALF = 55; // demi-largeur de chaque route (2 voies)
    private static final int LIGHT_OFFSET = 18; // distance entre l'intersection et le feu

    private static final Color GRASS = new Color(45, 95, 55);
    private static final Color ASPHALT = new Color(55, 55, 60);
    private static final Color LANE_LINE = new Color(230, 200, 60);

    private Intersection intersection;

    public Window(Intersection intersection) {
        this.intersection = intersection;
        setPreferredSize(new Dimension(Width, Height));
        setBackground(GRASS);

        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

    }

    public void keyPressed(KeyEvent e) {
        // Si la touche enfoncée est Échap, on ferme le programme
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            System.out.println("Création d'un véhicule allant vers le nord");
            Direction randomDirection = Direction.values()[new Random().nextInt(Direction.values().length)];
            Simulation.spawnVehicle(intersection.getSouthLane(), randomDirection);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            System.out.println("Création d'un véhicule allant vers le sud");
            Direction randomDirection = Direction.values()[new Random().nextInt(Direction.values().length)];
            Simulation.spawnVehicle(intersection.getNorthLane(), randomDirection);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            System.out.println("Création d'un véhicule allant vers l'est");
            Direction randomDirection = Direction.values()[new Random().nextInt(Direction.values().length)];
            Simulation.spawnVehicle(intersection.getWestLane(), randomDirection);
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            System.out.println("Création d'un véhicule allant vers l'ouest");
            Direction randomDirection = Direction.values()[new Random().nextInt(Direction.values().length)];
            Simulation.spawnVehicle(intersection.getEastLane(), randomDirection);
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            System.out.println("Création d'un véhicule aléatoire");
            Direction randomDirection = Direction.values()[new Random().nextInt(Direction.values().length)];
            Lane randomLane = intersection.getRandomLane();
            Simulation.spawnVehicle(randomLane, randomDirection);
        }
    }

    // Méthodes vides requises par les interfaces
    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(MARGIN, MARGIN);

        drawRoads(g2);
        drawTrafficLights(g2);
        drawVehicles(g2);
    }

    // ---- Routes ----
    private void drawRoads(Graphics2D g2) {
        g2.setColor(ASPHALT);
        // Route verticale (axe nord/sud)
        g2.fillRect(CENTER_X - ROAD_HALF, 0, ROAD_HALF * 2, SIM_SIZE);
        // Route horizontale (axe est/ouest)
        g2.fillRect(0, CENTER_Y - ROAD_HALF, SIM_SIZE, ROAD_HALF * 2);

        // Lignes de separation des deux voies de chaque route (pointillés)
        g2.setColor(LANE_LINE);
        float[] dash = { 10f, 10f };
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
        g2.drawLine(CENTER_X, 0, CENTER_X, CENTER_Y - ROAD_HALF);
        g2.drawLine(CENTER_X, CENTER_Y + ROAD_HALF, CENTER_X, SIM_SIZE);
        g2.drawLine(0, CENTER_Y, CENTER_X - ROAD_HALF, CENTER_Y);
        g2.drawLine(CENTER_X + ROAD_HALF, CENTER_Y, SIM_SIZE, CENTER_Y);
        g2.setStroke(new BasicStroke(1f));

        // Lignes d'arret (stop) à chaque entrée du carrefour
        g2.setColor(Color.WHITE);
        g2.fillRect(CENTER_X - ROAD_HALF, CENTER_Y - ROAD_HALF - 2, ROAD_HALF, 3); // entrée nord
        g2.fillRect(CENTER_X, CENTER_Y + ROAD_HALF, ROAD_HALF, 3); // entrée sud
        g2.fillRect(CENTER_X + ROAD_HALF - 1, CENTER_Y - ROAD_HALF, 3, ROAD_HALF); // entrée est
        g2.fillRect(CENTER_X - ROAD_HALF, CENTER_Y, 3, ROAD_HALF); // entrée ouest
    }

    // ---- Feux de circulation ----
    private void drawTrafficLights(Graphics2D g2) {
        drawLight(g2, CENTER_X - 10, CENTER_Y - ROAD_HALF - LIGHT_OFFSET,
                intersection.getNorthLane().getTrafficLight().getColor());
        drawLight(g2, CENTER_X + 10, CENTER_Y + ROAD_HALF + LIGHT_OFFSET,
                intersection.getSouthLane().getTrafficLight().getColor());
        drawLight(g2, CENTER_X + ROAD_HALF + LIGHT_OFFSET, CENTER_Y - 10,
                intersection.getEastLane().getTrafficLight().getColor());
        drawLight(g2, CENTER_X - ROAD_HALF - LIGHT_OFFSET, CENTER_Y + 10,
                intersection.getWestLane().getTrafficLight().getColor());
    }

    private void drawLight(Graphics2D g2, int centerX, int centerY, LightColor color) {
        int r = 8;
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(centerX - r - 2, centerY - r - 2, (r + 2) * 2, (r + 2) * 2, 6, 6);
        g2.setColor(color == LightColor.GREEN ? new Color(40, 200, 80) : new Color(220, 40, 40));
        g2.fillOval(centerX - r, centerY - r, r * 2, r * 2);
    }

    // ---- Véhicules ----
    private void drawVehicles(Graphics2D g2) {
        drawLane(g2, intersection.getNorthLane());
        drawLane(g2, intersection.getSouthLane());
        drawLane(g2, intersection.getEastLane());
        drawLane(g2, intersection.getWestLane());
    }

    private void drawLane(Graphics2D g2, Lane lane) {
        for (Vehicle vehicle : lane.getVehicles()) {
            drawVehicle(g2, vehicle);
        }
    }

    // Chaque véhicule est peint dans la couleur de son CarType
    private void drawVehicle(Graphics2D g2, Vehicle vehicle) {
        Origin heading = vehicle.getHeading();
        boolean verticalAxis = heading == Origin.North || heading == Origin.South;
        int w = verticalAxis ? 14 : 30;
        int h = verticalAxis ? 30 : 14;

        g2.setColor(colorFor(vehicle));
        g2.fillRect(vehicle.getX(), vehicle.getY(), w, h);
        g2.setColor(Color.BLACK);
        g2.drawRect(vehicle.getX(), vehicle.getY(), w, h);
    }

    private Color colorFor(Vehicle vehicle) {
        switch (vehicle.getColor().name) {
            case "green":
                return new Color(46, 204, 113); // tourne à gauche
            case "blue":
                return new Color(52, 152, 219); // continue tout droit
            case "purple":
                return new Color(155, 89, 182); // tourne à droite
            default:
                return Color.WHITE;
        }
    }

}