/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  Joystick joystick = new Joystick(0);

  Compressor compressor = new Compressor(11);

  Solenoid s0 = new Solenoid(11, 0);
  Solenoid s1 = new Solenoid(11, 1);

  Talon intakeMotor = new Talon(0);
  Talon tiltMotor = new Talon(1);

  WPI_VictorSPX frontLeftMotor = new WPI_VictorSPX(1);
  WPI_VictorSPX frontRightMotor = new WPI_VictorSPX(2);
  WPI_VictorSPX backLeftMotor = new WPI_VictorSPX(3);
  WPI_VictorSPX backRightMotor = new WPI_VictorSPX(4);

  MecanumDrive drive = new MecanumDrive(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);

  // Shuffleboard code
  ShuffleboardTab robotStatusTab = Shuffleboard.getTab("Robot Status");
  NetworkTableEntry pressureSwitchStatus = robotStatusTab.add("Pneumatic Pressure", false)
      .withProperties(Map.of("colorWhenTrue", "green", "colorWhenFalse", "maroon")).getEntry();

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    pressureSwitchStatus.setBoolean(compressor.getPressureSwitchValue());
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  /**
   * This function is called periodically during operator control.
   */

  @Override
  public void teleopPeriodic() {
    // Drive control
    drive.driveCartesian(joystick.getRawAxis(0), -joystick.getRawAxis(1), joystick.getRawAxis(3) * .5);

    // Arm Position
    if (joystick.getRawButton(7)) {
      s0.set(true);
      s1.set(false);
    } else if (joystick.getRawButton(6)) {
      s1.set(true);
      s0.set(false);
    } else {
      s0.set(false);
      s1.set(false);
    }

    // Intake
    if (joystick.getRawButton(1)) {
      intakeMotor.set(1);
    } else if (joystick.getRawButton(2)) {
      intakeMotor.set(-1);
    } else {
      intakeMotor.set(0);
    }

    // Tilt
    if (joystick.getRawButton(9)) {
      tiltMotor.set(.25);
    } else if (joystick.getRawButton(10)) {
      tiltMotor.set(-.25);
    } else {
      tiltMotor.set(0);
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
