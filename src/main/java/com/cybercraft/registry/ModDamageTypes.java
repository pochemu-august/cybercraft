package com.cybercraft.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

import com.cybercraft.Cybercraft;

/**
 * Ключи типов урона. Сами типы урона — data-driven, лежат в
 * {@code data/cybercraft/damage_type/*.json}.
 */
public final class ModDamageTypes {
	public static final ResourceKey<DamageType> SHORT_CIRCUIT =
			ResourceKey.create(Registries.DAMAGE_TYPE, Cybercraft.id("short_circuit"));

	private ModDamageTypes() {
	}

	/**
	 * Создаёт источник электрического урона, приписанный атакующему (чтобы мобы агрились
	 * на нетраннера и работали сообщения о смерти / кредит за убийство).
	 */
	public static DamageSource shortCircuit(ServerLevel level, Entity attacker) {
		Holder<DamageType> type = level.registryAccess()
				.lookupOrThrow(Registries.DAMAGE_TYPE)
				.getOrThrow(SHORT_CIRCUIT);
		return new DamageSource(type, attacker);
	}
}
