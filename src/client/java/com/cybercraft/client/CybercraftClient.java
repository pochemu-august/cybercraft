package com.cybercraft.client;

import net.fabricmc.api.ClientModInitializer;

import com.cybercraft.client.hud.ScannerHudOverlay;
import com.cybercraft.client.key.ModKeyBindings;
import com.cybercraft.client.scanner.ScannerManager;

public class CybercraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// 1. Регистрация горячих клавиш (включая Tab для сканера)
		ModKeyBindings.init();

		// 2. Слушатель тиков клиента для непрерывного рейкаста и удержания Tab
		ScannerManager.init();

		// 3. Регистрация оверлея визора в HUD
		ScannerHudOverlay.register();
	}
}