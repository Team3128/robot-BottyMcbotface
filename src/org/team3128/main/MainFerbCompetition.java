package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.util.units.Length;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.VictorSP;

public class MainFerbCompetition extends MainFerb {
	
	public MainFerbCompetition() {
		super();
	}
	
	@Override
	public void constructHardware() 
	{
		climberMotor = new MotorGroup(new VictorSP(0));
		climberMotor.invert();

		wheelDiameter = 4.2 * Length.in;
		
		gearshiftPistons = new Piston(0, 7);

		lightSignal = new DigitalOutput(0);
		
		gearRoller = new MotorGroup(new VictorSP(2));
		
		//gearMotors = new MotorGroup(new VictorSP(2));
		//gearMotors.invert();
				
		//floorIntakeMotor = new VictorSP(1);
		//gearPiston = new Piston(2, 5);
		//doorPiston = new Piston(1, 6);
		//gearInputSensor = new DigitalInput(5);
		//visionAimServo = new Servo(9);
		
		super.constructHardware();
		
		leftDriveFront.reverseSensor(true);
		leftDriveFront.reverseOutput(true);
		
		drive.setReversedAutonomous(true);
		
		
		CameraServer cameraServer = CameraServer.getInstance();
		
		UsbCamera camera = cameraServer.startAutomaticCapture(0);
		camera.setFPS(10);
		camera.setResolution(480, 320);
	}
	
	@Override
	protected void disabledPeriodic()
	{
		super.disabledPeriodic();
		leftDriveFront.configNominalOutputVoltage(1.5, -1.5);
		leftDriveFront.setAllowableClosedLoopErr(32);
		
		rightDriveFront.configNominalOutputVoltage(1.5, -1.5);
		rightDriveFront.setAllowableClosedLoopErr(32);
		
		armPivotMotor.configNominalOutputVoltage(.75, -.75);
		armPivotMotor.setNominalClosedLoopVoltage(12);
		
		drive.setRightSpeedScalar(1.0);
		drive.setLeftSpeedScalar(1.0);
	}
}
