// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.*;

public class RobotContainer {

  CommandXboxController m_driverController = new CommandXboxController(0);
  Drivetrain m_drivetrain = new Drivetrain();
  Shooter m_shoot = new Shooter();
  Intake m_takeIn = new Intake();
  
  public RobotContainer() {

    m_drivetrain.setDefaultCommand(m_drivetrain.driveOpenLoopThrottleCommand(
      () -> m_driverController.getLeftY(), 
      () -> m_driverController.getLeftX(), 
      () -> m_driverController.getRightX(), 
      () -> m_driverController.getLeftTriggerAxis()));

    configureBindings();

    m_shoot.setDefaultCommand(m_shoot.stopMotorCommand());

    m_takeIn.setDefaultCommand(m_takeIn.stopMotorCommand());

  }

  private void configureBindings() {
    m_driverController.start().onTrue(m_drivetrain.zeroGyroCommand(0));
    m_driverController.back().onTrue(m_drivetrain.toggleFieldOrientedCommand());
    
    m_driverController.b().onTrue(m_shoot.shoot());

    m_driverController.y().whileTrue(m_takeIn.takeIn());
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command(s) configured");
  }
}
