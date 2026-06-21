import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class Main {

    public static Window createWindow(Intersection intersection) {
        // Création de la fenêtre principale
        JFrame frame = new JFrame("Jraffic");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Window panel = new Window(intersection);
        frame.add(panel);

        // Ajuste la taille de la fenêtre à la taille préférée du panel
        frame.pack();

        // Affiche la fenêtre
        frame.setVisible(true);
        panel.requestFocusInWindow();

        return panel;
    }

    public static void main(String[] args) {

        TrafficLight northLight = new TrafficLight(LightColor.GREEN);

        TrafficLight southLight = new TrafficLight(LightColor.GREEN);

        TrafficLight eastLight = new TrafficLight(LightColor.RED);

        TrafficLight westLight = new TrafficLight(LightColor.RED);

        Lane northLane = new Lane(500, northLight, LaneType.NORTH);

        Lane southLane = new Lane(500, southLight, LaneType.SOUTH);

        Lane eastLane = new Lane(500, eastLight, LaneType.EAST);

        Lane westLane = new Lane(500, westLight, LaneType.WEST);

        Intersection intersection = new Intersection(
                northLane,
                southLane,
                eastLane,
                westLane);
        // simulation spawn d'un vehicule
        Simulation.spawnVehicle(northLane, Direction.AHEAD);
        // création du panel
        Window panel = createWindow(intersection);
        // timer pour faire avancer les vehicules
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                intersection.update();

                Simulation.update(intersection);

                panel.repaint();
            }
        }, 0, 16);

        System.out.println("Simulation créée");

    }

}