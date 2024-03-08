// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Constants.ControllerConstants.DriverXbox;
import frc.robot.Constants.LiftPivotSetpoint;
import frc.robot.subsystems.*;
// import frc.robot.subsystems.SpinNEOS;
// import monologue.Logged;
import frc.robot.subsystems.vision.Optometrist;
import frc.robot.utils.CommandContainer;
import monologue.Logged;
import static frc.robot.Constants.ControllerConstants.*;

import com.pathplanner.lib.commands.PathPlannerAuto;

public class RobotContainer implements Logged {
  CommandXboxController m_driverController = new CommandXboxController(DriverXbox.kControllerID);
  
  //CommandXboxController m_operatorController = new CommandXboxController(OperatorXbox.kControllerID);
  Drivetrain m_drivetrain = new Drivetrain();
  Pivot m_pivot = new Pivot();

  Shooter m_shooter = new Shooter();

  Intake m_intake = new Intake();

  Climber m_climber = new Climber();

  TrapLift m_lift = new TrapLift();

  CommandContainer m_commandContainer = new CommandContainer(m_intake, m_pivot, m_shooter, m_climber, m_lift);

  private Optometrist eyedoctor = new Optometrist();

  private final SendableChooser<Command> m_chooser = new SendableChooser<>();

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

    m_lift.setDefaultCommand(m_lift.idle());

    //todo fix
    m_chooser.setDefaultOption("No Auto", Commands.print("No auto selected :P"));
    m_chooser.addOption("2-Note Center Auto", new PathPlannerAuto("simpleCenter"));
    m_chooser.addOption("Just Shoot Auto (Does not initialize gyro properly)", m_commandContainer.shootFancy(1).withTimeout(3));
    SmartDashboard.putData("Auto choices", m_chooser);

    configureBindings();
  }

  private void configureBindings() {

    //m_driverController.b().onTrue(m_shoot.shoot());

    m_driverController.leftBumper().whileTrue(m_intake.takeInFancy());
    m_driverController.b().and(() -> !m_driverController.getHID().getLeftBumper()).whileTrue(m_commandContainer.retractIntakeFancy());
    m_driverController.rightBumper().whileTrue(m_commandContainer.shootFancy(0.5));
    m_driverController.a().whileTrue(m_commandContainer.shootFancy(0.15)); //TODO make better shoot command
    m_driverController.povUp().whileTrue(m_climber.climb(0.75));
    m_driverController.povDown().whileTrue(m_climber.climb(-0.75));
    m_driverController.x().onTrue(m_commandContainer.ampHandoffScore());
    m_driverController.back().toggleOnTrue(m_commandContainer.raisePivotLiftForClimb());

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
    //return Commands.print("No autonomous command(s) configured: Yeah the sendablechooser doesnt work lol, this is xbox controls btw");
    return m_chooser.getSelected();
    //return m_commandContainer.shootFancy(1).withTimeout(3); //THIS SIMPLE AUTO BYPASSES THE SENDABLECHOOSER
    //return new PathPlannerAuto("simpleCenter"); //This auto is tested and working
  }
}
