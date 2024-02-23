// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// import java.lang.reflect.InaccessibleObjectException;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.*;
// import frc.robot.subsystems.SpinNEOS;
// import monologue.Logged;
import frc.robot.subsystems.vision.Optometrist;

public class RobotContainer {

  CommandXboxController m_driverController = new CommandXboxController(0);

  CommandXboxController m_operatorController = new CommandXboxController(1);

  Drivetrain m_drivetrain = new Drivetrain();
  private Optometrist eyedoctor = new Optometrist();

  
  public RobotContainer() {

    m_drivetrain.setDefaultCommand(m_drivetrain.driveOpenLoopThrottleCommand(
      () -> m_driverController.getLeftY(), 
      () -> m_driverController.getLeftX(), 
      () -> m_driverController.getRightX(), 
      () -> m_driverController.getLeftTriggerAxis()));

    configureBindings();
  }

  private void configureBindings() {
    m_driverController.rightBumper().onTrue(eyedoctor.peek());
      //Left stick=Translation2d(Driver)
      //Rigtht stick=Rotation(Driver)
      //Left trigger=Crawl mode(Driver)
      
      //Right trigger=RobotCentric(Driver)
      //m_driverController.rightTrigger().whileTrue(Robot Centric)
      
      //left or right bumper=intake(Driver)
      //m_driverController.rightBumber.whileTrue(intake)
      //m_driverController.leftBumber.whielTrue(intake)
      
      //Face buttons=robot rotation presets(Driver)
        //m_driverController.a().onTrue(Rotation preset 1);
        //m_driverController.b().onTrue(Rotation preset 2);
        //m_driverController.y().onTrue(Rotation preset 3);
        //m_driverController.x().onTrue(Rotation preset 4);
      
      
      //m_operatorController.rightTrigger().whileTrue(Shooter.shoot());
  
      //m_operatorController.a().onTrue(default/speaker shooter position)
      
      //m _operatorController.b().onTrue(amp shooter position)
      
      //m_operatorController.y().onTrue(trap shooter position)
      

  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command(s) configured");
  }
}
