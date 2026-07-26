package me.lemonboi439.archeryRevamped.entity;

import me.lemonboi439.archeryRevamped.arrow.ArrowBehaviorRegistry;
import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import me.lemonboi439.archeryRevamped.arrow.RicochetBehavior;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import me.lemonboi439.archeryRevamped.physics.ArrowPhysicsEngine;
import me.lemonboi439.archeryRevamped.mixin.PersistentProjectileEntityAccessor;
import me.lemonboi439.archeryRevamped.fracture.FractureScheduler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class ArcheryArrowEntity extends PersistentProjectileEntity {
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
    private double fractureReleaseSpeed;
    private boolean fractureReleaseSpeedCaptured;
    private boolean spawnPositionCaptured;

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

    public ArcheryArrowEntity(EntityType<? extends ArcheryArrowEntity> type, World world) {
        super(type, world);
        this.setStack(new ItemStack(Items.ARROW));
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public void tick() {
        this.captureFractureReleaseSpeedIfNeeded();

        if (!this.spawnPositionCaptured) {
            this.spawnX = this.getX();
            this.spawnY = this.getY();
            this.spawnZ = this.getZ();
            this.spawnPositionCaptured = true;
        }

        super.tick();

        if (!this.isRemoved()) {
            ArrowPhysicsEngine.applyPhysics(this);
            if (!this.isRemoved()) {
                this.splitTimer++;
                if (!this.isInGround() && !this.hasSpread && this.fractureLevel > 0
                        && this.splitTimer >= this.getFractureSplitDelayTicks()) {
                    this.hasSpread = true;
                    FractureScheduler.schedule(this);
                }
                ArrowBehaviorRegistry.getBehavior(this.arrowType).onTick(this);
            }
        }
    }

    public ArrowType getArrowType() {
        return this.arrowType;
    }

    public void setArrowType(ArrowType arrowType) {
        this.arrowType = arrowType == null ? ArrowType.NORMAL : arrowType;
    }

    public boolean canRicochet() {
        return this.bounceCount < this.ricochetLevel;
    }

    public void setRicochetLevel(int level) {
        this.ricochetLevel = Math.max(0, Math.min(level, 5));
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

    public int advancePhysicsAge() {
        return ++this.physicsAge;
    }

    public void setOverdrawDamageBonus(double bonus) {
        this.overdrawDamageBonus = Math.max(0.0D, bonus);
    }

    public void setLongshotLevel(int level) {
        this.longshotLevel = Math.max(0, Math.min(level, 1));
    }

    public void setFractureLevel(int level) {
        this.fractureLevel = Math.max(0, Math.min(level, 2));
    }

    public void setFractureReleaseSpeed(double speed) {
        if (Double.isFinite(speed) && speed > 1.0E-6D) {
            this.fractureReleaseSpeed = speed;
            this.fractureReleaseSpeedCaptured = true;
        }
    }

    public int getFractureLevel() {
        return this.fractureLevel;
    }

    public ArcheryArrowEntity createFractureChild(double angleDegrees) {
        // The owner/weapon constructor validates that the firing weapon is a
        // real ranged weapon. A fracture child is not fired by a weapon, so
        // construct it directly and copy the projectile state below.
        ArcheryArrowEntity child = new ArcheryArrowEntity(this.getEntityWorld());

        child.setOwner(this.getOwner());
        child.setProjectileStack(this.getItemStack().copy());
        child.setPosition(this.getX(), this.getY(), this.getZ());
        child.setVelocity(this.getVelocity().rotateY((float) Math.toRadians(angleDegrees)));
        child.setCritical(this.isCritical());
        child.setNoClip(this.isNoClip());
        child.setDamage(((PersistentProjectileEntityAccessor) (Object) this).archeryRevamped$getDamage());
        child.setFireTicks(this.getFireTicks());

        child.arrowType = this.arrowType;
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
        child.fractureReleaseSpeed = this.fractureReleaseSpeed;
        child.fractureReleaseSpeedCaptured = this.fractureReleaseSpeedCaptured;
        return child;
    }

    public ArcheryArrowEntity createBurstChild() {
        ArcheryArrowEntity child = new ArcheryArrowEntity(this.getEntityWorld());

        child.setOwner(this.getOwner());
        child.setProjectileStack(this.getItemStack().copy());
        child.setPosition(this.getX(), this.getY(), this.getZ());
        child.setVelocity(this.getVelocity());
        child.setCritical(this.isCritical());
        child.setNoClip(this.isNoClip());
        child.setDamage(((PersistentProjectileEntityAccessor) (Object) this).archeryRevamped$getDamage());
        child.setFireTicks(this.getFireTicks());

        child.arrowType = this.arrowType;
        child.ricochetLevel = this.ricochetLevel;
        child.bounceCount = 0;
        child.distanceTravelled = 0.0D;
        child.originalDamage = this.originalDamage;
        child.overdrawDamageBonus = this.overdrawDamageBonus;
        child.longshotLevel = this.longshotLevel;
        child.fractureLevel = this.fractureLevel;
        child.fractureReleaseSpeed = this.fractureReleaseSpeed;
        child.fractureReleaseSpeedCaptured = this.fractureReleaseSpeedCaptured;
        child.splitTimer = 0;
        child.hasSpread = false;
        child.spawnPositionCaptured = false;
        child.longshot16Triggered = false;
        child.longshot32Triggered = false;
        child.longshot48Triggered = false;
        child.longshot64Triggered = false;
        return child;
    }

    private void captureFractureReleaseSpeedIfNeeded() {
        if (!this.fractureReleaseSpeedCaptured) {
            this.setFractureReleaseSpeed(this.getVelocity().length());
        }
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
            serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    this.getX(), this.getY(), this.getZ(), 12,
                    0.25D, 0.25D, 0.25D, 0.05D);
            this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 0.6F, 0.65F);
        }

        if (!this.longshot48Triggered
                && this.distanceTravelled >= ConfigManager.getLongshot48Threshold()) {
            this.longshot48Triggered = true;
        }

        if (!this.longshot64Triggered
                && this.distanceTravelled >= ConfigManager.getLongshot64Threshold()) {
            this.longshot64Triggered = true;
            serverWorld.spawnParticles(ParticleTypes.WAX_ON,
                    this.getX(), this.getY(), this.getZ(), 16,
                    0.3D, 0.3D, 0.3D, 0.08D);
            this.getEntityWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 0.75F, 1.3F);
        }
    }

    private void applyLongshotDamage() {
        if (this.longshotLevel <= 0) {
            return;
        }

        double multiplier = this.distanceTravelled >= ConfigManager.getLongshot64Threshold()
                ? ConfigManager.getLongshot64Multiplier()
                : this.distanceTravelled >= ConfigManager.getLongshot48Threshold()
                ? ConfigManager.getLongshot48Multiplier()
                : this.distanceTravelled >= ConfigManager.getLongshot32Threshold()
                ? ConfigManager.getLongshot32Multiplier()
                : this.distanceTravelled >= ConfigManager.getLongshot16Threshold()
                ? ConfigManager.getLongshot16Multiplier() : 1.0D;
        if (multiplier > 1.0D) {
            double damage = ((PersistentProjectileEntityAccessor) (Object) this).archeryRevamped$getDamage();
            this.setDamage(damage * multiplier);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult hit) {
        this.updateDistanceTravelled();
        if (this.canRicochet() && !this.isInGround()) {
            RicochetBehavior.applyRicochet(this, hit);
            return;
        }

        ArrowBehaviorRegistry.getBehavior(this.arrowType).onBlockHit(this, hit);
        if (!this.isRemoved()) {
            super.onBlockHit(hit);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        this.updateDistanceTravelled();
        this.applyLongshotDamage();
        super.onEntityHit(hit);
        ArrowBehaviorRegistry.getBehavior(this.arrowType).onEntityHit(this, hit);
    }

    protected void saveSyncData(NbtCompound tag) {
        tag.putString(ARROW_TYPE_KEY, this.arrowType.name());
        tag.putInt(RICOCHET_LEVEL_KEY, this.ricochetLevel);
        tag.putInt(BOUNCE_COUNT_KEY, this.bounceCount);
        tag.putDouble(DISTANCE_TRAVELLED_KEY, this.distanceTravelled);
        tag.putDouble(ORIGINAL_DAMAGE_KEY, this.originalDamage);
        tag.putDouble(SPAWN_X_KEY, this.spawnX);
        tag.putDouble(SPAWN_Y_KEY, this.spawnY);
        tag.putDouble(SPAWN_Z_KEY, this.spawnZ);
        tag.putInt(PHYSICS_AGE_KEY, this.physicsAge);
        tag.putDouble(OVERDRAW_DAMAGE_BONUS_KEY, this.overdrawDamageBonus);
        tag.putInt(LONGSHOT_LEVEL_KEY, this.longshotLevel);
        tag.putBoolean(LONGSHOT_16_TRIGGERED_KEY, this.longshot16Triggered);
        tag.putBoolean(LONGSHOT_32_TRIGGERED_KEY, this.longshot32Triggered);
        tag.putBoolean(LONGSHOT_48_TRIGGERED_KEY, this.longshot48Triggered);
        tag.putBoolean(LONGSHOT_64_TRIGGERED_KEY, this.longshot64Triggered);
        tag.putInt(FRACTURE_LEVEL_KEY, this.fractureLevel);
        tag.putInt(SPLIT_TIMER_KEY, this.splitTimer);
        tag.putBoolean(HAS_SPREAD_KEY, this.hasSpread);
        tag.putDouble(FRACTURE_RELEASE_SPEED_KEY, this.fractureReleaseSpeed);
    }

    protected void loadSyncData(NbtCompound tag) {
        this.arrowType = parseArrowType(tag.getString(ARROW_TYPE_KEY).orElse(ArrowType.NORMAL.name()));
        this.ricochetLevel = tag.getInt(RICOCHET_LEVEL_KEY).orElse(0);
        this.bounceCount = tag.getInt(BOUNCE_COUNT_KEY).orElse(0);
        this.distanceTravelled = tag.getDouble(DISTANCE_TRAVELLED_KEY).orElse(0.0D);
        this.originalDamage = tag.getDouble(ORIGINAL_DAMAGE_KEY).orElse(0.0D);
        this.spawnX = tag.getDouble(SPAWN_X_KEY).orElse(0.0D);
        this.spawnY = tag.getDouble(SPAWN_Y_KEY).orElse(0.0D);
        this.spawnZ = tag.getDouble(SPAWN_Z_KEY).orElse(0.0D);
        this.physicsAge = tag.getInt(PHYSICS_AGE_KEY).orElse(0);
        this.overdrawDamageBonus = tag.getDouble(OVERDRAW_DAMAGE_BONUS_KEY).orElse(0.0D);
        this.longshotLevel = tag.getInt(LONGSHOT_LEVEL_KEY).orElse(0);
        this.longshot16Triggered = tag.getBoolean(LONGSHOT_16_TRIGGERED_KEY).orElse(false);
        this.longshot32Triggered = tag.getBoolean(LONGSHOT_32_TRIGGERED_KEY).orElse(false);
        this.longshot48Triggered = tag.getBoolean(LONGSHOT_48_TRIGGERED_KEY).orElse(false);
        this.longshot64Triggered = tag.getBoolean(LONGSHOT_64_TRIGGERED_KEY).orElse(false);
        this.fractureLevel = tag.getInt(FRACTURE_LEVEL_KEY).orElse(0);
        this.splitTimer = tag.getInt(SPLIT_TIMER_KEY).orElse(0);
        this.hasSpread = tag.getBoolean(HAS_SPREAD_KEY).orElse(false);
        this.fractureReleaseSpeed = tag.getDouble(FRACTURE_RELEASE_SPEED_KEY).orElse(0.0D);
        this.fractureReleaseSpeedCaptured = this.fractureReleaseSpeed > 1.0E-6D;
        this.spawnPositionCaptured = tag.contains(SPAWN_X_KEY)
                || tag.contains(SPAWN_Y_KEY)
                || tag.contains(SPAWN_Z_KEY);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putString(ARROW_TYPE_KEY, this.arrowType.name());
        view.putInt(RICOCHET_LEVEL_KEY, this.ricochetLevel);
        view.putInt(BOUNCE_COUNT_KEY, this.bounceCount);
        view.putDouble(DISTANCE_TRAVELLED_KEY, this.distanceTravelled);
        view.putDouble(ORIGINAL_DAMAGE_KEY, this.originalDamage);
        view.putDouble(SPAWN_X_KEY, this.spawnX);
        view.putDouble(SPAWN_Y_KEY, this.spawnY);
        view.putDouble(SPAWN_Z_KEY, this.spawnZ);
        view.putInt(PHYSICS_AGE_KEY, this.physicsAge);
        view.putDouble(OVERDRAW_DAMAGE_BONUS_KEY, this.overdrawDamageBonus);
        view.putInt(LONGSHOT_LEVEL_KEY, this.longshotLevel);
        view.putBoolean(LONGSHOT_16_TRIGGERED_KEY, this.longshot16Triggered);
        view.putBoolean(LONGSHOT_32_TRIGGERED_KEY, this.longshot32Triggered);
        view.putBoolean(LONGSHOT_48_TRIGGERED_KEY, this.longshot48Triggered);
        view.putBoolean(LONGSHOT_64_TRIGGERED_KEY, this.longshot64Triggered);
        view.putInt(FRACTURE_LEVEL_KEY, this.fractureLevel);
        view.putInt(SPLIT_TIMER_KEY, this.splitTimer);
        view.putBoolean(HAS_SPREAD_KEY, this.hasSpread);
        view.putDouble(FRACTURE_RELEASE_SPEED_KEY, this.fractureReleaseSpeed);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.arrowType = parseArrowType(view.getString(ARROW_TYPE_KEY, ArrowType.NORMAL.name()));
        this.ricochetLevel = view.getInt(RICOCHET_LEVEL_KEY, 0);
        this.bounceCount = view.getInt(BOUNCE_COUNT_KEY, 0);
        this.distanceTravelled = view.getDouble(DISTANCE_TRAVELLED_KEY, 0.0D);
        this.originalDamage = view.getDouble(ORIGINAL_DAMAGE_KEY, 0.0D);
        this.spawnX = view.getDouble(SPAWN_X_KEY, 0.0D);
        this.spawnY = view.getDouble(SPAWN_Y_KEY, 0.0D);
        this.spawnZ = view.getDouble(SPAWN_Z_KEY, 0.0D);
        this.physicsAge = view.getInt(PHYSICS_AGE_KEY, 0);
        this.overdrawDamageBonus = view.getDouble(OVERDRAW_DAMAGE_BONUS_KEY, 0.0D);
        this.longshotLevel = view.getInt(LONGSHOT_LEVEL_KEY, 0);
        this.longshot16Triggered = view.getBoolean(LONGSHOT_16_TRIGGERED_KEY, false);
        this.longshot32Triggered = view.getBoolean(LONGSHOT_32_TRIGGERED_KEY, false);
        this.longshot48Triggered = view.getBoolean(LONGSHOT_48_TRIGGERED_KEY, false);
        this.longshot64Triggered = view.getBoolean(LONGSHOT_64_TRIGGERED_KEY, false);
        this.fractureLevel = view.getInt(FRACTURE_LEVEL_KEY, 0);
        this.splitTimer = view.getInt(SPLIT_TIMER_KEY, 0);
        this.hasSpread = view.getBoolean(HAS_SPREAD_KEY, false);
        this.fractureReleaseSpeed = view.getDouble(FRACTURE_RELEASE_SPEED_KEY, 0.0D);
        this.fractureReleaseSpeedCaptured = this.fractureReleaseSpeed > 1.0E-6D;
        this.spawnPositionCaptured = view.getOptionalString(ARROW_TYPE_KEY).isPresent();
    }

    private static ArrowType parseArrowType(String value) {
        try {
            return ArrowType.valueOf(value.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return ArrowType.NORMAL;
        }
    }
}
