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
	double currAcc;
	double prevAng;
	double currAng;
	double preRotV;
	double curRotV;
	double curRotA;
	int step = 0;
	int units;
	List<Double> drawList = new ArrayList<Double>();

	static Network drawN = null;

	@Override
	public double getThreshold() {
		// TODO Auto-generated method stub
		return 9999;
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
		inList = new ArrayList<Double>();
		inList.add(prevPos);
		inList.add(prevVel);
		inList.add(prevAng);
		inList.add(preRotV);
		timeStep = 0.001;
		prevPos = 0d;
		currPos = prevPos;
		prevVel = 0;
		currVel = prevVel;
		currAcc = 0;
		prevAng = 0.001d;
		currAng = prevAng;
		preRotV = 0;
		curRotV = preRotV;
		curRotA = 0;
	}

	public List<Double> calculateStateVar() {
		currVel = prevVel;
		currAcc = -((I + m * l * l) * b) / (I * (M + m) + M * m * l * l) * prevVel
				+ (m * m * l * l * g) / (I * (M + m) + M * m * l * l) * prevAng
				+ (m * l) / (I * (M + m) + M * m * l * l) * u;
		curRotV = preRotV;
		curRotA = -(m * l * b) / (I * (M + m) + M * m * l * l) * prevVel
				+ (m * l * g) * (M + m) / (I * (M + m) + M * m * l * l) * prevAng
				+ (I + m * l * l) / (I * (M + m) + M * m * l * l) * u;
		currPos = prevPos;
		currAng = prevAng;

		prevVel = currVel + currAcc * timeStep;
		prevPos = currPos + currVel * timeStep;
		preRotV = curRotV + curRotA * timeStep;
		prevAng = currAng + curRotV * timeStep;

		List<Double> inList = new ArrayList<Double>();

		inList.add(prevPos);
		inList.add(prevVel);
		inList.add(prevAng);
		inList.add(preRotV);
		return inList;

	}

	@Override
	public double calculateFitness(Network net) {
		// TODO Auto-generated method stub
		M = 0.5; // kg
		m = 0.2; // kg
		l = 0.3; // m
		b = 0.1; // N/m/sec
		I = 0.006; // kgm^2
		g = 9.8; // m/s^2
		u = 0;

		double fit = 0;
		List<Double> inList = new ArrayList<>();
		initStateVar(inList);

		while (Math.abs(prevPos) < 1.5 && Math.abs(prevAng) < 0.35 && fit < 10000) {
			
			u = (net.calculate(calculateStateVar()).get(0) - 0.5) * 2;
			fit++;
		}
		return fit;
	}

	public void drawNet(Network net) {
		drawN = net;
		System.out.println("Draw Network " + (net == null));
		PApplet.main("neat.InvertedPendulumOnCart");
	}

	public void settings() {
		size(2400, 600);
	}

	public void setup() {
		fill(204, 102, 0);
		units = width / 12;
		System.out.println("Setup is called");

		M = 0.5; // kg
		m = 0.2; // kg
		l = 0.3; // m
		b = 0.1; // N/m/sec
		I = 0.006; // kgm^2
		g = 9.8; // m/s^2
		u = 0;

		step = 0;

		initStateVar(drawList);
	}

	public void draw() {
		clear();
		background(204);
		text(String.format("%d", step), 10, 10);
		text(String.format("%.3f", prevPos), 10, 20);
		text(String.format("%.3f", prevAng), 10, 30);


		u = (drawN.calculate(calculateStateVar()).get(0) - 0.5) * 2;

		step++;

		rect((float) (width / 2 - width / 24 + prevPos * units), height / 2, width / 12, height / 6);
		line((float) (width / 2 + prevPos * units), height / 2,
				(float) (width / 2 + prevPos * units + 0.3 * units * sin((float) prevAng)),
				(float) (height / 2 + 0.3 * units * -cos((float) prevAng)));
		ellipse((float) (width / 2 + prevPos * units + 0.3 * units * sin((float) prevAng)),
				(float) (height / 2 + 0.3 * units * -cos((float) prevAng)), 50, 50);
	}

}
