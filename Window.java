import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.JPanel;
import java.awt.Dimension;

public class Window extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    // Dimensions de la fenêtre
    private static final int Width = 900;
    private static final int Height = 700;
    private Intersection intersection;

        public Window(Intersection intersection) {
            this.intersection = intersection;
        setPreferredSize(new Dimension(Width, Height));
        setBackground(Color.DARK_GRAY);

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
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

}
