package arcane.data;

import arcane.ArcaneMod;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Способности Мечника и Лучника (пассивные бонусы и «Вихрь клинков»). */
public final class ClassAbilities {
	private ClassAbilities() {
	}

	private static final int WHIRL_COOLDOWN = 160;
	private static final Map<UUID, Long> WHIRL_READY = new HashMap<>();

	public static void register() {
		// Стрелы игрока: меткость и огненные стрелы.
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof PersistentProjectileEntity arrow && !(entity instanceof TridentEntity)
					&& entity.age == 0 && arrow.getOwner() instanceof ServerPlayerEntity player) {
				ArcaneData d = ArcaneData.get(player);
				int sharp = d.rank(Skills.SHARP);
				if (sharp > 0) {
					arrow.setDamage(arrow.getDamage() + 0.5 * sharp);
				}
				if (d.rank(Skills.FIRE) > 0) {
					arrow.setOnFireForTicks(100);
				}
			}
		});

		// Вихрь клинков: Shift + ПКМ с мечом в руке.
		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (world.isClient || hand != Hand.MAIN_HAND || !player.isSneaking()) {
				return ActionResult.PASS;
			}
			if (!(player instanceof ServerPlayerEntity sp) || !(world instanceof ServerWorld sw)) {
				return ActionResult.PASS;
			}
			ItemStack stack = player.getStackInHand(hand);
			if (!stack.isIn(ItemTags.SWORDS)) {
				return ActionResult.PASS;
			}
			ArcaneData d = ArcaneData.get(sp);
			if (d.rank(Skills.WHIRL) <= 0) {
				return ActionResult.PASS;
			}
			long now = sw.getTime();
			long ready = WHIRL_READY.getOrDefault(sp.getUuid(), 0L);
			if (now < ready) {
				long left = (ready - now + 19) / 20;
				sp.sendMessage(Text.translatable("message.arcane.whirlwind_cd", left), true);
				return ActionResult.SUCCESS;
			}
			WHIRL_READY.put(sp.getUuid(), now + WHIRL_COOLDOWN);
			whirlwind(sp, sw, d);
			return ActionResult.SUCCESS;
		});

		// После возрождения заново применяем бонусы навыков.
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> ArcaneLogic.sync(newPlayer));
	}

	private static void whirlwind(ServerPlayerEntity player, ServerWorld world, ArcaneData d) {
		sendCast(player);
		float damage = 6.0f + 2.0f * d.rank(Skills.BLADE);
		Box area = player.getBoundingBox().expand(3.5, 1.0, 3.5);
		for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, area,
				x -> x != player && x.isAlive() && x instanceof Monster)) {
			e.damage(world, world.getDamageSources().playerAttack(player), damage);
			e.takeKnockback(0.9, player.getX() - e.getX(), player.getZ() - e.getZ());
		}
		world.spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX(), player.getY() + 1.0, player.getZ(),
				12, 1.5, 0.3, 1.5, 0.0);
		world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
				SoundCategory.PLAYERS, 1.0f, 0.8f);
	}

	private static void sendCast(ServerPlayerEntity player) {
		player.sendMessage(Text.translatable("skill.arcane." + Skills.WHIRL), true);
	}

	/** Вызывается, когда игрок убил монстра. */
	public static void onKill(ServerPlayerEntity player, DamageSource source) {
		ArcaneData d = ArcaneData.get(player);
		int blood = d.rank(Skills.BLOOD);
		if (blood > 0) {
			player.heal(2.0f * blood);
		}
		int rush = d.rank(Skills.RUSH);
		if (rush > 0 && source.getSource() instanceof PersistentProjectileEntity) {
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60 * rush, 1));
		}
	}

	/** Применяет пассивные бонусы навыков к характеристикам игрока. */
	public static void applyAttributes(ServerPlayerEntity player) {
		ArcaneData d = ArcaneData.get(player);
		setModifier(player, EntityAttributes.ATTACK_DAMAGE, "skill_blade",
				1.0 * d.rank(Skills.BLADE), EntityAttributeModifier.Operation.ADD_VALUE);
		setModifier(player, EntityAttributes.ARMOR, "skill_iron",
				2.0 * d.rank(Skills.IRON), EntityAttributeModifier.Operation.ADD_VALUE);
		setModifier(player, EntityAttributes.MAX_HEALTH, "skill_vitality",
				2.0 * d.rank(Skills.VITALITY), EntityAttributeModifier.Operation.ADD_VALUE);
		setModifier(player, EntityAttributes.MOVEMENT_SPEED, "skill_swift",
				0.10 * d.rank(Skills.SWIFT), EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		setModifier(player, EntityAttributes.SAFE_FALL_DISTANCE, "skill_light",
				2.0 * d.rank(Skills.LIGHT), EntityAttributeModifier.Operation.ADD_VALUE);
	}

	private static void setModifier(ServerPlayerEntity player, RegistryEntry<EntityAttribute> attribute, String name,
									double value, EntityAttributeModifier.Operation operation) {
		EntityAttributeInstance instance = player.getAttributeInstance(attribute);
		if (instance == null) {
			return;
		}
		Identifier id = ArcaneMod.id(name);
		instance.removeModifier(id);
		if (value != 0.0) {
			instance.addTemporaryModifier(new EntityAttributeModifier(id, value, operation));
		}
	}
}
