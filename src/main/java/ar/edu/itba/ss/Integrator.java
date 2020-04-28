package ar.edu.itba.ss;

import java.util.List;

public interface Integrator {

    void updateSpeed(List<Particle> particles);
    void updatePosition(List<Particle> particles);
}
