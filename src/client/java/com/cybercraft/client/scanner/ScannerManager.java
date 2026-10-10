package com.cybercraft.client.scanner;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.cybercraft.client.key.ModKeyBindings;
import com.cybercraft.hack.HackTarget;
import com.cybercraft.hack.ShortCircuitHack;
import com.cybercraft.network.ExecuteHackPayload;
import com.cybercraft.registry.ModItems;
import com.cybercraft.util.NetrunnerRaycast;

/**
 * Клиентский менеджер сканера визора.
 * Отслеживает условия: надет шлем ModItems.CYBERDECK + зажат Tab.
 */
public final class ScannerManager {
	private static boolean scanning = false;
	private static HitResult currentHit = null;

	private ScannerManager() {
	}

	public static void init() {
		ClientTickEvents.END_CLIENT_TICK.register(ScannerManager::onClientTick);
	}

	public static boolean isScanning() {
		return scanning;
	}

	public static HitResult getCurrentHit() {
		return currentHit;
	}

	private static void onClientTick(Minecraft client) {
		if (client.player == null || client.level == null) {
			scanning = false;
			currentHit = null;
			return;
		}

		// Условие (а): Надет шлем CYBERDECK в слоте HEAD
		boolean visorEquipped = client.player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.CYBERDECK);

		// Условие (б): Зажата клавиша сканирования (Tab)
		boolean keyHeld = ModKeyBindings.SCAN_KEY.isDown();

		// Сканер активен ТОЛЬКО при одновременном выполнении условий (а) и (б)
		if (visorEquipped && keyHeld) {
			scanning = true;
			// Непрерывный рейкаст до 20 блоков
			currentHit = NetrunnerRaycast.raycast(client.player, NetrunnerRaycast.MAX_REACH);

			// Быстрый взлом по клавише R при наведенной цели во время сканирования
			while (ModKeyBindings.EXECUTE_HACK_KEY.consumeClick()) {
				if (currentHit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity living) {
					ClientPlayNetworking.send(new ExecuteHackPayload(
							ShortCircuitHack.ID,
							HackTarget.ofEntity(living.getId())
					));
				}
			}
		} else {
			// Мгновенный сброс, если шлем снят или Tab отпущен
			scanning = false;
			currentHit = null;
		}
	}
}
