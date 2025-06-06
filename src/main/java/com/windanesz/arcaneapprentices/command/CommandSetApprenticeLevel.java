package com.windanesz.arcaneapprentices.command;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.data.PlayerData;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.arcaneapprentices.handler.XpProgression;
import com.windanesz.wizardryutils.tools.WizardryUtilsTools;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandSetApprenticeLevel extends CommandBase {

	public static final String COMMAND = "setapprenticelevel";

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
			// Tab completion for apprentice name
			if (sender instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) sender;
				List<String> apprenticeNames = new ArrayList<>();

				List<UUID> apprenticeUUIDs = PlayerData.getApprentices(player);
				for (UUID uuid : apprenticeUUIDs) {
					Entity entity = ((WorldServer) player.world).getEntityFromUuid(uuid);
					if (entity instanceof EntityWizardInitiate) {
						apprenticeNames.add(entity.getName());
					}
				}

				return getListOfStringsMatchingLastWord(arguments, apprenticeNames);
			}
		} else if (arguments.length == 2) {
			// Tab completion for level
			List<String> levels = new ArrayList<>();
			for (int i = 1; i <= XpProgression.getMaxLevel(); i++) {
				levels.add(String.valueOf(i));
			}
			return getListOfStringsMatchingLastWord(arguments, levels);
		}

		return super.getTabCompletions(server, sender, arguments, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException {
		if (arguments.length != getRequiredArgsCount()) {
			throw new WrongUsageException(getUsage(sender));
		}

		if (!(sender instanceof EntityPlayer)) {
			throw new CommandException("commands." + ArcaneApprentices.MODID + ":player_only");
		}

		EntityPlayer player = (EntityPlayer) sender;
		String apprenticeName = arguments[0];
		int targetLevel;

		try {
			targetLevel = Integer.parseInt(arguments[1]);
		} catch (NumberFormatException e) {
			throw new CommandException("commands." + ArcaneApprentices.MODID + ":invalid_level");
		}

		if (targetLevel < 1 || targetLevel > XpProgression.getMaxLevel()) {
			throw new CommandException("commands." + ArcaneApprentices.MODID + ":level_out_of_range", 1, XpProgression.getMaxLevel());
		}

		EntityWizardInitiate apprentice = findApprenticeByName(player, apprenticeName);
		if (apprentice == null) {
			throw new CommandException("commands." + ArcaneApprentices.MODID + ":apprentice_not_found", apprenticeName);
		}

		// Set the level and update XP
		int currentLevel = apprentice.getLevel();
		apprentice.setLevel(targetLevel);

		// Set the XP to what's needed for this level
		double xpForLevel = XpProgression.calculateTotalXpRequired(targetLevel);
		apprentice.setXp((int) xpForLevel);

		WizardryUtilsTools.sendMessage(player,
				"commands." + ArcaneApprentices.MODID + ":level_set_success",
				false,
				apprentice.getName(),
				targetLevel);
	}

	public static int getRequiredArgsCount() {
		return 2;
	}

	/**
	 * Finds an apprentice by name for the given player
	 *
	 * @param player The player to check apprentices for
	 * @param name   The name of the apprentice to find
	 * @return The apprentice entity or null if not found
	 */
	@Nullable
	private EntityWizardInitiate findApprenticeByName(EntityPlayer player, String name) {
		List<UUID> apprenticeUUIDs = PlayerData.getApprentices(player);

		for (UUID uuid : apprenticeUUIDs) {
			Entity entity = ((WorldServer) player.world).getEntityFromUuid(uuid);
			if (entity instanceof EntityWizardInitiate && entity.getName().equalsIgnoreCase(name)) {
				return (EntityWizardInitiate) entity;
			}
		}

		return null;
	}
}
