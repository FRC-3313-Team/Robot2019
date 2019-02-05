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
  // Default configuration values
  private static final double DEFAULT_TURN_MULTIPLIER = .5;
  private static final double DEFAULT_DRIVE_MULTIPLIER = 1;

  Joystick thrustmaster = new Joystick(0);
  Joystick logitech = new Joystick(1);

  Compressor compressor = new Compressor(11);

  // Arm Solenoids
  DualValveSolenoid armSolenoid = new DualValveSolenoid(11, 0, 1);

  // Brake Solenoids
  DualValveSolenoid brakeSolenoid = new DualValveSolenoid(11, 2, 3);

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

  NetworkTableEntry turnSpeedMultiplier = robotStatusTab.add("Turn Speed Multiplier", DEFAULT_TURN_MULTIPLIER)
      .getEntry();
  NetworkTableEntry driveSpeedMultiplier = robotStatusTab.add("Drive Speed Multiplier", DEFAULT_DRIVE_MULTIPLIER)
      .getEntry();

  NetworkTableEntry resolvedDriveMultiplier = robotStatusTab.add("Resolved Drive Speed", 0).getEntry();

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
    resolvedDriveMultiplier
        .setDouble(driveSpeedMultiplier.getDouble(DEFAULT_DRIVE_MULTIPLIER) * (-thrustmaster.getRawAxis(2) + 1));
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
    drive.driveCartesian(thrustmaster.getRawAxis(0) * driveSpeedMultiplier.getDouble(DEFAULT_DRIVE_MULTIPLIER),
        -thrustmaster.getRawAxis(1) * driveSpeedMultiplier.getDouble(DEFAULT_DRIVE_MULTIPLIER)
            * (-thrustmaster.getRawAxis(2) + 1),
        thrustmaster.getRawAxis(3) * turnSpeedMultiplier.getDouble(DEFAULT_TURN_MULTIPLIER)
            * (-thrustmaster.getRawAxis(2) + 1));

    // Arm Position
    if (thrustmaster.getRawButton(7) || logitech.getRawButton(6)) {
      armSolenoid.set(1);
    } else if (thrustmaster.getRawButton(6) || logitech.getRawButton(7)) {
      armSolenoid.set(0);
    } else {
      armSolenoid.set(2);
    }

    // Brake
    if (logitech.getRawAxis(2) > .75 || logitech.getRawButton(8)) { // Start breaking
      brakeSolenoid.set(1);
    } else { // Release brake
      brakeSolenoid.set(0);
    }

    // Shoot/Intake
    if (thrustmaster.getRawButton(1) || logitech.getRawButton(1)) { // Shoot
      intakeMotor.set(1);
    } else if (thrustmaster.getRawButton(2) || logitech.getRawButton(4) || logitech.getRawButton(5)) { // Intake
      intakeMotor.set(-1);
    } else { // Not Work
      intakeMotor.set(0);
    }

    // Tilt
    if (thrustmaster.getRawButton(9) || logitech.getRawButton(3)) {
      tiltMotor.set(.65);
    } else if (thrustmaster.getRawButton(10) || logitech.getRawButton(2)) {
      tiltMotor.set(-.65);
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
