package neat;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class InvertedPendulumOnCart extends PApplet implements FitnessFunction {
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
	List<Double> drawList = new ArrayList<Double>();

	static Network drawN = null;

	@Override
	public double getThreshold() {
		return 29999;
	}

	@Override
	public int numInputs() {
		return 4;
	}

	@Override
	public int numOutputs() {
		return 1;
	}

	public void initStateVar(List<Double> inList) {
		M = 0.5; // kg
		m = 0.2; // kg
		l = 0.3; // m
		b = 0.1; // N/m/sec
		I = 0.006; // kgm^2
		g = 9.8; // m/s^2
		u = 0;
		inList = new ArrayList<Double>();
		inList.add(prevPos);
		inList.add(prevVel);
		inList.add(prevAng);
		inList.add(preRotV);
		timeStep = 0.001;
		prevPos = 0d;
		currPos = 0;
		prevVel = 0;
		currVel = 0;
		prevAcc = 0;
		currAcc = 0;
		prevAng = 0.07d;
		currAng = 0;
		preRotV = 0;
		curRotV = 0;
		preRotA = 0;
		curRotA = 0;
	}

	public List<Double> calculateStateVar() {
		double sinTh = Math.sin(prevAng);
		double cosTh = Math.cos(prevAng);
		currVel = prevVel + prevAcc * timeStep;
		currAcc = ((u - b*prevVel + (m*l*preRotV*preRotV*sinTh))*(I+m*l*l) + g*sinTh)/((M+m)*(I+m*l*l)-(m*l*cosTh));

		curRotV = preRotV + preRotA * timeStep;
		curRotA = ((-(m*l*cosTh)*(u-b*prevVel+(m*l*preRotV*preRotV*sinTh)))+(m*g*l*sinTh))/((M+m)*(I+m*l*l)-(m*l*cosTh)*(m*l*cosTh));


		currPos = prevPos + prevVel * timeStep;
		currAng = prevAng + preRotV * timeStep;

		List<Double> inList = new ArrayList<Double>();

		inList.add(currPos);
		inList.add(prevPos);
		inList.add(currAng);
		inList.add(prevAng);
		
		prevPos = currPos;
		prevVel = currVel;
		prevAcc = currAcc;
		prevAng = currAng;
		preRotV = curRotV;
		preRotA = curRotA;	
		
		return inList;

	}

	@Override
	public double calculateFitness(Network net) {


		double fit = 0;
		List<Double> inList = new ArrayList<>();
		initStateVar(inList);

		while (Math.abs(prevPos) < 1.5 && Math.abs(prevAng) < 0.35 && fit < getThreshold()+1) {
			
			u = (net.calculate(calculateStateVar()).get(0) - 0.5) * 10;
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
		fill(204, 102, 0);
		units = width / 4;
		System.out.println("Setup is called");

		u = 0;

		step = 0;

		initStateVar(drawList);
	}

	public void draw() {
		clear();
		background(204);
		text(String.format("TimeStep:\t %d", step), 10, 10);
		text(String.format("Position:\t %.3fm", prevPos), 10, 20);
		text(String.format("Velocity:\t %.3fm/s", prevVel), 10, 30);
		text(String.format("Angle:\t %.3frad", prevAng), 10, 40);
		text(String.format("Anglular V:\t %.3frad/s", preRotV), 10, 50);
		
		
		u = (drawN.calculate(calculateStateVar()).get(0) - 0.5) * 10;
		
		text(String.format("Network Out:\t %.3f", u), 10, 60);
		step++;

		prevAng = -prevAng;
		rect((float) (width / 2 - width / 24 + prevPos * units), height / 2, width / 12, height / 6);
		line((float) (width / 2 + prevPos * units), height / 2,
				(float) (width / 2 + prevPos * units + 0.3 * units * sin((float) prevAng)),
				(float) (height / 2 + 0.3 * units * -cos((float) prevAng)));
		ellipse((float) (width / 2 + prevPos * units + 0.3 * units * sin((float) prevAng)),
				(float) (height / 2 + 0.3 * units * -cos((float) prevAng)), 50, 50);
		prevAng = -prevAng;
	}

}