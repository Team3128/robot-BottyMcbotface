package org.team3128.mechanisms;

import org.team3128.autonomous.commands.CmdAimToHighGoal;
import org.team3128.common.util.Log;
import org.team3128.common.util.RobotMath;
import org.team3128.main.MainFerb;

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
	CANTalon intakeRoller;
	MainFerb robot;
	
	Command cmdAim;

	final static double LAUNCH_WHEEL_SPEED = 3500; // RPM
	final static double ALLOWABLE_WHEEL_SPEED_ERROR = 100; //RPM
	final static double SHOOTER_INTAKE_ROLLER_SPEED = -.5; //speed to run the shooter intake roller at to feed balls into the shooter
		
	private Thread thread;

	boolean feedFlag;
	boolean spinFlag;

	// set by thread to tell program what is going on
	private ShooterState state;

	/**
	 * 
	 * @param launcherWheel
	 * @param intakeWheel
	 */
	public Shooter(MainFerb robot, CANTalon launcherWheel, CANTalon intakeWheel)
	{
		this.launcherWheel = launcherWheel;
		this.intakeRoller = intakeWheel;
		this.robot = robot;
		this.state = ShooterState.STOPPED;
		
		launcherWheel.changeControlMode(TalonControlMode.Speed);
		launcherWheel.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		launcherWheel.set(0);
		launcherWheel.disableControl();

		thread = new Thread(this::shooterThread);
		thread.start();
		
		cmdAim = new CmdAimToHighGoal(robot, 6000);
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
				if(spinFlag)
				{
					state = ShooterState.SPINNING_UP;

					launcherWheel.enableControl();
					launcherWheel.set(LAUNCH_WHEEL_SPEED);
				}
				break;
			
			case SPINNING_UP:
				if((RobotMath.abs(launcherWheel.getClosedLoopError()) < ALLOWABLE_WHEEL_SPEED_ERROR)) //&& (!cmdAim.isRunning()))
				{
					state = ShooterState.SPUN_UP;
				}
				
				if (!spinFlag) {
					state = ShooterState.STOPPED;
					
					intakeRoller.set(0);
					launcherWheel.disableControl();
					continue;
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
				if (feedFlag) {
					state = ShooterState.LAUNCHING;
					
					intakeRoller.set(SHOOTER_INTAKE_ROLLER_SPEED);
					continue;
				}
				else if (!spinFlag) {
					state = ShooterState.STOPPED;
					
					intakeRoller.set(0);
					launcherWheel.disableControl();
					continue;
				}
				
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
				
				if(!feedFlag)
				{
					state = ShooterState.SPUN_UP;
					
					intakeRoller.set(0);
				}
				else if (!spinFlag) {
					state = ShooterState.STOPPED;
					
					intakeRoller.set(0);
					launcherWheel.disableControl();
				}

				break;

			}

		}
	}


	//synchronized setter so that this variable can be modified by multiple threads
	private synchronized void setSpinFlag(boolean spinFlag)
	{
		this.spinFlag = spinFlag;

		this.notify();
	}
	
	/**
	 * Set the flywheel to enabled or disabled. 
	 */
	public void setFlywheelEnabled(boolean enabled)
	{
		setSpinFlag(enabled);
	}

	/**
	 * Tell the shooter to spin up, but not start shooting
	 */
	public void enableFlywheel()
	{
		setSpinFlag(true);
		setFeedFlag(false);
	}

	/**
	 * Tell the shooter to spin down
	 */
	public void disableFlywheel()
	{
		setSpinFlag(false);
		setFeedFlag(false);
	}
	
	public void toggleFlywheel() 
	{
		if (state == ShooterState.STOPPED) {
			//cmdAim.start();
			enableFlywheel();
		}
		else {
			disableFlywheel();
		}
	}
	
	//synchronized setter so that this variable can be modified by multiple threads
	private synchronized void setFeedFlag(boolean feedFlag)
	{
		this.feedFlag = feedFlag;

		this.notify();
	}
	
	/**
	 * Set the shooter intake roller to enabled or disabled. 
	 */
	public void setFeederEnabled(boolean enabled)
	{
		setFeedFlag(enabled);
	}

	/**
	 * Tell the shooter to spin up and start shooting when it is ready
	 */
	public void enableFeeder()
	{
		setFeedFlag(true);
	}

	/**
	 * Tell the shooter to stop feeding balls and spin down
	 */
	public void disableFeeder()
	{
		setFeedFlag(false);
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
			enableFlywheel();
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
			disableFlywheel();
		}

		protected void interrupted()
		{
			end();
		}

	}

}