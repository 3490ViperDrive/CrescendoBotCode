package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.io.PivotIO;
import frc.robot.io.pivot.PivotFalcon;
import frc.robot.io.pivot.PivotSim;
import frc.robot.utils.Visualizer;
import monologue.Logged;
import monologue.Annotations.Log;
import frc.robot.Constants.LiftPivotSetpoint;

import frc.robot.Constants.PivotConstants;

public class Pivot extends SubsystemBase implements Logged {

    PivotIO pivot;
    LiftPivotSetpoint lastSetpoint = LiftPivotSetpoint.kStowed;

    public Pivot() {
        if (Robot.isReal()) {
            pivot = new PivotFalcon();
        } else {
            pivot = new PivotSim();
        }
    }

    @Override
    public void periodic() {
        Visualizer.getInstance().setPivotAngle(pivot.getAngle().getDegrees());
    }


    public Command requestPosition(double angleDeg) {
        return run(() -> {
            pivot.setAngle(Rotation2d.fromDegrees(angleDeg));
        }).beforeStarting(() -> pivot.zeroFalconToAbsEncoder(), this);
    }

    public Command runOpenLoop(DoubleSupplier pivotSup) {
        return run(() -> {
            pivot.moveOpenLoop(-pivotSup.getAsDouble() * 12);
        }).andThen(() -> {
            pivot.moveOpenLoop(0);
        });
    }

    @Log
    public boolean atSetpoint() {
        return (pivot.getAngle().getDegrees() <= lastSetpoint.pivotAngle + PivotConstants.kSetpointTolerance)
        && (pivot.getAngle().getDegrees() >= lastSetpoint.pivotAngle - PivotConstants.kSetpointTolerance);
    }
}
