// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.subsystems.*;
import frc.robot.subsystems.vision.BreakTheBeam;
// import frc.robot.subsystems.SpinNEOS;
// import monologue.Logged;
import frc.robot.subsystems.vision.Optometrist;
import monologue.Logged;
import static frc.robot.Constants.ControllerConstants.*;




public class RobotContainer implements Logged {
  CommandXboxController m_driverController = new CommandXboxController(DriverXbox.kControllerID);
  
  Drivetrain m_drivetrain = new Drivetrain();

  Shooter m_shooter = new Shooter();

  Intake m_intake = new Intake();

  Lift m_lift = new Lift();

  private Optometrist eyedoctor = new Optometrist();

  private BreakTheBeam beambreak = new BreakTheBeam();

  //private Optometrist m_DIValue = new Optometrist();

  public RobotContainer() {

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
        () -> m_driverController.getRightTriggerAxis() > DriverXbox.kThumbstickDeadband) //this is evil but i can't think of a better way of doing it
    );


    // m_shooter.setDefaultCommand(m_shooter.shoot());

    // m_intake.setDefaultCommand(m_intake.takeIn());

    // m_lift.setDefaultCommand(m_lift.lift());

    configureBindings();
  }

  private void configureBindings() {
    // m_driverController.b().onTrue(m_shooter.shoot());

    // m_driverController.y().onTrue(m_intake.takeIn());

    // m_driverController.x().onTrue(m_lift.lift());
    
    m_driverController.rightBumper().onTrue(eyedoctor.peek());

    m_driverController.leftBumper().onTrue(beambreak.DIValue());
    
   // thisButton.onTrue(do this thing)

    m_driverController.start().onTrue(m_drivetrain.zeroYawCommand());
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command(s) configured");
  }
}
