// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.subsystems.*;
import frc.robot.subsystems.Shooter.Pivot;
import frc.robot.subsystems.Shooter.ShooterExtension;
import frc.robot.subsystems.vision.Optometrist;
import monologue.Logged;
import static frc.robot.Constants.ControllerConstants.*;

import java.util.*;

public class RobotContainer implements Logged {
  CommandXboxController m_driverController = new CommandXboxController(DriverXbox.kControllerID);
  CommandJoystick m_driverJoystick = new CommandJoystick(0);
  
  Drivetrain m_drivetrain = new Drivetrain();

  Shooter m_shooter = new Shooter();

  Intake m_intake = new Intake();

  Lift m_lift = new Lift();

  Pivot m_pivot = new Pivot();

  ShooterExtension m_extension = new ShooterExtension();

  private Optometrist eyedoctor = new Optometrist();

  public RobotContainer() {

    //TODO add some way for the user to pick between controller types 
    //cough cough sendable chooser/dropdown/what have you

    int choice = 0;
    //TODO "choice" will be determined by user input
    setDriveDefault(m_driverController, choice);
    configureBindings();
  }

  private void configureBindings() {

    //m_driverController.b().onTrue(m_shoot.shoot());

    m_driverController.leftBumper().whileTrue(m_intake.takeIn());
    m_driverController.rightBumper().and(() -> !m_driverController.getHID().getLeftBumper()).whileTrue(m_intake.takeOut());

//     m_driverController.b().onTrue(m_shooter.shoot());

//     m_driverController.y().onTrue(m_intake.takeIn());

//     m_driverController.x().onTrue(m_climb.climb());

    
    //m_driverController.rightBumper().onTrue(eyedoctor.peek());

    // thisButton.onTrue(do this thing)

    m_driverController.start().onTrue(m_drivetrain.zeroYawCommand());
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command(s) configured");
  }

  // public void setDriveDefault(CommandXboxController m_driverController, Drivetrain m_drivetrain){
  //   m_drivetrain.setDefaultCommand(
  //     m_drivetrain.driveTeleopCommand(
  //       m_driverController::getLeftY,
  //       m_driverController::getLeftX,
  //       m_driverController::getRightX,
  //       m_driverController::getLeftTriggerAxis,
  //       m_driverController.getHID()::getAButton,
  //       m_driverController.getHID()::getBButton,
  //       m_driverController.getHID()::getXButton,
  //       m_driverController.getHID()::getYButton,
  //       () -> m_driverController.getRightTriggerAxis() > DriverXbox.kThumbstickDeadband)
  //   );
  // }

  public void setDriveDefault(CommandGenericHID m_driverJoystick, int whichType){
    switch(whichType){
      case 0:
          m_drivetrain.setDefaultCommand(
          m_drivetrain.driveTeleopCommandGeneric(
            ()-> m_driverJoystick.getRawAxis(0),
            ()-> m_driverJoystick.getRawAxis(1),
            ()->m_driverJoystick.getRawAxis(3))
          );
        break;
      case 1:
          m_drivetrain.setDefaultCommand(
          m_drivetrain.driveTeleopCommand(
          m_driverController::getLeftY,
          m_driverController::getLeftX,
          m_driverController::getRightX,
          m_driverController::getLeftTriggerAxis,
          m_driverController.getHID()::getAButton,
          m_driverController.getHID()::getBButton,
          m_driverController.getHID()::getXButton,
          m_driverController.getHID()::getYButton,
          () -> m_driverController.getRightTriggerAxis() > DriverXbox.kThumbstickDeadband)
          );
        break;
      default:
        break;
    }
    
  }
}
