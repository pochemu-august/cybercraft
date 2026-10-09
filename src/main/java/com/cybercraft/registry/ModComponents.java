package com.cybercraft.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import com.cybercraft.Cybercraft;
import com.cybercraft.item.component.CyberdeckSpec;

/** Регистрация собственных Data Components для предметов. */
public final class ModComponents {
	/** Аппаратные характеристики деки. Сохраняется на диск и синхронизируется с клиентом (нужно для HUD). */
	public static final DataComponentType<CyberdeckSpec> CYBERDECK_SPEC = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			Cybercraft.id("cyberdeck_spec"),
			DataComponentType.<CyberdeckSpec>builder()
					.persistent(CyberdeckSpec.CODEC)
					.networkSynchronized(CyberdeckSpec.STREAM_CODEC)
					.build()
	);

	private ModComponents() {
	}

	/** Принудительно загружает класс, чтобы статические регистрации выполнились до регистрации предметов. */
	public static void init() {
	}
}
