package arcane.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;

/** Кристальный голем: крепкий и сильный враждебный моб, не горит на солнце. */
public class CrystalGolemEntity extends ZombieEntity {

	public CrystalGolemEntity(EntityType<? extends ZombieEntity> type, World world) {
		super(type, world);
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return ZombieEntity.createZombieAttributes()
				.add(EntityAttributes.MAX_HEALTH, 40.0)
				.add(EntityAttributes.ATTACK_DAMAGE, 6.0)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.25)
				.add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.5);
	}

	@Override
	protected boolean burnsInDaylight() {
		return false;
	}

	@Override
	protected boolean canConvertInWater() {
		return false;
	}
}
