package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.util.units.Length;

import edu.wpi.first.wpilibj.VictorSP;

public class MainFerbPractice extends MainFerb {
	
	public MainFerbPractice() {
		super();
	}
	
	@Override
	public void constructHardware() 
	{	
		climberMotor = new MotorGroup(new VictorSP(0));
		climberMotor.invert();

		wheelDiameter = 3.85 * Length.in;

		gearshiftPistons = new Piston(1, 6);
		gearshiftPistons.invertPiston();
		
		gearRoller = new MotorGroup(new VictorSP(1));

		//gearPiston = new Piston(3, 4);
		//doorPiston = new Piston(2, 5);
		//gearInputSensor = new DigitalInput(5);
		//visionAimServo = new Servo(9);
		//gearMotors = new MotorGroup(new VictorSP(2));
		//floorIntakeMotor = new VictorSP(1);
						
		super.constructHardware();
		compressor.stop();


		leftDriveFront.reverseSensor(true);
		leftDriveFront.reverseOutput(true);
		rightDriveFront.reverseSensor(false);
		rightDriveFront.reverseOutput(false);
		
		
		//CameraServer cameraServer = CameraServer.getInstance();
		
//		UsbCamera camera = cameraServer.startAutomaticCapture(0);
//   	camera.setResolution(120, 80);
//		camera.setFPS(20);
		
		//cameraServer.startAutomaticCapture(0).setFPS(20);
	}
	
	@Override
	public void disabledPeriodic(){
		super.disabledPeriodic();
		leftDriveFront.configNominalOutputVoltage(1.5, -1.5);
		leftDriveFront.setAllowableClosedLoopErr(32);
		
		rightDriveFront.configNominalOutputVoltage(1.5, -1.5);
		rightDriveFront.setAllowableClosedLoopErr(32);
		
		armPivotMotor.configNominalOutputVoltage(.75, -.75);
		rightDriveFront.setAllowableClosedLoopErr(48);
		
		armPivotMotor.setNominalClosedLoopVoltage(12);

		drive.setRightSpeedScalar(1);
	}
}
