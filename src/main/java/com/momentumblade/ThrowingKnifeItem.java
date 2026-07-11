package com.momentumblade;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

/**
 * A throwing knife. When used, it performs a raycast in the direction the player
 * is looking. If it hits an entity, the damage scales with the distance the ray
 * travelled before hitting — the farther the knife "flew", the more damage it deals.
 */
public class ThrowingKnifeItem extends Item {

    // Maximum distance (in blocks) the knife can travel.
    private static final double MAX_RANGE = 24.0;
    // Base damage at point-blank range.
    private static final float BASE_DAMAGE = 2.0f;
    // Extra damage added per block travelled.
    private static final float DAMAGE_PER_BLOCK = 0.55f;

    public ThrowingKnifeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient) {
            // Play throwing sound on client for feedback.
            world.playSound(user, user.getBlockPos(), SoundEvents.ENTITY_SNOWBALL_THROW,
                    SoundCategory.PLAYERS, 0.6f, 1.2f);
            return TypedActionResult.success(stack, true);
        }

        Vec3d start = user.getCameraPosVec(1.0f);
        Vec3d look = user.getRotationVec(1.0f);
        Vec3d end = start.add(look.multiply(MAX_RANGE));

        // Raycast against blocks first to know how far the knife could travel.
        BlockHitResult blockHit = world.raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                user));

        Vec3d blockEnd = blockHit.getType() != HitResult.Type.MISS ? blockHit.getPos() : end;

        // Find the closest entity along the ray up to the block hit point.
        EntityHitResult entityHit = raycastEntities(world, user, start, blockEnd);

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
            double distance = start.distanceTo(entityHit.getPos());
            float damage = computeDamage(distance);

            target.damage(user.getDamageSources().playerAttack(user), damage);

            // Knockback in the direction the knife travelled.
            target.takeKnockback(0.4f, -look.x, -look.z);

            world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_ARROW_HIT_PLAYER,
                    SoundCategory.PLAYERS, 0.8f, 1.0f);

            user.getItemCooldownManager().set(this, 20);
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            return TypedActionResult.success(stack, false);
        }

        // Missed — still consume with a shorter cooldown to keep it balanced.
        user.getItemCooldownManager().set(this, 10);
        if (!user.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        return TypedActionResult.success(stack, false);
    }

    private float computeDamage(double distance) {
        double clamped = Math.min(distance, MAX_RANGE);
        return BASE_DAMAGE + (float) (clamped * DAMAGE_PER_BLOCK);
    }

    private EntityHitResult raycastEntities(World world, PlayerEntity user, Vec3d start, Vec3d end) {
        Box searchBox = new Box(start, end).expand(1.0);
        List<Entity> candidates = world.getOtherEntities(user, searchBox,
                e -> e instanceof LivingEntity && e.isAlive() && e.canHit());

        EntityHitResult closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : candidates) {
            Box hitBox = entity.getBoundingBox().expand(0.3);
            var optional = hitBox.raycast(start, end);
            if (optional.isPresent()) {
                double dist = start.squaredDistanceTo(optional.get());
                if (dist < closestDist) {
                    closest = new EntityHitResult(entity, optional.get());
                    closestDist = dist;
                }
            }
        }
        return closest;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.momentumblade.knife.line1")
                .formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Damage grows with distance: " + BASE_DAMAGE
                + " up to ~" + computeDamage(MAX_RANGE))
                .formatted(Formatting.DARK_AQUA));
    }
}