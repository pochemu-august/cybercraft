package com.cybercraft.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

/**
 * Неизменяемые аппаратные характеристики кибердеки. Хранятся как Data Component на ItemStack.
 *
 * <p>Через этот компонент делаются разные «модели» дек (MK1, MK2, ...): один и тот же класс
 * {@link com.cybercraft.item.CyberdeckItem} с разными значениями компонента. Поскольку это
 * record, компонент можно безопасно шарить между стаками — у Data Components семантика значений.</p>
 *
 * @param tier                аппаратный тир деки (будущие скрипты могут требовать минимальный тир)
 * @param maxRam              ёмкость пула RAM
 * @param ramRegenPerSecond   сколько RAM восстанавливается в секунду
 */
public record CyberdeckSpec(int tier, int maxRam, float ramRegenPerSecond) {
	public static final CyberdeckSpec MK1 = new CyberdeckSpec(1, 12, 1.0F);

	/** Codec для диска (сохранения мира / команда /give с компонентами). */
	public static final Codec<CyberdeckSpec> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.POSITIVE_INT.fieldOf("tier").forGetter(CyberdeckSpec::tier),
			ExtraCodecs.POSITIVE_INT.fieldOf("max_ram").forGetter(CyberdeckSpec::maxRam),
			ExtraCodecs.POSITIVE_FLOAT.fieldOf("ram_regen_per_second").forGetter(CyberdeckSpec::ramRegenPerSecond)
	).apply(instance, CyberdeckSpec::new));

	/** Компактный бинарный кодек для синхронизации стака по сети. */
	public static final StreamCodec<ByteBuf, CyberdeckSpec> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, CyberdeckSpec::tier,
			ByteBufCodecs.VAR_INT, CyberdeckSpec::maxRam,
			ByteBufCodecs.FLOAT, CyberdeckSpec::ramRegenPerSecond,
			CyberdeckSpec::new
	);
}
