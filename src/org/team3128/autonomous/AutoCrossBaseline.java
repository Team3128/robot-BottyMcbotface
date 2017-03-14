package org.team3128.autonomous;

import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoCrossBaseline extends CommandGroup {
	public AutoCrossBaseline(MainFerb robot) 
	{
		addSequential(robot.drive.new CmdMoveForward(-84 * Length.in, 15000, true));
	}
}
