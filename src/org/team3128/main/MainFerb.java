package org.team3128.main;

import org.team3128.autonomous.AutoCrossBaseline;
import org.team3128.autonomous.AutoPlaceFarGear;
import org.team3128.autonomous.AutoPlaceMiddleGear;
import org.team3128.autonomous.AutoTestTurn;
import org.team3128.autonomous.AutoTestTurn.TurnType;
import org.team3128.common.NarwhalRobot;
import org.team3128.common.drive.SRXTankDrive;
import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.misc.TwoSpeedGearshift;
import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.POVValue;
import org.team3128.common.listener.controllers.ControllerExtreme3D;
import org.team3128.common.listener.controltypes.Button;
import org.team3128.common.listener.controltypes.POV;
import org.team3128.common.util.GenericSendableChooser;
import org.team3128.common.util.Log;
import org.team3128.common.util.RobotMath;
import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Angle;
import org.team3128.common.util.units.Length;
import org.team3128.mechanisms.GearShovel;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MainFerb extends NarwhalRobot 
{
	private final double LOW_GEAR_RATIO = 1;
	private final double HIGH_GEAR_RATIO = 1;
	
	private boolean fullSpeed = false;
	
	// Drivetrain
	public SRXTankDrive drive;
	
	public CANTalon leftDriveFront, leftDriveBack;
	public CANTalon rightDriveFront, rightDriveBack;
	
	public TwoSpeedGearshift gearshift;
	public Piston gearshiftPistons;
	
	// Gear mechanism
	public GearShovel gearShovel;
	
	public CANTalon armPivotMotor;
	public MotorGroup gearRoller;
	
	// Climber
	public MotorGroup climberMotor;
	
	// Controls
	public ListenerManager lmRight;
	public ListenerManager lmLeft;
	
	public Joystick rightJoystick;
	public Joystick leftJoystick;
	
	// Robot
	final static int GEAR_ROLLER_PDP_PORT = 12;
	public PowerDistributionPanel powerDistPanel;
	
	public Compressor compressor;
	
	public ADXRS450_Gyro gyro;
	
	public DigitalOutput lightSignal;
	public double wheelDiameter;
	public boolean scaleLights = false;
	
	// SmartDashboard
	long LAST_BLINK_TIME;
	boolean currentBlinkState = false;
	int BLINK_TIME_MILLISECONDS = 1000;
	
	int time_remaining = 150;
	boolean timer_running = false;
	long last_tick_time;
	
	
	// Graveyard
	
	//public GearRollerBackDoor gearRollerBackDoor;
	//public Piston doorPiston, gearPiston;
	//public DigitalInput gearInputSensor;
	//MotorGroup gearMotors;
		
	//public Shooter shooter;
	//public CANTalon shooterMotorRight, shooterMotorLeft;
	//public CANTalon elevatorMotor;	
	//public VictorSP floorIntakeMotor;	
	
	//public PhoneCamera phoneCamera;
	
	public GenericSendableChooser<Alliance> allianceChooser;

	// true for competition-legal locked mode
	// false for illegal (but fun) unlocked mode
	final static boolean INTAKE_LOCKED = false;
	
	@Override
	protected void constructHardware() 
	{
		// Drivetrain
		leftDriveFront = new CANTalon(3);
		leftDriveBack = new CANTalon(4);
		rightDriveFront = new CANTalon(1);
		rightDriveBack = new CANTalon(2);
				
		leftDriveFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		rightDriveFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		leftDriveBack.changeControlMode(TalonControlMode.Follower);
		leftDriveBack.set(leftDriveFront.getDeviceID());
		
		rightDriveBack.changeControlMode(TalonControlMode.Follower);
		rightDriveBack.set(rightDriveFront.getDeviceID());
		
		gearshift = new TwoSpeedGearshift(false, gearshiftPistons);
		gearshift.shiftToLow();
		
		drive = new SRXTankDrive(leftDriveFront, rightDriveFront, wheelDiameter * Math.PI, 1, 23.70*Length.in, 28.45*Length.in, 380);
		drive.setGearRatio(HIGH_GEAR_RATIO);

		// Gear Shovel
		armPivotMotor = new CANTalon(7);
		gearShovel = new GearShovel(armPivotMotor, gearRoller, this, INTAKE_LOCKED);
        gearShovel.zeroArm();
		
		// General Electronics
		powerDistPanel = new PowerDistributionPanel();
		compressor = new Compressor();
		
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
		
		powerDistPanel = new PowerDistributionPanel();
		
		// Listener Managers
		rightJoystick = new Joystick(0);
		lmRight = new ListenerManager(rightJoystick);
		
		leftJoystick = new Joystick(1);
		lmLeft = new ListenerManager(leftJoystick);
		
		Log.info("MainFerb", "Activating Ferb");
        Log.info("MainFerb", "Hey! Where's Perry?");
        Log.info("MainFerb", INTAKE_LOCKED ? "Gear mechanism is locked and legal." : "Gear mechanism unlocked!  This may void the warranty!");
        
        // Climber
        //climberLeader.changeControlMode(TalonControlMode.PercentVbus);
        //climberFollower.changeControlMode(TalonControlMode.PercentVbus);
        
        // SmartDashboard
        LAST_BLINK_TIME = System.currentTimeMillis();
        allianceChooser = new GenericSendableChooser<Alliance>();
        allianceChooser.addDefault("BLUE", Alliance.Blue);
        allianceChooser.addObject("RED", Alliance.Red);
		SmartDashboard.putData("Alliance:", allianceChooser);

        
        // Graveyard
     	//gearRollerBackDoor = new GearRollerBackDoor(doorPiston, gearPiston, gearMotors, gearInputSensor);
     	//
     	//shooterMotorRight = new CANTalon(6);
     	//shooterMotorLeft = new CANTalon(5);
     	//elevatorMotor = new CANTalon(7);
     	//elevatorMotor.changeControlMode(TalonControlMode.PercentVbus);
     	//
     	//shooterMotorRight.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
     	//
     	//shooterMotorLeft.changeControlMode(TalonControlMode.Follower);
     	//shooterMotorLeft.set(shooterMotorRight.getDeviceID());
     	//shooterMotorLeft.reverseOutput(false);
     	//shooter = new Shooter(this, shooterMotorRight, elevatorMotor);
     	//
     	//phoneCamera = new PhoneCamera(new PIDConstants(.1, 0, 0), new PIDConstants(.1, 0, 0));
	}

	@Override
	protected void setupListeners() {
		// Drive
		lmRight.nameControl(ControllerExtreme3D.TWIST, "MoveTurn");
		lmRight.nameControl(ControllerExtreme3D.JOYY, "MoveForwards");
		lmRight.nameControl(ControllerExtreme3D.THROTTLE, "Throttle");		
		lmRight.nameControl(new Button(4), "FullSpeed");

		lmRight.addMultiListener(() -> {
			drive.arcadeDrive(/*.5 * */lmRight.getAxis("MoveTurn"),
					lmRight.getAxis("MoveForwards"),
					-1 * lmRight.getAxis("Throttle"),
					true);
			
			//floorIntakeMotor.set(RobotMath.clamp(lmRight.getAxis("MoveForwards"), 0, 1));
			//Log.debug("MainFerb", String.format("MoveTurn: %f, MoveForwards: %f, Throttle: %f", lmRight.getAxis("MoveTurn"), lmRight.getAxis("MoveForwards"), lmRight.getAxis("Throttle")));
		
		}, "MoveTurn", "MoveForwards", "Throttle", "FullSpeed");

		lmRight.nameControl(new Button(2), "GearShift");
		lmRight.addButtonDownListener("GearShift", () -> {
			gearshift.shiftToOtherGear();
			drive.setGearRatio(gearshift.isInHighGear() ? HIGH_GEAR_RATIO : LOW_GEAR_RATIO);
		});
		
		lmRight.addButtonDownListener("FullSpeed", this::toggleFullSpeed);
				
		// Gear Shovel
		lmRight.nameControl(ControllerExtreme3D.TRIGGER, "DepositGear");
		lmRight.addButtonDownListener("DepositGear", gearShovel::setDepositingMode);
		lmRight.addButtonUpListener("DepositGear", gearShovel::setVerticalMode);
		
		lmRight.nameControl(new POV(0), "GearShovelPOV");
		lmRight.addListener("GearShovelPOV", (POVValue value) -> {
			switch(value.getDirectionValue())
			{
			// Sides or Center
			case 3:
			case 7:
			case 0:
				gearShovel.setVerticalMode();
				break;
			// Forwards
			case 1:
			case 2:
			case 8:
				gearShovel.setFloorMode();
				break;
			// Backwards
			case 4:
			case 5:
			case 6:
				gearShovel.setLoadingMode();
				break;
			}
		});
		
		lmRight.nameControl(new Button(11), "ZeroShovelPivotArm");
		lmRight.addButtonDownListener("ZeroShovelPivotArm", () -> 
		{
			armPivotMotor.changeControlMode(TalonControlMode.PercentVbus);
			armPivotMotor.set(-.25);
		});
		lmRight.addButtonUpListener("ZeroShovelPivotArm", () -> 
		{
			armPivotMotor.changeControlMode(TalonControlMode.Position);
			gearShovel.zeroArm();
		});
		
		lmRight.nameControl(new Button(5), "BallCleanup");
		lmRight.addButtonDownListener("BallCleanup", () ->
		{
			gearShovel.setCleaningMode();
		});
		
		// Climber
		lmRight.nameControl(new Button(8), "CatchRope");
		lmRight.addButtonDownListener("CatchRope", () -> 
		{
			climberMotor.setTarget(0.2);	
		});
		lmRight.addButtonUpListener("CatchRope", ()->
		{
			climberMotor.setTarget(0);
		});
		
		lmRight.nameControl(new Button(7), "Climb");
		lmRight.addButtonDownListener("Climb", () -> 
		{
			climberMotor.setTarget(1.0);	
		});
		lmRight.addButtonUpListener("Climb", ()->
		{
			climberMotor.setTarget(0);
		});
		
		
		// General Robot
		lmRight.nameControl(new Button(9), "StartCompressor");
		lmRight.addButtonDownListener("StartCompressor", compressor::start);
		
		lmRight.nameControl(new Button(10), "StopCompressor");
		lmRight.addButtonDownListener("StopCompressor", compressor::stop);
		
		lmRight.nameControl(new Button(12), "ClearStickyFaults");
		lmRight.addButtonDownListener("ClearStickyFaults", powerDistPanel::clearStickyFaults);
		
		addListenerManager(lmRight);
		
		if(INTAKE_LOCKED)
		{
			lmLeft.nameControl(ControllerExtreme3D.JOYY, "LeftGearShovelControl");
			lmLeft.addListener("LeftGearShovelControl", (double joyY) ->
			{
				if (joyY < -0.3)
				{
					gearShovel.setFloorMode();
				}
				else if (joyY > 0.3)
				{
					gearShovel.setLoadingMode();
				}
				else
				{
					gearShovel.setVerticalMode();
				}
			});
			

		}
		else
		{
			lmLeft.nameControl(ControllerExtreme3D.JOYY, "RollerAngleControl");
			lmLeft.addListener("RollerAngleControl", gearShovel::setPosition);
			
			lmLeft.nameControl(ControllerExtreme3D.POV, "RollerPOV");
			lmLeft.addListener("RollerPOV", (POVValue value) -> {
				switch(value.getDirectionValue())
				{
				// Sides or Center
				case 3:
				case 7:
				case 0:
					// do nothing
					break;
				// Forwards
				case 1:
				case 2:
				case 8:
					gearShovel.suck();
					break;
				// Backwards
				case 4:
				case 5:
				case 6:
					gearShovel.release();
					break;
				}
			});
			
		}
		lmLeft.nameControl(ControllerExtreme3D.TRIGGER, "LeftGearDeposit");
		lmLeft.addButtonDownListener("LeftGearDeposit", gearShovel::setDepositingMode);
		lmLeft.addButtonUpListener("LeftGearDeposit", gearShovel::setVerticalMode);
		
		lmLeft.nameControl(new Button(5), "LeftBallCleanup");
		lmLeft.addButtonDownListener("LeftBallCleanup", gearShovel::setFloorMode);
		
		lmLeft.nameControl(new Button(11), "HalfSpeed");
		lmLeft.addButtonDownListener("HalfSpeed", () ->
		{
			drive.tankDrive(.5, .5);
		});
		lmLeft.addButtonUpListener("HalfSpeed", () ->
		{
			drive.tankDrive(0, 0);
		});
		
		lmLeft.nameControl(new Button(12), "ClearEncoders");
		lmLeft.addButtonDownListener("ClearEncoders", () ->
		{
			drive.clearEncoders();
		});
		
		addListenerManager(lmLeft);
	}

	@Override
	protected void teleopInit() {
		fullSpeed = true;
		gearShovel.setVerticalMode();
	}
	
	@Override
	protected void autonomousInit() {
		fullSpeed = false;
		gearShovel.setVerticalMode();
		resetTimer();
	}
	
	protected void constructAutoPrograms(GenericSendableChooser<CommandGroup> programChooser)
	{		
		programChooser.addDefault("None", null);
		programChooser.addObject("Cross Baseline", new AutoCrossBaseline(this));
		//programChooser.addObject("Place Gear From Retrieval Zone", new AutoPlaceGearFromRetrievalZone(this, currAlliance));
		//programChooser.addObject("Place Gear From Key", new AutoPlaceGearFromKey(this, currAlliance));
		programChooser.addObject("Turn Right Far Gear", new AutoPlaceFarGear(this, Direction.RIGHT, allianceChooser.getSelected()));
		programChooser.addObject("Turn Left Far Gear", new AutoPlaceFarGear(this, Direction.LEFT, allianceChooser.getSelected()));
		programChooser.addObject("Place Middle Gear", new AutoPlaceMiddleGear(this));
		//programChooser.addObject("Trigger Hopper & Shoot", new AutoShootFromHopper(this));
		
		//programChooser.addObject("DEBUG: Deposit Gear", new AutoTestDepositGear(this));
		//programChooser.addObject("DEBUG: In Place Encoder Turn", new AutoTestTurn(this, TurnType.ENCODERS_INPLACE));
		programChooser.addObject("DEBUG: Arc Encoder Turn", new AutoTestTurn(this, TurnType.ENCODERS_ARC));
		//programChooser.addObject("DEBUG: Gyro Turn", new AutoTestTurn(this, TurnType.GYRO));


	}
	
	@Override
	protected void updateDashboard() {
		blinkUpdate();
		tick();
		
		SmartDashboard.putString("Time Remaining: ", time_remaining + " sec");
		SmartDashboard.putNumber("Gyro Angle", RobotMath.normalizeAngle(gyro.getAngle()));
		SmartDashboard.putString("Full Speed?", (currentBlinkState && !fullSpeed) ? "NOT FULL SPEED" : "");
		SmartDashboard.putString("Current Gear", gearshift.isInHighGear() ? "High Gear" : "Low Gear");
		SmartDashboard.putNumber("Gear Roller Current", powerDistPanel.getCurrent(GEAR_ROLLER_PDP_PORT));
		SmartDashboard.putNumber("Encoder Heading", drive.getRobotAngle());
		SmartDashboard.putString("Compressor State", compressor.enabled() ? "On" : "Off");
		SmartDashboard.putNumber("Left Distance (in)", drive.encDistanceToCm(leftDriveFront.getPosition() * Angle.ROTATIONS) / Length.in);
		SmartDashboard.putNumber("Right Distance (in)", drive.encDistanceToCm(rightDriveFront.getPosition() * Angle.ROTATIONS) / Length.in);
		SmartDashboard.putNumber("Left Encoder Position", leftDriveFront.getEncPosition());
		SmartDashboard.putNumber("Right Encoder Position", rightDriveFront.getEncPosition());
		SmartDashboard.putNumber("Left Speed", leftDriveFront.getSpeed());
		SmartDashboard.putNumber("Right Speed", rightDriveFront.getSpeed());
		SmartDashboard.putString("Shovel State", gearShovel.getState().name());
	}

	@Override
	protected void disabledInit()
	{
		drive.stopMovement();
		disableTimer();
		//shooter.disableFeeder();
		//shooter.disableFlywheel();
	}
	
	@Override
	protected void teleopPeriodic()
	{
		//if(powerDistPanel.getCurrent(GEAR_ROLLER_PDP_PORT) > 100)
		//{
		//	Log.recoverable("MainFerb", "To much current in the gear roller.");
		//	gearRoller.setTarget(0);
		//}
		
//		if (gearShovel.getState() == ShovelState.DEPOSITING)
//		{
//			if((leftDriveFront.getSpeed() < -5 || leftDriveFront.getSpeed() < -5))
//			{
//				Log.debug("MainFerb", "Robot is going backwards, running gear roller");
//				gearShovel.roller.setTarget(-1);
//			}
//			else
//			{
//				gearShovel.roller.setTarget(0);
//			}
//		}
	}
	
	public void enableScaleLights()
	{
		lightSignal.set(true);
	}
	
	public void disableScaleLights()
	{
		lightSignal.set(false);
	}
	
	public void toggleFullSpeed()
	{
		fullSpeed = !fullSpeed;
	}
	
	public void blinkUpdate()
	{
		if (System.currentTimeMillis() - LAST_BLINK_TIME > BLINK_TIME_MILLISECONDS)
		{
			currentBlinkState = !currentBlinkState;
		}
	}
	
	public void disableTimer()
	{
		timer_running = false;
	}
	
	public void resetTimer()
	{
		time_remaining = 150;
		last_tick_time = System.currentTimeMillis();
		timer_running = true;
	}
	
	public void tick()
	{
		if (System.currentTimeMillis() - last_tick_time >= 1000 && time_remaining >= 0 && timer_running)
		{
			time_remaining -= 1;
			last_tick_time = System.currentTimeMillis();
		}
	}
}
