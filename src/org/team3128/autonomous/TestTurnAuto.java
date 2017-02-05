package org.team3128.autonomous;

import org.team3128.common.autonomous.movement.CmdTurnGyro;
import org.team3128.common.util.datatypes.PIDConstants;
import org.team3128.common.util.enums.Direction;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * It's a turn-off, folks!  Our three contestants this time are the perennial champion InPlaceTurn,
 * the dark horse ArcTurn, and the up-and-coming crowd favorite, CmdTurnGyro!
 * 
 * Who will win?  Well, that's for the judges to decide!
 * @author Jamie
 *
 */
public class TestTurnAuto extends CommandGroup
{
	public TestTurnAuto(MainFerb robot)
	{
		// turn 180 degrees to the right, then back to the left in smaller increments.  Judges will decide how far off the robot is from its original position.
		addSequential(robot.drive.new CmdInPlaceTurn(180, 5000, Direction.RIGHT));
		addSequential(robot.drive.new CmdInPlaceTurn(25, 2000, Direction.LEFT));
		addSequential(robot.drive.new CmdInPlaceTurn(65, 3000, Direction.LEFT));
		addSequential(robot.drive.new CmdInPlaceTurn(90, 4000, Direction.LEFT));

		addSequential(robot.drive.new CmdArcTurn(180, 5000, .5, Direction.RIGHT));
		addSequential(robot.drive.new CmdArcTurn(25, 2000, .5, Direction.LEFT));
		addSequential(robot.drive.new CmdArcTurn(65, 3000, .5, Direction.LEFT));
		addSequential(robot.drive.new CmdArcTurn(90, 4000, .5, Direction.LEFT));
		
		PIDConstants gyroPIDConstants = new PIDConstants(.001, 0, 0);
		
		addSequential(new CmdTurnGyro(robot.gyro, robot.drive, -180, .5, gyroPIDConstants, 5000));
		addSequential(new CmdTurnGyro(robot.gyro, robot.drive, 25, .5, gyroPIDConstants, 5000));
		addSequential(new CmdTurnGyro(robot.gyro, robot.drive, 65, .5, gyroPIDConstants, 5000));
		addSequential(new CmdTurnGyro(robot.gyro, robot.drive, 90, .5, gyroPIDConstants, 5000));

	}
}
