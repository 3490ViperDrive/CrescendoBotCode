// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Constants.ControllerConstants.DriverXbox;
import frc.robot.subsystems.*;
import frc.robot.utils.CommandContainer;
import monologue.Logged;
import static frc.robot.Constants.ShooterConstants.*;

import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

public class RobotContainer implements Logged {
  CommandXboxController m_driverController = new CommandXboxController(DriverXbox.kControllerID);
  CommandJoystick m_driverJoystick = new CommandJoystick(0); 
  
  Drivetrain m_drivetrain = new Drivetrain();
  Pivot m_pivot = new Pivot();
  Shooter m_shooter = new Shooter();
  Intake m_intake = new Intake();
  Climber m_climber = new Climber();
  TrapLift m_lift = new TrapLift();

  SendableChooser<Command> m_chooser = new SendableChooser<>();
   
  CommandContainer m_commandContainer = new CommandContainer(m_intake, m_pivot, m_shooter, m_climber, m_lift);

  public RobotContainer() {
    m_chooser.setDefaultOption("Adam's Auto",new PathPlannerAuto("simpleCenter"));
    m_chooser.addOption("2 note left Auto", new PathPlannerAuto("2noteLeftAuto"));
    m_chooser.addOption("2 note right Auto", new PathPlannerAuto("2noteRightAuto"));
    m_chooser.addOption("3 note middle centerline auto", new PathPlannerAuto("middleCenterlineAuto"));
    m_chooser.addOption("3 note left centerline auto", new PathPlannerAuto("leftCenterlineAuto"));
    m_chooser.addOption("3 note right centerline auto", new PathPlannerAuto("rightCenterlineAuto"));
    m_chooser.addOption("4 note middle Auto", new PathPlannerAuto("middleAutoBasic"));
    m_chooser.addOption("4 note left Auto", new PathPlannerAuto("AmpAutoBasic"));
    m_chooser.addOption("4 note right auto", new PathPlannerAuto("sourceAutoBasic"));
    m_chooser.addOption("No Auto", new PrintCommand("No auto was selected. Why would you do this?"));
    SmartDashboard.putData("THE AutoChoices", m_chooser);
    
    //Temp
    // m_pivot.setDefaultCommand(
    //   m_pivot.runOpenLoop(
    //     m_operatorController::getLeftY, m_operatorController::getRightY));
    // m_drivetrain.setDefaultCommand(m_drivetrain.sysIDTranslationCommand(6));

    NamedCommands.registerCommand("Shooter", m_shooter.shoot(kShooterSpeed, m_intake));
    NamedCommands.registerCommand("Intake", m_intake.takeIn(1));
    // m_shooter.setDefaultCommand(m_shooter.shoot());
    // m_intake.setDefaultCommand(m_intake.takeIn());
    //TODO USE A BETTER COMMAND THAN THIS
    m_pivot.setDefaultCommand(m_pivot.requestPosition(55));
    m_lift.setDefaultCommand(m_lift.idle());

    //TODO get "choice" from smartdash/shuffleboard
    String temp = "Joystick";

    setDriveDefault(m_driverController, temp);
    configureBindings();
  }

  private void configureBindings() {
    m_driverJoystick.button(1).whileTrue(m_intake.takeInFancy());
    m_driverJoystick.button(2).whileTrue(m_commandContainer.shootFancy(0.6125)); //Shoot regular;
    m_driverJoystick.button(5).whileTrue(m_commandContainer.retractIntakeFancy());
    //TODO add shoot low power
    //TODO make button 8 "crawl" (button press)
    //TODO robot oriented toggle on 12
    m_driverJoystick.button(3).onTrue(m_commandContainer.ampHandoffScore()); //Score Amp
    m_driverJoystick.button(9).whileTrue(m_climber.climb(0.75));
    m_driverJoystick.button(11).whileTrue(m_climber.climb(-0.75));
    m_driverJoystick.button(10).toggleOnTrue(m_commandContainer.raisePivotLiftForClimb());
    m_driverJoystick.button(12).onTrue(m_drivetrain.zeroYawCommand());
    m_driverJoystick.button(7).onTrue(m_drivetrain.toggleRobotCentric());
    m_driverJoystick.button(8).onTrue(m_drivetrain.toggleCrawling());

    m_driverJoystick.button(4).whileTrue(m_commandContainer.wetShoot(0.6125, 22));

    
    //m_driverController.start().onTrue(m_drivetrain.zeroYawCommand());

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
    return m_chooser.getSelected();
    //return Commands.print("No autonomous command(s) configured");
    //return m_commandContainer.shootFancy(1).withTimeout(3); //THIS SIMPLE AUTO BYPASSES THE SENDABLECHOOSER
    //return new PathPlannerAuto("simpleCenter"); //This auto is tested and working
  }

  //Robot.java needs to call this, but m_drivetrain is not visible. There may be a better way to resolve this
  public void setDriverPerspective() {
    m_drivetrain.setDriverPerspective();
  }


  public void setDriveDefault(CommandGenericHID m_driverJoystick, String whichType){
    switch(whichType){
      case "Joystick":
          // m_drivetrain.setDefaultCommand(
          // m_drivetrain.driveTeleopCommandGeneric(
          //   ()-> m_driverJoystick.getRawAxis(1),
          //   ()-> m_driverJoystick.getRawAxis(0),
          //   ()-> -m_driverJoystick.getRawAxis(2),
          //   ()-> m_driverJoystick.button(7).getAsBoolean(),
          //   ()-> m_driverJoystick.button(8).getAsBoolean())
          // );
          m_drivetrain.setDefaultCommand(
          m_drivetrain.driveTeleopCommandGeneric(
            ()-> m_driverJoystick.getRawAxis(1),
            ()-> m_driverJoystick.getRawAxis(0),
            ()-> -m_driverJoystick.getRawAxis(2))
          );
        break;
      case "Xbox Controller":
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
