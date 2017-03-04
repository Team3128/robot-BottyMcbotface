package org.team3128.autonomous;

import org.team3128.common.drive.SRXTankDrive.CmdMoveForward;
import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;
import org.team3128.mechanisms.GearRollerBackDoor.CmdSetDepositingMode;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoPlaceFarGear extends CommandGroup
{
	public AutoPlaceFarGear(MainFerb robot, Direction dir){
		//real numbers still need to be added for the move and turn commands
		addSequential(robot.drive.new CmdMoveForward(-150 * Length.in, 6000, 0.5));
		addSequential(robot.drive.new CmdInPlaceTurn(62, 1000, dir));
		addSequential(robot.drive.new CmdMoveForward(-65 * Length.in, 3000, 0.50));
		addSequential(robot.gearRollerBackDoor.new CmdSetDepositingMode(true));
		addSequential(robot.drive.new CmdMoveForward(24 * Length.in, 6000, 0.5));
		addSequential(robot.gearRollerBackDoor.new CmdSetDepositingMode(false));
	}
}
