package com.windanesz.arcaneapprentices.command;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.data.PlayerData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class CommandResetApprenticeData extends CommandBase {


	public static final String COMMAND = "resetapprenticedata";

	public String getName() {
		return COMMAND;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getUnlocalizedName() + ".usage";
	}

	public static String getUnlocalizedName() {
		return "commands." + ArcaneApprentices.MODID + ":" + COMMAND;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] arguments, BlockPos pos) {

		if (arguments.length == 1) {
			return getListOfStringsMatchingLastWord(arguments, server.getOnlinePlayerNames());
		}
		return super.getTabCompletions(server, sender, arguments, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException {

		if (arguments.length != getRequiredArgsCount()) {
			throw new WrongUsageException(getUsage(sender));
		}

		EntityPlayer targetPlayer = getPlayer(server, sender, arguments[0]);

		TextComponentTranslation textComponentTranslation;
		if (targetPlayer != null) {
			PlayerData.clearApprentices(targetPlayer);
		}
	}

	public static int getRequiredArgsCount() { return 1; }

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}
}
