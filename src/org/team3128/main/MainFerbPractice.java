package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Victor;

public class MainFerbPractice extends MainFerb {
	
	public MainFerbPractice() {
		super();
	}
	
	@Override
	public void constructHardware() 
	{
		gearRollerMotor = new Victor(2);
		
		lowerIntakeMotor = new Victor(1);
		shooterIntakeMotor = new Victor(3);
		
		lifterMotor = new Victor(0);
		
		gearPiston = new Piston(0, 7);
		doorPiston = new Piston(1, 6);
		
		gearshiftPistons = new Piston(2, 5);
		
		gearInputSensor = new DigitalInput(5);
		
		visionAimServo = new Servo(9);
		
		super.constructHardware();
		
		leftDriveFront.reverseSensor(true);
	}
}
