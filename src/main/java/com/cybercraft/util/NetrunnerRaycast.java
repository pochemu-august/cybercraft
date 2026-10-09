package com.cybercraft.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Трассировка луча (Raycast) для визора кибердеки.
 * Находит моба или блок перед взглядом игрока в радиусе до 20 блоков.
 */
public final class NetrunnerRaycast {
	public static final double MAX_REACH = 20.0;

	private NetrunnerRaycast() {
	}

	/**
	 * Выполняет комбинированный рейкаст (блок + сущность), отдавая приоритет сущности,
	 * если она находится ближе или не заблокирована сплошным блоком.
	 *
	 * @param eyePosition  точка обзора (глаза игрока)
	 * @param viewVector   нормализованный вектор взгляда
	 * @param level        мир (клиентский или серверный)
	 * @param sourceEntity сущность, от лица которой идет рейкаст (игрок)
	 * @param maxDistance  дистанция луча (максимум 20.0)
	 * @return результат пересечения: EntityHitResult, BlockHitResult или Miss
	 */
	public static HitResult raycast(
			Vec3 eyePosition,
			Vec3 viewVector,
			Level level,
			Entity sourceEntity,
			double maxDistance
	) {
		Vec3 reachEnd = eyePosition.add(viewVector.scale(maxDistance));

		// 1. Проверяем блоки по лучу взгляда
		BlockHitResult blockHit = level.clip(new ClipContext(
				eyePosition,
				reachEnd,
				ClipContext.Block.OUTLINE,
				ClipContext.Fluid.NONE,
				sourceEntity
		));

		double entityReach = maxDistance;
		if (blockHit.getType() != HitResult.Type.MISS) {
			entityReach = eyePosition.distanceTo(blockHit.getLocation());
		}

		// 2. Ищем сущности в объеме луча в пределах видимости до блока
		Vec3 entityEnd = eyePosition.add(viewVector.scale(entityReach));
		AABB searchBox = sourceEntity.getBoundingBox()
				.expandTowards(viewVector.scale(maxDistance))
				.inflate(1.0);

		Predicate<Entity> filter = entity -> !entity.isSpectator()
				&& entity.isPickable()
				&& entity.isAlive()
				&& !entity.equals(sourceEntity);

		List<Entity> candidateEntities = level.getEntities(sourceEntity, searchBox, filter);
		EntityHitResult closestEntityHit = null;
		double closestDistanceSq = entityReach * entityReach;

		for (Entity candidate : candidateEntities) {
			AABB hitBox = candidate.getBoundingBox().inflate(candidate.getPickRadius());
			Optional<Vec3> clipResult = hitBox.clip(eyePosition, entityEnd);

			if (clipResult.isPresent()) {
				Vec3 hitVec = clipResult.get();
				double distanceSq = eyePosition.distanceToSqr(hitVec);

				if (distanceSq < closestDistanceSq) {
					closestEntityHit = new EntityHitResult(candidate, hitVec);
					closestDistanceSq = distanceSq;
				}
			}
		}

		// Если найдена сущность и блок ее не перекрывает — возвращаем ее
		if (closestEntityHit != null) {
			return closestEntityHit;
		}

		return blockHit;
	}

	/**
	 * Удобная перегрузка от сущности (игрока).
	 */
	public static HitResult raycast(Entity caster, double maxDistance) {
		return raycast(caster.getEyePosition(), caster.getViewVector(1.0F), caster.level(), caster, maxDistance);
	}
}
