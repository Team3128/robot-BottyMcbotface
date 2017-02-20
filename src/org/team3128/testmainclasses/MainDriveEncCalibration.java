package org.team3128.testmainclasses;


import org.team3128.common.NarwhalRobot;
import org.team3128.common.util.datatypes.PIDConstants;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Test shooter for our 2017 robot, Ferb
 */
public class MainDriveEncCalibration extends NarwhalRobot
{
	CANTalon driveMotor, driveMotor2;
	
	PIDConstants pidC = new PIDConstants(.25);

	double desiredDistance = 15;
	
	double lastEnablePosition = 0;
	
	@Override
	protected void constructHardware() {
		driveMotor = new CANTalon(3);
		driveMotor2 = new CANTalon(4);
		
		driveMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		driveMotor.changeControlMode(TalonControlMode.Position);
		driveMotor.configPeakOutputVoltage(7, -7);
		driveMotor.configNominalOutputVoltage(2, -2);
		driveMotor.setAllowableClosedLoopErr(256); // .25 rotations
		driveMotor.reverseSensor(false);	
		
		driveMotor2.changeControlMode(TalonControlMode.Follower);
		driveMotor2.set(3);
		
		pidC.putOnSmartDashboard("Drive PID");
	}
	
	
	@Override
	public void teleopPeriodic()
	{
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
		SmartDashboard.putNumber("Drive encoder pos", driveMotor.getPosition() - lastEnablePosition);		
		
		//desiredSpeed = SmartDashboard.getNumber("Desired Speed", 10500);
		
		//shooterMotor.setP(SmartDashboard.getNumber("p Value", 0.0));
		//shooterMotor.setI(SmartDashboard.getNumber("i Value", 0.0));
		//shooterMotor.setD(SmartDashboard.getNumber("d Value", 0.0));
		
		driveMotor.setP(pidC.getkP());
		driveMotor.setI(pidC.getkI());
		driveMotor.setD(pidC.getkD());
		
		//SmartDashboard.putNumber("Random Number", randomNumber);
	}


	@Override
	protected void teleopInit() {
		lastEnablePosition = driveMotor.getPosition();

		driveMotor.set(desiredDistance + lastEnablePosition);
	}

}