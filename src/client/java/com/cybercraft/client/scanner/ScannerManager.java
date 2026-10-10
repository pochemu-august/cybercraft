package com.cybercraft.client.scanner;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.cybercraft.client.key.ModKeyBindings;
import com.cybercraft.hack.HackTarget;
import com.cybercraft.hack.ShortCircuitHack;
import com.cybercraft.item.CyberdeckItem;
import com.cybercraft.network.ExecuteHackPayload;
import com.cybercraft.util.NetrunnerRaycast;

/**
 * Клиентский менеджер сканера визора.
 * Отслеживает условия: надет шлем ModItems.CYBERDECK + зажат Tab.
 * Выполняет рейкаст строго на клиентских тиках (20 раз/сек) и кэширует цель для оверлея.
 */
public final class ScannerManager {
	private static boolean scanning = false;
	private static HitResult currentHit = null;
	private static LivingEntity cachedTarget = null;

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

	/**
	 * Возвращает закэшированную цель для отрисовки в HUD.
	 * Если сущность погибла или удалена из мира, возвращает null.
	 */
	public static LivingEntity getTargetEntity() {
		if (cachedTarget != null && (!cachedTarget.isAlive() || cachedTarget.isRemoved())) {
			cachedTarget = null;
		}
		return cachedTarget;
	}

	private static void onClientTick(Minecraft client) {
		if (client.player == null || client.level == null) {
			reset();
			return;
		}

		// Условие (а): Надет шлем CYBERDECK в слоте HEAD
		boolean visorEquipped = CyberdeckItem.isEquipped(client.player);

		// Условие (б): Зажата клавиша сканирования (Tab)
		boolean keyHeld = ModKeyBindings.SCAN_KEY.isDown();

		// Сканер активен ТОЛЬКО при одновременном выполнении условий (а) и (б)
		if (visorEquipped && keyHeld) {
			scanning = true;
			// Рейкаст выполняется на тике клиента (20 Hz), не перегружая отрисовку каждого кадра
			currentHit = NetrunnerRaycast.raycast(client.player, NetrunnerRaycast.MAX_REACH);

			if (currentHit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity living && living.isAlive()) {
				cachedTarget = living;
			} else {
				cachedTarget = null;
			}

			// Быстрый взлом по клавише R при наведенной валидной цели во время сканирования
			while (ModKeyBindings.EXECUTE_HACK_KEY.consumeClick()) {
				if (cachedTarget != null && cachedTarget.isAlive()) {
					ClientPlayNetworking.send(new ExecuteHackPayload(
							ShortCircuitHack.ID,
							HackTarget.ofEntity(cachedTarget.getId())
					));
				}
			}
		} else {
			reset();
		}
	}

	private static void reset() {
		scanning = false;
		currentHit = null;
		cachedTarget = null;
		// Очищаем нажатия клавиши R, сделанные вне режима сканирования
		while (ModKeyBindings.EXECUTE_HACK_KEY.consumeClick()) {
		}
	}
}
