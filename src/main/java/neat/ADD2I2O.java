package neat;

import java.util.ArrayList;
import java.util.List;

public class ADD2I2O implements FitnessFunction {

@Override
public int numInputs() {
    return 2;
}

@Override
public int numOutputs() {
    return 2;
}

@Override
public double calculateFitness(Network net) {
    List<Double> inList = new ArrayList<Double>();
    inList.add(1d);
    inList.add(1d);
    double d10 = net.calculate(inList).get(0);
    double d11 = 1d - net.calculate(inList).get(1);
    inList.set(0, 0d);
    inList.set(1, 0d);
    double d20 = net.calculate(inList).get(0);
    double d21 = net.calculate(inList).get(1);
    inList.set(0, 0d);
    inList.set(1, 1d);
    double d40 = 1d - net.calculate(inList).get(0);
    double d41 = net.calculate(inList).get(1);
    inList.set(0, 1d);
    inList.set(1, 0d);
    double d60 = 1d - net.calculate(inList).get(0);
    double d61 = net.calculate(inList).get(1);

    double d = d10 + d11 + d20 + d21 + d40 + d41 + d60 + d61;
    return (8d - d) * (8d - d);
}

public double getThreshold() {
    return 63d;
}
}
