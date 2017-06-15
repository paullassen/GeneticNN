package neat;

import processing.core.PApplet;

public class InvertedPendulumOnCart extends PApplet implements FitnessFunction {

int step = 0;
int units;

static Network drawN = null;
IPoCSimulation drawS = null;

@Override
public double getThreshold() {
    return 50000;
}

@Override
public int numInputs() {
    return 6;
}

@Override
public int numOutputs() {
    return 1;
}

@Override
public double calculateFitness(Network net) {
    IPoCSimulation sim = new IPoCSimulation();

    double fit = 0;
    sim.initStateVar();

    while (Math.abs(sim.prevPos) < 1.5 && Math.abs(sim.prevAng) < 0.35 && fit < getThreshold() + 1) {

        sim.u = (net.calculate(sim.calculateStateVar()).get(0) - 0.5) * 8;
        fit++;
    }
    return fit;
}

public void drawNet(Network net) {
    drawN = net;
    System.out.println("Draw Network " + (net != null));
    PApplet.main("neat.InvertedPendulumOnCart");
}

public void settings() {
    size(2400, 600);
}

public void setup() {
    drawS = new IPoCSimulation();

    fill(204, 102, 0);
    units = width / 4;
    System.out.println("Setup is called");

    step = 0;

    drawS.initStateVar();
}

public void draw() {
    clear();
    background(204);
    text(String.format("TimeStep:\t %d", step), 10, 10);
    text(String.format("Position:\t %.3fm", drawS.prevPos), 10, 20);
    text(String.format("Velocity:\t %.3fm/s", drawS.prevVel), 10, 30);
    text(String.format("Angle:\t %.3frad", drawS.prevAng), 10, 40);
    text(String.format("Anglular V:\t %.3frad/s", drawS.preRotV), 10, 50);

    drawS.u = (drawN.calculate(drawS.calculateStateVar()).get(0) - 0.5) * 8;

    text(String.format("Network Out:\t %.3f", drawS.u), 10, 60);
    step++;

    drawS.prevAng = -drawS.prevAng;
    rect((float) (width / 2 - width / 24 + drawS.prevPos * units), height / 2, width / 12, height / 6);
    line((float) (width / 2 + drawS.prevPos * units), height / 2,
            (float) (width / 2 + drawS.prevPos * units + 0.3 * units * sin((float) drawS.prevAng)),
            (float) (height / 2 + 0.3 * units * -cos((float) drawS.prevAng)));
    ellipse((float) (width / 2 + drawS.prevPos * units + 0.3 * units * sin((float) drawS.prevAng)),
            (float) (height / 2 + 0.3 * units * -cos((float) drawS.prevAng)), 50, 50);
    drawS.prevAng = -drawS.prevAng;
}

}