package org.team3128.mechanisms;

import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.util.units.Angle;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Control system for the gear mechanism V.2 - now with floor scooping!
 * 
 * @author Ronak
 * 
 */
public class GearShovel {
	/**
	 * Enum that describes the 3 possible states for the shovel.
	 * 
	 * @author Ronak
	 *
	 */
	public enum ShovelState
	{
		LOADING,
		VERTICAL,
		FLOOR;
	}
	
	CANTalon armPivot;
	MotorGroup roller;
	ShovelState state;
	CmdDepositGear depositGearCommand;
	
	private double armPivotGearRatio = 3.0;
	
	/**
	 * Control system for the gear mechanism V.2 - now with floor scooping!
	 * 
	 * @param armPivot
	 * @param roller
	 */
	public GearShovel(CANTalon armPivot, MotorGroup roller, MainFerb robot)
	{
		this.armPivot = armPivot;
		armPivot.changeControlMode(TalonControlMode.Position);

		this.roller = roller;
		
		depositGearCommand = new CmdDepositGear(robot);
		
		setState(ShovelState.VERTICAL);
	}
	
	/**
	 * Sets the state of the shovel.
	 * 
	 * @param state
	 */
	private void setState(ShovelState state)
	{
		this.state = state;
		
		if (state == ShovelState.LOADING)
		{
			armPivot.set(armPivotAppropriatePosition(0.0));
			roller.setTarget(0.5);
		}
		else if (state == ShovelState.VERTICAL)
		{
			armPivot.set(armPivotAppropriatePosition(40.0));
			roller.setTarget(0);
		}
		else if (state == ShovelState.FLOOR)
		{
			armPivot.set(armPivotAppropriatePosition(130.0));
			roller.setTarget(0.5);
		}
	}
	
	public void setLoadingMode()
	{
		setState(ShovelState.LOADING);
	}
	
	public void setFloorMode()
	{
		setState(ShovelState.FLOOR);
	}
	
	public void setVerticalMode()
	{
		setState(ShovelState.VERTICAL);
	}
	
	public void depositGear()
	{
		depositGearCommand.start();
	}
	
	/**
	 * Converts an angle to the appropriate input for the geared up pivot motor.
	 * 
	 * @param angle - The desired angle in degrees.
	 * @return - The appropriate input for armPivot.set() in order to make the arm actually turn the desired angle.
	 */
	private double armPivotAppropriatePosition(double angle)
	{
		return armPivotGearRatio * angle / Angle.ROTATIONS;
	}
	
	public double getArmAngle()
	{
		return armPivot.getPosition() / 3.0;
	}
	
	public void zeroArm()
	{
		armPivot.reset();
	}
	
	/**
	 * Determines whether or not the driver should be able to drive the robot around after depositing a gear.
	 * 
	 * @return - If the drive backwards has completed.
	 */
	public boolean depositingDone()
	{
		return !depositGearCommand.isRunning();
	}
	
	/**
	 * Command that tells the shovel to lower and eject the gear or reel itself back in.
	 * 
	 * @author Ronak
	 */
	public class CmdSetDepositingMode extends Command
	{
		boolean depositing = false;
		public CmdSetDepositingMode(boolean depositing)
		{
			super(1);
			this.depositing = depositing;
		}
		
		@Override
		protected void initialize() {
			roller.setTarget((depositing) ? -0.5 : 0.0);
			armPivot.set(armPivotAppropriatePosition((depositing) ? 70.0 : 40.0));
		}
		
		@Override
		protected void end() {
		}
		
		@Override
		protected boolean isFinished() {
			return isTimedOut();
		}
	}
	
	/**
	 * CommandGroup that runs to deposit a gear, either in autonomous of teleop.
	 * 
	 * @author Ronak
	 *
	 */
	public class CmdDepositGear extends CommandGroup
	{
		public CmdDepositGear(MainFerb robot)
		{
			addParallel(new CmdSetDepositingMode(true));
			addParallel(robot.drive.new CmdMoveForward(-2 * Length.ft, 2000, 0.5));
			
			addSequential(new CmdSetDepositingMode(false));
		}
	}
}
