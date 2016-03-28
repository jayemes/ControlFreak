package com.jayemes;

import controlP5.*;
import controlP5.Button;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ProcessingApplet extends PApplet {

    private ControlP5 cp5;
    private Knob QDotKnob, kValveKnob, FInKnob, tempSPKnob, hSPKnob;
    private Toggle runButton, controlTempButton, controlHButton;
    private Button configButton;
    private HeatedTankODE tank;
    private Timer runTimer;
    private Chart hChart, tChart;
    private Textlabel tChartLabel, hChartLabel;
    private Group configGroup;
    private Textfield tempKpField, levelKpField;

    private boolean running = false;
    private boolean controllingTemp = false;
    private boolean controllingH = false;

    private double time = 0;
    private double tempSetPoint = 20;
    private double hSetPoint = 1;
    private float tempKp = .01f;
    private float levelKp = .5f;

    private ArrayList<Double[]> pointList = new ArrayList<>();

    private PGraphics cg;
    private Textlabel tempKpLabel, levelKpLabel;

    public static void main(String[] args) {
        String[] a = {"MAIN"};
        PApplet.runSketch(a, new ProcessingApplet());
    }

    public void settings() {
        size(1120, 500);
    }

    public void setup() {

        frameRate(30);
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
                .setRange(0.1f, 2f)
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
                .setRange(.5f, 5f)
                .setValue((float) hSetPoint)
                .setPosition(10, 330)
                .setRadius(30)
                .setDragDirection(Knob.VERTICAL)
        ;

        runButton = cp5.addToggle("runButton")
                .setPosition(100, 50)
                .setLabel("Run");

        controlTempButton = cp5.addToggle("controlTempButton")
                .setPosition(100, 100)
                .setLabel("Temperature\nControl");

        controlHButton = cp5.addToggle("controlHButton")
                .setPosition(100, 150)
                .setLabel("Level\nControl");

        configButton = cp5.addButton("configButton")
                .setPosition(100, 300)
                .setWidth(40)
                .setLabel("Config");

        configGroup = cp5.addGroup("configGroup")
                .setSize(120, 90)
                .setPosition(140, 300)
                .setBackgroundColor(0)
                .setMoveable(true)
                .hideBar()
                .hide();

        tempKpField = cp5.addTextfield("tempKpField")
                .setGroup("configGroup")
                .setSize(50, 20)
                .setPosition(10, 10)
                .setText("" + tempKp)
                .setInputFilter(Textfield.FLOAT)
                .setLabel("Temp Kp");

        tempKpLabel = cp5.addTextlabel("tempKpLabel")
                .setGroup("configGroup")
                .setPosition(70, 20)
                .setValue("Kp: " + tempKp);

        levelKpField = cp5.addTextfield("levelKpField")
                .setGroup("configGroup")
                .setSize(50, 20)
                .setPosition(10, 50)
                .setText("" + levelKp)
                .setInputFilter(Textfield.FLOAT)
                .setLabel("Level Kp");

        levelKpLabel = cp5.addTextlabel("levelKpLabel")
                .setGroup("configGroup")
                .setPosition(70, 60)
                .setValue("Kp: " + levelKp);

        hChart = cp5.addChart("hChart")
                .setPosition(180, 25)
                .setSize(700, 200)
                .setRange(0, 5)
                .setView(Chart.LINE)
                .setColor(new CColor(0, color(50), 0, color(255), 0));

        tChart = cp5.addChart("tChart")
                .setPosition(180, 260)
                .setSize(700, 200)
                .setRange(0, 100)
                .setView(Chart.LINE)
                .setColor(new CColor(0, color(50), 0, color(255), 0));

        configGroup.bringToFront();

/////// Series Definition ////////////////////////////////////////////
        hChart.addDataSet("h");
        hChart.setData("h", new float[200]);
        hChart.setColors("h", color(200, 200, 0, 200));

        hChart.addDataSet("valveK");
        hChart.setData("valveK", new float[200]);
        hChart.setColors("valveK", color(200, 0, 0, 200));

        hChart.addDataSet("setPoint");
        hChart.setData("setPoint", new float[200]);
        hChart.setColors("setPoint", color(0, 200, 200, 200));

        tChart.addDataSet("t");
        tChart.setData("t", new float[200]);
        tChart.setColors("t", color(200, 200, 0, 200));

        tChart.addDataSet("Q");
        tChart.setData("Q", new float[200]);
        tChart.setColors("Q", color(200, 0, 0, 200));

        tChart.addDataSet("setPoint");
        tChart.setData("setPoint", new float[200]);
        tChart.setColors("setPoint", color(0, 200, 200, 200));

        tChart.setStrokeWeight(2);
        tChart.getDataSet("setPoint").setStrokeWeight(1);
        hChart.setStrokeWeight(2);
        hChart.getDataSet("setPoint").setStrokeWeight(1);

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

        image(cg, 900, 75, 200, 400);

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

    public void tempKpField(String theValue) {
        tempKp = Float.valueOf(theValue);
        tempKpLabel.setValue("Kp: " + theValue);
    }

    public void levelKpField(String theValue) {
        levelKp = Float.valueOf(theValue);
        levelKpLabel.setValue("Kp: " + theValue);
    }


    /////////////////////////////////////////////////////////////////////////////

    public void runButton() {
        if (!running) {
            running = true;
            runTimer = new Timer("tick");
            runTimer.schedule(new RunTask(), 100, 100);
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

    public void configButton() {
        configGroup.setVisible(!configGroup.isVisible());
    }

    ////////////////////////////////////////////////////////////////////////////

    private PGraphics drawTank(int h, double temp) {
        PGraphics cg;
        int waterColor = color((int) temp * 4, 50, 220);

        cg = createGraphics(400, 700);
        cg.beginDraw();

        cg.fill(255, 200);
        cg.stroke(0);
        cg.strokeWeight(5);
        cg.rect(50, 50, 300, 500, 25, 25, 25, 25);

        cg.noStroke();
        cg.fill(waterColor);
        cg.rect(52, 550 - h, 296, h, 0, 0, 25, 25);


        PShape s = createShape();
        s.setFill(waterColor);

        s.beginShape();
        s.noStroke();

        s.vertex(52 + 296, 550 - h);
        s.vertex(52, 550 - h);

        for (int i = 0; i < 10; i++) {
            s.vertex(52 + 296 / 10 * i, 550 - h - random(5));
        }

        s.vertex(52 + 296, 550 - h - random(5));

        s.endShape();
        cg.shape(s);
        cg.endDraw();
        return cg;

    }

    private class RunTask extends TimerTask {
        @Override
        public void run() {
            time += 1.0;
            tank.run();
            pointList.add(new Double[]{time, tank.getTOut()});

            hChart.push("h", (float) tank.getH());
            hChart.push("valveK", kValveKnob.getValue());
            hChart.push("setPoint", (float) hSetPoint);

            tChart.push("t", (float) tank.getTOut());
            tChart.push("Q", map(QDotKnob.getValue(), 0, 20000, 0, 50));
            tChart.push("setPoint", (float) tempSetPoint);

            /// Controlling...
            if (controllingTemp) {
                double valueToSet = tank.getQDot() * (1 + tempKp * (tempSetPoint - tank.getTOut()));
                tank.setQDot(max((float) valueToSet, 100));
                QDotKnob.setValue((float) valueToSet);
            }

            if (controllingH) {
                double valueToSet = tank.getkValve() * (1 + levelKp * (hSetPoint - tank.getH()));
                tank.setkValve(min(max((float) valueToSet, 0.1f),2f));
                kValveKnob.setValue((float) valueToSet);
            }

        }
    }

}
