package com.cybercraft.hack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Реестр доступных быстрых взломов.
 */
public final class QuickhackRegistry {
	private static final Map<Identifier, Quickhack> HACKS = new HashMap<>();

	public static final Quickhack SHORT_CIRCUIT = register(new ShortCircuitHack());

	private QuickhackRegistry() {
	}

	public static <T extends Quickhack> T register(T hack) {
		HACKS.put(hack.id(), hack);
		return hack;
	}

	@Nullable
	public static Quickhack get(Identifier id) {
		return HACKS.get(id);
	}

	public static Map<Identifier, Quickhack> getAll() {
		return Collections.unmodifiableMap(HACKS);
	}

	public static void init() {
	}
}
