package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.motor.MotorGroup;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class MainFerbCompetition extends MainFerb {
	
	public MainFerbCompetition() {
		super();
	}
	
	@Override
	public void constructHardware() 
	{
		gearMotors = new MotorGroup(new VictorSP(2));
		gearMotors.invert();
		
		floorIntakeMotor = new VictorSP(1);
		climberMotor = new MotorGroup(new VictorSP(0));
		climberMotor.invert();
				
		gearPiston = new Piston(2, 5);
		doorPiston = new Piston(1, 6);
		
		gearshiftPistons = new Piston(0, 7);
		
		gearInputSensor = new DigitalInput(5);
		
//		visionAimServo = new Servo(9);
		
		super.constructHardware();
		
		leftDriveFront.reverseSensor(true);
		leftDriveFront.reverseOutput(true);
		
		drive.setReversedAutonomous(true);
		
		leftDriveFront.configPeakOutputVoltage(10, -10);
		leftDriveFront.configNominalOutputVoltage(2, -2);
		leftDriveFront.setAllowableClosedLoopErr(64);
		leftDriveFront.setF(0.2871);
		
		rightDriveFront.configPeakOutputVoltage(10, -10);
		rightDriveFront.configNominalOutputVoltage(2, -2);
		rightDriveFront.setAllowableClosedLoopErr(64);
		rightDriveFront.setF(0.2887);
		
		CameraServer cameraServer = CameraServer.getInstance();
		
		cameraServer.startAutomaticCapture(0).setFPS(20);
	}
}
