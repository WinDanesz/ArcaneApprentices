package com.windanesz.apprenticearcana.packet;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.client.gui.AAGuiHandler;
import com.windanesz.apprenticearcana.data.Speech;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import com.windanesz.apprenticearcana.inventory.ContainerWizardInfo;
import com.windanesz.apprenticearcana.inventory.ContainerWizardInitiateInventory;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

				ItemStack wand = player.getHeldItemMainhand();

				if (!(wand.getItem() instanceof ISpellCastingItem)) {
					wand = player.getHeldItemOffhand();
				}

				switch (message.controlType) {

					case FOLLOW_BUTTON:

						if (!(player.openContainer instanceof ContainerWizardInitiateInventory)) {
							Wizardry.logger.warn("Received a PacketControlInput, but the player that sent it was not " +
									"currently interacting with an NPC. This should not happen!");
						} else {
							((ContainerWizardInitiateInventory) player.openContainer).wizard.setTask(EntityWizardInitiate.Task.FOLLOW);
							((ContainerWizardInitiateInventory) player.openContainer).wizard.sayImmediately(
									new TextComponentTranslation(Speech.WIZARD_FOLLOWING_PLAYER.getRandom(), player.getDisplayName()));
							player.closeScreen();
						}

						break;

					case STAY_BUTTON:

						if (!(player.openContainer instanceof ContainerWizardInitiateInventory)) {
							Wizardry.logger.warn("Received a PacketControlInput, but the player that sent it was not " +
									"currently interacting with an NPC. This should not happen!");
						} else {
							((ContainerWizardInitiateInventory) player.openContainer).wizard.setTask(EntityWizardInitiate.Task.STAY);
							((ContainerWizardInitiateInventory) player.openContainer).wizard.sayImmediately(
									new TextComponentTranslation(Speech.WIZARD_PLAYER_CLICK_HOLD_POSITION_BUTTON.getRandom()));
							player.closeScreen();
						}

						break;

					case STUDY_BUTTON:

						if (!(player.openContainer instanceof ContainerWizardInitiateInventory)) {
							Wizardry.logger.warn("Received a PacketControlInput, but the player that sent it was not " +
									"currently interacting with an NPC. This should not happen!");
						} else {
							((ContainerWizardInitiateInventory) player.openContainer).wizard.setTask(EntityWizardInitiate.Task.STUDY);
							((ContainerWizardInitiateInventory) player.openContainer).wizard.sayImmediately(
									new TextComponentTranslation(Speech.WIZARD_PLAYER_CLICK_STUDY_BUTTON.getRandom()));
							player.closeScreen();
						}

						break;
					case INFO_BUTTON:

						if (!(player.openContainer instanceof ContainerWizardInitiateInventory)) {
							Wizardry.logger.warn("Received a PacketControlInput, but the player that sent it was not " +
									"currently interacting with an NPC. This should not happen!");
						} else {
							player.openGui(ApprenticeArcana.MODID, AAGuiHandler.WIZARD_STATS_GUI, player.world,
									((ContainerWizardInitiateInventory) player.openContainer).wizard.getEntityId(), 0, 0);
						}

						break;

					case SPELL_TOGGLE:

						if (!(player.openContainer instanceof ContainerWizardInfo)) {
							Wizardry.logger.warn("Received a PacketControlInput, but the player that sent it was not " +
									"currently interacting with an NPC. This should not happen!");
						} else {
							((ContainerWizardInfo) player.openContainer).wizard.toggleSpellDisablement(message.spell);
						}

						break;
				}
			});
		}

		return null;
	}

	public enum ControlType {
		FOLLOW_BUTTON, STAY_BUTTON, STUDY_BUTTON, INFO_BUTTON, SPELL_TOGGLE
	}

	public static class Message implements IMessage {

		private ControlType controlType;
		private Spell spell = Spells.none;

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

		@Override
		public void fromBytes(ByteBuf buf) {
			// The order is important
			this.spell = Spell.byNetworkID(buf.readInt());
			this.controlType = ControlType.values()[buf.readInt()];
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(spell.networkID());
			buf.writeInt(controlType.ordinal());
		}
	}
}
