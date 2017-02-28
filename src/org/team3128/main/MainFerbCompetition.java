package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.motor.MotorGroup;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;
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
		
		visionAimServo = new Servo(9);
		
		super.constructHardware();
		
		rightDriveFront.reverseSensor(true);
		
		drive.setReversedAutonomous(false);
		
		leftDriveFront.configPeakOutputVoltage(6, -6);
		leftDriveFront.configNominalOutputVoltage(1.5, -1.5);
		leftDriveFront.setAllowableClosedLoopErr(64);
		
		rightDriveFront.configPeakOutputVoltage(6, -6);
		rightDriveFront.configNominalOutputVoltage(1.5, -1.5);
		rightDriveFront.setAllowableClosedLoopErr(64);
	}
}
