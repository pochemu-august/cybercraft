package com.cybercraft.hack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

/**
 * Базовый интерфейс быстрого взлома (Quickhack).
 */
public interface Quickhack {
	/**
	 * Уникальный идентификатор скрипта.
	 */
	Identifier id();

	/**
	 * Отображаемое имя скрипта.
	 */
	Component displayName();

	/**
	 * Стоимость применения в единицах RAM.
	 */
	int ramCost();

	/**
	 * Перезарядка в тиках (20 тиков = 1 секунда).
	 */
	int cooldownTicks();

	/**
	 * Может ли данный хак быть применен к сущности.
	 */
	default boolean canTargetEntity(LivingEntity entity) {
		return true;
	}

	/**
	 * Серверная логика выполнения хака по сущности.
	 *
	 * @param caster  игрок, вызвавший взлом
	 * @param target  целевой живой моб/игрок
	 * @return true, если выполнение прошло успешно
	 */
	boolean executeOnEntity(ServerPlayer caster, LivingEntity target);
}
