// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Constants.ControllerConstants.DriverXbox;
import frc.robot.Constants.LiftPivotSetpoint;
import frc.robot.subsystems.*;
import frc.robot.subsystems.vision.BreakTheBeam;
// import frc.robot.subsystems.SpinNEOS;
// import monologue.Logged;
import frc.robot.subsystems.vision.Optometrist;
import frc.robot.utils.CommandContainer;
import monologue.Logged;
import static frc.robot.Constants.ControllerConstants.*;
import static frc.robot.Constants.ShooterConstants.*;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

//import java.util.*;





public class RobotContainer implements Logged {
  CommandXboxController m_driverController = new CommandXboxController(DriverXbox.kControllerID);
  CommandJoystick m_driverJoystick = new CommandJoystick(0); //TODO potentially change port 0
  
  //CommandXboxController m_operatorController = new CommandXboxController(OperatorXbox.kControllerID);
  Drivetrain m_drivetrain = new Drivetrain();
  Pivot m_pivot = new Pivot();
  Shooter m_shooter = new Shooter();
  Intake m_intake = new Intake();
  Climber m_climber = new Climber();
  TrapLift m_lift = new TrapLift();
  //private BreakTheBeam beamBreak = new BreakTheBeam();
  CommandContainer m_commandContainer = new CommandContainer(m_intake, m_pivot, m_shooter, m_climber, m_lift);


  //private Optometrist m_DIValue = new Optometrist();

  public RobotContainer() {
    
    //TODO add in "setDriveDefault" here, integrate Colton's GUI stuff
    //TODO "setDriveDefault" called later in code
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
        () -> m_driverController.getLeftTriggerAxis() > DriverXbox.kThumbstickDeadband)
    ); //this is evil but i can't think of a better way of doing it
    
    //Temp
    // m_pivot.setDefaultCommand(
    //   m_pivot.runOpenLoop(
    //     m_operatorController::getLeftY, m_operatorController::getRightY));
    // m_drivetrain.setDefaultCommand(m_drivetrain.sysIDTranslationCommand(6));






    NamedCommands.registerCommand("Shooter", m_shooter.shoot(kShooterSpeed));
    NamedCommands.registerCommand("Intake", m_intake.takeIn(1));
    // m_shooter.setDefaultCommand(m_shooter.shoot());
    // m_intake.setDefaultCommand(m_intake.takeIn());
    //TODO USE A BETTER COMMAND THAN THIS
    m_pivot.setDefaultCommand(m_pivot.requestPosition(55));
    m_lift.setDefaultCommand(m_lift.idle());




    //TODO get "choice" from smartdash/shuffleboard
    String temp = "";

    setDriveDefault(m_driverController, temp);
    configureBindings();
  }

  private void configureBindings() {
    // m_driverController.b().onTrue(m_shooter.shoot());
    //m_driverController.b().onTrue(m_shoot.shoot());

    //TODO comment for simplicity 
    m_driverController.leftBumper().whileTrue(m_intake.takeInFancy()); //Run intake
    m_driverController.b().and(() -> !m_driverController.getHID().getLeftBumper()).whileTrue(m_commandContainer.retractIntakeFancy()); //???
    m_driverController.rightBumper().whileTrue(m_commandContainer.shootFancy(0.5)); //Shoot
    m_driverController.a().whileTrue(m_commandContainer.shootFancy(0.15)); //TODO make better shoot command //Shoot low power
    m_driverController.povUp().whileTrue(m_climber.climb(0.75));
    m_driverController.povDown().whileTrue(m_climber.climb(-0.75));
    m_driverController.x().onTrue(m_commandContainer.ampHandoffScore());
    m_driverController.back().toggleOnTrue(m_commandContainer.raisePivotLiftForClimb());

    m_driverJoystick.button(1).whileTrue(m_intake.takeInFancy());
    m_driverJoystick.button(2).whileTrue(m_commandContainer.shootFancy(0.5)); //Shoot regular
    //TODO add shoot low power
    //TODO make button 8 "crawl" (button press)
    //TODO robot oriented toggle on 12
    m_driverJoystick.button(3).onTrue(m_commandContainer.ampHandoffScore()); //Score Amp
    m_driverJoystick.button(9).whileTrue(m_climber.climb(0.75)); //TODO "lift up"
    m_driverJoystick.button(11).whileTrue(m_climber.climb(-0.75)); //TODO "lift down"
    //m_driverJoystick.button(10).onTrue(null); //TODO "home"
    //m_driverJoystick.button(6).onTrue(null);
    
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


    return new PathPlannerAuto("middleAutoBasic");
    //return Commands.print("No autonomous command(s) configured");
    //return m_commandContainer.shootFancy(1).withTimeout(3); //THIS SIMPLE AUTO BYPASSES THE SENDABLECHOOSER
    //return new PathPlannerAuto("simpleCenter"); //This auto is tested and working
  }

  public void setDriveDefault(CommandGenericHID m_driverJoystick, String whichType){
    switch(whichType){
      case "Joystick":
          m_drivetrain.setDefaultCommand(
          m_drivetrain.driveTeleopCommandGeneric(
            ()-> m_driverJoystick.getRawAxis(0),
            ()-> m_driverJoystick.getRawAxis(1),
            ()->m_driverJoystick.getRawAxis(3),
            m_driverJoystick.button(12).getAsBoolean())
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
