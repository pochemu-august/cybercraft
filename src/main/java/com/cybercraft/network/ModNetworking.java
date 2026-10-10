package com.cybercraft.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import com.cybercraft.Cybercraft;
import com.cybercraft.hack.HackTarget;
import com.cybercraft.hack.Quickhack;
import com.cybercraft.hack.QuickhackRegistry;
import com.cybercraft.item.CyberdeckItem;
import com.cybercraft.util.NetrunnerRaycast;

/**
 * Регистрация и серверная обработка сетевых пакетов мода.
 */
public final class ModNetworking {

	private ModNetworking() {
	}

	public static void init() {
		// 1. Регистрация C2S пакета в PayloadTypeRegistry
		PayloadTypeRegistry.serverboundPlay().register(
				ExecuteHackPayload.TYPE,
				ExecuteHackPayload.CODEC
		);

		// 2. Регистрация серверного обработчика
		ServerPlayNetworking.registerGlobalReceiver(
				ExecuteHackPayload.TYPE,
				ModNetworking::handleExecuteHack
		);
	}

	private static void handleExecuteHack(ExecuteHackPayload payload, ServerPlayNetworking.Context context) {
		// Базовая валидация пакета от сетевого слоя
		if (payload == null || payload.hackId() == null || payload.target() == null) {
			return;
		}

		// Безопасное перенаправление в главный серверный поток (Thread Safety)
		context.server().execute(() -> {
			ServerPlayer player = context.player();
			if (player == null || player.isRemoved() || !player.isAlive()) {
				return;
			}

			// 1. Строгая проверка слота HEAD: визор ОБЯЗАТЕЛЕН на голове
			if (!CyberdeckItem.isEquipped(player)) {
				player.sendSystemMessage(
						Component.literal("[ICE Alert] Нейроинтерфейс не подключен!").withStyle(ChatFormatting.RED),
						true
				);
				return;
			}

			// 2. Валидация скрипта в реестре
			Quickhack hack = QuickhackRegistry.get(payload.hackId());
			if (hack == null) {
				Cybercraft.LOGGER.warn("Player {} attempted to execute unknown quickhack: {}", player.getName().getString(), payload.hackId());
				return;
			}

			// 3. Валидация цели
			HackTarget target = payload.target();
			if (target.type() == HackTarget.TargetType.ENTITY) {
				int entityId = target.entityId();

				// Валидация ID: отсекаем некорректные идентификаторы и попытку взломать самого себя
				if (entityId <= 0 || entityId == player.getId()) {
					return;
				}

				Entity rawEntity = target.resolveEntity(player.level());
				if (!(rawEntity instanceof LivingEntity livingTarget) || livingTarget.isRemoved() || !livingTarget.isAlive()) {
					player.sendSystemMessage(
							Component.literal("[ICE Alert] Target lost or invalid!").withStyle(ChatFormatting.RED),
							true
					);
					return;
				}

				// 4. Серверная проверка дистанции (защита от читов, не более MAX_REACH = 20 блоков)
				double distanceSq = player.distanceToSqr(livingTarget.position());
				if (distanceSq > NetrunnerRaycast.MAX_REACH * NetrunnerRaycast.MAX_REACH) {
					player.sendSystemMessage(
							Component.literal("[ICE Alert] Target out of range!").withStyle(ChatFormatting.RED),
							true
					);
					return;
				}

				// 5. Проверка линии видимости (Line of sight)
				if (!player.hasLineOfSight(livingTarget)) {
					player.sendSystemMessage(
							Component.literal("[ICE Alert] Line of sight obstructed!").withStyle(ChatFormatting.RED),
							true
					);
					return;
				}

				// 6. Проверка совместимости моба с протоколом
				if (!hack.canTargetEntity(livingTarget)) {
					player.sendSystemMessage(
							Component.literal("[ICE Alert] Target immune to this protocol!").withStyle(ChatFormatting.RED),
							true
					);
					return;
				}

				// 7. Выполнение скрипта
				boolean success = hack.executeOnEntity(player, livingTarget);
				if (success) {
					player.sendSystemMessage(
							Component.literal("[Upload Complete] ")
									.withStyle(ChatFormatting.AQUA)
									.append(hack.displayName().copy().withStyle(ChatFormatting.GREEN)),
							true
					);
				}
			}
		});
	}
}
