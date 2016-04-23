package com.jayemes;

import controlP5.*;
import processing.core.PApplet;

public class ConfigApplet extends PApplet {
    private ProcessingApplet parent;

    boolean isShowing = true;
    ControlP5 cp5;
    private Textfield tempKpField, tempKiField, tempTauField, tempKdField;
    private Textlabel tempKpLabel, tempKiLabel, tempTauLabel, tempKdLabel;
    private Button saveButton, loadButton;


    public ConfigApplet(ProcessingApplet parent) {
        this.parent = parent;
    }

    public void settings() {
        size(400, 400);
    }

    public void setup() {
        frameRate(30);
        surface.setAlwaysOnTop(true);

        cp5 = new ControlP5(this);

        saveButton = cp5.addButton("saveButton")
                .setPosition(50, 300);

        loadButton = cp5.addButton("loadButton")
                .setPosition(150, 300);

        tempKpField = cp5.addTextfield("tempKpField")
                .setSize(40, 20)
                .setPosition(10, 10)
                .setText("" + parent.tempKp)
                .setInputFilter(Textfield.FLOAT)
                .setLabel("Temp Kp")
                .setAutoClear(false);

        tempKpLabel = cp5.addTextlabel("tempKpLabel")
                .setPosition(55, 15)
                .setValue("[" + parent.tempKp + "]");

        tempKiField = cp5.addTextfield("tempKiField")
                .setSize(40, 20)
                .setPosition(100, 10)
                .setText("" + parent.tempKi)
                .setInputFilter(Textfield.FLOAT)
                .setLabel("Temp Ki")
                .setAutoClear(false);

        tempKiLabel = cp5.addTextlabel("tempKiLabel")
                .setPosition(145, 15)
                .setValue("[" + parent.tempKi + "]");

        tempTauField = cp5.addTextfield("tempTauField")
                .setSize(40, 20)
                .setPosition(190, 10)
                .setText("" + parent.tempTau)
                .setInputFilter(Textfield.FLOAT)
                .setLabel("Temp Tau")
                .setAutoClear(false);

        tempTauLabel = cp5.addTextlabel("tempTauLabel")
                .setPosition(235, 15)
                .setValue("[" + parent.tempTau + "]");

        tempKdField = cp5.addTextfield("tempKdField")
                .setSize(40, 20)
                .setPosition(280, 10)
                .setText("" + parent.tempKd)
                .setInputFilter(Textfield.FLOAT)
                .setLabel("Temp Kd")
                .setAutoClear(false);

        tempKdLabel = cp5.addTextlabel("tempKdLabel")
                .setPosition(325, 15)
                .setValue("[" + parent.tempKd + "]");


//        levelKpField = cp5.addTextfield("levelKpField")
//                .setGroup("configGroup")
//                .setSize(50, 20)
//                .setPosition(10, 50)
//                .setText("" + levelKp)
//                .setInputFilter(Textfield.FLOAT)
//                .setLabel("Level Kp");
//
//        levelKpLabel = cp5.addTextlabel("levelKpLabel")
//                .setGroup("configGroup")
//                .setPosition(70, 60)
//                .setValue("Kp: " + levelKp);

    }

    ///// LISTENER FUNCTIONS /////

    public void tempKpField(String theValue) {
        parent.tempKp = Float.valueOf(theValue);
        tempKpLabel.setValue("[" + parent.tempKp + "]");
    }

    public void tempKiField(String theValue) {
        parent.tempKi = Float.valueOf(theValue);
        tempKiLabel.setValue("[" + parent.tempKi + "]");
    }

    public void tempTauField(String theValue) {
        parent.tempTau = Float.valueOf(theValue).intValue();
        tempTauLabel.setValue("[" + parent.tempTau + "]");
    }

    public void tempKdField(String theValue) {
        parent.tempKd = Float.valueOf(theValue);
        tempKdLabel.setValue("[" + parent.tempKd + "]");
    }

//    public void levelKpField(String theValue) {
//        levelKp = Float.valueOf(theValue);
//        levelKpLabel.setValue("Kp: " + theValue);
//    }

    public void saveButton() {
        cp5.saveProperties("default", "default");
    }
    public void loadButton() {
        cp5.loadProperties("default");
    }

    public void draw() {
        background(200, 100, 100);
    }

    public void hide() {
        noLoop();
        getSurface().setVisible(false);
        isShowing = false;
    }

    public void show() {
        loop();
        getSurface().setVisible(true);
        isShowing = true;
    }

    @Override
    public void exit() {
        hide();
        ///// DO NOTHING /////
    }
}
