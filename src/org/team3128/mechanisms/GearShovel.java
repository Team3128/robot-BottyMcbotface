package org.team3128.mechanisms;

import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.util.units.Angle;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
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
		LOADING(0, 1.0),
		VERTICAL(55.0, 0.0),
		DEPOSITING(80.0, 0.0),
		FLOOR(145.0, 1.0);
		
		double angle;
		double power;
		
		private ShovelState(double angle, double power)
		{
			this.angle = angle;
			this.power = power;
		}
		
		public double getAngle()
		{
			return angle;
		}
		
		public double getPower()
		{
			return power;
		}
	}
	
	CANTalon armPivot;
	public MotorGroup roller;
	ShovelState state;
	CmdDepositGear depositGearCommand;
	MainFerb robot;
	
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
		armPivot.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		this.roller = roller;
		
		this.robot = robot;
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
		
		armPivot.set(armPivotAppropriatePosition(state.getAngle()));
		roller.setTarget(state.getPower());
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
	
	public void setDepositingMode()
	{
		setState(ShovelState.DEPOSITING);
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
		return armPivot.getPosition() * Angle.ROTATIONS / 3.0;
	}
	
	public void zeroArm()
	{
		armPivot.setPosition(0);
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
	
	public ShovelState getState()
	{
		return state;
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
			super(2);
			this.depositing = depositing;
		}
		
		@Override
		protected void initialize() {
			if (depositing)
			{
				setState(ShovelState.DEPOSITING);
			}
			else
			{
				setState(ShovelState.VERTICAL);
			}
		}
		
		@Override
		protected void end() {
		}
		
		@Override
		protected boolean isFinished() {
			return isTimedOut() || (getArmAngle() > state.getAngle() - 3.0 && getArmAngle() < state.getAngle() + 3.0);
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
			addParallel(robot.drive.new CmdMoveForward(-2 * Length.ft, 4000, 0.5));
			
			addSequential(new CmdSetDepositingMode(false));
		}
	}
}
