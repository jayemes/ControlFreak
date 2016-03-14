package com.jayemes;

import controlP5.Button;
import controlP5.ControlP5;
import controlP5.Knob;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ProcessingApplet extends PApplet {

    private ControlP5 cp5;
    private Knob QDotKnob;
    private Button runButton;
    private HeatedTankODE tank;
    private boolean running = false;
    private Timer runTimer;

    private double time = 0;
    private ArrayList<Double[]> pointList = new ArrayList<>();

    private PGraphics cg;

    public static void main(String[] args) {
        String[] a = {"MAIN"};
        PApplet.runSketch(a, new ProcessingApplet());
    }

    public void settings() {
        size(1030, 500);
    }

    public void setup() {
        tank = new HeatedTankODE(50, 10, 10, 2000, 500, 4.2);

        cp5 = new ControlP5(this);

        QDotKnob = cp5.addKnob("QDotKnob")
                .setRange(0, 5000)
                .setValue((float) tank.getQDot())
                .setPosition(50, 70)
                .setRadius(40)
                .setDragDirection(Knob.VERTICAL)
        ;

        QDotKnob = cp5.addKnob("mDotKnob")
                .setRange(0, 200)
                .setValue((float) tank.getmDot())
                .setPosition(50, 200)
                .setRadius(40)
                .setDragDirection(Knob.VERTICAL)
        ;

        runButton = cp5.addButton("runButton")
                .setPosition(55, 350);

    }

    public void draw() {
        background(100, 100, 230);
        textSize(32);
        fill(0);

        ArrayList<Double[]> clone = (ArrayList<Double[]>) pointList.clone();

        cg = createGraphics(800, 400); // graphics for the chart
        cg.beginDraw();
        cg.stroke(0);
        cg.fill(255, 255, 255);
        cg.rect(0, 0, cg.width - 1, cg.height - 1);
        cg.fill(0);
        cg.noStroke();

        int pointNum = 160; // draw last '#' points

        for (int i = 1; i < pointNum; i++) {
            int index = clone.size() - i;
            if (index > 0) {
                Double[] p = clone.get(index);
                cg.ellipse((160 - i) * 5, cg.height - p[1].floatValue() * 5, 5, 5);
            }
        }

        cg.fill(0);
        if (clone.size() > 0) {
            float textY = clone.get(clone.size() - 1)[1].floatValue() * 5;
            String textCh = String.format("%.3f", tank.getTOut());

            cg.text(textCh, cg.width - 50, cg.height - 10 - textY);
        }

        cg.endDraw();
        image(cg, 200, 70);

    }


    public void QDotKnob(int theValue) {
        tank.setQDot(theValue);
    }

    public void mDotKnob(int theValue) {
        tank.setmDot(theValue);
    }

    public void runButton() {

        if (!running) {
            running = true;
            runTimer = new Timer("tick");

            runTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    time += 1.0;
                    tank.run();

                    pointList.add(new Double[]{time, tank.getTOut()});
                }
            }, 100, 100);
        } else {
            runTimer.cancel();
            running = false;
        }
    }
}
