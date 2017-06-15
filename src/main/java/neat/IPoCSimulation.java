package neat;

import java.util.ArrayList;
import java.util.List;

public class IPoCSimulation {

double M, m, l, b, I, g, u;
double timeStep;
double prevPos;
double currPos;
double prevVel;
double currVel;
double prevAcc;
double currAcc;
double prevAng;
double currAng;
double preRotV;
double curRotV;
double preRotA;
double curRotA;
int step = 0;
int units;

public void initStateVar() {
    M = 0.5; // kg
    m = 0.2; // kg
    l = 0.6; // m
    b = 0.1; // N/m/sec
    I = 0.006; // kgm^2
    g = 9.8; // m/s^2
    u = 0;

    timeStep = 0.001;
    prevPos = 0d;
    currPos = 0;
    prevVel = 0;
    currVel = 0;
    prevAcc = 0;
    currAcc = 0;
    prevAng = 0;
    currAng = 0.1;
    preRotV = 0;
    curRotV = 0;
    preRotA = 0;
    curRotA = 0;
}

public List<Double> calculateStateVar() {
    double sinTh = Math.sin(currAng);
    double cosTh = Math.cos(currAng);
    List<Double> inList = new ArrayList<Double>();

    inList.add(prevPos);
    inList.add(prevAng);
    inList.add(currPos);
    inList.add(currAng);

    prevPos = currPos;
    prevVel = currVel;
    prevAcc = currAcc;
    prevAng = currAng;
    preRotV = curRotV;
    preRotA = curRotA;
    currVel = prevVel + prevAcc * timeStep;
    currAcc = ((u - b * prevVel + (m * l * preRotV * preRotV * sinTh)) * (I + m * l * l) + g * sinTh)
            / ((M + m) * (I + m * l * l) - (m * l * cosTh));

    curRotV = preRotV + preRotA * timeStep;
    curRotA = ((-(m * l * cosTh) * (u - b * prevVel + (m * l * preRotV * preRotV * sinTh))) + (m * g * l * sinTh))
            / ((M + m) * (I + m * l * l) - (m * l * cosTh) * (m * l * cosTh));

    currPos = prevPos + prevVel * timeStep;
    currAng = prevAng + preRotV * timeStep;

    inList.add(currPos);
    inList.add(currAng);

    return inList;

}
}
