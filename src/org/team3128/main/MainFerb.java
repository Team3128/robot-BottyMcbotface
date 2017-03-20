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
import org.team3128.mechanisms.GearShovel.ShovelState;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalOutput;
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
	public Joystick rightJoystick;
	
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
	
	//public GenericSendableChooser<Alliance> allianceChooser;

	
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
		
		drive = new SRXTankDrive(leftDriveFront, rightDriveFront, wheelDiameter, 1, 23.70*Length.in, 28.45*Length.in, 380);
		drive.setGearRatio(HIGH_GEAR_RATIO);

		// Gear Shovel
		armPivotMotor = new CANTalon(5);
		gearShovel = new GearShovel(armPivotMotor, gearRoller, this);
		
		// General Electronics
		powerDistPanel = new PowerDistributionPanel();
		compressor = new Compressor();
		
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
		
		powerDistPanel = new PowerDistributionPanel();
		
		// Listener Managers
		rightJoystick = new Joystick(0);
		lmRight = new ListenerManager(rightJoystick);
		
		Log.info("MainFerb", "Activating Ferb");
        Log.info("MainFerb", "Hey! Where's Perry?");
        
        // SmartDashboard
        LAST_BLINK_TIME = System.currentTimeMillis();
        
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
		lmRight.nameControl(ControllerExtreme3D.TWIST, "MoveTurn");
		lmRight.nameControl(ControllerExtreme3D.JOYY, "MoveForwards");
		lmRight.nameControl(ControllerExtreme3D.THROTTLE, "Throttle");
		
		lmRight.nameControl(ControllerExtreme3D.TRIGGER, "DepositGear");
				
		lmRight.nameControl(new POV(0), "GearMechanismPOV");
		lmRight.nameControl(new Button(2), "GearShift");
		lmRight.nameControl(new Button(4), "FullSpeed");
		
		lmRight.nameControl(new Button(7), "Climb");
		lmRight.nameControl(new Button(9), "StartCompressor");
		lmRight.nameControl(new Button(10), "StopCompressor");
		lmRight.nameControl(new Button(11), "ScaleLights");
		lmRight.nameControl(new Button(12), "ClearStickyFaults");
		
		lmRight.nameControl(new Button(8), "HalfSpeed");
		lmRight.addButtonDownListener("HalfSpeed", () -> 
		{
			drive.arcadeDrive(.5, 0, 1, true);
		});
		
		
		lmRight.addMultiListener(() -> {
			drive.arcadeDrive(/*.5 * */lmRight.getAxis("MoveTurn"),
					lmRight.getAxis("MoveForwards"),
					-1 * lmRight.getAxis("Throttle"),
					fullSpeed);
			
			//floorIntakeMotor.set(RobotMath.clamp(lmRight.getAxis("MoveForwards"), 0, 1));
			//Log.debug("MainFerb", String.format("MoveTurn: %f, MoveForwards: %f, Throttle: %f", lmRight.getAxis("MoveTurn"), lmRight.getAxis("MoveForwards"), lmRight.getAxis("Throttle")));
		
		}, "MoveTurn", "MoveForwards", "Throttle", "FullSpeed");
		
		lmRight.addButtonDownListener("FullSpeed", this::toggleFullSpeed);
		
		lmRight.addButtonDownListener("ClearStickyFaults", powerDistPanel::clearStickyFaults);
		
		lmRight.addButtonDownListener("GearShift", () -> {
			gearshift.shiftToOtherGear();
			drive.setGearRatio(gearshift.isInHighGear() ? HIGH_GEAR_RATIO : LOW_GEAR_RATIO);
		});
		
		lmRight.addButtonDownListener("StartCompressor", compressor::start);
		lmRight.addButtonDownListener("StopCompressor", compressor::stop);
		
		lmRight.addButtonDownListener("DepositGear", gearShovel::setDepositingMode);
		lmRight.addButtonUpListener("DepositGear", gearShovel::setVerticalMode);
				
		lmRight.addButtonDownListener("Climb", () -> 
		{
			//gearRollerBackDoor.activateDepositingMode();
			climberMotor.setTarget(1);	
		});
		
		lmRight.addButtonUpListener("Climb", ()->
		{
			//gearRollerBackDoor.deactivateDepositingMode();
			climberMotor.setTarget(0);
		});
		
		lmRight.addButtonDownListener("ScaleLights", this::enableScaleLights);
		lmRight.addButtonUpListener("ScaleLights", this::disableScaleLights);
		
		lmRight.addListener("GearMechanismPOV", (POVValue value) -> {
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
		
		addListenerManager(lmRight);

	}

	@Override
	protected void teleopInit() {
		//shooter.disableFeeder();
		//shooter.disableFlywheel();
	}
	
	@Override
	protected void autonomousInit() {
		gearShovel.zeroArm();
	}
	
	protected void constructAutoPrograms(GenericSendableChooser<CommandGroup> programChooser)
	{		
		programChooser.addDefault("None", null);
		programChooser.addObject("Cross Baseline", new AutoCrossBaseline(this));
		//programChooser.addObject("Place Gear From Retrieval Zone", new AutoPlaceGearFromRetrievalZone(this, currAlliance));
		//programChooser.addObject("Place Gear From Key", new AutoPlaceGearFromKey(this, currAlliance));
		programChooser.addObject("Turn Right Far Gear", new AutoPlaceFarGear(this, Direction.RIGHT));
		programChooser.addObject("Turn Left Far Gear", new AutoPlaceFarGear(this, Direction.LEFT));
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
		
		SmartDashboard.putNumber("Gyro Angle", RobotMath.normalizeAngle(gyro.getAngle()));
		SmartDashboard.putString("Full Speed?", (currentBlinkState && fullSpeed) ? "NOT FULL SPEED" : "");
		SmartDashboard.putString("Current Gear", gearshift.isInHighGear() ? "High" : "Low");
		SmartDashboard.putNumber("Gear Roller Current", powerDistPanel.getCurrent(GEAR_ROLLER_PDP_PORT));
		SmartDashboard.putNumber("Encoder Heading", drive.getRobotAngle());
		SmartDashboard.putString("Compressor State", compressor.enabled() ? "On" : "Off");
		SmartDashboard.putNumber("Left Distance (in)", drive.encDistanceToCm(leftDriveFront.getPosition() * Angle.ROTATIONS) / Length.in);
		SmartDashboard.putNumber("Right Distance (in)", drive.encDistanceToCm(rightDriveFront.getPosition() * Angle.ROTATIONS) / Length.in);
		SmartDashboard.putNumber("Left Encoder Position", leftDriveFront.getEncPosition());
		SmartDashboard.putNumber("Right Encoder Position", rightDriveFront.getEncPosition());
		SmartDashboard.putString("Allowed To Drive?", (currentBlinkState && !gearShovel.depositingDone()) ? "DO NOT DRIVE" : "");
		SmartDashboard.putNumber("Gear Angle", gearShovel.getArmAngle());
	}

	@Override
	protected void disabledInit()
	{
		drive.stopMovement();
		//shooter.disableFeeder();
		//shooter.disableFlywheel();
	}
	
	@Override
	protected void teleopPeriodic()
	{
		if(powerDistPanel.getCurrent(GEAR_ROLLER_PDP_PORT) > 100)
		{
			Log.recoverable("MainFerb", "To much current in the gear roller.");
			gearRoller.setTarget(0);

		}
		
		if (gearShovel.getState() == ShovelState.DEPOSITING && leftDriveFront.getSpeed() < 0)
		{
			gearShovel.roller.setTarget(-0.5);
		}
		else if (gearShovel.getState() == ShovelState.DEPOSITING && (leftDriveFront.getSpeed() == 0 || leftDriveFront.getSpeed() > 0))
		{
			gearShovel.roller.setTarget(0);
		}
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
}
