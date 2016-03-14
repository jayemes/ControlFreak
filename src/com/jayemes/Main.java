package com.jayemes;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

public class Main {

    public static void main(String[] args) {

        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(1);

        FirstOrderDifferentialEquations ODE = new HeatedTankODE(50, 20, 20, 2000, 1000, 4.2);

        StepHandler stepHandler = new StepHandler() {
            public void init(double t0, double[] TOut0, double t) {
            }

            public void handleStep(StepInterpolator interpolator, boolean isLast) {
                double t = interpolator.getCurrentTime();
                double[] TOut = interpolator.getInterpolatedState();
                System.out.println(t + ", " + TOut[0]);
            }
        };
        integrator.addStepHandler(stepHandler);


        double[] TOut = new double[]{20}; // initial state

        integrator.integrate(ODE, 0.0, TOut, 60.0, TOut); // now y contains final state at time t=16.0

    }
}
