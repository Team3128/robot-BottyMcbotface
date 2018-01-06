package org.team3128.testmainclasses;


import org.team3128.common.NarwhalRobot;
import org.team3128.common.util.Constants;
import org.team3128.common.util.datatypes.PIDConstants;
import org.team3128.common.util.units.Angle;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Test shooter for our 2017 robot, Ferb
 */
public class MainDriveEncCalibration extends NarwhalRobot
{
	TalonSRX driveMotor, driveMotor2;
	
	PIDConstants pidC = new PIDConstants(.25);

	double desiredDistance = 15;
	
	double lastEnablePosition = 0;
	
	@Override
	protected void constructHardware() {
		driveMotor = new TalonSRX(1);
		driveMotor2 = new TalonSRX(2);
		
		driveMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Constants.CAN_TIMEOUT);
		driveMotor.configPeakOutputForward(7, Constants.CAN_TIMEOUT);
		driveMotor.configPeakOutputReverse(7, Constants.CAN_TIMEOUT);
		driveMotor.configNominalOutputForward(2, Constants.CAN_TIMEOUT);
		driveMotor.configNominalOutputReverse(2, Constants.CAN_TIMEOUT);
		driveMotor.configAllowableClosedloopError(0, 256, Constants.CAN_TIMEOUT); // .25 rotations
		driveMotor.setSensorPhase(false);
		
		driveMotor2.set(ControlMode.Follower, driveMotor.getDeviceID());
		
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
		SmartDashboard.putNumber("Drive encoder pos", driveMotor.getSelectedSensorPosition(0) - lastEnablePosition);		
		
		//desiredSpeed = SmartDashboard.getNumber("Desired Speed", 10500);
		
		//shooterMotor.setP(SmartDashboard.getNumber("p Value", 0.0));
		//shooterMotor.setI(SmartDashboard.getNumber("i Value", 0.0));
		//shooterMotor.setD(SmartDashboard.getNumber("d Value", 0.0));
		
		driveMotor.config_kP(0, pidC.getkP(), Constants.CAN_TIMEOUT);
		driveMotor.config_kI(0, pidC.getkI(), Constants.CAN_TIMEOUT);
		driveMotor.config_kD(0, pidC.getkD(), Constants.CAN_TIMEOUT);
		
		//SmartDashboard.putNumber("Random Number", randomNumber);
	}


	@Override
	protected void teleopInit() {
		lastEnablePosition = driveMotor.getSelectedSensorPosition(0) * Angle.CTRE_MAGENC_NU;

		driveMotor.set(ControlMode.Position, desiredDistance + lastEnablePosition);
	}

}