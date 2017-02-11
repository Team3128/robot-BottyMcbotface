package org.team3128.mechanisms;

import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.util.Log;
import org.team3128.common.util.RobotMath;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter
{
	//describes possible states the turret can be in
	public enum ShooterState
	{
		LAUNCHING("Launching"), //ball is currently being fired
		SPINNING_UP("Spinning Up"), //shooter is preparing to launch
		SPUN_UP("Spun Up"), //shooter is ready to start launching
		STOPPED("Stopped"); //turret is not doing anything
		
		String name;
		
		private ShooterState(String name)
		{
			this.name = name;
		}
		
		public String toString()
		{
			return name;
		}
	}
	
	CANTalon launcherWheel;
	MotorGroup intakeWheel;

	final static double LAUNCH_WHEEL_SPEED = 100; // RPM
	final static double ALLOWABLE_WHEEL_SPEED_ERROR = 100; //RPM
	final static double BALL_HOLDER_LAUNCH_SPEED = .5; //speed to run the middle roller at to release the ball
	final static long LAUNCH_TIME = 1000; //ms - time it takes once we start running the middle roller for the ball to be launched
	
	
	private Thread thread;
	
	// read by thread to know when to start launching
	// after the thread reads it, it sets it to false again
	boolean launchFlag;
	
	// set by thread to tell program what is going on
	private ShooterState state;
		
	/**
	 * 
	 * @param launcherWheel
	 * @param intakeWheel
	 */
	public Shooter(CANTalon launcherWheel, MotorGroup intakeWheel)
	{
		this.launcherWheel = launcherWheel;
		this.intakeWheel = intakeWheel;
				
		launcherWheel.changeControlMode(TalonControlMode.Speed);
		launcherWheel.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		launcherWheel.set(0);
		launcherWheel.disableControl();
		
		thread = new Thread(this::shooterThread);
		thread.start();
		
		this.state = ShooterState.STOPPED;
	}
	
	/**
	 * Manages turret state in the background.
	 * Run as a separate thread
	 */
	private void shooterThread()
	{
		Log.info("Shooter", "Shooter Thread Starting...");
		while(true)
		{
			SmartDashboard.putString("Shooter State", state.toString());
			// State machine to handle shooter
			switch(state)
			{
			case STOPPED:
				synchronized(this)
				{
					try 
					{
						this.wait();
					}
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				if(launchFlag)
				{
					state = ShooterState.SPINNING_UP;
					
					launcherWheel.enableControl();
					launcherWheel.set(LAUNCH_WHEEL_SPEED);
				}

				break;
			case SPINNING_UP:
				if((RobotMath.abs(launcherWheel.getClosedLoopError()) < ALLOWABLE_WHEEL_SPEED_ERROR))
				{
					state = ShooterState.SPUN_UP;
					
					intakeWheel.setTarget(BALL_HOLDER_LAUNCH_SPEED);	
				}
				
				try 
				{
					Thread.sleep(50);
				}
				catch (InterruptedException e)
				{
					return;
				}
				break;
			case SPUN_UP:
				state = ShooterState.LAUNCHING;
				
				break;
			case LAUNCHING:
				synchronized(this)
				{
					try 
					{
						this.wait();
					}
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				if(!launchFlag)
				{
					state = ShooterState.STOPPED;
					
					intakeWheel.setTarget(0);
					launcherWheel.disableControl();
				}

				break;
				
			}
			
		}
	}
	
	
	//synchronized setter so that this variable can be modified by multiple threads
	private synchronized void setLaunchFlag(boolean launchFlag)
	{
		this.launchFlag = launchFlag;
		
		this.notify();
	}
	
	/**
	 * Set the shooter to enabled or disabled. 
	 */
	public void setShooterEnabled(boolean enabled)
	{
		setLaunchFlag(enabled);
	}
	
	/**
	 * Tell the shooter to spin up and start shooting when it is ready
	 */
	public void enableShooter()
	{
		setLaunchFlag(true);
	}
	
	/**
	 * Tell the shooter to spin down
	 */
	public void disableShooter()
	{
		setLaunchFlag(false);
	}
	
	/**
	 *  Get the state of the shooter
	 * @return
	 */
	public ShooterState getState()
	{
		return state;
	}
	
	public class CmdShoot extends Command 
	{
		
		public CmdShoot(int msec)
		{
			super(msec / 1000.0);
		}
		
		protected void initialize()
	    {
			enableShooter();
	    }

	    protected void execute()
	    {
	    }

	    protected boolean isFinished()
	    {
	    	return isTimedOut();
	    }

	    protected void end()
	    {
	    	disableShooter();
	    }

	    protected void interrupted()
	    {
	    	end();
	    }
	    
	}
	
}