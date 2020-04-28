package ar.edu.itba.ss;

import java.util.Set;

public class Particle implements Comparable<Particle> {

    int id;
    double x;
    double y;
    double vx;
    double vy;
    double mass;
    double ax;
    double ay;
    double prevAx;
    double prevAy;
    double radius;
    Set<Particle> neighbours;

    public Particle(int id, double x, double y, double mass, double radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.mass = mass;
        this.radius = radius;
    }

    double getDistanceTo(Particle neighbour) {
        return Math.sqrt(Math.pow(this.x - neighbour.x, 2) + Math.pow(this.y - neighbour.y, 2));
    }

    @Override
    public int compareTo(Particle particle) {
        return id - particle.id;
    }
}
