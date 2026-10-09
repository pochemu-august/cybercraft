package com.cybercraft.hack;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import com.cybercraft.Cybercraft;
import com.cybercraft.registry.ModDamageTypes;

/**
 * Скрипт "Short Circuit" (Короткое замыкание).
 * Наносит мгновенный электрический урон, оглушает моба (эффект замедления)
 * и создает сноп электрических искр со звуковым эффектом.
 */
public class ShortCircuitHack implements Quickhack {
	public static final Identifier ID = Cybercraft.id("short_circuit");
	private static final float BASE_DAMAGE = 12.0F; // 6 сердец чистого урона
	private static final int RAM_COST = 4;
	private static final int COOLDOWN = 60; // 3 секунды

	@Override
	public Identifier id() {
		return ID;
	}

	@Override
	public Component displayName() {
		return Component.translatable("quickhack.cybercraft.short_circuit");
	}

	@Override
	public int ramCost() {
		return RAM_COST;
	}

	@Override
	public int cooldownTicks() {
		return COOLDOWN;
	}

	@Override
	public boolean executeOnEntity(ServerPlayer caster, LivingEntity target) {
		ServerLevel level = caster.level();

		// 1. Формируем типизированный урон Short Circuit, привязанный к игроку
		DamageSource source = ModDamageTypes.shortCircuit(level, caster);
		boolean damaged = target.hurtServer(level, source, BASE_DAMAGE);

		if (damaged) {
			// 2. Эффект перегрузки нейросети / сервоприводов (Slowness II на 4 секунды)
			target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1), caster);

			// 3. Звук деактивации ICE / электрического разряда
			level.playSound(
					null,
					target.getX(),
					target.getY(),
					target.getZ(),
					SoundEvents.BEACON_DEACTIVATE,
					SoundSource.PLAYERS,
					1.2F,
					1.8F
			);

			// 4. Частицы электрического разряда вокруг хитбокса цели
			level.sendParticles(
					ParticleTypes.ELECTRIC_SPARK,
					target.getX(),
					target.getY() + (target.getBbHeight() / 2.0),
					target.getZ(),
					25,
					target.getBbWidth() / 2.0,
					target.getBbHeight() / 3.0,
					target.getBbWidth() / 2.0,
					0.15
			);
			return true;
		}

		return false;
	}
}
