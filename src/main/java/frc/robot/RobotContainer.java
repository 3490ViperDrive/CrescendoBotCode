// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Drivetrain;
import monologue.Logged;

public class RobotContainer implements Logged {
  CommandXboxController m_driverController = new CommandXboxController(0);
  Drivetrain m_drivetrain = new Drivetrain();
  
  public RobotContainer() {
    configureBindings();

    m_drivetrain.setDefaultCommand(m_drivetrain.driveOpenLoopCommand(() -> m_driverController.getLeftY(), () -> m_driverController.getLeftX(), () -> m_driverController.getRightX()));
  }

  private void configureBindings() {
    m_driverController.start().onTrue(m_drivetrain.zeroGyroCommand(0));
    m_driverController.back().onTrue(m_drivetrain.toggleFieldOrientedCommand());
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
