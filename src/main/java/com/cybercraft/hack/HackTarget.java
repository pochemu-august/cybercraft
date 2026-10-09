package com.cybercraft.hack;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Обобщенная цель для нетраннинга — моб или блок в мире.
 */
public record HackTarget(TargetType type, int entityId, @Nullable BlockPos blockPos) {

	public enum TargetType {
		ENTITY,
		BLOCK
	}

	public static HackTarget ofEntity(int entityId) {
		return new HackTarget(TargetType.ENTITY, entityId, null);
	}

	public static HackTarget ofBlock(BlockPos pos) {
		return new HackTarget(TargetType.BLOCK, -1, pos);
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, HackTarget> STREAM_CODEC = StreamCodec.of(
			HackTarget::write,
			HackTarget::read
	);

	private static void write(RegistryFriendlyByteBuf buf, HackTarget target) {
		buf.writeEnum(target.type());
		if (target.type() == TargetType.ENTITY) {
			buf.writeVarInt(target.entityId());
		} else {
			BlockPos.STREAM_CODEC.encode(buf, target.blockPos() != null ? target.blockPos() : BlockPos.ZERO);
		}
	}

	private static HackTarget read(RegistryFriendlyByteBuf buf) {
		TargetType type = buf.readEnum(TargetType.class);
		if (type == TargetType.ENTITY) {
			return ofEntity(buf.readVarInt());
		} else {
			return ofBlock(BlockPos.STREAM_CODEC.decode(buf));
		}
	}

	@Nullable
	public Entity resolveEntity(Level level) {
		return type == TargetType.ENTITY ? level.getEntity(entityId) : null;
	}
}
