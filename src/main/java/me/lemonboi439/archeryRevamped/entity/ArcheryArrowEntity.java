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
import net.minecraft.potion.PotionUtil;
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
    private ItemStack projectileStack = new ItemStack(Items.ARROW);

    public ArcheryArrowEntity(World world) {
        super(ModEntities.ARCHERY_ARROW, world);
    }

    public ArcheryArrowEntity(World world, LivingEntity owner) {
        this(world, owner, new ItemStack(Items.ARROW), owner.getMainHandStack().copy());
    }

    public ArcheryArrowEntity(World world, LivingEntity owner, ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        super(ModEntities.ARCHERY_ARROW, owner, world);
        this.projectileStack = pickupItemStack.isEmpty() ? new ItemStack(Items.ARROW) : pickupItemStack.copy();
    }

    public ArcheryArrowEntity(World world, double x, double y, double z,
                              ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        super(ModEntities.ARCHERY_ARROW, x, y, z, world);
        this.projectileStack = pickupItemStack.isEmpty() ? new ItemStack(Items.ARROW) : pickupItemStack.copy();
    }

    public ArcheryArrowEntity(EntityType<? extends ArcheryArrowEntity> type, World world) {
        super(type, world);
        this.projectileStack = new ItemStack(Items.ARROW);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TRACKED_ARROW_TYPE, ArrowType.NORMAL.name());
        this.dataTracker.startTracking(TRACKED_TRAJECTORY_ENABLED, false);
        this.dataTracker.startTracking(TRACKED_TRAJECTORY_FINISHED, false);
        this.dataTracker.startTracking(TRACKED_TRAJECTORY_SPAWN_X, 0.0F);
        this.dataTracker.startTracking(TRACKED_TRAJECTORY_SPAWN_Y, 0.0F);
        this.dataTracker.startTracking(TRACKED_TRAJECTORY_SPAWN_Z, 0.0F);
        this.dataTracker.startTracking(TRACKED_TRAJECTORY_SPAWN_CAPTURED, false);
        this.dataTracker.startTracking(TRACKED_TRAJECTORY_VELOCITY_X, 0.0F);
        this.dataTracker.startTracking(TRACKED_TRAJECTORY_VELOCITY_Y, 0.0F);
        this.dataTracker.startTracking(TRACKED_TRAJECTORY_VELOCITY_Z, 0.0F);
    }

    @Override
    protected ItemStack asItemStack() {
        return this.projectileStack.copy();
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
                if (!this.inGround && !this.hasSpread && this.fractureLevel > 0
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
        this.projectileStack = stack.isEmpty() ? new ItemStack(Items.ARROW) : stack.copy();
    }

    public ItemStack getItemStack() {
        return this.projectileStack;
    }

    public void incrementBounceCount() {
        this.bounceCount++;
    }

    public void clearInGround() {
        this.inGround = false;
    }

    public boolean isArrowInGround() {
        return this.inGround;
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
        return this.getOwner() instanceof LivingEntity owner
                ? owner.getMainHandStack().copy()
                : ItemStack.EMPTY;
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
        target.archeryRevamped$setPickupType(source.archeryRevamped$getPickupType());
    }

    private void captureFractureReleaseSpeedIfNeeded() {
        if (!this.fractureReleaseSpeedCaptured) {
            this.setFractureReleaseSpeed(this.getVelocity().length());
        }
    }

    /** Child pickup stacks are always copied as ordinary, pickable 1.20.1 items. */
    private ItemStack createChildPickupStack() {
        return this.projectileStack.copy();
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
                    new DustParticleEffect(new org.joml.Vector3f(1.0F, 0.84F, 0.0F), 1.0F), 16);
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
        if (this.canRicochet() && !this.inGround) {
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
        double headshotMultiplier = this.consumeHeadshotDamageMultiplier();
        if (headshotMultiplier != 1.0D) {
            PersistentProjectileEntityAccessor accessor = (PersistentProjectileEntityAccessor) (Object) this;
            this.setDamage(accessor.archeryRevamped$getDamage() * headshotMultiplier);
        }
        this.setTrajectoryFinished(true);
        super.onEntityHit(hit);
        this.applyTippedEffects(hit.getEntity());
        ArrowBehaviorRegistry.getBehavior(this.arrowType).onEntityHit(this, hit);
    }

    private void applyTippedEffects(Entity target) {
        if (!(target instanceof LivingEntity living) || !this.projectileStack.isOf(Items.TIPPED_ARROW)) {
            return;
        }
        for (net.minecraft.entity.effect.StatusEffectInstance effect : PotionUtil.getPotionEffects(this.projectileStack)) {
            living.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    effect.getEffectType(), Math.max(1, effect.getDuration() / 8), effect.getAmplifier(),
                    effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()
            ), this.getOwner());
        }
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
        int dataVersion = tag.getInt(DATA_VERSION_KEY);
        // Version 0 is the original format. Unknown future versions are
        // intentionally read using known keys so adding fields remains safe.
        if (dataVersion < 2 && tag.contains("bouncingLevel") && !tag.contains(RICOCHET_LEVEL_KEY)) {
            this.ricochetLevel = tag.getInt("bouncingLevel");
        }
        this.setArrowType(parseStoredArrowType(
                tag.contains(ARROW_TYPE_KEY) ? tag.getString(ARROW_TYPE_KEY) : ArrowType.NORMAL.name(), dataVersion));
        this.ricochetLevel = ConfigManager.limitEnchantmentLevel(
                tag.contains(RICOCHET_LEVEL_KEY) ? tag.getInt(RICOCHET_LEVEL_KEY) : this.ricochetLevel, 5);
        this.bounceCount = Math.max(0, tag.getInt(BOUNCE_COUNT_KEY));
        this.distanceTravelled = finiteOrZero(tag.getDouble(DISTANCE_TRAVELLED_KEY));
        this.originalDamage = finiteOrZero(tag.getDouble(ORIGINAL_DAMAGE_KEY));
        this.spawnX = finiteOrZero(tag.getDouble(SPAWN_X_KEY));
        this.spawnY = finiteOrZero(tag.getDouble(SPAWN_Y_KEY));
        this.spawnZ = finiteOrZero(tag.getDouble(SPAWN_Z_KEY));
        this.physicsAge = Math.max(0, tag.getInt(PHYSICS_AGE_KEY));
        this.overdrawDamageBonus = finiteOrZero(tag.getDouble(OVERDRAW_DAMAGE_BONUS_KEY));
        this.longshotLevel = ConfigManager.limitEnchantmentLevel(
                tag.getInt(LONGSHOT_LEVEL_KEY), 1);
        this.longshot16Triggered = tag.getBoolean(LONGSHOT_16_TRIGGERED_KEY);
        this.longshot32Triggered = tag.getBoolean(LONGSHOT_32_TRIGGERED_KEY);
        this.longshot48Triggered = tag.getBoolean(LONGSHOT_48_TRIGGERED_KEY);
        this.longshot64Triggered = tag.getBoolean(LONGSHOT_64_TRIGGERED_KEY);
        this.fractureLevel = ConfigManager.limitEnchantmentLevel(
                tag.getInt(FRACTURE_LEVEL_KEY), 2);
        this.splitTimer = Math.max(0, tag.getInt(SPLIT_TIMER_KEY));
        this.hasSpread = tag.getBoolean(HAS_SPREAD_KEY);
        this.fractureReleaseSpeed = finiteOrZero(tag.getDouble(FRACTURE_RELEASE_SPEED_KEY));
        this.extraAmmoFree = tag.getBoolean(EXTRA_AMMO_FREE_KEY);
        this.impactDamageModifiersApplied = tag.getBoolean(IMPACT_DAMAGE_MODIFIERS_APPLIED_KEY);
        this.fractureReleaseSpeedCaptured = this.fractureReleaseSpeed > 1.0E-6D;
        this.spawnPositionCaptured = tag.contains(SPAWN_POSITION_CAPTURED_KEY)
                ? tag.getBoolean(SPAWN_POSITION_CAPTURED_KEY)
                : tag.contains(SPAWN_X_KEY) && tag.contains(SPAWN_Y_KEY) && tag.contains(SPAWN_Z_KEY);
        if (this.spawnPositionCaptured) {
            this.setTrajectorySpawnPosition(this.spawnX, this.spawnY, this.spawnZ);
        }
        this.burstArrowsRemaining = Math.max(0, tag.getInt(BURST_ARROWS_REMAINING_KEY));
        this.burstStaggerDelay = Math.max(1, tag.getInt(BURST_STAGGER_DELAY_KEY));
        this.burstStaggerTimer = Math.max(0, tag.getInt(BURST_STAGGER_TIMER_KEY));
        this.burstScheduled = false;
        this.setTrajectoryFinished(tag.contains(TRAJECTORY_FINISHED_KEY)
                ? tag.getBoolean(TRAJECTORY_FINISHED_KEY) : this.inGround);
        this.headshotLevel = ConfigManager.limitEnchantmentLevel(
                tag.getInt(HEADSHOT_LEVEL_KEY), 3);
        readDelayedImpact(tag);
        readReleaseVelocity(tag);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        this.saveSyncData(tag);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        this.loadSyncData(tag);
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

    private void readDelayedImpact(NbtCompound tag) {
        this.delayedImpactDelay = Math.max(0, tag.getInt(DELAYED_IMPACT_DELAY_KEY));
        String type = tag.contains(DELAYED_IMPACT_TYPE_KEY) ? tag.getString(DELAYED_IMPACT_TYPE_KEY) : "";
        this.delayedImpactType = type.isEmpty() ? null : parseArrowType(type);
        this.delayedImpactPosition = this.delayedImpactDelay > 0 && this.delayedImpactType != null
                ? new Vec3d(
                finiteOrZero(tag.getDouble(DELAYED_IMPACT_X_KEY)),
                finiteOrZero(tag.getDouble(DELAYED_IMPACT_Y_KEY)),
                finiteOrZero(tag.getDouble(DELAYED_IMPACT_Z_KEY)))
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

    private void readReleaseVelocity(NbtCompound tag) {
        if (!tag.getBoolean(RELEASE_VELOCITY_PRESENT_KEY)) {
            this.releaseVelocity = null;
            return;
        }
        this.releaseVelocity = new Vec3d(
                finiteOrZero(tag.getDouble(RELEASE_VELOCITY_X_KEY)),
                finiteOrZero(tag.getDouble(RELEASE_VELOCITY_Y_KEY)),
                finiteOrZero(tag.getDouble(RELEASE_VELOCITY_Z_KEY))
        );
        this.setTrajectoryInitialVelocity(this.releaseVelocity);
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
