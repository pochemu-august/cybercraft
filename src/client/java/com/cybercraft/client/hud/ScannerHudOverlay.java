package com.cybercraft.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import com.cybercraft.Cybercraft;
import com.cybercraft.client.scanner.ScannerManager;

/**
 * Оверлей HUD сканера визора.
 * Отрисовывается только тогда, когда активен режим сканирования с надетого нейровизора.
 */
public class ScannerHudOverlay implements HudElement {

	public static void register() {
		HudElementRegistry.addLast(Cybercraft.id("scanner_overlay"), new ScannerHudOverlay());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null) {
			return;
		}

		// Если сканер не активен — ничего не рендерим
		if (!ScannerManager.isScanning()) {
			return;
		}

		Font font = client.font;
		int width = extractor.guiWidth();
		int height = extractor.guiHeight();
		int cx = width / 2;
		int cy = height / 2;

		// Цветовая палитра киберпанка
		int colorCyan = 0xCC00F0FF;
		int colorDimCyan = 0x660088AA;
		int colorBackground = 0xBB0D1117;

		// 1. Прицельная рамка по центру экрана (Tactical brackets)
		int bracketSize = 18;
		int bracketThick = 1;
		int bracketLen = 6;

		// Верх-лево
		extractor.fill(cx - bracketSize, cy - bracketSize, cx - bracketSize + bracketLen, cy - bracketSize + bracketThick, colorCyan);
		extractor.fill(cx - bracketSize, cy - bracketSize, cx - bracketSize + bracketThick, cy - bracketSize + bracketLen, colorCyan);

		// Верх-право
		extractor.fill(cx + bracketSize - bracketLen, cy - bracketSize, cx + bracketSize, cy - bracketSize + bracketThick, colorCyan);
		extractor.fill(cx + bracketSize - bracketThick, cy - bracketSize, cx + bracketSize, cy - bracketSize + bracketLen, colorCyan);

		// Низ-лево
		extractor.fill(cx - bracketSize, cy + bracketSize - bracketThick, cx - bracketSize + bracketLen, cy + bracketSize, colorCyan);
		extractor.fill(cx - bracketSize, cy + bracketSize - bracketLen, cx - bracketSize + bracketThick, cy + bracketSize, colorCyan);

		// Низ-право
		extractor.fill(cx + bracketSize - bracketLen, cy + bracketSize - bracketThick, cx + bracketSize, cy + bracketSize, colorCyan);
		extractor.fill(cx + bracketSize - bracketThick, cy + bracketSize - bracketLen, cx + bracketSize, cy + bracketSize, colorCyan);

		// Центральное цифровое перекрестие
		extractor.fill(cx - 1, cy - 1, cx + 1, cy + 1, 0xFF00FFFF);

		// Верхний статус визора
		extractor.centeredText(font, Component.literal("[NEURO-VISOR // SCANNING]"), cx, cy - bracketSize - 14, 0xFF00F0FF);

		// 2. Данные о цели под перекрестием
		HitResult hit = ScannerManager.getCurrentHit();
		if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target) {
			int panelW = 140;
			int panelH = 42;
			int panelX = cx - panelW / 2;
			int panelY = cy + bracketSize + 8;

			// Полупрозрачный темный фон карточки цели
			extractor.fill(panelX, panelY, panelX + panelW, panelY + panelH, colorBackground);

			// Тонкая неоновая окантовка карточки
			extractor.fill(panelX, panelY, panelX + panelW, panelY + 1, colorCyan);
			extractor.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, colorCyan);
			extractor.fill(panelX, panelY, panelX + 1, panelY + panelH, colorCyan);
			extractor.fill(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, colorCyan);

			// Заголовок цели
			extractor.centeredText(font, target.getDisplayName(), cx, panelY + 4, 0xFFFFFFFF);

			// Полоса здоровья (HP Bar)
			float health = Math.max(0, target.getHealth());
			float maxHealth = Math.max(1, target.getMaxHealth());
			float hpRatio = Math.min(1.0F, health / maxHealth);

			int barX = panelX + 10;
			int barY = panelY + 17;
			int barW = panelW - 20;
			int barH = 5;

			// Фон полосы HP
			extractor.fill(barX, barY, barX + barW, barY + barH, 0xFF22252A);

			// Заполненная часть HP (зеленый / оранжевый / красный градиент)
			int hpColor = hpRatio > 0.5F ? 0xFF00FF88 : (hpRatio > 0.2F ? 0xFFFFCC00 : 0xFFFF3333);
			extractor.fill(barX, barY, barX + (int) (barW * hpRatio), barY + barH, hpColor);

			// Текстовое значение здоровья
			String hpText = String.format("%.1f / %.1f HP", health, maxHealth);
			extractor.centeredText(font, Component.literal(hpText), cx, panelY + 25, 0xFFCCCCCC);

			// Подсказка действия
			extractor.centeredText(font, Component.literal("[R] Quickhack: Short Circuit"), cx, panelY + 34, 0xFF00F0FF);
		} else {
			// Если моб не в прицеле
			extractor.centeredText(font, Component.literal("[NO HOST IN RANGE]"), cx, cy + bracketSize + 8, colorDimCyan);
		}
	}
}
