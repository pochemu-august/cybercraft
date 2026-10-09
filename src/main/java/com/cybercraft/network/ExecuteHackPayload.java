package com.cybercraft.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import com.cybercraft.Cybercraft;
import com.cybercraft.hack.HackTarget;

/**
 * Сетевой пакет клиента на сервер с запросом запуска скрипта взлома.
 *
 * @param hackId ID запрашиваемого квикхака (например, cybercraft:short_circuit)
 * @param target цель (моб с entityId или блок)
 */
public record ExecuteHackPayload(Identifier hackId, HackTarget target) implements CustomPacketPayload {

	public static final Type<ExecuteHackPayload> TYPE = new Type<>(Cybercraft.id("execute_hack"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteHackPayload> CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC,
			ExecuteHackPayload::hackId,
			HackTarget.STREAM_CODEC,
			ExecuteHackPayload::target,
			ExecuteHackPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
