package org.team3128.autonomous;

import org.team3128.autonomous.commands.CmdAimToHighGoal;
import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Autonomous which drives over to the hopper and releases it, then shoots balls at the boiler
 * 
 * NOTE: Starts backwards from just inside the key tape line
 * 
 * @author Narwhal
 *
 */
public class AutoShootFromHopper extends CommandGroup 
{
	public AutoShootFromHopper(MainFerb robot)
	{
		addSequential(new AutoTriggerHopper(robot));
		
		addSequential(robot.drive.new CmdMoveForward(1 * Length.ft, 5000, true));
		addSequential(robot.drive.new CmdInPlaceTurn(75, 5000, Direction.LEFT));
		
		addSequential(new CmdAimToHighGoal(robot, 6000));
		addSequential(robot.shooter.new CmdShoot(10000));
	}
	
}
