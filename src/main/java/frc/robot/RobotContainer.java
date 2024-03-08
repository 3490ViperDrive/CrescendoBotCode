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
import frc.robot.Constants.ControllerConstants.DriverXbox;
import frc.robot.Constants.LiftPivotSetpoint;
import frc.robot.subsystems.*;


// import frc.robot.subsystems.SpinNEOS;
// import monologue.Logged;
import frc.robot.subsystems.vision.Optometrist;
import frc.robot.utils.CommandContainer;
import monologue.Logged;
import static frc.robot.Constants.ControllerConstants.*;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import java.util.*;





public class RobotContainer implements Logged {
  CommandXboxController m_driverController = new CommandXboxController(DriverXbox.kControllerID);
  CommandJoystick m_driverJoystick = new CommandJoystick(0);
  
  //CommandXboxController m_operatorController = new CommandXboxController(OperatorXbox.kControllerID);
  Drivetrain m_drivetrain = new Drivetrain();
  Pivot m_pivot = new Pivot();

  Shooter m_shooter = new Shooter();

  Intake m_intake = new Intake();

  Climber m_climber = new Climber();


  TrapLift m_lift = new TrapLift();
  private BreakTheBeam beamBreak = new BreakTheBeam();
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






    NamedCommands.registerCommand("Shooter", m_shooter.shoot());
    NamedCommands.registerCommand("Intake", m_intake.takeIn());
    // m_shooter.setDefaultCommand(m_shooter.shoot());
    // m_intake.setDefaultCommand(m_intake.takeIn());
    //TODO USE A BETTER COMMAND THAN THIS
    m_pivot.setDefaultCommand(m_pivot.requestPosition(55));
    m_lift.setDefaultCommand(m_lift.idle());


    int choice = 0;
    //TODO "choice" will be determined by user input
    setDriveDefault(m_driverController, choice);
    configureBindings();
  }

  private void configureBindings() {
    // m_driverController.b().onTrue(m_shooter.shoot());
    //m_driverController.b().onTrue(m_shoot.shoot());

    //TODO comment for simplicity 
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


    // m_driverController.x().onTrue(m_lift.lift());
    
    //m_driverController.rightBumper().onTrue(eyedoctor.peek());

    m_driverController.leftBumper().onTrue(beambreak.DIValue());
    
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
