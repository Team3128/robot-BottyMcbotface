package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;

import com.ctre.CANTalon;

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
		
		shooterMotorRight = new CANTalon(5);
		shooterMotorLeft = new CANTalon(6);
		
		leftDriveFront = new CANTalon(3);
		leftDriveBack = new CANTalon(4);
		rightDriveFront = new CANTalon(1);
		rightDriveBack = new CANTalon(2);
		
		gearPiston = new Piston(0, 7);
		doorPiston = new Piston(1, 6);
		
		gearshiftPistons = new Piston(2, 5);
		
		gearInputSensor = new DigitalInput(5);
		
		visionAimServo = new Servo(9);
		
		super.constructHardware();
	}
}
