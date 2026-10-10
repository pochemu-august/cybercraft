package com.cybercraft.client.key;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

import com.cybercraft.Cybercraft;

/**
 * Регистрация горячих клавиш нетраннера на клиенте.
 */
public final class ModKeyBindings {
	public static final KeyMapping.Category CATEGORY =
			KeyMapping.Category.register(Cybercraft.id("main"));

	/** Клавиша сканирования визора: удержание Tab */
	public static final KeyMapping SCAN_KEY = new KeyMapping(
			"key.cybercraft.scan",
			InputConstants.KEY_TAB,
			CATEGORY
	);

	/** Дополнительная клавиша мгновенного запуска взлома по наведенной цели (по умолчанию 'R') */
	public static final KeyMapping EXECUTE_HACK_KEY = new KeyMapping(
			"key.cybercraft.execute_hack",
			InputConstants.KEY_R,
			CATEGORY
	);

	private ModKeyBindings() {
	}

	public static void init() {
		KeyMappingHelper.registerKeyMapping(SCAN_KEY);
		KeyMappingHelper.registerKeyMapping(EXECUTE_HACK_KEY);
	}
}
