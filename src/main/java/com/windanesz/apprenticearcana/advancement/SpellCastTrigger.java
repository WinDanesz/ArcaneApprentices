package com.windanesz.apprenticearcana.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import electroblob.wizardry.advancement.SpellPredicate;
import electroblob.wizardry.spell.Spell;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Advancement trigger which is triggered when a spell is cast. The majority of any
 * ICriterionTrigger class is just boilerplate, and this is no exception. */
public class SpellCastTrigger implements ICriterionTrigger<SpellCastTrigger.Instance> {

	private final ResourceLocation id;
	private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

	public SpellCastTrigger(ResourceLocation id){
		this.id = id;
	}

	public ResourceLocation getId(){
		return this.id;
	}

	public void addListener(PlayerAdvancements advancements, Listener<Instance> listener){

		Listeners listeners = this.listeners.get(advancements);

		if(listeners == null){
			listeners = new Listeners(advancements);
			this.listeners.put(advancements, listeners);
		}

		listeners.add(listener);
	}

	public void removeListener(PlayerAdvancements advancements, Listener<Instance> listener){

		Listeners listeners = this.listeners.get(advancements);

		if(listeners != null){
			listeners.remove(listener);

			if(listeners.isEmpty()){
				this.listeners.remove(advancements);
			}
		}
	}

	public void removeAllListeners(PlayerAdvancements advancements){
		this.listeners.remove(advancements);
	}

	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context){
		return new Instance(this.id, SpellPredicate.deserialize(json.get("spell")),
				ItemPredicate.deserialize(json.get("item")));
	}

	public void trigger(EntityPlayerMP player, Spell spell, ItemStack stack){

		Listeners listeners = this.listeners.get(player.getAdvancements());

		if(listeners != null){
			listeners.trigger(spell, stack);
		}
	}

	public static class Instance extends AbstractCriterionInstance {

		private final SpellPredicate spell;
		private final ItemPredicate item;

		public Instance(ResourceLocation criterion, SpellPredicate spell, ItemPredicate item){
			super(criterion);
			this.spell = spell;
			this.item = item;
		}

		public boolean test(Spell spell, ItemStack stack){
			return this.spell.test(spell) && item.test(stack);
		}
	}

	static class Listeners {

		private final PlayerAdvancements playerAdvancements;
		private final Set<Listener<Instance>> listeners = Sets.newHashSet();

		public Listeners(PlayerAdvancements advancements){
			this.playerAdvancements = advancements;
		}

		public boolean isEmpty(){
			return this.listeners.isEmpty();
		}

		public void add(Listener<Instance> listener){
			this.listeners.add(listener);
		}

		public void remove(Listener<Instance> listener){
			this.listeners.remove(listener);
		}

		public void trigger(Spell spell, ItemStack stack){

			List<Listener<Instance>> list = null;

			for(Listener<Instance> listener : this.listeners){

				if(listener.getCriterionInstance().test(spell, stack)){

					if(list == null){
						list = Lists.newArrayList();
					}

					list.add(listener);
				}
			}

			if(list != null){
				for(Listener<Instance> listener : list){
					listener.grantCriterion(this.playerAdvancements);
				}
			}
		}
	}
}