package org.team3128.testmainclasses;


import org.team3128.common.NarwhalRobot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Test shooter for our 2017 robot, Ferb
 */
public class MainShooterTest extends NarwhalRobot
{
	CANTalon shooterMotor, followerShooterMotor;
	VictorSP feederMotor;
	
	Joystick joystick;
	
	ADXRS450_Gyro gyro;
	
	double nativePerRotation = 4096;
	double desiredSpeed = 3000;
	
	boolean shouldIn = false;
	boolean shouldCheck = true;
	
	double nativePerHundredMs;
	double fGain;
	
	
	@Override
	protected void constructHardware() {
		shooterMotor = new CANTalon(6);
		//shooterMotor.changeControlMode(TalonControlMode.Speed);
		shooterMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		shooterMotor.changeControlMode(TalonControlMode.Speed);
		shooterMotor.reverseSensor(false);	
		
		followerShooterMotor = new CANTalon(5);
		followerShooterMotor.changeControlMode(TalonControlMode.Follower);
		followerShooterMotor.set(shooterMotor.getDeviceID());
		
		
		feederMotor = new VictorSP(0);
		
		shooterMotor.set(desiredSpeed);
		
		joystick = new Joystick(0);
		
		nativePerHundredMs = nativePerRotation * desiredSpeed / 600.0; 
		fGain = .80 * 1023 / nativePerHundredMs;
		
		//shooterMotor.setProfile(0);
		//shooterMotor.setF(fGain);
		
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
	}
	
	
	@Override
	public void teleopPeriodic()
	{
		shooterMotor.set(desiredSpeed);
	}

	

	@Override
	protected void setupListeners() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void autonomousInit() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void updateDashboard() {
		SmartDashboard.putNumber("Shooter encoder speed", shooterMotor.getSpeed());
		SmartDashboard.putNumber("Gyro angle", gyro.getAngle());
		
		
		//desiredSpeed = SmartDashboard.getNumber("Desired Speed", 10500);
		
		//shooterMotor.setP(SmartDashboard.getNumber("p Value", 0.0));
		//shooterMotor.setI(SmartDashboard.getNumber("i Value", 0.0));
		//shooterMotor.setD(SmartDashboard.getNumber("d Value", 0.0));
		
		//SmartDashboard.putNumber("Random Number", randomNumber);
	}


	@Override
	protected void teleopInit() {
		// TODO Auto-generated method stub
		
	}

}
//import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.SampleRobot;
//import edu.wpi.first.wpilibj.Timer;
//
///**
// * This is a demo program showing the use of the RobotDrive class, specifically
// * it contains the code necessary to operate a robot with tank drive.
// *
// * The VM is configured to automatically run this class, and to call the
// * functions corresponding to each mode, as described in the SampleRobot
// * documentation. If you change the name of this class or the package after
// * creating this project, you must also update the manifest file in the resource
// * directory.
// *
// * WARNING: While it may look like a good choice to use for your code if you're
// * inexperienced, don't. Unless you know what you are doing, complex code will
// * be much more difficult under this system. Use IterativeRobot or Command-Based
// * instead if you're new.
// */
//public class MainShooterTest extends SampleRobot {
//	//RobotDrive myRobot = new RobotDrive(0, 1); // class that handles basic drive
//												// operations
//	Joystick leftStick = new Joystick(0); // set to ID 1 in DriverStation
//	Joystick rightStick = new Joystick(1); // set to ID 2 in DriverStation
//
//	public MainShooterTest() {
//		//myRobot.setExpiration(0.1);
//	}
//
//	/**
//	 * Runs the motors with tank steering.
//	 */
//	@Override
//	public void operatorControl() {
//		//myRobot.setSafetyEnabled(true);
//		while (isOperatorControl() && isEnabled()) {
//		//	myRobot.tankDrive(leftStick, rightStick);
//			System.out.println(leftStick.getRawAxis(1));
//			Timer.delay(0.005); // wait for a motor update time
//		}
//	}
//}
