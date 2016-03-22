package com.jayemes;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ProcessingApplet extends PApplet {

    private ControlP5 cp5;
    private Knob QDotKnob, kValveKnob, FInKnob, tempSPKnob, hSPKnob;
    private Toggle runButton, controlTempButton, controlHButton;
    private HeatedTankODE tank;
    private Timer runTimer;
    private Chart hChart, tChart;
    private Textlabel tChartLabel, hChartLabel;

    private boolean running = false;
    private boolean controllingTemp = false;
    private boolean controllingH = false;

    private double time = 0;
    private double tempSetPoint = 20;
    private double hSetPoint = 1;

    private ArrayList<Double[]> pointList = new ArrayList<>();

    private PGraphics cg;

    public static void main(String[] args) {
        String[] a = {"MAIN"};
        PApplet.runSketch(a, new ProcessingApplet());
    }

    public void settings() {
        size(1230, 500);
    }

    public void setup() {
        tank = new HeatedTankODE(0.07d, 10, 10, 2000, 1, 0.1d, 0.015d);

        cp5 = new ControlP5(this);

        QDotKnob = cp5.addKnob("QDotKnob")
                .setRange(0, 20000)
                .setValue((float) tank.getQDot())
                .setPosition(10, 10)
                .setRadius(30)
                .setDragDirection(Knob.VERTICAL)
        ;

        kValveKnob = cp5.addKnob("kValveKnob")
                .setRange(0.1f, 1f)
                .setValue((float) tank.getkValve())
                .setPosition(10, 90)
                .setRadius(30)
                .setDragDirection(Knob.VERTICAL)
        ;

        FInKnob = cp5.addKnob("FInKnob")
                .setRange(25, 100) // l/s
                .setValue((float) tank.getFIn() * 1000)
                .setPosition(10, 170)
                .setRadius(30)
                .setDragDirection(Knob.VERTICAL)
        ;

        tempSPKnob = cp5.addKnob("tempSPKnob")
                .setRange(20, 50)
                .setValue((float) tempSetPoint)
                .setPosition(10, 250)
                .setRadius(30)
                .setDragDirection(Knob.VERTICAL)
        ;

        hSPKnob = cp5.addKnob("hSPKnob")
                .setRange(.5f, 2.5f)
                .setValue((float) hSetPoint)
                .setPosition(10, 330)
                .setRadius(30)
                .setDragDirection(Knob.VERTICAL)
        ;

        runButton = cp5.addToggle("runButton")
                .setPosition(100, 50);

        controlTempButton = cp5.addToggle("controlTempButton")
                .setPosition(100, 100);

        controlHButton = cp5.addToggle("controlHButton")
                .setPosition(100, 150);

        hChart = cp5.addChart("hChart")
                .setPosition(180, 25)
                .setSize(700, 200)
                .setRange(0, 5)
                .setView(Chart.LINE)
                .setStrokeWeight(1.5f)
                .setColorCaptionLabel(color(40));

        tChart = cp5.addChart("tChart")
                .setPosition(180, 260)
                .setSize(700, 200)
                .setRange(0, 100)
                .setView(Chart.LINE)
                .setStrokeWeight(1.5f)
                .setColorCaptionLabel(color(40));

        hChart.addDataSet("h");
        hChart.setData("h", new float[200]);

        tChart.addDataSet("t");
        tChart.setData("t", new float[200]);

        tChartLabel = cp5.addTextlabel("%.2f", "",
                (int) tChart.getPosition()[0] + tChart.getWidth() - 40,
                (int) tChart.getPosition()[1] + 20);

        hChartLabel = cp5.addTextlabel("%.3f", "",
                (int) hChart.getPosition()[0] + hChart.getWidth() - 40,
                (int) hChart.getPosition()[1] + 20);

    }

    public void draw() {
        background(80, 150, 80);
        textSize(12);
        fill(255);

        tChartLabel.setValue(String.format("%.2f", tank.getTOut()));
        hChartLabel.setValue(String.format("%.3f", tank.getH()));

        int level = (int) map((float) tank.getH(), 0, 5, 50, 550);
        cg = drawTank(level, tank.getTOut());

        image(cg, 900, 25, 200, 400);
//        ArrayList<Double[]> clone = (ArrayList<Double[]>) pointList.clone();
//
//        cg = createGraphics(800, 400); // graphics for the chart
//        cg.beginDraw();
//        cg.stroke(0);
//        cg.fill(255, 255, 255);
//        cg.rect(0, 0, cg.width - 1, cg.height - 1);
//        cg.fill(0);
//        cg.noStroke();
//
//        int pointNum = 160; // draw last '#' points
//
//        for (int i = 1; i < pointNum; i++) {
//            int index = clone.size() - i;
//            if (index > 0) {
//                Double[] p = clone.get(index);
//                cg.ellipse((160 - i) * 5, cg.height - p[1].floatValue() * 5, 5, 5);
//            }
//        }
//
//        cg.fill(0);
//        if (clone.size() > 0) {
//            float textY = clone.get(clone.size() - 1)[1].floatValue() * 5;
//            String textCh = String.format("%.3f", tank.getTOut());
//
//            cg.text(textCh, cg.width - 50, cg.height - 10 - textY);
//        }
//
//        cg.endDraw();
//        image(cg, 200, 70);

    }

    /////////////// Controllers Callback Functions //////////////////////////////

    public void QDotKnob(int theValue) {
        tank.setQDot(theValue);
    }

    public void kValveKnob(float theValue) {
        tank.setkValve(theValue);
    }

    public void FInKnob(float theValue) {
        tank.setFIn(theValue / 1000);
    }

    public void tempSPKnob(int theValue) {
        tempSetPoint = theValue;
    }

    public void hSPKnob(float theValue) {
        hSetPoint = theValue;
    }


    /////////////////////////////////////////////////////////////////////////////

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

                    hChart.push("h", (float) tank.getH());
                    tChart.push("t", (float) tank.getTOut());

                    /// Controlling...
                    if (controllingTemp) {
                        double valueToSet = tank.getQDot() * (1 + .01 * (tempSetPoint - tank.getTOut()));
                        tank.setQDot(valueToSet);
                        QDotKnob.setValue((float) valueToSet);
                    }

                    if (controllingH) {
                        double valueToSet = tank.getkValve() * (1 + .5 * (hSetPoint - tank.getH()));
                        tank.setkValve(valueToSet);
                        kValveKnob.setValue((float) valueToSet);
                    }

                }
            }, 100, 100);
        } else {
            runTimer.cancel();
            running = false;
        }
    }

    public void controlTempButton() {
        controllingTemp = !controllingTemp;
    }

    public void controlHButton() {
        controllingH = !controllingH;
    }

    ////////////////////////////////////////////////////////////////////////////

    public PGraphics drawTank(int h, double temp) {
        PGraphics cg;

        cg = createGraphics(310, 610);
        cg.beginDraw();

        cg.fill(255, 200);
        cg.stroke(0);
        cg.strokeWeight(2);
        cg.rect(0, 0, 300, 600);

        cg.strokeWeight(0);
        cg.fill((int) temp * 4, 50, 220);
        cg.rect(1, 600 - h, 298, h);

        cg.endDraw();
        return cg;

    }
}
