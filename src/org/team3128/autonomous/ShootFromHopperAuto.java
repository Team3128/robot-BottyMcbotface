package org.team3128.autonomous;

import org.team3128.autonomous.commands.CmdAimToHighGoal;
import org.team3128.common.util.datatypes.PIDConstants;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Autonomous which drives over to the hopper and releases it, then shoots balls at the shooter
 * @author Narwhal
 *
 */
public class ShootFromHopperAuto extends CommandGroup 
{
	public ShootFromHopperAuto(MainFerb robot)
	{
		addSequential(robot.drive.new CmdMoveForward(9 * Length.ft, 10000, .5));
		addSequential(new CmdAimToHighGoal(robot, new PIDConstants(.1, 0, 0), 6000));
	}
	
}
