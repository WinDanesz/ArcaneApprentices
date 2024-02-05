package com.windanesz.arcaneapprentices.packet;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.Utils;
import com.windanesz.arcaneapprentices.client.gui.AAGuiHandler;
import com.windanesz.arcaneapprentices.data.JourneyType;
import com.windanesz.arcaneapprentices.data.PlayerData;
import com.windanesz.arcaneapprentices.data.Speech;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.arcaneapprentices.handler.JourneySurvivalHandler;
import com.windanesz.arcaneapprentices.inventory.ContainerWizardBase;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.Location;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>[Client -> Server]</b> This packet is for control events such as buttons in GUIs and key presses.
 */
public class PacketControlInput implements IMessageHandler<PacketControlInput.Message, IMessage> {

	@Override
	public IMessage onMessage(Message message, MessageContext ctx) {

		// Just to make sure that the side is correct
		if (ctx.side.isServer()) {

			final EntityPlayerMP player = ctx.getServerHandler().player;

			player.getServerWorld().addScheduledTask(() -> {

				switch (message.controlType) {

					case FOLLOW_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							((ContainerWizardBase) player.openContainer).getWizard().setTask(EntityWizardInitiate.Task.FOLLOW);
							((ContainerWizardBase) player.openContainer).getWizard().sayImmediately(
									new TextComponentTranslation(Speech.WIZARD_FOLLOWING_PLAYER.getRandom(), player.getDisplayName()));
							player.closeScreen();
						}

						break;

					case STAY_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							((ContainerWizardBase) player.openContainer).getWizard().setTask(EntityWizardInitiate.Task.STAY);
							((ContainerWizardBase) player.openContainer).getWizard().sayImmediately(
									new TextComponentTranslation(Speech.WIZARD_PLAYER_CLICK_HOLD_POSITION_BUTTON.getRandom()));
							player.closeScreen();
						}

						break;

					case STUDY_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							((ContainerWizardBase) player.openContainer).getWizard().setTask(EntityWizardInitiate.Task.STUDY);
						//	((ContainerWizardBase) player.openContainer).getWizard().resetStudyProgress();
							((ContainerWizardBase) player.openContainer).getWizard().sayImmediately(
									new TextComponentTranslation(Speech.WIZARD_PLAYER_CLICK_STUDY_BUTTON.getRandom()));
							player.closeScreen();
						}

						break;

					case OPEN_STATS_GUI_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							player.openGui(ArcaneApprentices.MODID, AAGuiHandler.WIZARD_STATS_GUI, player.world,
									((ContainerWizardBase) player.openContainer).getWizard().getEntityId(), 0, 0);
						}

						break;

					case OPEN_DISMISS_WIZARD_GUI_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							player.openGui(ArcaneApprentices.MODID, AAGuiHandler.WIZARD_DISMISS_CONFIRM_GUI, player.world,
									((ContainerWizardBase) player.openContainer).getWizard().getEntityId(), 0, 0);
						}

						break;

