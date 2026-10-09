package com.cybercraft;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cybercraft.hack.QuickhackRegistry;
import com.cybercraft.network.ModNetworking;
import com.cybercraft.registry.ModComponents;
import com.cybercraft.registry.ModItems;

public class Cybercraft implements ModInitializer {
	public static final String MOD_ID = "cybercraft";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Cybercraft] Initializing Netrunning & ICE protocols...");

		ModComponents.init();
		ModItems.init();
		QuickhackRegistry.init();
		ModNetworking.init();

		LOGGER.info("[Cybercraft] Cyberdeck firmware online.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
