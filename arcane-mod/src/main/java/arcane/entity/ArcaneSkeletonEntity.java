package arcane.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.world.World;

/** Арканный скелет: быстрый лучник, не горит на солнце. */
public class ArcaneSkeletonEntity extends SkeletonEntity {

	public ArcaneSkeletonEntity(EntityType<? extends SkeletonEntity> type, World world) {
		super(type, world);
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return AbstractSkeletonEntity.createAbstractSkeletonAttributes()
				.add(EntityAttributes.MAX_HEALTH, 30.0)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.30);
	}

	@Override
	protected boolean isAffectedByDaylight() {
		return false;
	}
}