					case SPELL_TOGGLE_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							((ContainerWizardBase) player.openContainer).getWizard().toggleSpellDisablement(message.spell);
						}

						break;

					case SET_HOME_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							EntityWizardInitiate wizard = ((ContainerWizardBase) player.openContainer).getWizard();
							wizard.setHome(new Location(wizard.getPos(), wizard.dimension));
							wizard.sayImmediately(new TextComponentTranslation(Speech.WIZARD_SET_HOME.getRandom()));
							player.closeScreen();
						}

						break;

					case GO_HOME_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							((ContainerWizardBase) player.openContainer).getWizard().setTask(EntityWizardInitiate.Task.GO_HOME);
							((ContainerWizardBase) player.openContainer).getWizard().sayImmediately(new TextComponentTranslation(Speech.WIZARD_GO_HOME.getRandom()));
							player.closeScreen();
						}

						break;

					case IDENTIFY_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							((ContainerWizardBase) player.openContainer).getWizard().setTask(EntityWizardInitiate.Task.IDENTIFY);
							((ContainerWizardBase) player.openContainer).getWizard().resetStudyProgress();
							((ContainerWizardBase) player.openContainer).getWizard().sayImmediately(new TextComponentTranslation(Speech.WIZARD_TASKED_TO_IDENTIFY_SPELL.getRandom()));
							player.closeScreen();
						}

						break;

					case OPEN_JOURNEY_GUI_BUTTON:
						if (player.openContainer instanceof ContainerWizardBase) {
							player.openGui(ArcaneApprentices.MODID, AAGuiHandler.WIZARD_ADVENTURING_GUI, player.world,
									((ContainerWizardBase) player.openContainer).getWizard().getEntityId(), 0, 0);
						}

						break;

					case OPEN_WIZARD_INVENTORY_BUTTON:
						if (player.openContainer instanceof ContainerWizardBase) {
							player.openGui(ArcaneApprentices.MODID, AAGuiHandler.WIZARD_INVENTORY_GUI, player.world,
									((ContainerWizardBase) player.openContainer).getWizard().getEntityId(), 0, 0);
						}
						break;

					case JOURNEY_CONFIRM_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							EntityWizardInitiate wizard = ((ContainerWizardBase) player.openContainer).getWizard();
							if (!wizard.verifyWandManaRequirementForJourney(message.journeyType)) {
								wizard.sayImmediately(new TextComponentTranslation("message.arcaneapprentices:no_wand_with_mana_for_journey"));
							} else {
								wizard.setJourneyType(message.journeyType);
								wizard.goOnJourney();
							}
							player.closeScreen();
						}

						break;

					case DISMISS_WIZARD_BUTTON:

						if (player.openContainer instanceof ContainerWizardBase) {
							List<ItemStack> stackList = new ArrayList<>();
							EntityWizardInitiate wizard = ((ContainerWizardBase) player.openContainer).getWizard();
							for (int i = 0; i < wizard.inventory.getSizeInventory(); i++) {
								stackList.add(wizard.inventory.getStackInSlot(i).copy());
							}
							for (ItemStack stack : stackList) {
								Utils.giveStackToPlayer(player, stack);
							}
							PlayerData.removeApprentice(player, wizard);
							player.world.removeEntityDangerously(wizard);
							player.closeScreen();
						}

						break;

				}
			});
		}

		return null;
	}

	public enum ControlType {
		FOLLOW_BUTTON, STAY_BUTTON, STUDY_BUTTON, OPEN_STATS_GUI_BUTTON, SPELL_TOGGLE_BUTTON, OPEN_DISMISS_WIZARD_GUI_BUTTON, DISMISS_WIZARD_BUTTON,
		CLOSE_WINDOW_BUTTON, SET_HOME_BUTTON, GO_HOME_BUTTON, IDENTIFY_BUTTON, OPEN_JOURNEY_GUI_BUTTON, OPEN_WIZARD_INVENTORY_BUTTON, JOURNEY_CONFIRM_BUTTON
	}

	public static class Message implements IMessage {

		private ControlType controlType;
		private Spell spell = Spells.none;
		private JourneyType journeyType = JourneyType.NOT_ADVENTURING;

		// This constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
		public Message() {
		}

		public Message(ControlType type) {
			this.controlType = type;
		}

		public Message(ControlType type, Spell spell) {
			this.controlType = type;
			this.spell = spell;
		}

		public Message(ControlType type, JourneyType journeyType) {
			this.controlType = type;
			this.journeyType = journeyType;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			// The order is important
			this.controlType = ControlType.values()[buf.readInt()];
			this.spell = Spell.byNetworkID(buf.readInt());
			this.journeyType = JourneyType.values()[buf.readInt()];
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(controlType.ordinal());
			buf.writeInt(spell.networkID());
			buf.writeInt(journeyType.ordinal());
		}
	}
}
