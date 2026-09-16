package pulsetest;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Neuro KillAura for the local Fabric 1.21.4 test client.
 *
 * Legit-oriented checks:
 * - short range
 * - FOV check
 * - line-of-sight raycast
 * - target priority
 * - vanilla attack cooldown
 * - smooth, speed-limited rotation
 * - no attacks while a screen is open
 */
public final class NeuroKillAura extends Module {
    private final MinecraftClient client = MinecraftClient.getInstance();

    private final Setting<Double> range = add(new Setting<>("Range", 3.1D, 2.5D, 4.0D));
    private final Setting<Double> fov = add(new Setting<>("FOV", 75.0D, 30.0D, 180.0D));
    private final Setting<Double> yawSpeed = add(new Setting<>("Yaw Speed", 7.0D, 1.0D, 15.0D));
    private final Setting<Double> pitchSpeed = add(new Setting<>("Pitch Speed", 5.0D, 1.0D, 12.0D));
    private final Setting<Boolean> players = add(new Setting<>("Players", true));
    private final Setting<Boolean> hostile = add(new Setting<>("Hostile", true));
    private final Setting<Boolean> passive = add(new Setting<>("Passive", false));
    private final Setting<Boolean> visibility = add(new Setting<>("Visibility", true));

    private int nextAttackDelay;

    public NeuroKillAura() {
        super("Neuro KillAura", "Legit-oriented target selection and attack module", Category.COMBAT);
        resetDelay();
    }

    @Override
    public void onEnable() {
        resetDelay();
    }

    @Override
    public void onDisable() {
        nextAttackDelay = 0;
    }

    @Override
    public void tick() {
        if (client.player == null || client.world == null || client.interactionManager == null) return;
        if (client.currentScreen != null || client.player.isSpectator()) return;

        LivingEntity target = findTarget();
        if (target == null) return;

        rotateSmoothly(target);

        if (!legitChecks(target)) return;
        if (--nextAttackDelay > 0) return;

        client.interactionManager.attackEntity(client.player, target);
        client.player.swingHand(Hand.MAIN_HAND);
        resetDelay();
    }

    private LivingEntity findTarget() {
        Box area = client.player.getBoundingBox().expand(range.getValue());
        List<LivingEntity> candidates = client.world.getEntitiesByClass(
                LivingEntity.class,
                area,
                this::isCandidate
        );

        return candidates.stream()
                .filter(this::legitChecks)
                .min(Comparator
                        .comparingInt(this::priority)
                        .thenComparingDouble(client.player::distanceTo)
                        .thenComparingDouble(this::angleToTarget))
                .orElse(null);
    }

    private boolean isCandidate(LivingEntity entity) {
        if (entity == client.player || !entity.isAlive()) return false;
        if (entity instanceof PlayerEntity) return players.getValue();
        if (entity instanceof HostileEntity) return hostile.getValue();
        if (entity instanceof PassiveEntity) return passive.getValue();
        return false;
    }

    private int priority(LivingEntity entity) {
        if (entity instanceof PlayerEntity) return 1;
        if (entity instanceof HostileEntity) return 2;
        if (entity instanceof PassiveEntity) return 3;
        return 4;
    }

    private boolean legitChecks(LivingEntity target) {
        if (client.player == null || !target.isAlive()) return false;
        if (target == client.player) return false;

        if (target instanceof PlayerEntity player && (player.isSpectator() || player.isCreative())) {
            return false;
        }

        if (client.player.distanceTo(target) > range.getValue()) return false;
        if (!isInFov(target)) return false;
        if (visibility.getValue() && !canSee(target)) return false;
        if (client.player.getAttackCooldownProgress(0.0F) < 0.95F) return false;
        if (!isLookingAtTarget(target)) return false;
        return true;
    }

    private boolean isInFov(LivingEntity target) {
        Vec3d eye = client.player.getEyePos();
        Vec3d pos = target.getBoundingBox().getCenter();
        float targetYaw = (float) Math.toDegrees(Math.atan2(pos.z - eye.z, pos.x - eye.x)) - 90.0F;
        return Math.abs(wrapDegrees(targetYaw - client.player.getYaw())) <= fov.getValue() / 2.0D;
    }

    private boolean canSee(LivingEntity target) {
        Vec3d start = client.player.getEyePos();
        Vec3d end = target.getBoundingBox().getCenter();

        BlockHitResult hit = client.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                client.player
        ));

        return hit.getType() == HitResult.Type.MISS;
    }

    private boolean isLookingAtTarget(LivingEntity target) {
        Vec3d direction = target.getBoundingBox().getCenter()
                .subtract(client.player.getEyePos()).normalize();
        Vec3d look = client.player.getRotationVec(1.0F).normalize();
        return look.dotProduct(direction) > 0.985D;
    }

    private void rotateSmoothly(LivingEntity target) {
        Vec3d eye = client.player.getEyePos();
        Vec3d pos = target.getBoundingBox().getCenter();

        double dx = pos.x - eye.x;
        double dy = pos.y - eye.y;
        double dz = pos.z - eye.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));

        float yawDelta = wrapDegrees(targetYaw - client.player.getYaw());
        float pitchDelta = targetPitch - client.player.getPitch();

        client.player.setYaw(client.player.getYaw() + clamp(yawDelta, -(float) yawSpeed.getValue(), (float) yawSpeed.getValue()));
        client.player.setPitch(client.player.getPitch() + clamp(pitchDelta, -(float) pitchSpeed.getValue(), (float) pitchSpeed.getValue()));
    }

    private double angleToTarget(Entity target) {
        Vec3d direction = target.getBoundingBox().getCenter().subtract(client.player.getEyePos()).normalize();
        double dot = client.player.getRotationVec(1.0F).normalize().dotProduct(direction);
        return 1.0D - dot;
    }

    private void resetDelay() {
        nextAttackDelay = ThreadLocalRandom.current().nextInt(9, 14);
    }

    private static float wrapDegrees(float value) {
        value %= 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
