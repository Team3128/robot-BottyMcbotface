package org.team3128.mechanisms;

import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.util.Log;
import org.team3128.common.util.RobotMath;
import org.team3128.common.util.units.Angle;
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
		VERTICAL(65.0, 0.0),
		DEPOSITING(87.0, 0),
		CLEAN(145.0, 0),
		FLOOR(154.0, 1.0);
		
		private double angle;
		private double rollerPower;
		
		private ShovelState(double angle, double power)
		{
			this.angle = angle;
			this.rollerPower = power;
		}
		
		public double getAngle()
		{
			return angle;
		}
		
		public double getPower()
		{
			return rollerPower;
		}
	}
	
	CANTalon armPivot;
	public MotorGroup roller;
	ShovelState state;
	CmdDepositGear depositGearCommand;
	MainFerb robot;
	Thread depositingRollerThread;
	boolean auto_override = false;
	
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
		
		depositingRollerThread = new Thread(() ->
		{
			while(true)
			{
				if(state == ShovelState.DEPOSITING)
				{
					if(RobotMath.abs(getArmAngle() - ShovelState.DEPOSITING.angle) < 10 * Angle.DEGREES || auto_override)
					{
						roller.setTarget(-1);
					}
					else
					{
						roller.setTarget(0);
					}
				}
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
			}
		});
		depositingRollerThread.start();
	}
	
	/**
	 * Sets the state of the shovel.
	 * 
	 * @param state
	 */
	private void setState(ShovelState state)
	{
		this.state = state;
		
		auto_override = false;
		
		armPivot.set(armPivotAppropriatePosition(state.getAngle()));
		roller.setTarget(state.getPower());
		
		Log.debug("GearShovel", "Pivot angle: " + state.getAngle() + ", roller power:" + state.getPower());
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
	
	public void setCleaningMode()
	{
		setState(ShovelState.CLEAN);
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
			super(1);
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
		protected void end() 
		{
			if(depositing && roller.getTarget() == 0)
			{
				overrideOffload(true);
			}
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
			addSequential(new CmdSetDepositingMode(true));
//			addSequential(robot.drive.new CmdMoveForward(-2 * Length.ft, 4000, 0.3));
//			addSequential(new CmdSetDepositingMode(false));
		}
	}
	
	public void overrideOffload(boolean override) {
		auto_override = override;
	}
}
