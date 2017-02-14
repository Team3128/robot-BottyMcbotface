package org.team3128.autonomous;

import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Auto program that uses dead-reckoning to drive over and trigger the hopper
 * 
 * NOTE: Starts backwards from just inside the key tape line
 * 
 * @author Narwhal
 *
 */
public class AutoTriggerHopper extends CommandGroup 
{
	public AutoTriggerHopper(MainFerb robot)
	{
		addSequential(robot.drive.new CmdMoveForward(-70 * Length.in, 5000, true));
		addSequential(robot.drive.new CmdInPlaceTurn(90, 5000, Direction.LEFT));
		
		addSequential(robot.drive.new CmdMoveForward(-70 * Length.in, 5000, true));
	}
}
