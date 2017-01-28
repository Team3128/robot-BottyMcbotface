package org.team3128.main.mechanisms;

import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.motor.MotorGroup;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.InterruptHandlerFunction;

public class GearRollerBackDoor  {

	public enum GearState
	{
		INERT,
		SUCKIN,
		REVERSE;		
	}
	Piston doorPiston, gearPiston;
	MotorGroup gearRoller;
	DigitalInput digitalInput;
	GearState state; //may be accessed from an interrupt, do not write directly

	public GearRollerBackDoor(Piston doorPiston, Piston gearPiston, MotorGroup gearRoller, DigitalInput digitalInput) 
	{
		this.doorPiston = doorPiston;
		this.gearPiston = gearPiston;
		this.gearRoller = gearRoller;
		this.digitalInput = digitalInput;
		
		deactivateLoadingMode();
		
		digitalInput.enableInterrupts();
		digitalInput.requestInterrupts(new InterruptHandlerFunction<Object>()
		{
			public void interruptFired(int interruptAssertedMask, Object param) 
			{
				onGearLimitSwitchTriggered();
			};
		});
	}
	
	private synchronized void setState(GearState newState)
	{
		this.state = newState;
	}
	
	public void activateLoadingMode()
	{
		setState(GearState.SUCKIN);
		doorPiston.setPistonOn();
		gearPiston.setPistonOn();
		gearRoller.setTarget(-1);
	}
	
	private void onGearLimitSwitchTriggered()
	{
		setState(GearState.REVERSE);
		gearRoller.setTarget(1);
		gearPiston.setPistonOff();
	}
	public void deactivateLoadingMode()
	{
		setState(GearState.INERT);
		gearRoller.setTarget(0);
		doorPiston.setPistonOff();
	}
	
	
}
