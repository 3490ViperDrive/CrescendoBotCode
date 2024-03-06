// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Constants.LiftPivotSetpoint;
import frc.robot.Constants.ControllerConstants.DriverXbox;
import frc.robot.subsystems.*;
// import frc.robot.subsystems.SpinNEOS;
// import monologue.Logged;
import frc.robot.subsystems.vision.Optometrist;
import monologue.Logged;
import static frc.robot.Constants.ControllerConstants.*;

public class RobotContainer implements Logged {
  CommandXboxController m_driverController = new CommandXboxController(DriverXbox.kControllerID);
  
  CommandXboxController m_operatorController = new CommandXboxController(OperatorXbox.kControllerID);
  Drivetrain m_drivetrain = new Drivetrain();
  Pivot m_pivot = new Pivot();

  Shooter m_shooter = new Shooter();

  Intake m_intake = new Intake();

  Climber m_climber = new Climber();

  private Optometrist eyedoctor = new Optometrist();

  public RobotContainer() {

    m_drivetrain.setDefaultCommand(
      m_drivetrain.driveTeleopCommand(
        m_driverController::getLeftY,
        m_driverController::getLeftX,
        m_driverController::getRightX,
        m_driverController::getRightTriggerAxis,
        // m_driverController.getHID()::getAButton,
        // m_driverController.getHID()::getBButton,
        // m_driverController.getHID()::getXButton,
        // m_driverController.getHID()::getYButton,
        () -> false,
        () -> false,
        () -> false,
        () -> false,
        () -> m_driverController.getLeftTriggerAxis() > DriverXbox.kThumbstickDeadband)); //this is evil but i can't think of a better way of doing it
    
    //Temp
    // m_pivot.setDefaultCommand(
    //   m_pivot.runOpenLoop(
    //     m_operatorController::getLeftY, m_operatorController::getRightY));
    // m_drivetrain.setDefaultCommand(m_drivetrain.sysIDTranslationCommand(6));

    //m_shooter.setDefaultCommand(m_shooter.stopMotorCommand());

    //m_intake.setDefaultCommand(m_intake.stopMotorCommand());

    //m_climb.setDefaultCommand(m_climb.stopMotorCommand());

    //TODO USE A BETTER COMMAND THAN THIS
    m_pivot.setDefaultCommand(m_pivot.requestPosition(55));


    configureBindings();
  }

  private void configureBindings() {

    //m_driverController.b().onTrue(m_shoot.shoot());

    m_driverController.leftBumper().whileTrue(m_intake.takeInFancy());
    m_driverController.b().and(() -> !m_driverController.getHID().getLeftBumper()).whileTrue(m_intake.takeOut());
    m_driverController.rightBumper().whileTrue(m_shooter.shoot(0.5));
    m_driverController.a().whileTrue(m_shooter.shoot(0.15)); //TODO make better shoot command
    m_driverController.povUp().whileTrue(m_climber.climb(0.75));
    m_driverController.povDown().whileTrue(m_climber.climb(-0.75));

//     m_driverController.b().onTrue(m_shooter.shoot());

//     m_driverController.y().onTrue(m_intake.takeIn());

//     m_driverController.x().onTrue(m_climb.climb());

    
    //m_driverController.rightBumper().onTrue(eyedoctor.peek());

    // thisButton.onTrue(do this thing)

    m_driverController.start().onTrue(m_drivetrain.zeroYawCommand());

    //Temp
    //m_operatorController.a().whileTrue(m_pivot.setPosition(LiftPivotSetpoint.kShoot));
    // m_operatorController.b().whileTrue(m_pivot.setPosition(LiftPivotSetpoint.kAmp));
    //m_operatorController.x().whileTrue(m_pivot.setPosition(LiftPivotSetpoint.kStowed));
    // m_operatorController.y().whileTrue(m_pivot.setPosition(LiftPivotSetpoint.kTrap));
    // m_operatorController.b().whileTrue(m_pivot.bruh(50));
    // m_operatorController.y().whileTrue(m_pivot.bruh(0));
    //m_driverController.a().whileTrue(m_shooter.shoot());
    //m_driverController.b().whileTrue(m_intake.takeInFancy());
    //m_driverController.y().whileTrue(m_intake.takeOut());
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command(s) configured");
  }
}
