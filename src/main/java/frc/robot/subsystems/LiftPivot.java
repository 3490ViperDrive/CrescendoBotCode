package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Robot;
import frc.robot.io.LiftIO;
import frc.robot.io.PivotIO;
import frc.robot.io.lift.LiftSim;
import frc.robot.io.pivot.PivotSim;
import frc.robot.utils.Visualizer;
import monologue.Logged;
import monologue.Annotations.Log;
import frc.robot.Constants.LiftPivotSetpoint;
import frc.robot.Constants.LiftConstants;
import frc.robot.Constants.PivotConstants;

public class LiftPivot extends SubsystemBase implements Logged {
    LiftIO lift;
    PivotIO pivot;
    LiftPivotSetpoint lastSetpoint = LiftPivotSetpoint.kStowed;

    public LiftPivot() {
        if (Robot.isReal()) {
            //TODO add real IO
        } else {
            lift = new LiftSim();
            pivot = new PivotSim();
        }
    }

    @Override
    public void periodic() {
        Visualizer.getInstance().setPivotAngle(pivot.getAngle().getDegrees());
        Visualizer.getInstance().setLiftDistance(lift.getDistance());
    }

    public Command setPosition(LiftPivotSetpoint setpoint) {
        return runOnce(() -> {
            lift.setDistance(setpoint.liftDistance);
            pivot.setAngle(Rotation2d.fromDegrees(setpoint.pivotAngle));
            lastSetpoint = setpoint;
        }).withName("Set lift/pivot position to " + setpoint);
    }

    public Command setPositionAndWait(LiftPivotSetpoint setpoint) {
        return this.setPosition(setpoint)
            .andThen(new WaitUntilCommand(this::atSetpoint))
            .withName("Waiting until lift/pivot reaches " + setpoint + " position");
    }

    @Log
    public boolean atSetpoint() {
        return (lift.getDistance() <= lastSetpoint.liftDistance + LiftConstants.kSetpointTolerance)
        && (lift.getDistance() >= lastSetpoint.liftDistance - LiftConstants.kSetpointTolerance)
        && (pivot.getAngle().getDegrees() <= lastSetpoint.pivotAngle + PivotConstants.kSetpointTolerance)
        && (pivot.getAngle().getDegrees() >= lastSetpoint.pivotAngle - PivotConstants.kSetpointTolerance);
    }
}
