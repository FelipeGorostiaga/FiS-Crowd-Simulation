package ar.edu.itba.ss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static ar.edu.itba.ss.CommandLineParser.FPS;
import static ar.edu.itba.ss.CommandLineParser.dt;
import static ar.edu.itba.ss.Generator.particles;

public class App {

    public static void main( String[] args ) {

        try {
            File file = new File("output.xyz");
            System.setOut(new PrintStream(new FileOutputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CommandLineParser.parseOptions(args);
        Generator.generateParticles();

        int iterations = 0;
        printParticles(iterations++);

        int dt2 = 0;
        for (double t = 0; particles.size() > 2; t += dt){
            Beeman.updatePosition();
            Beeman.updateSpeed();
            if (++dt2 % FPS == 0) {
                printParticles(iterations++);
            }
        }
    }

    private static void printParticles(int iterations) {
        System.out.println(particles.size());
        System.out.println(iterations);
        for (Particle p: particles) {
            double speed = Math.sqrt(Math.pow(p.vx,2) + Math.pow(p.vy,2));
            System.out.println(p.x + "\t" + p.y + "\t" + p.radius + "\t" + speed);
        }
    }
}
