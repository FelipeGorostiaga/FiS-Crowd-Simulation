package ar.edu.itba.ss;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ar.edu.itba.ss.CommandLineParser.N;

public class Generator {

    static List<Particle> particles = new ArrayList<>();
    private final static double MINIMUM_RADIUS = 0.2;
    private final static double MAXIMUM_RADIUS = 0.25;
    private final static double MASS = 80;
    private final static double WALL_Y = 2;
    private final static double ROOM_LENGTH = 20;

    static void generateParticles() {

        Random r = new Random();
        for(int i = 0 ; i < N ; i++) {
            double radius = r.nextDouble() * (MAXIMUM_RADIUS - MINIMUM_RADIUS) + MINIMUM_RADIUS;
            double x, y;
            do {
                x = radius + (ROOM_LENGTH - 2 * radius) * Math.random();
                y = WALL_Y + radius+  (ROOM_LENGTH - 2 * radius) * Math.random();
            } while (!isValidPosition(x, y, radius));
            particles.add(new Particle(i + 1, x, y, MASS, radius));
        }
    }

    private static boolean isValidPosition(double x, double y, double radius) {
        for (Particle p: particles){
            if(!((Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)) > Math.pow(p.radius + radius, 2))) {
                return false;
            }
        }
        return true;
    }
}
