package org.team3128.autonomous;

import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoPlaceFarGear extends CommandGroup
{
	public AutoPlaceFarGear(MainFerb robot, Direction side){
		
		double net_horizontal = (125-34-14.75) * Length.in;
		double robot_half_length = 18 * Length.in;
		double arc_turn_reduction = (robot.drive.wheelBase * Length.in) / (2 * Math.sqrt(3));
		double segment_one = 129 * Length.in - robot_half_length - (net_horizontal / Math.sqrt(3)) - arc_turn_reduction;
		double segment_two = ((2 / Math.sqrt(3)) * net_horizontal) - robot_half_length - arc_turn_reduction;
		
		double segment_one_inch_offset = 2 * Length.in;
		double segment_two_inch_offset = 2 * Length.in;
		
		addSequential(robot.drive.new CmdMoveForward(-1 * (segment_one + segment_one_inch_offset), 4000, 0.5));
		addSequential(robot.drive.new CmdArcTurn(-60, 1500, (side == Direction.RIGHT) ? Direction.LEFT : Direction.RIGHT));
		addSequential(robot.drive.new CmdMoveForward(-1 * (segment_two + segment_two_inch_offset), 3000, 0.5));
		addSequential(robot.gearRollerBackDoor.new CmdSetDepositingMode(true));
		addSequential(robot.drive.new CmdMoveForward(24 * Length.in, 6000, 0.5));
		addSequential(robot.gearRollerBackDoor.new CmdSetDepositingMode(false));
	}
}
