package me.lemonboi439.archeryRevamped.entity;

import me.lemonboi439.archeryRevamped.arrow.ArrowBehaviorRegistry;
import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.arrow.RicochetBehavior;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import me.lemonboi439.archeryRevamped.physics.ArrowPhysicsEngine;
import me.lemonboi439.archeryRevamped.mixin.PersistentProjectileEntityAccessor;
import me.lemonboi439.archeryRevamped.fracture.FractureScheduler;
import me.lemonboi439.archeryRevamped.burst.BurstArrowHandler;
import me.lemonboi439.archeryRevamped.debug.TrajectoryVisualizer;
import me.lemonboi439.archeryRevamped.headshot.HeadshotManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArcheryArrowEntity extends PersistentProjectileEntity {
    private static final TrackedData<String> TRACKED_ARROW_TYPE = DataTracker.registerData(
            ArcheryArrowEntity.class, TrackedDataHandlerRegistry.STRING
    );
    private static final TrackedData<Boolean> TRACKED_TRAJECTORY_ENABLED = DataTracker.registerData(
            ArcheryArrowEntity.class, TrackedDataHandlerRegistry.BOOLEAN
    );
    private static final TrackedData<Boolean> TRACKED_TRAJECTORY_FINISHED = DataTracker.registerData(
            ArcheryArrowEntity.class, TrackedDataHandlerRegistry.BOOLEAN
    );
    private static final TrackedData<Float> TRACKED_TRAJECTORY_SPAWN_X = DataTracker.registerData(
            ArcheryArrowEntity.class, TrackedDataHandlerRegistry.FLOAT
    );
    private static final TrackedData<Float> TRACKED_TRAJECTORY_SPAWN_Y = DataTracker.registerData(
            ArcheryArrowEntity.class, TrackedDataHandlerRegistry.FLOAT
    );
    private static final TrackedData<Float> TRACKED_TRAJECTORY_SPAWN_Z = DataTracker.registerData(
            ArcheryArrowEntity.class, TrackedDataHandlerRegistry.FLOAT
    );
    private static final TrackedData<Boolean> TRACKED_TRAJECTORY_SPAWN_CAPTURED = DataTracker.registerData(
            ArcheryArrowEntity.class, TrackedDataHandlerRegistry.BOOLEAN
    );
    private static final TrackedData<Float> TRACKED_TRAJECTORY_VELOCITY_X = DataTracker.registerData(
            ArcheryArrowEntity.class, TrackedDataHandlerRegistry.FLOAT
    );
    private static final TrackedData<Float> TRACKED_TRAJECTORY_VELOCITY_Y = DataTracker.registerData(
            ArcheryArrowEntity.class, TrackedDataHandlerRegistry.FLOAT
    );
    private static final TrackedData<Float> TRACKED_TRAJECTORY_VELOCITY_Z = DataTracker.registerData(
            ArcheryArrowEntity.class, TrackedDataHandlerRegistry.FLOAT
    );
    private static final int CURRENT_DATA_VERSION = 5;
    private static final String DATA_VERSION_KEY = "archeryRevampedDataVersion";
    private static final String ARROW_TYPE_KEY = "arrowType";
    private static final String RICOCHET_LEVEL_KEY = "ricochetLevel";
    private static final String BOUNCE_COUNT_KEY = "bounceCount";
    private static final String DISTANCE_TRAVELLED_KEY = "distanceTravelled";
    private static final String ORIGINAL_DAMAGE_KEY = "originalDamage";
    private static final String SPAWN_X_KEY = "spawnX";
    private static final String SPAWN_Y_KEY = "spawnY";
    private static final String SPAWN_Z_KEY = "spawnZ";
    private static final String PHYSICS_AGE_KEY = "physicsAge";
    private static final String OVERDRAW_DAMAGE_BONUS_KEY = "overdrawDamageBonus";
    private static final String LONGSHOT_LEVEL_KEY = "longshotLevel";
    private static final String LONGSHOT_16_TRIGGERED_KEY = "longshot16Triggered";
    private static final String LONGSHOT_32_TRIGGERED_KEY = "longshot32Triggered";
    private static final String LONGSHOT_48_TRIGGERED_KEY = "longshot48Triggered";
    private static final String LONGSHOT_64_TRIGGERED_KEY = "longshot64Triggered";
    private static final String FRACTURE_LEVEL_KEY = "fractureLevel";
    private static final String SPLIT_TIMER_KEY = "splitTimer";
    private static final String HAS_SPREAD_KEY = "hasSpread";
    private static final String FRACTURE_RELEASE_SPEED_KEY = "fractureReleaseSpeed";
    private static final String EXTRA_AMMO_FREE_KEY = "extraAmmoFree";
    private static final String IMPACT_DAMAGE_MODIFIERS_APPLIED_KEY = "impactDamageModifiersApplied";
    private static final String SPAWN_POSITION_CAPTURED_KEY = "spawnPositionCaptured";
    private static final String RELEASE_VELOCITY_PRESENT_KEY = "releaseVelocityPresent";
    private static final String RELEASE_VELOCITY_X_KEY = "releaseVelocityX";
    private static final String RELEASE_VELOCITY_Y_KEY = "releaseVelocityY";
    private static final String RELEASE_VELOCITY_Z_KEY = "releaseVelocityZ";
    private static final String BURST_ARROWS_REMAINING_KEY = "burstArrowsRemaining";
    private static final String BURST_STAGGER_DELAY_KEY = "burstStaggerDelay";
    private static final String BURST_STAGGER_TIMER_KEY = "burstStaggerTimer";
    private static final String TRAJECTORY_FINISHED_KEY = "trajectoryFinished";
    private static final String HEADSHOT_LEVEL_KEY = "headshotLevel";
    private static final String DELAYED_IMPACT_DELAY_KEY = "delayedImpactDelay";
    private static final String DELAYED_IMPACT_TYPE_KEY = "delayedImpactType";
    private static final String DELAYED_IMPACT_X_KEY = "delayedImpactX";
    private static final String DELAYED_IMPACT_Y_KEY = "delayedImpactY";
    private static final String DELAYED_IMPACT_Z_KEY = "delayedImpactZ";

    private ArrowType arrowType = ArrowType.NORMAL;
    private int ricochetLevel;
    private int bounceCount;
    private double distanceTravelled;
    private double originalDamage;
    private double spawnX;
    private double spawnY;
    private double spawnZ;
    private int physicsAge;
    private double overdrawDamageBonus;
    private int longshotLevel;
    private boolean longshot16Triggered;
    private boolean longshot32Triggered;
    private boolean longshot48Triggered;
    private boolean longshot64Triggered;
    private int fractureLevel;
    private int splitTimer;
    private boolean hasSpread;
    // Delayed fracture work is held in a server-side scheduler. This flag is
    // deliberately transient: if the chunk/server is reloaded before the
    // scheduled task runs, the saved arrow must be eligible to schedule again.
    private boolean fractureScheduled;
    private double fractureReleaseSpeed;
    private boolean fractureReleaseSpeedCaptured;
    private boolean extraAmmoFree;
    private boolean impactDamageModifiersApplied;
    private boolean spawnPositionCaptured;
    private Vec3d releaseVelocity;
    private int burstArrowsRemaining;
    private int burstStaggerDelay;
    private int burstStaggerTimer;
    private boolean burstScheduled;
    private int headshotLevel;
    private double pendingHeadshotDamageMultiplier = 1.0D;
    private int delayedImpactDelay;
    private ArrowType delayedImpactType;
    private Vec3d delayedImpactPosition;
    private float tidalSpin;

    public ArcheryArrowEntity(World world) {
        super(ModEntities.ARCHERY_ARROW, world);
    }

    public ArcheryArrowEntity(World world, LivingEntity owner) {
        this(world, owner, new ItemStack(Items.ARROW), owner.getMainHandStack().copy());
    }

    public ArcheryArrowEntity(World world, LivingEntity owner, ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        super(ModEntities.ARCHERY_ARROW, owner, world,
                pickupItemStack.isEmpty() ? new ItemStack(Items.ARROW) : pickupItemStack,
                firedFromWeapon);
    }

    public ArcheryArrowEntity(World world, double x, double y, double z,
                              ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        super(ModEntities.ARCHERY_ARROW, x, y, z, world,
                pickupItemStack.isEmpty() ? new ItemStack(Items.ARROW) : pickupItemStack,
                firedFromWeapon);
    }

    public ArcheryArrowEntity(EntityType<? extends ArcheryArrowEntity> type, World world) {
        super(type, world);
        this.setStack(new ItemStack(Items.ARROW));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(TRACKED_ARROW_TYPE, ArrowType.NORMAL.name());
        builder.add(TRACKED_TRAJECTORY_ENABLED, false);
        builder.add(TRACKED_TRAJECTORY_FINISHED, false);
        builder.add(TRACKED_TRAJECTORY_SPAWN_X, 0.0F);
        builder.add(TRACKED_TRAJECTORY_SPAWN_Y, 0.0F);
        builder.add(TRACKED_TRAJECTORY_SPAWN_Z, 0.0F);
        builder.add(TRACKED_TRAJECTORY_SPAWN_CAPTURED, false);
        builder.add(TRACKED_TRAJECTORY_VELOCITY_X, 0.0F);
        builder.add(TRACKED_TRAJECTORY_VELOCITY_Y, 0.0F);
        builder.add(TRACKED_TRAJECTORY_VELOCITY_Z, 0.0F);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    protected float getDragInWater() {
        return this.getArrowType() == ArrowType.TIDAL ? 0.99F : super.getDragInWater();
    }

    /** Never let a child arrow immediately collide with the shooter it inherits. */
    @Override
    protected boolean canHit(Entity entity) {
        return entity != this.getOwner() && super.canHit(entity);
    }

    @Override
    public void tick() {
        if (!this.getEntityWorld().isClient()) {
            this.setTrajectoryPreviewEnabled(TrajectoryVisualizer.isEnabled());
        }

        // RangedWeaponItem.createArrowEntity is called before vanilla applies
        // the bow's release velocity. Capture it on the first entity tick so
        // burst shots can reuse the actual shot speed and direction.
        if (this.releaseVelocity == null && this.getVelocity().lengthSquared() > 1.0E-7D) {
            this.releaseVelocity = this.getVelocity();
            this.setTrajectoryInitialVelocity(this.releaseVelocity);
        }

        this.captureFractureReleaseSpeedIfNeeded();

        if (!this.getEntityWorld().isClient() && !this.spawnPositionCaptured) {
            this.spawnX = this.getX();
            this.spawnY = this.getY();
            this.spawnZ = this.getZ();
            this.spawnPositionCaptured = true;
            this.setTrajectorySpawnPosition(this.spawnX, this.spawnY, this.spawnZ);
        }

        super.tick();

        if (!this.isRemoved()) {
            ArrowPhysicsEngine.applyPhysics(this);
            if (!this.isRemoved()) {
                this.processDelayedImpact();
            }
            if (!this.isRemoved()) {
                if (this.burstArrowsRemaining > 0 && !this.burstScheduled
                        && this.getOwner() instanceof net.minecraft.server.network.ServerPlayerEntity) {
                    BurstArrowHandler.scheduleRestored(this);
                }
                this.splitTimer++;
                if (!this.isInGround() && !this.hasSpread && this.fractureLevel > 0
                        && !this.fractureScheduled
                        && this.splitTimer >= this.getFractureSplitDelayTicks()) {
                    this.fractureScheduled = true;
                    FractureScheduler.schedule(this);
                }
                ArrowBehaviorRegistry.getBehavior(this.arrowType).onTick(this);
            }
        }
    }

    public Vec3d getReleaseVelocity() {
        return this.releaseVelocity;
    }

    public ArrowType getArrowType() {
        return parseArrowType(this.dataTracker.get(TRACKED_ARROW_TYPE));
    }

    public void setArrowType(ArrowType arrowType) {
        this.arrowType = arrowType == null ? ArrowType.NORMAL : arrowType;
        this.dataTracker.set(TRACKED_ARROW_TYPE, this.arrowType.name());
    }

    public boolean isTrajectoryPreviewEnabled() {
        return this.dataTracker.get(TRACKED_TRAJECTORY_ENABLED);
    }

    public void setTrajectoryPreviewEnabled(boolean enabled) {
        this.dataTracker.set(TRACKED_TRAJECTORY_ENABLED, enabled);
    }

    public Vec3d getTrajectorySpawnPosition() {
        if (!this.dataTracker.get(TRACKED_TRAJECTORY_SPAWN_CAPTURED)) {
            return null;
        }
        return new Vec3d(
                this.dataTracker.get(TRACKED_TRAJECTORY_SPAWN_X).doubleValue(),
                this.dataTracker.get(TRACKED_TRAJECTORY_SPAWN_Y).doubleValue(),
                this.dataTracker.get(TRACKED_TRAJECTORY_SPAWN_Z).doubleValue()
        );
    }

    /**
     * Returns the immutable release velocity used to rebuild the first part
     * of a trail if the client sees an arrow only after it has travelled.
     */
    public Vec3d getTrajectoryInitialVelocity() {
        Vec3d velocity = new Vec3d(
                this.dataTracker.get(TRACKED_TRAJECTORY_VELOCITY_X).doubleValue(),
                this.dataTracker.get(TRACKED_TRAJECTORY_VELOCITY_Y).doubleValue(),
                this.dataTracker.get(TRACKED_TRAJECTORY_VELOCITY_Z).doubleValue()
        );
        return velocity.lengthSquared() < 1.0E-7D ? null : velocity;
    }

    private void setTrajectorySpawnPosition(double x, double y, double z) {
        this.dataTracker.set(TRACKED_TRAJECTORY_SPAWN_X, (float) x);
        this.dataTracker.set(TRACKED_TRAJECTORY_SPAWN_Y, (float) y);
        this.dataTracker.set(TRACKED_TRAJECTORY_SPAWN_Z, (float) z);
        this.dataTracker.set(TRACKED_TRAJECTORY_SPAWN_CAPTURED, true);
    }

    private void setTrajectoryInitialVelocity(Vec3d velocity) {
        this.dataTracker.set(TRACKED_TRAJECTORY_VELOCITY_X, (float) velocity.x);
        this.dataTracker.set(TRACKED_TRAJECTORY_VELOCITY_Y, (float) velocity.y);
        this.dataTracker.set(TRACKED_TRAJECTORY_VELOCITY_Z, (float) velocity.z);
    }

    public boolean isTrajectoryFinished() {
        return this.dataTracker.get(TRACKED_TRAJECTORY_FINISHED);
    }

    private void setTrajectoryFinished(boolean finished) {
        this.dataTracker.set(TRACKED_TRAJECTORY_FINISHED, finished);
    }

    public boolean canRicochet() {
        return this.bounceCount < this.ricochetLevel;
    }

    public int getRicochetLevel() {
        return this.ricochetLevel;
    }

    public int getBounceCount() {
        return this.bounceCount;
    }

    public void setRicochetLevel(int level) {
        this.ricochetLevel = ConfigManager.limitEnchantmentLevel(level, 5);
    }

    public void setProjectileStack(ItemStack stack) {
        this.setStack(stack.isEmpty() ? new ItemStack(Items.ARROW) : stack);
    }

    public void incrementBounceCount() {
        this.bounceCount++;
    }

    public void clearInGround() {
        this.setInGround(false);
    }

    public boolean isArrowInGround() {
        return this.isInGround();
    }

    public void scheduleDelayedImpact(ArrowType type, Vec3d impact, int delay) {
        if (this.delayedImpactDelay > 0 || type == null || impact == null) {
            return;
        }
        this.delayedImpactType = type;
        this.delayedImpactPosition = impact;
        this.delayedImpactDelay = Math.max(1, delay);
    }

    public float getTidalSpin() {
        return this.tidalSpin;
    }

    public void advanceTidalSpin(float amount) {
        this.tidalSpin = (this.tidalSpin + amount) % 360.0F;
    }

    private void processDelayedImpact() {
        if (this.delayedImpactDelay <= 0 || this.delayedImpactType == null
                || this.delayedImpactPosition == null) {
            return;
        }
        this.delayedImpactDelay--;
        if (this.delayedImpactDelay > 0) {
            return;
        }

        ArrowType type = this.delayedImpactType;
        Vec3d impact = this.delayedImpactPosition;
        this.delayedImpactType = null;
        this.delayedImpactPosition = null;
        ArrowBehaviorRegistry.getBehavior(type).onDelayedImpact(this, impact);
    }

    public int advancePhysicsAge() {
        return ++this.physicsAge;
    }

    public void setOverdrawDamageBonus(double bonus) {
        this.overdrawDamageBonus = Math.max(0.0D, bonus);
    }

    public void setLongshotLevel(int level) {
        this.longshotLevel = ConfigManager.limitEnchantmentLevel(level, 1);
    }

    public void setFractureLevel(int level) {
        this.fractureLevel = ConfigManager.limitEnchantmentLevel(level, 2);
    }

    public int getHeadshotLevel() {
        return this.headshotLevel;
    }

    public void setHeadshotLevel(int level) {
        this.headshotLevel = ConfigManager.limitEnchantmentLevel(level, 3);
    }

    public void prepareHeadshot(EntityHitResult hit) {
        this.pendingHeadshotDamageMultiplier = HeadshotManager.prepareHeadshot(this, hit);
    }

    public double consumeHeadshotDamageMultiplier() {
        double multiplier = this.pendingHeadshotDamageMultiplier;
        this.pendingHeadshotDamageMultiplier = 1.0D;
        return Double.isFinite(multiplier) && multiplier > 0.0D ? multiplier : 1.0D;
    }

    public void setFractureReleaseSpeed(double speed) {
        if (Double.isFinite(speed) && speed > 1.0E-6D) {
            this.fractureReleaseSpeed = speed;
            this.fractureReleaseSpeedCaptured = true;
        }
    }

    public void setExtraAmmoFree(boolean extraAmmoFree) {
        this.extraAmmoFree = extraAmmoFree;
    }

    public boolean isExtraAmmoFree() {
        return this.extraAmmoFree;
    }

    public void setBurstState(int arrowsRemaining, int staggerDelay) {
        this.burstArrowsRemaining = Math.max(0, arrowsRemaining);
        this.burstStaggerDelay = Math.max(1, staggerDelay);
        this.burstStaggerTimer = 0;
    }

    public int getBurstArrowsRemaining() {
        return this.burstArrowsRemaining;
    }

    public int getBurstStaggerDelay() {
        return Math.max(1, this.burstStaggerDelay);
    }

    public int getBurstStaggerTimer() {
        return this.burstStaggerTimer;
    }

    public void setBurstStaggerTimer(int timer) {
        this.burstStaggerTimer = Math.max(0, timer);
    }

    public void setBurstScheduled(boolean scheduled) {
        this.burstScheduled = scheduled;
    }

    public ItemStack getBurstWeaponStack() {
        return ((PersistentProjectileEntityAccessor) (Object) this)
                .archeryRevamped$getWeapon().copy();
    }

    public int getFractureLevel() {
        return this.fractureLevel;
    }

    public void setHasSpread(boolean hasSpread) {
        this.hasSpread = hasSpread;
    }

    public void setFractureScheduled(boolean fractureScheduled) {
        this.fractureScheduled = fractureScheduled;
    }

    public ArcheryArrowEntity createFractureChild(double angleDegrees) {
        // The owner/weapon constructor validates that the firing weapon is a
        // real ranged weapon. A fracture child is not fired by a weapon, so
        // construct it directly and copy the projectile state below.
        ArcheryArrowEntity child = new ArcheryArrowEntity(this.getEntityWorld());

        child.setOwner(this.getOwner());
        child.setProjectileStack(this.createChildPickupStack());
        Vec3d childVelocity = this.getVelocity().rotateY((float) Math.toRadians(angleDegrees));
        Vec3d childDirection = childVelocity.lengthSquared() > 1.0E-7D
                ? childVelocity.normalize() : new Vec3d(0.0D, 0.0D, 1.0D);
        Vec3d childPosition = new Vec3d(this.getX(), this.getY(), this.getZ())
                .add(childDirection.multiply(0.35D));
        child.setPosition(childPosition.x, childPosition.y, childPosition.z);
        child.setVelocity(childVelocity);
        child.setTrajectorySpawnPosition(child.getX(), child.getY(), child.getZ());
        this.copyVanillaProjectileStateTo(child);

        child.setArrowType(this.arrowType);
        child.ricochetLevel = this.ricochetLevel;
        child.bounceCount = this.bounceCount;
        child.distanceTravelled = this.distanceTravelled;
        child.originalDamage = this.originalDamage;
        child.spawnX = this.spawnX;
        child.spawnY = this.spawnY;
        child.spawnZ = this.spawnZ;
        child.physicsAge = this.physicsAge;
        child.spawnPositionCaptured = this.spawnPositionCaptured;
        child.overdrawDamageBonus = this.overdrawDamageBonus;
        child.longshotLevel = this.longshotLevel;
        child.longshot16Triggered = this.longshot16Triggered;
        child.longshot32Triggered = this.longshot32Triggered;
        child.longshot48Triggered = this.longshot48Triggered;
        child.longshot64Triggered = this.longshot64Triggered;
        child.fractureLevel = this.fractureLevel;
        child.splitTimer = this.splitTimer;
        child.hasSpread = true;
        child.fractureScheduled = false;
        child.fractureReleaseSpeed = this.fractureReleaseSpeed;
        child.fractureReleaseSpeedCaptured = this.fractureReleaseSpeedCaptured;
        child.extraAmmoFree = this.extraAmmoFree;
        child.headshotLevel = this.headshotLevel;
        child.impactDamageModifiersApplied = false;
        child.releaseVelocity = this.releaseVelocity;
        child.burstArrowsRemaining = 0;
        child.burstStaggerDelay = 0;
        child.burstStaggerTimer = 0;
        child.burstScheduled = false;
        return child;
    }

    public ArcheryArrowEntity createBurstChild() {
        ArcheryArrowEntity child = new ArcheryArrowEntity(this.getEntityWorld());

        child.setOwner(this.getOwner());
        child.setProjectileStack(this.createChildPickupStack());
        child.setPosition(this.getX(), this.getY(), this.getZ());
        child.setVelocity(this.getVelocity());
        this.copyVanillaProjectileStateTo(child);

        child.setArrowType(this.arrowType);
        child.ricochetLevel = this.ricochetLevel;
        child.bounceCount = 0;
        child.distanceTravelled = 0.0D;
        child.originalDamage = this.originalDamage;
        child.overdrawDamageBonus = this.overdrawDamageBonus;
        child.longshotLevel = this.longshotLevel;
        child.fractureLevel = this.fractureLevel;
        child.fractureReleaseSpeed = this.fractureReleaseSpeed;
        child.fractureReleaseSpeedCaptured = this.fractureReleaseSpeedCaptured;
        child.extraAmmoFree = this.extraAmmoFree;
        child.headshotLevel = this.headshotLevel;
        child.splitTimer = 0;
        child.hasSpread = false;
        child.fractureScheduled = false;
        child.spawnPositionCaptured = false;
        child.longshot16Triggered = false;
        child.longshot32Triggered = false;
        child.longshot48Triggered = false;
        child.longshot64Triggered = false;
        child.impactDamageModifiersApplied = false;
        child.releaseVelocity = this.releaseVelocity;
        child.burstArrowsRemaining = 0;
        child.burstStaggerDelay = 0;
        child.burstStaggerTimer = 0;
        child.burstScheduled = false;
        return child;
    }

    private void copyVanillaProjectileStateTo(ArcheryArrowEntity child) {
        PersistentProjectileEntityAccessor source = (PersistentProjectileEntityAccessor) (Object) this;
        PersistentProjectileEntityAccessor target = (PersistentProjectileEntityAccessor) (Object) child;
        child.setCritical(this.isCritical());
        child.setNoClip(this.isNoClip());
        target.archeryRevamped$setPierceLevel(this.getPierceLevel());
        child.setDamage(source.archeryRevamped$getDamage());
        child.setFireTicks(this.getFireTicks());
        target.archeryRevamped$setWeapon(source.archeryRevamped$getWeapon().copy());
        target.archeryRevamped$setPickupType(source.archeryRevamped$getPickupType());
    }

    private void captureFractureReleaseSpeedIfNeeded() {
        if (!this.fractureReleaseSpeedCaptured) {
            this.setFractureReleaseSpeed(this.getVelocity().length());
        }
    }

    /**
     * The intangible-projectile component is a firing/ammo marker used by
     * vanilla for creative and Infinity shots. It must not be copied to a
     * child arrow's pickup stack, otherwise a child can become permanently
     * unpickable even when the original arrow was a normal pickup arrow.
     */
    private ItemStack createChildPickupStack() {
        ItemStack pickupStack = this.getItemStack().copy();
        pickupStack.remove(DataComponentTypes.INTANGIBLE_PROJECTILE);
        return pickupStack;
    }

    private int getFractureSplitDelayTicks() {
        double referenceSpeed = Math.max(ConfigManager.getFractureReferenceReleaseSpeed(), 1.0E-6D);
        double releaseSpeed = Math.max(this.fractureReleaseSpeed, 1.0E-6D);
        double scaledDelay = ConfigManager.getFractureSplitDelayTicks()
                * (referenceSpeed / releaseSpeed);
        int delay = (int) Math.ceil(scaledDelay);
        int minimum = Math.max(1, ConfigManager.getFractureMinSplitDelayTicks());
        int maximum = Math.max(minimum, ConfigManager.getFractureMaxSplitDelayTicks());
        return Math.max(minimum, Math.min(delay, maximum));
    }

    public void updateDistanceTravelled() {
        if (!this.spawnPositionCaptured) {
            return;
        }

        double dx = this.getX() - this.spawnX;
        double dy = this.getY() - this.spawnY;
        double dz = this.getZ() - this.spawnZ;
        this.distanceTravelled = Math.sqrt(dx * dx + dy * dy + dz * dz);
        this.checkLongshotThresholds();
    }

    private void checkLongshotThresholds() {
        if (this.longshotLevel <= 0 || !(this.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        if (!this.longshot16Triggered
                && this.distanceTravelled >= ConfigManager.getLongshot16Threshold()) {
            this.longshot16Triggered = true;
        }

        if (!this.longshot32Triggered
                && this.distanceTravelled >= ConfigManager.getLongshot32Threshold()) {
            this.longshot32Triggered = true;
            Vec3d effectPosition = new Vec3d(this.getX(), this.getY(), this.getZ());
            EffectManager.spawnParticles(this.getEntityWorld(), effectPosition,
                    ParticleTypes.HAPPY_VILLAGER, 12);
            EffectManager.playSound(this.getEntityWorld(), effectPosition,
                    SoundEvents.BLOCK_NOTE_BLOCK_PLING, 0.6F, 0.65F);
        }

        if (!this.longshot48Triggered
                && this.distanceTravelled >= ConfigManager.getLongshot48Threshold()) {
            this.longshot48Triggered = true;
        }

        if (!this.longshot64Triggered
                && this.distanceTravelled >= ConfigManager.getLongshot64Threshold()) {
            this.longshot64Triggered = true;
            Vec3d effectPosition = new Vec3d(this.getX(), this.getY(), this.getZ());
            EffectManager.spawnParticles(this.getEntityWorld(), effectPosition,
                    new DustParticleEffect(0xFFD700, 1.0F), 16);
            EffectManager.playSound(this.getEntityWorld(), effectPosition,
                    SoundEvents.BLOCK_NOTE_BLOCK_PLING, 0.75F, 1.3F);
        }
    }

    private void applyImpactDamageModifiers() {
        if (this.impactDamageModifiersApplied) {
            return;
        }

        double damage = ((PersistentProjectileEntityAccessor) (Object) this).archeryRevamped$getDamage();
        if (this.overdrawDamageBonus > 0.0D) {
            damage *= 1.0D + this.overdrawDamageBonus;
        }

        if (this.longshotLevel > 0) {
            double multiplier = this.distanceTravelled >= ConfigManager.getLongshot64Threshold()
                    ? ConfigManager.getLongshot64Multiplier()
                    : this.distanceTravelled >= ConfigManager.getLongshot48Threshold()
                    ? ConfigManager.getLongshot48Multiplier()
                    : this.distanceTravelled >= ConfigManager.getLongshot32Threshold()
                    ? ConfigManager.getLongshot32Multiplier()
                    : this.distanceTravelled >= ConfigManager.getLongshot16Threshold()
                    ? ConfigManager.getLongshot16Multiplier() : 1.0D;
            damage *= multiplier;
        }

        this.setDamage(damage);
        this.impactDamageModifiersApplied = true;
    }

    @Override
    protected void onBlockHit(BlockHitResult hit) {
        this.updateDistanceTravelled();
        if (this.canRicochet() && !this.isInGround()) {
            RicochetBehavior.applyRicochet(this, hit);
            return;
        }

        this.setTrajectoryFinished(true);
        ArrowBehaviorRegistry.getBehavior(this.arrowType).onBlockHit(this, hit);
        if (!this.isRemoved()) {
            super.onBlockHit(hit);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        this.updateDistanceTravelled();
        this.applyImpactDamageModifiers();
        this.prepareHeadshot(hit);
        this.setTrajectoryFinished(true);
        super.onEntityHit(hit);
        ArrowBehaviorRegistry.getBehavior(this.arrowType).onEntityHit(this, hit);
    }

    protected void saveSyncData(NbtCompound tag) {
        tag.putInt(DATA_VERSION_KEY, CURRENT_DATA_VERSION);
        tag.putString(ARROW_TYPE_KEY, this.arrowType.name());
        tag.putInt(RICOCHET_LEVEL_KEY, ConfigManager.limitEnchantmentLevel(this.ricochetLevel, 5));
        tag.putInt(BOUNCE_COUNT_KEY, Math.max(0, this.bounceCount));
        tag.putDouble(DISTANCE_TRAVELLED_KEY, finiteOrZero(this.distanceTravelled));
        tag.putDouble(ORIGINAL_DAMAGE_KEY, finiteOrZero(this.originalDamage));
        tag.putDouble(SPAWN_X_KEY, finiteOrZero(this.spawnX));
        tag.putDouble(SPAWN_Y_KEY, finiteOrZero(this.spawnY));
        tag.putDouble(SPAWN_Z_KEY, finiteOrZero(this.spawnZ));
        tag.putInt(PHYSICS_AGE_KEY, Math.max(0, this.physicsAge));
        tag.putDouble(OVERDRAW_DAMAGE_BONUS_KEY, finiteOrZero(this.overdrawDamageBonus));
        tag.putInt(LONGSHOT_LEVEL_KEY, ConfigManager.limitEnchantmentLevel(this.longshotLevel, 1));
        tag.putBoolean(LONGSHOT_16_TRIGGERED_KEY, this.longshot16Triggered);
        tag.putBoolean(LONGSHOT_32_TRIGGERED_KEY, this.longshot32Triggered);
        tag.putBoolean(LONGSHOT_48_TRIGGERED_KEY, this.longshot48Triggered);
        tag.putBoolean(LONGSHOT_64_TRIGGERED_KEY, this.longshot64Triggered);
        tag.putInt(FRACTURE_LEVEL_KEY, ConfigManager.limitEnchantmentLevel(this.fractureLevel, 2));
        tag.putInt(SPLIT_TIMER_KEY, Math.max(0, this.splitTimer));
        tag.putBoolean(HAS_SPREAD_KEY, this.hasSpread);
        tag.putDouble(FRACTURE_RELEASE_SPEED_KEY, finiteOrZero(this.fractureReleaseSpeed));
        tag.putBoolean(EXTRA_AMMO_FREE_KEY, this.extraAmmoFree);
        tag.putBoolean(IMPACT_DAMAGE_MODIFIERS_APPLIED_KEY, this.impactDamageModifiersApplied);
        tag.putBoolean(SPAWN_POSITION_CAPTURED_KEY, this.spawnPositionCaptured);
        tag.putInt(BURST_ARROWS_REMAINING_KEY, Math.max(0, this.burstArrowsRemaining));
        tag.putInt(BURST_STAGGER_DELAY_KEY, Math.max(1, this.burstStaggerDelay));
        tag.putInt(BURST_STAGGER_TIMER_KEY, Math.max(0, this.burstStaggerTimer));
        tag.putBoolean(TRAJECTORY_FINISHED_KEY, this.isTrajectoryFinished());
        tag.putInt(HEADSHOT_LEVEL_KEY, ConfigManager.limitEnchantmentLevel(this.headshotLevel, 3));
        writeDelayedImpact(tag);
        writeReleaseVelocity(tag);
    }

    protected void loadSyncData(NbtCompound tag) {
        int dataVersion = tag.getInt(DATA_VERSION_KEY).orElse(0);
        // Version 0 is the original format. Unknown future versions are
        // intentionally read using known keys so adding fields remains safe.
        if (dataVersion < 2 && tag.contains("bouncingLevel") && !tag.contains(RICOCHET_LEVEL_KEY)) {
            this.ricochetLevel = tag.getInt("bouncingLevel").orElse(0);
        }
        this.setArrowType(parseStoredArrowType(
                tag.getString(ARROW_TYPE_KEY).orElse(ArrowType.NORMAL.name()), dataVersion));
        this.ricochetLevel = ConfigManager.limitEnchantmentLevel(
                tag.getInt(RICOCHET_LEVEL_KEY).orElse(this.ricochetLevel), 5);
        this.bounceCount = Math.max(0, tag.getInt(BOUNCE_COUNT_KEY).orElse(0));
        this.distanceTravelled = finiteOrZero(tag.getDouble(DISTANCE_TRAVELLED_KEY).orElse(0.0D));
        this.originalDamage = finiteOrZero(tag.getDouble(ORIGINAL_DAMAGE_KEY).orElse(0.0D));
        this.spawnX = finiteOrZero(tag.getDouble(SPAWN_X_KEY).orElse(0.0D));
        this.spawnY = finiteOrZero(tag.getDouble(SPAWN_Y_KEY).orElse(0.0D));
        this.spawnZ = finiteOrZero(tag.getDouble(SPAWN_Z_KEY).orElse(0.0D));
        this.physicsAge = Math.max(0, tag.getInt(PHYSICS_AGE_KEY).orElse(0));
        this.overdrawDamageBonus = finiteOrZero(tag.getDouble(OVERDRAW_DAMAGE_BONUS_KEY).orElse(0.0D));
        this.longshotLevel = ConfigManager.limitEnchantmentLevel(
                tag.getInt(LONGSHOT_LEVEL_KEY).orElse(0), 1);
        this.longshot16Triggered = tag.getBoolean(LONGSHOT_16_TRIGGERED_KEY).orElse(false);
        this.longshot32Triggered = tag.getBoolean(LONGSHOT_32_TRIGGERED_KEY).orElse(false);
        this.longshot48Triggered = tag.getBoolean(LONGSHOT_48_TRIGGERED_KEY).orElse(false);
        this.longshot64Triggered = tag.getBoolean(LONGSHOT_64_TRIGGERED_KEY).orElse(false);
        this.fractureLevel = ConfigManager.limitEnchantmentLevel(
                tag.getInt(FRACTURE_LEVEL_KEY).orElse(0), 2);
        this.splitTimer = Math.max(0, tag.getInt(SPLIT_TIMER_KEY).orElse(0));
        this.hasSpread = tag.getBoolean(HAS_SPREAD_KEY).orElse(false);
        this.fractureReleaseSpeed = finiteOrZero(tag.getDouble(FRACTURE_RELEASE_SPEED_KEY).orElse(0.0D));
        this.extraAmmoFree = tag.getBoolean(EXTRA_AMMO_FREE_KEY).orElse(false);
        this.impactDamageModifiersApplied = tag.getBoolean(IMPACT_DAMAGE_MODIFIERS_APPLIED_KEY).orElse(false);
        this.fractureReleaseSpeedCaptured = this.fractureReleaseSpeed > 1.0E-6D;
        this.spawnPositionCaptured = tag.getBoolean(SPAWN_POSITION_CAPTURED_KEY).orElse(
                tag.contains(SPAWN_X_KEY) && tag.contains(SPAWN_Y_KEY) && tag.contains(SPAWN_Z_KEY));
        if (this.spawnPositionCaptured) {
            this.setTrajectorySpawnPosition(this.spawnX, this.spawnY, this.spawnZ);
        }
        this.burstArrowsRemaining = Math.max(0, tag.getInt(BURST_ARROWS_REMAINING_KEY).orElse(0));
        this.burstStaggerDelay = Math.max(1, tag.getInt(BURST_STAGGER_DELAY_KEY).orElse(1));
        this.burstStaggerTimer = Math.max(0, tag.getInt(BURST_STAGGER_TIMER_KEY).orElse(0));
        this.burstScheduled = false;
        this.setTrajectoryFinished(tag.getBoolean(TRAJECTORY_FINISHED_KEY).orElse(this.isInGround()));
        this.headshotLevel = ConfigManager.limitEnchantmentLevel(
                tag.getInt(HEADSHOT_LEVEL_KEY).orElse(0), 3);
        readDelayedImpact(tag);
        readReleaseVelocity(tag);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putInt(DATA_VERSION_KEY, CURRENT_DATA_VERSION);
        view.putString(ARROW_TYPE_KEY, this.arrowType.name());
        view.putInt(RICOCHET_LEVEL_KEY, ConfigManager.limitEnchantmentLevel(this.ricochetLevel, 5));
        view.putInt(BOUNCE_COUNT_KEY, Math.max(0, this.bounceCount));
        view.putDouble(DISTANCE_TRAVELLED_KEY, finiteOrZero(this.distanceTravelled));
        view.putDouble(ORIGINAL_DAMAGE_KEY, finiteOrZero(this.originalDamage));
        view.putDouble(SPAWN_X_KEY, finiteOrZero(this.spawnX));
        view.putDouble(SPAWN_Y_KEY, finiteOrZero(this.spawnY));
        view.putDouble(SPAWN_Z_KEY, finiteOrZero(this.spawnZ));
        view.putInt(PHYSICS_AGE_KEY, Math.max(0, this.physicsAge));
        view.putDouble(OVERDRAW_DAMAGE_BONUS_KEY, finiteOrZero(this.overdrawDamageBonus));
        view.putInt(LONGSHOT_LEVEL_KEY, ConfigManager.limitEnchantmentLevel(this.longshotLevel, 1));
        view.putBoolean(LONGSHOT_16_TRIGGERED_KEY, this.longshot16Triggered);
        view.putBoolean(LONGSHOT_32_TRIGGERED_KEY, this.longshot32Triggered);
        view.putBoolean(LONGSHOT_48_TRIGGERED_KEY, this.longshot48Triggered);
        view.putBoolean(LONGSHOT_64_TRIGGERED_KEY, this.longshot64Triggered);
        view.putInt(FRACTURE_LEVEL_KEY, ConfigManager.limitEnchantmentLevel(this.fractureLevel, 2));
        view.putInt(SPLIT_TIMER_KEY, Math.max(0, this.splitTimer));
        view.putBoolean(HAS_SPREAD_KEY, this.hasSpread);
        view.putDouble(FRACTURE_RELEASE_SPEED_KEY, finiteOrZero(this.fractureReleaseSpeed));
        view.putBoolean(EXTRA_AMMO_FREE_KEY, this.extraAmmoFree);
        view.putBoolean(IMPACT_DAMAGE_MODIFIERS_APPLIED_KEY, this.impactDamageModifiersApplied);
        view.putBoolean(SPAWN_POSITION_CAPTURED_KEY, this.spawnPositionCaptured);
        view.putInt(BURST_ARROWS_REMAINING_KEY, Math.max(0, this.burstArrowsRemaining));
        view.putInt(BURST_STAGGER_DELAY_KEY, Math.max(1, this.burstStaggerDelay));
        view.putInt(BURST_STAGGER_TIMER_KEY, Math.max(0, this.burstStaggerTimer));
        view.putBoolean(TRAJECTORY_FINISHED_KEY, this.isTrajectoryFinished());
        view.putInt(HEADSHOT_LEVEL_KEY, ConfigManager.limitEnchantmentLevel(this.headshotLevel, 3));
        writeDelayedImpact(view);
        writeReleaseVelocity(view);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        int dataVersion = view.getInt(DATA_VERSION_KEY, 0);
        this.setArrowType(parseStoredArrowType(
                view.getString(ARROW_TYPE_KEY, ArrowType.NORMAL.name()), dataVersion));
        this.ricochetLevel = ConfigManager.limitEnchantmentLevel(
                readIntWithLegacy(view, RICOCHET_LEVEL_KEY, "bouncingLevel", 0), 5);
        this.bounceCount = Math.max(0, view.getInt(BOUNCE_COUNT_KEY, 0));
        this.distanceTravelled = finiteOrZero(view.getDouble(DISTANCE_TRAVELLED_KEY, 0.0D));
        this.originalDamage = finiteOrZero(view.getDouble(ORIGINAL_DAMAGE_KEY, 0.0D));
        this.spawnX = finiteOrZero(view.getDouble(SPAWN_X_KEY, 0.0D));
        this.spawnY = finiteOrZero(view.getDouble(SPAWN_Y_KEY, 0.0D));
        this.spawnZ = finiteOrZero(view.getDouble(SPAWN_Z_KEY, 0.0D));
        this.physicsAge = Math.max(0, view.getInt(PHYSICS_AGE_KEY, 0));
        this.overdrawDamageBonus = finiteOrZero(view.getDouble(OVERDRAW_DAMAGE_BONUS_KEY, 0.0D));
        this.longshotLevel = ConfigManager.limitEnchantmentLevel(view.getInt(LONGSHOT_LEVEL_KEY, 0), 1);
        this.longshot16Triggered = view.getBoolean(LONGSHOT_16_TRIGGERED_KEY, false);
        this.longshot32Triggered = view.getBoolean(LONGSHOT_32_TRIGGERED_KEY, false);
        this.longshot48Triggered = view.getBoolean(LONGSHOT_48_TRIGGERED_KEY, false);
        this.longshot64Triggered = view.getBoolean(LONGSHOT_64_TRIGGERED_KEY, false);
        this.fractureLevel = ConfigManager.limitEnchantmentLevel(view.getInt(FRACTURE_LEVEL_KEY, 0), 2);
        this.splitTimer = Math.max(0, view.getInt(SPLIT_TIMER_KEY, 0));
        this.hasSpread = view.getBoolean(HAS_SPREAD_KEY, false);
        this.fractureReleaseSpeed = finiteOrZero(view.getDouble(FRACTURE_RELEASE_SPEED_KEY, 0.0D));
        this.extraAmmoFree = view.getBoolean(EXTRA_AMMO_FREE_KEY, false);
        this.impactDamageModifiersApplied = view.getBoolean(IMPACT_DAMAGE_MODIFIERS_APPLIED_KEY, false);
        this.fractureReleaseSpeedCaptured = this.fractureReleaseSpeed > 1.0E-6D;
        this.spawnPositionCaptured = view.getBoolean(SPAWN_POSITION_CAPTURED_KEY,
                view.getOptionalString(ARROW_TYPE_KEY).isPresent());
        if (this.spawnPositionCaptured) {
            this.setTrajectorySpawnPosition(this.spawnX, this.spawnY, this.spawnZ);
        }
        this.burstArrowsRemaining = Math.max(0, view.getInt(BURST_ARROWS_REMAINING_KEY, 0));
        this.burstStaggerDelay = Math.max(1, view.getInt(BURST_STAGGER_DELAY_KEY, 1));
        this.burstStaggerTimer = Math.max(0, view.getInt(BURST_STAGGER_TIMER_KEY, 0));
        this.burstScheduled = false;
        this.setTrajectoryFinished(view.getBoolean(TRAJECTORY_FINISHED_KEY, this.isInGround()));
        this.headshotLevel = ConfigManager.limitEnchantmentLevel(view.getInt(HEADSHOT_LEVEL_KEY, 0), 3);
        readDelayedImpact(view);
        readReleaseVelocity(view);
    }

    private void writeDelayedImpact(NbtCompound tag) {
        tag.putInt(DELAYED_IMPACT_DELAY_KEY, Math.max(0, this.delayedImpactDelay));
        if (this.delayedImpactDelay > 0 && this.delayedImpactType != null
                && this.delayedImpactPosition != null) {
            tag.putString(DELAYED_IMPACT_TYPE_KEY, this.delayedImpactType.name());
            tag.putDouble(DELAYED_IMPACT_X_KEY, finiteOrZero(this.delayedImpactPosition.x));
            tag.putDouble(DELAYED_IMPACT_Y_KEY, finiteOrZero(this.delayedImpactPosition.y));
            tag.putDouble(DELAYED_IMPACT_Z_KEY, finiteOrZero(this.delayedImpactPosition.z));
        }
    }

    private void writeDelayedImpact(WriteView view) {
        view.putInt(DELAYED_IMPACT_DELAY_KEY, Math.max(0, this.delayedImpactDelay));
        if (this.delayedImpactDelay > 0 && this.delayedImpactType != null
                && this.delayedImpactPosition != null) {
            view.putString(DELAYED_IMPACT_TYPE_KEY, this.delayedImpactType.name());
            view.putDouble(DELAYED_IMPACT_X_KEY, finiteOrZero(this.delayedImpactPosition.x));
            view.putDouble(DELAYED_IMPACT_Y_KEY, finiteOrZero(this.delayedImpactPosition.y));
            view.putDouble(DELAYED_IMPACT_Z_KEY, finiteOrZero(this.delayedImpactPosition.z));
        }
    }

    private void readDelayedImpact(NbtCompound tag) {
        this.delayedImpactDelay = Math.max(0, tag.getInt(DELAYED_IMPACT_DELAY_KEY).orElse(0));
        String type = tag.getString(DELAYED_IMPACT_TYPE_KEY).orElse("");
        this.delayedImpactType = type.isEmpty() ? null : parseArrowType(type);
        this.delayedImpactPosition = this.delayedImpactDelay > 0 && this.delayedImpactType != null
                ? new Vec3d(
                finiteOrZero(tag.getDouble(DELAYED_IMPACT_X_KEY).orElse(0.0D)),
                finiteOrZero(tag.getDouble(DELAYED_IMPACT_Y_KEY).orElse(0.0D)),
                finiteOrZero(tag.getDouble(DELAYED_IMPACT_Z_KEY).orElse(0.0D)))
                : null;
    }

    private void readDelayedImpact(ReadView view) {
        this.delayedImpactDelay = Math.max(0, view.getInt(DELAYED_IMPACT_DELAY_KEY, 0));
        String type = view.getString(DELAYED_IMPACT_TYPE_KEY, "");
        this.delayedImpactType = type.isEmpty() ? null : parseArrowType(type);
        this.delayedImpactPosition = this.delayedImpactDelay > 0 && this.delayedImpactType != null
                ? new Vec3d(
                finiteOrZero(view.getDouble(DELAYED_IMPACT_X_KEY, 0.0D)),
                finiteOrZero(view.getDouble(DELAYED_IMPACT_Y_KEY, 0.0D)),
                finiteOrZero(view.getDouble(DELAYED_IMPACT_Z_KEY, 0.0D)))
                : null;
    }

    private void writeReleaseVelocity(NbtCompound tag) {
        tag.putBoolean(RELEASE_VELOCITY_PRESENT_KEY, this.releaseVelocity != null);
        if (this.releaseVelocity != null) {
            tag.putDouble(RELEASE_VELOCITY_X_KEY, finiteOrZero(this.releaseVelocity.x));
            tag.putDouble(RELEASE_VELOCITY_Y_KEY, finiteOrZero(this.releaseVelocity.y));
            tag.putDouble(RELEASE_VELOCITY_Z_KEY, finiteOrZero(this.releaseVelocity.z));
        }
    }

    private void writeReleaseVelocity(WriteView view) {
        view.putBoolean(RELEASE_VELOCITY_PRESENT_KEY, this.releaseVelocity != null);
        if (this.releaseVelocity != null) {
            view.putDouble(RELEASE_VELOCITY_X_KEY, finiteOrZero(this.releaseVelocity.x));
            view.putDouble(RELEASE_VELOCITY_Y_KEY, finiteOrZero(this.releaseVelocity.y));
            view.putDouble(RELEASE_VELOCITY_Z_KEY, finiteOrZero(this.releaseVelocity.z));
        }
    }

    private void readReleaseVelocity(NbtCompound tag) {
        if (!tag.getBoolean(RELEASE_VELOCITY_PRESENT_KEY).orElse(false)) {
            this.releaseVelocity = null;
            return;
        }
        this.releaseVelocity = new Vec3d(
                finiteOrZero(tag.getDouble(RELEASE_VELOCITY_X_KEY).orElse(0.0D)),
                finiteOrZero(tag.getDouble(RELEASE_VELOCITY_Y_KEY).orElse(0.0D)),
                finiteOrZero(tag.getDouble(RELEASE_VELOCITY_Z_KEY).orElse(0.0D))
        );
        this.setTrajectoryInitialVelocity(this.releaseVelocity);
    }

    private void readReleaseVelocity(ReadView view) {
        if (!view.getBoolean(RELEASE_VELOCITY_PRESENT_KEY, false)) {
            this.releaseVelocity = null;
            return;
        }
        this.releaseVelocity = new Vec3d(
                finiteOrZero(view.getDouble(RELEASE_VELOCITY_X_KEY, 0.0D)),
                finiteOrZero(view.getDouble(RELEASE_VELOCITY_Y_KEY, 0.0D)),
                finiteOrZero(view.getDouble(RELEASE_VELOCITY_Z_KEY, 0.0D))
        );
        this.setTrajectoryInitialVelocity(this.releaseVelocity);
    }

    private static int readIntWithLegacy(ReadView view, String key, String legacyKey, int fallback) {
        return view.getOptionalInt(key).orElse(view.getInt(legacyKey, fallback));
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static double finiteOrZero(double value) {
        return Double.isFinite(value) ? value : 0.0D;
    }

    private static ArrowType parseArrowType(String value) {
        try {
            return ArrowType.valueOf(value.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return ArrowType.NORMAL;
        }
    }

    private static ArrowType parseStoredArrowType(String value, int dataVersion) {
        // Before v1.2, IMPULSE meant the outward Shockwave behavior. Keep
        // arrows already in loaded worlds consistent with their old effect.
        if (dataVersion < 3 && "IMPULSE".equalsIgnoreCase(value)) {
            return ArrowType.SHOCKWAVE;
        }
        return parseArrowType(value);
    }
}
