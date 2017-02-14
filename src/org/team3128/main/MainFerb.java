package org.team3128.main;

import org.team3128.autonomous.AutoCrossBaseline;
import org.team3128.autonomous.AutoPlaceFarGear;
import org.team3128.autonomous.AutoPlaceMiddleGear;
import org.team3128.autonomous.AutoShootFromHopper;
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
import org.team3128.common.util.datatypes.PIDConstants;
import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;
import org.team3128.mechanisms.GearRollerBackDoor;
import org.team3128.mechanisms.PhoneCamera;
import org.team3128.mechanisms.Shooter;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MainFerb extends NarwhalRobot 
{
	private final double LOW_GEAR_RATIO = 1;
	private final double HIGH_GEAR_RATIO = 1;
	
	public Shooter shooter;
	public GearRollerBackDoor gearRollerBackDoor;

	public SRXTankDrive drive;
	public DigitalInput gearInputSensor;
	
	MotorGroup intakeMotors, gearMotors;
	
	public CANTalon leftDriveFront, leftDriveBack;
	public CANTalon rightDriveFront, rightDriveBack;
	
	public CANTalon shooterMotorRight, shooterMotorLeft;

	public Victor lowerIntakeMotor, shooterIntakeMotor;
	
	public Victor lifterMotor;
	
	public Victor gearRollerMotor;
	
	public ListenerManager lmRight;
	//public ListenerManager lmLeft;
	
	public Joystick rightJoystick;
	//public Joystick leftJoystick;
	
	public TwoSpeedGearshift gearshift;
	public Piston gearshiftPistons;
	
	public Piston doorPiston, gearPiston;

	public PowerDistributionPanel powerDistPanel;
	public Compressor compressor;
	
	public ADXRS450_Gyro gyro;
	
	public PhoneCamera phoneCamera;
	public Servo visionAimServo;
	
	@Override
	protected void constructHardware() 
	{
		shooterMotorRight = new CANTalon(5);
		shooterMotorLeft = new CANTalon(6);
		
		leftDriveFront = new CANTalon(3);
		leftDriveBack = new CANTalon(4);
		rightDriveFront = new CANTalon(1);
		rightDriveBack = new CANTalon(2);
		
		gearMotors = new MotorGroup();
		gearMotors.addMotor(gearRollerMotor);
		
		intakeMotors = new MotorGroup();
		intakeMotors.addMotor(lowerIntakeMotor);
		intakeMotors.addMotor(shooterIntakeMotor);
		
		gearRollerBackDoor = new GearRollerBackDoor(doorPiston, gearPiston, gearMotors, gearInputSensor);
				
		leftDriveFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		rightDriveFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		leftDriveBack.changeControlMode(TalonControlMode.Follower);
		leftDriveBack.set(leftDriveFront.getDeviceID());
		
		rightDriveBack.changeControlMode(TalonControlMode.Follower);
		rightDriveBack.set(rightDriveFront.getDeviceID());
		
		gearshift = new TwoSpeedGearshift(false, gearshiftPistons);
		
		drive = new SRXTankDrive(leftDriveFront, rightDriveFront, (4 * Math.PI)*Length.in, 1, 23.70*Length.in, 28.45*Length.in);
		
		shooterMotorRight.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		shooterMotorLeft.changeControlMode(TalonControlMode.Follower);
		shooterMotorLeft.set(shooterMotorRight.getDeviceID());
		shooterMotorLeft.reverseOutput(true);
		
		shooter = new Shooter(this, shooterMotorRight, intakeMotors);

		
		powerDistPanel = new PowerDistributionPanel();
		compressor = new Compressor();
		
		gearshift.shiftToHigh();
		drive.setGearRatio(HIGH_GEAR_RATIO);
		
		rightJoystick = new Joystick(0);
		lmRight = new ListenerManager(rightJoystick);
		
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
		
		phoneCamera = new PhoneCamera(visionAimServo, new PIDConstants(.1, 0, 0), new PIDConstants(.1, 0, 0), new PIDConstants(.1, 0, 0));
		
		//lmLeft = new ListenerManager(leftJoystick);
		//addListenerManager(lmLeft);
		
		Log.info("MainFerb", "Activating Ferb");
        Log.info("MainFerb", "Hey! Where's Perry?");
	}

	@Override
	protected void setupListeners() {
		lmRight.nameControl(ControllerExtreme3D.TWIST, "MoveTurn");
		lmRight.nameControl(ControllerExtreme3D.JOYY, "MoveForwards");
		lmRight.nameControl(ControllerExtreme3D.THROTTLE, "Throttle");
		lmRight.nameControl(ControllerExtreme3D.TRIGGER, "FeedBalls");
				
		lmRight.nameControl(new POV(0), "IntakePOV");
		lmRight.nameControl(new Button(2), "GearShift");
		lmRight.nameControl(new Button(3), "ToggleFlywheel");
		lmRight.nameControl(new Button(4), "FullSpeed");
		
		lmRight.nameControl(new Button(5), "LoadGear");
		lmRight.nameControl(new Button(6), "DepositGear");
		
		lmRight.nameControl(new Button(7), "Climb");
		lmRight.nameControl(new Button(9), "StartCompressor");
		lmRight.nameControl(new Button(10), "StopCompressor");
		
		
		lmRight.addMultiListener(() -> {
			drive.arcadeDrive(.5 * lmRight.getAxis("MoveTurn"),
					lmRight.getAxis("MoveForwards"),
					-1 * lmRight.getAxis("Throttle"),
					lmRight.getButton("FullSpeed"));
			
			//Log.debug("MainFerb", String.format("MoveTurn: %f, MoveForwards: %f, Throttle: %f", lmRight.getAxis("MoveTurn"), lmRight.getAxis("MoveForwards"), lmRight.getAxis("Throttle")));
		
		}, "MoveTurn", "MoveForwards", "Throttle", "FullSpeed");
		
		lmRight.addButtonDownListener("ClearStickyFaults", powerDistPanel::clearStickyFaults);
		
		lmRight.addButtonDownListener("GearShift", () -> {
			gearshift.shiftToOtherGear();
			
			if(gearshift.isInHighGear())
			{
				drive.setGearRatio(HIGH_GEAR_RATIO);
			}
			else
			{
				drive.setGearRatio(LOW_GEAR_RATIO);
			}
		});
		
		lmRight.addButtonDownListener("StartCompressor", compressor::start);
		lmRight.addButtonDownListener("StopCompressor", compressor::stop);
		
		lmRight.addButtonDownListener("FeedBalls", shooter::enableIntakeRoller);
		lmRight.addButtonUpListener("FeedBalls", shooter::disableIntakeRoller);
		
		lmRight.addButtonDownListener("ToggleFlywheel", shooter::toggleFeed);
		
		lmRight.addButtonDownListener("LoadGear", gearRollerBackDoor::activateLoadingMode);
		lmRight.addButtonUpListener("LoadGear", gearRollerBackDoor::deactivateLoadingMode);
		
		lmRight.addButtonDownListener("DepositGear", gearRollerBackDoor::activateDepositingMode);
		lmRight.addButtonUpListener("DepositGear", gearRollerBackDoor::deactivateDepositingMode);
		
		lmRight.addButtonDownListener("Climb", () -> 
		{
			lifterMotor.set(1);	
		});
		lmRight.addButtonUpListener("Climb", ()->
		{
			lifterMotor.set(0);
		});
	
		lmRight.addListener("IntakePOV", (POVValue value) -> {
			switch(value.getDirectionValue())
			{
			// Sides or Center
			case 3:
			case 7:
			case 0:
				lowerIntakeMotor.set(0);
				break;
			// Forwards
			case 1:
			case 2:
			case 8:
				lowerIntakeMotor.set(1);
				break;
			// Backwards
			case 4:
			case 5:
			case 6:
				lowerIntakeMotor.set(-1);
				break;
			}
		});
		
		addListenerManager(lmRight);

	}

	@Override
	protected void teleopInit() {
		shooter.disableIntakeRoller();
		shooter.disableFlywheel();
	}

	@Override
	protected void autonomousInit() {
		
	}
	
	protected void constructAutoPrograms(GenericSendableChooser<CommandGroup> programChooser) {
		programChooser.addDefault("None", null);
		programChooser.addObject("Cross Baseline", new AutoCrossBaseline(this));
		programChooser.addObject("Place Left Gear", new AutoPlaceFarGear(this, Direction.LEFT));
		programChooser.addObject("Place Middle Gear", new AutoPlaceMiddleGear(this));
		programChooser.addObject("Place Right Gear", new AutoPlaceFarGear(this, Direction.RIGHT));
		programChooser.addObject("Trigger Hopper & Shoot", new AutoShootFromHopper(this));
	}
	
	@Override
	protected void updateDashboard() {
		SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
	}

}
