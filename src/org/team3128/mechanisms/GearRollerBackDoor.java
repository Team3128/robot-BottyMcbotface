package org.team3128.mechanisms;

import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.util.Log;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.InterruptHandlerFunction;
import edu.wpi.first.wpilibj.command.Command;

public class GearRollerBackDoor
{

	public enum GearState
	{
		INERT, SUCKIN, REVERSE;
	}

	Piston doorPiston, gearPiston;
	MotorGroup gearRoller;
	DigitalInput digitalInput;
	GearState state; // may be accessed from an interrupt, do not write directly

	public GearRollerBackDoor(Piston doorPiston, Piston gearPiston,
			MotorGroup gearRoller, DigitalInput digitalInput)
	{
		this.doorPiston = doorPiston;
		this.gearPiston = gearPiston;
		this.gearRoller = gearRoller;
		this.digitalInput = digitalInput;

		deactivateLoadingMode();

		digitalInput.requestInterrupts(new InterruptHandlerFunction<Object>()
		{
			public void interruptFired(int interruptAssertedMask, Object param)
			{
				digitalInputFired();
			};
		});

		digitalInput.enableInterrupts();

	}

	private synchronized void setState(GearState newState)
	{
		this.state = newState;

		if (newState == GearState.INERT)
		{
			gearRoller.setTarget(0);
		} else if (newState == GearState.SUCKIN)
		{
			gearRoller.setTarget(.4);
		} else if (newState == GearState.REVERSE)
		{
			gearRoller.setTarget(-.4);
		}
	}

	public void activateLoadingMode()
	{
		setState(GearState.SUCKIN);

		doorPiston.setPistonOn();
		gearPiston.setPistonOn();
	}

	private void digitalInputFired()
	{
		if (digitalInput.get() == true)
		{
			setState(GearState.REVERSE);

			doorPiston.setPistonOff();
			gearPiston.setPistonOn();
		}
	}

	public void deactivateLoadingMode()
	{
		setState(GearState.INERT);

		doorPiston.setPistonOff();
		gearPiston.setPistonOn();
	}

	public void activateDepositingMode()
	{
		doorPiston.setPistonOff();
		gearPiston.setPistonOff();
	}

	public void deactivateDepositingMode()
	{
		doorPiston.setPistonOff();
		gearPiston.setPistonOn();
	}

	public class CmdSetDepositingMode extends Command
	{

		boolean depositGear;

		public CmdSetDepositingMode(boolean depositGear)
		{
			super(1.5);
			this.depositGear = depositGear;
		}

		@Override
		protected void initialize()
		{

			if (depositGear == true)
			{
				activateDepositingMode();
			} else
			{
				deactivateDepositingMode();
			}

			Log.debug("Gear/Door pistons",
					"Moving pistons to correct positions for depositing a gear");
		}

		@Override
		protected void execute()
		{

		}

		@Override
		protected void end()
		{

		}
		
		@Override
		protected void interrupted()
		{
			end();
		}

		
		@Override
		protected boolean isFinished()
		{
			return isTimedOut();
		}

	}
}
