package com.jayemes;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

public class HeatedTankODE implements FirstOrderDifferentialEquations {

    private double mDot, TIn, QDot, mTank, cp, TOut;

    public HeatedTankODE(double mDot, double TIn, double TOut, double QDot, double mTank, double cp) {
        this.mDot = mDot;
        this.TIn = TIn;
        this.TOut = TOut;
        this.QDot = QDot;
        this.mTank = mTank;
        this.cp = cp;
    }

    @Override
    public int getDimension() {
        return 1;
    }

    @Override
    public void computeDerivatives(double t, double[] TOut, double[] TOutDot) throws MaxCountExceededException, DimensionMismatchException {
        TOutDot[0] = mDot / mTank * (TIn - TOut[0]) + QDot / mTank / cp;
    }

    public void run() {
        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(1.0); // Set time step

        FirstOrderDifferentialEquations ODE = this;

        double[] TOut0 = new double[]{TOut}; // initial state
        double[] TOut1 = new double[]{0d}; // array to save the result

        integrator.integrate(ODE, 0.0, TOut0, 1.0, TOut1); // now TOut1 contains final state at time t=1.0

        TOut = TOut1[0];

    }

    public void setmDot(double mDot) {
        this.mDot = mDot;
    }

    public void setQDot(double QDot) {
        this.QDot = QDot;
    }

    public double getmDot() {
        return mDot;
    }

    public double getQDot() {
        return QDot;
    }

    public double getTOut() {
        return TOut;
    }
}
