package com.cybercraft.registry;

import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;

import com.cybercraft.Cybercraft;
import com.cybercraft.item.CyberdeckItem;
import com.cybercraft.item.component.CyberdeckSpec;

/** Регистрация предметов мода. */
public final class ModItems {
	/** Ключ ассета 3D-экипировки для рендеринга на голове игрока. */
	public static final ResourceKey<EquipmentAsset> CYBERDECK_EQUIPMENT_ASSET =
			ResourceKey.create(EquipmentAssets.ROOT_ID, Cybercraft.id("cyberdeck"));

	public static final Item CYBERDECK = register("cyberdeck", CyberdeckItem::new, new Item.Properties()
			.stacksTo(1)
			.rarity(Rarity.EPIC)
			// Характеристики деки по умолчанию (MK1)
			.component(ModComponents.CYBERDECK_SPEC, CyberdeckSpec.MK1)
			// Компонент экипировки: надевается на голову в слот шлема с привязкой 3D ассета и звука
			.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
					.setAsset(CYBERDECK_EQUIPMENT_ASSET)
					.setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC)
					.setSwappable(true)
					.build()));

	private ModItems() {
	}

	/**
	 * Регистрация предмета с предварительной привязкой ResourceKey в Properties.
	 */
	private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Cybercraft.id(name));
		T item = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static void init() {
	}
}
