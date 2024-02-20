// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.Drivetrain;
import monologue.Logged;

public class RobotContainer implements Logged {
  CommandXboxController m_controller = new CommandXboxController(0);
  Drivetrain m_drivetrain = TunerConstants.Drivetrain;

  public RobotContainer() {
    configureBindings();
    m_drivetrain.setDefaultCommand(m_drivetrain.run(() -> { 
      m_drivetrain.filterXboxControllerInputs(m_controller.getLeftY(), m_controller.getLeftX(), m_controller.getRightX());})); //TODO add drive command
  }

  private void configureBindings() {

    // Bind different button to a specific functionality (do the thing)

    // thisButton.onTrue(do this thing)
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command(s) configured");
  }
}
