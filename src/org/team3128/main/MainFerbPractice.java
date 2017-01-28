package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;

public class MainFerbPractice extends MainFerb {
	
	public MainFerbPractice() {
		super();
	}
	
	@Override
	protected void constructHardware() {
		gearRollerMotor = new Victor(0);
		
		lowerIntakeMotor = new Victor(1);
		shooterIntakeMotor = new Victor(2);
		
		lifterMotor = new Victor(3);
		
		shooterMotorRight = new CANTalon(0);
		shooterMotorLeft = new CANTalon(1);
		
		leftDriveFront = new CANTalon(2);
		leftDriveBack = new CANTalon(3);
		rightDriveFront = new CANTalon(4);
		rightDriveBack = new CANTalon(5);
		
		gearPiston = new Piston(1, 2);
		doorPiston = new Piston(3, 4);
		
		gearshiftPistons = new Piston(5, 6);
		
		gearInputSensor = new DigitalInput(5);
		
		super.constructHardware();
	}
}
