package com.cybercraft.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

import com.cybercraft.item.component.CyberdeckSpec;
import com.cybercraft.registry.ModComponents;
import com.cybercraft.registry.ModItems;

/**
 * Кибердека (Нейровизор) — носимый интерфейс нетраннера.
 * Может надеваться на голову в слот шлема (EquipmentSlot.HEAD) или удерживаться в руке.
 */
public class CyberdeckItem extends Item {

	public CyberdeckItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player user, InteractionHand hand) {
		ItemStack stackInHand = user.getItemInHand(hand);
		ItemStack headStack = user.getItemBySlot(EquipmentSlot.HEAD);

		// Если на голове пусто или игрок зажал Shift — экипируем/свапаем визор на голову через ванильный Equippable
		if (headStack.isEmpty() || user.isShiftKeyDown()) {
			return super.use(level, user, hand);
		}

		// Если визор уже на голове или игрок хочет удерживать прицел в руке — включаем сканирование
		user.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		// Анимация поднесения визора к глазам
		return ItemUseAnimation.SPYGLASS;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		// Длительность непрерывного удержания сканера
		return 72000;
	}

	/**
	 * Проверяет, экипирован ли активный визор на сущности (на голове или в любой руке).
	 */
	public static boolean isEquipped(LivingEntity entity) {
		return !getEquippedCyberdeck(entity).isEmpty();
	}

	/**
	 * Находит стек активной кибердеки (приоритет: слот головы -> главная рука -> вторая рука).
	 */
	public static ItemStack getEquippedCyberdeck(LivingEntity entity) {
		ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
		if (head.is(ModItems.CYBERDECK)) {
			return head;
		}
		ItemStack main = entity.getMainHandItem();
		if (main.is(ModItems.CYBERDECK)) {
			return main;
		}
		ItemStack off = entity.getOffhandItem();
		if (off.is(ModItems.CYBERDECK)) {
			return off;
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Получить аппаратные характеристики данной деки из Data Component.
	 */
	public static CyberdeckSpec getSpec(ItemStack stack) {
		CyberdeckSpec spec = stack.get(ModComponents.CYBERDECK_SPEC);
		return spec != null ? spec : CyberdeckSpec.MK1;
	}
}
