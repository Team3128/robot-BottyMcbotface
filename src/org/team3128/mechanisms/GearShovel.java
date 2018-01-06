package org.team3128.mechanisms;

import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.util.Assert;
import org.team3128.common.util.Constants;
import org.team3128.common.util.Log;
import org.team3128.common.util.RobotMath;
import org.team3128.common.util.units.Angle;
import org.team3128.main.MainFerb;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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
		CLEAN(130.0, 0),
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
	
	TalonSRX armPivot;
	public MotorGroup roller;
	ShovelState state; // last set state of the shovel.  NOT CORRECT in unlocked mode.
	CmdDepositGear depositGearCommand;
	MainFerb robot;
	Thread depositingRollerThread;
	
	private double armPivotGearRatio = 3.0;
	
	/**
	 * if true, the intake is in competition-legal mode and can only be set to specific positions.  If false, state is not tracked and the
	 * intake can be set to any position using the setAngle() method
	 */
	private boolean locked;
	
	/**
	 * Control system for the gear mechanism V.2 - now with floor scooping!
	 * 
	 * @param armPivotMotor
	 * @param roller
	 */
	public GearShovel(TalonSRX armPivotMotor, MotorGroup roller, MainFerb robot, boolean locked)
	{
		this.armPivot = armPivotMotor;
		armPivotMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Constants.CAN_TIMEOUT);
		
		this.roller = roller;
		this.locked = locked;
		this.robot = robot;
		depositGearCommand = new CmdDepositGear(robot);
		
		setState(ShovelState.VERTICAL);
		
		depositingRollerThread = new Thread(() ->
		{
			while(true)
			{
				if(state == ShovelState.DEPOSITING)
				{
					if(RobotMath.abs(getArmAngle() - ShovelState.DEPOSITING.angle) < 10 * Angle.DEGREES)
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
		
		if(locked)
		{
			depositingRollerThread.start();
		}
	}
	
	/**
	 * Sets the state of the shovel.
	 * 
	 * @param state
	 */
	private void setState(ShovelState state)
	{
		// NOTE: state CAN be set in unlocked mode
		this.state = state;
				
		armPivot.set(ControlMode.Position, armPivotAppropriatePosition(state.getAngle()));
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
		return armPivotGearRatio * angle / Angle.CTRE_MAGENC_NU;
	}
	
	public double getArmAngle()
	{
		return armPivot.getSelectedSensorPosition(0) * Angle.CTRE_MAGENC_NU / 3.0;
	}
	
	public void zeroArm()
	{
		armPivot.setSelectedSensorPosition(0, 0, Constants.CAN_TIMEOUT);
	}
	

	public ShovelState getState()
	{
		// state is not tracked in unlocked mode
		Assert.that(locked);
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
				roller.setTarget(-.5);
			}
			else
			{
				setState(ShovelState.VERTICAL);
			}
		}
		
		@Override
		protected void end() 
		{
		}
		
		@Override
		protected boolean isFinished() {
			return isTimedOut() || (getArmAngle() > state.getAngle() - 3.0 && getArmAngle() < state.getAngle() + 3.0);
		}
	}
	
	// ----------------------------------------------------------------------------------------------
	// unlocked mode functions
	
	/**
	 * Controls the angle of the intake manually.
	 * Can ONLY be used in unlocked mode
	 * @param anglePercentage a double from -1 to 1, where -1 represents all the way up and 1 represents all the way down.
	 */
	public void setPosition(double position)
	{
		Assert.that(!locked);
		
		double armAngle = ((position + 1)* ShovelState.FLOOR.angle/2.0);
		
		armPivot.set(ControlMode.Position, armPivotAppropriatePosition(armAngle));
	}
	
	public void suck()
	{
		Assert.that(!locked);
		roller.setTarget(ShovelState.LOADING.rollerPower);
	}

	public void stopRoller()
	{
		Assert.that(!locked);
		roller.setTarget(ShovelState.DEPOSITING.rollerPower);
	}
	
	
	public void release()
	{
		Assert.that(!locked);
		roller.setTarget(-1); // since releasing is done by the watcher thread, we can't use a constant from ShovelState
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
}
