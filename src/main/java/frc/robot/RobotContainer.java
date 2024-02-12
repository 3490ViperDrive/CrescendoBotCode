// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.SpinNEOS;
import monologue.Logged;

public class RobotContainer implements Logged {
  CommandXboxController m_driverController = new CommandXboxController(0);
  Drivetrain m_drivetrain = new Drivetrain();
  SpinNEOS m_spin = new SpinNEOS();
  
  public RobotContainer() {
    configureBindings();
    m_drivetrain.setDefaultCommand(m_drivetrain.driveOpenLoopThrottleCommand(() -> m_driverController.getLeftY(), () -> m_driverController.getLeftX(), () -> m_driverController.getRightX(), () -> m_driverController.getLeftTriggerAxis()));
    m_spin.setDefaultCommand(m_spin.stopMotorCommand());
  }

  private void configureBindings() {
    m_driverController.start().onTrue(m_drivetrain.zeroGyroCommand(0));
    m_driverController.back().onTrue(m_drivetrain.toggleFieldOrientedCommand());
    m_driverController.a().whileTrue(m_spin.spinnyCommand());
  }

  public Command getAutonomousCommand() {
    return new PathPlannerAuto("Rather Simple Auto");
  }
}
