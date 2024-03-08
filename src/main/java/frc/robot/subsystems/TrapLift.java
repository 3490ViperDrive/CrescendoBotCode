package frc.robot.subsystems;

import frc.robot.Constants.LiftPivotSetpoint;

import static frc.robot.Constants.LiftConstants.*;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.io.LiftIO;
import frc.robot.io.lift.*;
import frc.robot.utils.Visualizer;

public class TrapLift extends SubsystemBase {
    LiftIO lift;
    // PivotIO pivot;
    LiftPivotSetpoint lastSetpoint = LiftPivotSetpoint.kStowed;

    public TrapLift() {
        if (Robot.isReal()) {
            lift = new LiftFalcon();
            // pivot = new PivotFalcon();
        } else {
            lift = new LiftSim();
            // pivot = new PivotSim();
        }
    }

    @Override
    public void periodic() {
        // Visualizer.getInstance().setPivotAngle(pivot.getAngle().getDegrees());
        Visualizer.getInstance().setLiftDistance(lift.getDistance());
    }

    public Command requestPosition(double distance) {
        return run(() -> {
            lift.setDistance(distance);
        });
    }

    public Command runOpenLoop(DoubleSupplier liftSup) {
        return run(() -> {
            lift.moveOpenLoop(-liftSup.getAsDouble() * kFwdVoltageLimit);
            // pivot.moveOpenLoop(-pivotSup.getAsDouble() * 12);
        }).andThen(() -> {
            lift.moveOpenLoop(0);
            // pivot.moveOpenLoop(0);
        });
    }

    public Command idle() {
        return run(() -> lift.idle());
    }
}
