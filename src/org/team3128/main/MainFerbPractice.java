package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.util.units.Length;
import org.team3128.narwhalvision.NarwhalVisionReceiver;

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

		wheelDiameter = 3.73 * Length.in;

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

		leftDriveFront.setInverted(true);
		
		//CameraServer cameraServer = CameraServer.getInstance();
		
//		UsbCamera camera = cameraServer.startAutomaticCapture(0);
//   	camera.setResolution(120, 80);
//		camera.setFPS(20);
		
		//cameraServer.startAutomaticCapture(0).setFPS(20);
		
		NarwhalVisionReceiver phone = new NarwhalVisionReceiver();
	}
	
	@Override
	public void disabledPeriodic(){
		super.disabledPeriodic();
		
		drive.setRightSpeedScalar(1);
	}
}
