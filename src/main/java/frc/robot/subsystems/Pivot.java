package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.geometry.Rotation2d;
//import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj2.command.Command;
//import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
//import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Robot;
//import frc.robot.io.LiftIO;
import frc.robot.io.PivotIO;
//import frc.robot.io.lift.LiftFalcon;
//import frc.robot.io.lift.LiftSim;
import frc.robot.io.pivot.PivotFalcon;
import frc.robot.io.pivot.PivotSim;
import frc.robot.utils.Visualizer;
import monologue.Logged;
import monologue.Annotations.Log;
import frc.robot.Constants.LiftPivotSetpoint;
//import frc.robot.Constants.LiftConstants;
import frc.robot.Constants.PivotConstants;

public class Pivot extends SubsystemBase implements Logged {
    // LiftIO lift;
    PivotIO pivot;
    LiftPivotSetpoint lastSetpoint = LiftPivotSetpoint.kStowed;

    public Pivot() {
        if (Robot.isReal()) {
            // lift = new LiftFalcon();
            pivot = new PivotFalcon();
        } else {
            // lift = new LiftSim();
            pivot = new PivotSim();
        }
    }

    @Override
    public void periodic() {
        Visualizer.getInstance().setPivotAngle(pivot.getAngle().getDegrees());
       // Visualizer.getInstance().setLiftDistance(lift.getDistance());
    }

    // public Command setPosition(LiftPivotSetpoint setpoint) {
    //     return run(() -> {
    //         lift.setDistance(setpoint.liftDistance);
    //         pivot.setAngle(Rotation2d.fromDegrees(setpoint.pivotAngle));
    //         lastSetpoint = setpoint;
    //     }).withName("Set lift/pivot position to " + setpoint).alongWith(new PrintCommand("setPosition called with setpoint" + setpoint));
    // }

    public Command requestPosition(double angleDeg) {
        return run(() -> {
            pivot.setAngle(Rotation2d.fromDegrees(angleDeg));
        }).beforeStarting(() -> pivot.zeroFalconToAbsEncoder(), this);
    }

    public Command runOpenLoop(DoubleSupplier pivotSup) {
        return run(() -> {
            // lift.moveOpenLoop(-liftSup.getAsDouble() * 12);
            pivot.moveOpenLoop(-pivotSup.getAsDouble() * 12);
        }).andThen(() -> {
            // lift.moveOpenLoop(0);
            pivot.moveOpenLoop(0);
        });
    }

    // public Command setPositionAndWait(LiftPivotSetpoint setpoint) {
    //     return this.setPosition(setpoint) //todo fix this
    //         .andThen(new WaitUntilCommand(this::atSetpoint))
    //         .withName("Waiting until lift/pivot reaches " + setpoint + " position");
    // }

    @Log
    public boolean atSetpoint() {
        return /*(lift.getDistance() <= lastSetpoint.liftDistance + LiftConstants.kSetpointTolerance)
        && (lift.getDistance() >= lastSetpoint.liftDistance - LiftConstants.kSetpointTolerance)
        &&*/ (pivot.getAngle().getDegrees() <= lastSetpoint.pivotAngle + PivotConstants.kSetpointTolerance)
        && (pivot.getAngle().getDegrees() >= lastSetpoint.pivotAngle - PivotConstants.kSetpointTolerance);
    }
}
