package ar.edu.itba.ss;

import java.util.List;

public class Beeman implements Integrator {

    private final static double MINIMUM_RADIUS = 0.25;
    private final static double MAXIMUM_RADIUS = 0.29;
    private final static double MASS = 80;
    private final static double DOOR_LENGTH = 1.2;
    private final static double WALL_Y = 2;
    private final static double CELL_INDEX_RADIUS = 0.6;

    private double dt;

    private final static double ELASTIC_CONSTANT = 1.2 * Math.pow(10, 5);
    private final static double KT = 2.4 * Math.pow(10, 5);
    private final static double SOCIAL_FORCE = 2000; // Newton
    private final static double SOCIAL_DISTANCE = 0.08; // Metres
    private final static double ROOM_LENGTH = 20;
    private final static double DRIVING_TIME = 0.5;
    private final static double desiredSpeed = 0.8;


    public Beeman(double dt) {
        this.dt = dt;
    }

    public void updatePosition(List<Particle> particles) {

        for (Particle p: particles){
           /* if (p.isWall)
                break;*/
            if (p.ax == 0 || p.ay == 0){
                double[] acc =  getAcceleration(p);
                p.ax = acc[0];
                p.ay = acc[1];
            }

            p.x = p.x + p.vx * dt + (2.0 / 3) * p.ax * Math.pow(dt, 2) -
                    (1.0 / 6) * p.prevAx *  Math.pow(dt, 2);
            p.y = p.y + p.vy * dt + (2.0 / 3) * p.ay * Math.pow(dt, 2) -
                    (1.0 / 6) * p.prevAy *  Math.pow(dt, 2);
        }

    }

    public void updateSpeed(List<Particle> particles){

        for (Particle p : particles) {

            /*if (p.isWall)
                break;*/
            double prevVx = p.vx;
            double prevVy = p.vy;

            p.vx = p.vx +  (3.0 / 2) + p.ax * dt - (1.0 / 2) * p.prevAx * dt;
            p.vy = p.vy +  (3.0 / 2) + p.ay * dt - (1.0 / 2) * p.prevAy * dt;

            double[] newAcceleration = getAcceleration(p);

            p.vx = prevVx + (1.0 / 3) * newAcceleration[0] * dt + (5.0 / 6) * p.ax * dt - (1.0 / 6) * p.prevAx * dt;
            p.vy = prevVy + (1.0 / 3) * newAcceleration[1] * dt + (5.0 / 6) * p.ay * dt - (1.0 / 6) * p.prevAy * dt;

            p.prevAx = p.ax;
            p.prevAy = p.ay;
            p.ax = newAcceleration[0];
            p.ay = newAcceleration[1];
        }
    }

    private double[] getAcceleration(Particle p){
        double[] newForce = forces(p);
        newForce[0] = newForce[0] / p.mass;
        newForce[1] = newForce[1] / p.mass;
        return newForce;
    }

    private double[] forces(Particle p) {

        double[] force = new double[2];

        if (contactWithWall(p)) {
            force = wallForce(p);
        }

        force = lateralCollision(p, force, 0);
        force = lateralCollision(p, force, ROOM_LENGTH);

        for (Particle neighbour : p.neighbours) {

            if (!neighbour.equals(p)){

                // Social Force
                double distance = p.getDistanceTo(neighbour);
                double superposition = p.radius + neighbour.radius - distance;
                double dx = neighbour.x - p.x;
                double dy = neighbour.y - p.y;
                double ex = (dx / distance);
                double ey = (dy / distance);
                double socialForce = -SOCIAL_FORCE * Math.exp(superposition/ SOCIAL_DISTANCE);
                force[0] += socialForce * ex;
                force[1] += socialForce * ey;
            }
        }

        // Driving Force
        double[] target = getTarget(p);
        double dxTarget = target[0] - p.x;
        double dyTarget = target[1] - p.y;
        double mod = Math.sqrt(Math.pow(dxTarget, 2) + Math.pow(dyTarget, 2));
        double ex = dxTarget / mod;
        double ey = dyTarget / mod;

        force[0] += p.mass * (desiredSpeed * ex - p.vx) / DRIVING_TIME;
        force[1] += p.mass * (desiredSpeed * ey - p.vy) / DRIVING_TIME;

        return force;
    }

    private static double[] getTarget(Particle p) {
        double[] target;
        double doorX = ROOM_LENGTH/2 - DOOR_LENGTH/2;

        if (p.x < doorX && p.y > WALL_Y) {
            target = new double[]{doorX + p.radius, WALL_Y};
        }
        else if(p.x > doorX + DOOR_LENGTH && p.y > WALL_Y) {
            target = new double[]{doorX + DOOR_LENGTH - p.radius, WALL_Y};
        }
        else {
            target = new double[]{ROOM_LENGTH/2, -1};
        }
        return target;
    }

    private static boolean contactWithWall(Particle p) {
        return p.y > WALL_Y && p.y < (p.radius + WALL_Y) &&
                (p.x < (p.radius + ROOM_LENGTH/2 - DOOR_LENGTH/2) ||
                        p.x > (ROOM_LENGTH/2 + DOOR_LENGTH/2 - p.radius));
    }


    private static double[] wallForce(Particle p) {
        double force[] = new double[2];
        double superposition = p.radius - (Math.abs(p.y - WALL_Y));

        if (superposition > 0) {

            double dx = 0;
            double dy = -Math.abs(p.y - WALL_Y);

            double mod = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            double ex = (dx / mod);
            double ey = (dy / mod);

            double relativeSpeed = p.vx * (-ey) + p.vy * ex;

            double normalForce = -ELASTIC_CONSTANT * superposition;

            force[0] += normalForce * ex;
            force[1] += normalForce * ey;

            double tangentForce = - KT * superposition * relativeSpeed;
            force[0] += tangentForce * (-ey);
            force[1] += tangentForce * (ex);
        }

        if (p.x + p.radius < ROOM_LENGTH/2 - DOOR_LENGTH/2 &&
                p.x - p.radius > ROOM_LENGTH/2 + DOOR_LENGTH/2){
            force[1] += -SOCIAL_FORCE * Math.exp(-(Math.abs(p.y - WALL_Y) - p.radius) / SOCIAL_DISTANCE);
        }
        return force;
    }

    private static double[] lateralCollision(Particle p, double[] force, double wallX){
        double superposition = p.radius - (Math.abs(p.x - wallX));

        double dx = wallX - p.x;
        double dy = 0;

        double mod = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        double ex = (dx / mod);
        double ey = (dy / mod);

        if (superposition > 0) {
            double relativeSpeed = p.vx * (-ey) + p.vy * ex;

            double normalForce = -ELASTIC_CONSTANT * superposition;

            force[0] += normalForce * ex;
            force[1] += normalForce * ey;

            double tangentForce = - KT * superposition * relativeSpeed;
            force[0] += tangentForce * (-ey);
            force[1] += tangentForce * (ex);
        }

        force[0] += -SOCIAL_FORCE * Math.exp(-(Math.abs(p.x - wallX) - p.radius) / SOCIAL_DISTANCE) * ex;
        return force;
    }


}
