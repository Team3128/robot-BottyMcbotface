package org.team3128.autonomous;

import org.team3128.common.util.Log;
import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoPlaceFarGear extends CommandGroup
{
	public AutoPlaceFarGear(MainFerb robot, Direction side, Alliance alliance){
		double side_distance;
		if (alliance == Alliance.Blue)
		{
			if (side == Direction.LEFT)
			{
				side_distance = 122 * Length.in;
			}
			else {
				side_distance = 128 * Length.in;
			}
		}
		else {
			side_distance = 125 * Length.in;
		}
		
		double side_peg_offset;
		if (alliance == Alliance.Blue)
		{
			side_peg_offset = 32.5 * Length.in;
		}
		else {
			side_peg_offset = 33.5 * Length.in;
		}
		
		double robot_width = robot.drive.wheelBase * Length.in;
		double robot_length = 36 * Length.in;
		
		double effective_delta_horiz = side_distance - robot_width;
		double arc_turn_reduction = robot_width / 4;
		double segment_one = 130 * Length.in - ((effective_delta_horiz - side_peg_offset) / Math.sqrt(3)) - robot_length - arc_turn_reduction;
		double segment_two = (effective_delta_horiz - side_peg_offset) * 4/(Math.sqrt(3));
		
		double segment_one_inch_offset = (alliance == Alliance.Red) ? -9 * Length.in : -8 * Length.in;
		double segment_two_inch_offset = 1 * Length.in;
		
		Log.debug("AutoPlaceFarGear", "segment one:" + segment_one + ", segment two: " + segment_two);
		
		addSequential(robot.drive.new CmdMoveForward(segment_one + segment_one_inch_offset, 4000, 0.5));
		addSequential(robot.drive.new CmdArcTurn(65, 3000, side));
		addSequential(robot.drive.new CmdMoveForward(segment_two + segment_two_inch_offset, 3000, 0.5));
		addSequential(robot.gearShovel.new CmdDepositGear(robot));
	}
}
