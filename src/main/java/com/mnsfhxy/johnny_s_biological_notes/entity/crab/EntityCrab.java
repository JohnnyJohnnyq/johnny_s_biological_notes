package com.mnsfhxy.johnny_s_biological_notes.entity.crab;

import com.mnsfhxy.johnny_s_biological_notes.JohnnySBiologicalNotes;
import com.mnsfhxy.johnny_s_biological_notes.init.RegistrationInit;
import com.mnsfhxy.johnny_s_biological_notes.init.SoundInit;
import com.mnsfhxy.johnny_s_biological_notes.util.UtilItem;
import com.mnsfhxy.johnny_s_biological_notes.util.UtilLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.GravelBlock;
import net.minecraft.world.level.block.MudBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class EntityCrab extends Animal implements Bucketable {
    private static final float START_HEALTH = 6.0F;
    private static final float ATTACK_DAMAGE = 2.0F;
    private static final float MOVEMENT_SPEED = 0.15F;
//    private CrabWalkingSoundInstance crabWalkingSoundInstance;
    //20tick 1秒
    private final int MOLT_TIME = 50;//脱壳时间
    private final int MOLT_INTERVAL = 100;//脱壳间隔
    private final int RECOVER_TIME = 100;//总时间
    //总时间=脱壳时间加恢复时间
    private int moltInterval = MOLT_INTERVAL;
    private int moltTime = MOLT_TIME;
    private int recoverTime = RECOVER_TIME;
    //    private boolean isDug ;
    public AnimationState diggingAnimationState = new AnimationState();
    public AnimationState restAnimationState = new AnimationState();
    public AnimationState walkingAnimationState = new AnimationState();
    public AnimationState diggingOutAnimationState = new AnimationState();


    private static final EntityDataAccessor<Boolean> IS_DUG = SynchedEntityData.defineId(EntityCrab.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> COLOR =
            SynchedEntityData.defineId(EntityCrab.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> BORN_COLOR =
            SynchedEntityData.defineId(EntityCrab.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityCrab.class, EntityDataSerializers.BOOLEAN);

    public static final String[] colors = {"RED", "BROWN", "SPOTTED", "CYAN", "BLACK"};
    private static final Predicate<LivingEntity> SCARY_MOB = (p) -> {
        if (p instanceof Player && ((Player) p).isCreative()) {
            return false;
        } else if (p.getType() == RegistrationInit.CRAB.get()) {
            return false;
        } else if (p.getType().is(Tags.EntityTypes.BOSSES)) {
            return false;
        } else return true;
    };

    public int getMoltInterval() {
        return moltInterval;
    }

    public void setMoltInterval(int moltInterval) {
        this.moltInterval = moltInterval;
    }

    public int getMoltTime() {
        return moltTime;
    }

    public void setMoltTime(int moltTime) {
        this.moltTime = moltTime;
    }

    public int getRecoverTime() {
        return recoverTime;
    }

    public void setRecoverTime(int recoverTime) {
        this.recoverTime = recoverTime;
    }


    public EntityCrab(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
//        if(this.level.isClientSide)
//        crabWalkingSoundInstance=new CrabWalkingSoundInstance(SoundInit.CRAB_WALKING.get(),this.getSoundSource(),this.getSoundVolume(),this.getVoicePitch(),this,this.getRandom().nextLong());
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, (double) MOVEMENT_SPEED)
                .add(Attributes.MAX_HEALTH, START_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.isAlive()) {
            for (LivingEntity livingEntity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3D), SCARY_MOB)) {
//                JohnnySBiologicalNotes.LOGGER.info("mob type:" + mob.getType().getDescription().getString());
                if (livingEntity.isAlive()) {
                    this.touch(livingEntity);
                }
            }
        }

    }

//    @Override
//    public boolean attackable() {
//        return !isDug() && super.attackable();
//    }

    private void touch(LivingEntity livingEntity) {
        livingEntity.hurt(DamageSource.mobAttack(this), (float) (1));
    }


    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor pLevel,
            DifficultyInstance pDifficulty,
            MobSpawnType pReason,
            @javax.annotation.Nullable SpawnGroupData pSpawnData,
            @javax.annotation.Nullable CompoundTag pDataTag) {
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundInit.CRAB_HURT.get();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (isDug()) return false;
        this.diggingOutAnimationState.stop();
        this.diggingAnimationState.stop();
        digTick = UtilLevel.TIME.SECOND.getTick() * 2;
        setNoAi(false);
        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean isPushable() {
        return !isDug() && super.isPushable();
    }

    @Override
    @Nonnull
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        if (isDug()) return InteractionResult.FAIL;
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    public boolean doHurtTarget(Entity pEntity) {
        boolean flag =
                pEntity.hurt(
                        DamageSource.mobAttack(this),
                        (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.doEnchantDamageEffects(this, pEntity);
        }

        return flag;
    }

    protected void registerGoals() {

        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new MoltGoal(this));
        this.goalSelector.addGoal(3, new CrabRandomStrollGoal(this, MOVEMENT_SPEED, 150));

    }

    public static boolean checkCrabSpawnRules(
            EntityType<EntityCrab> pCrab,
            LevelAccessor pLevel,
            MobSpawnType pSpawnType,
            BlockPos pPos,
            RandomSource pRandom) {

        return (pPos.getY() < pLevel.getSeaLevel() + 4
                || pLevel.getBiome(pPos.below()).is(Tags.Biomes.IS_SWAMP))
                && isBrightEnoughToSpawn(pLevel, pPos);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }


    public MobType getMobType() {
        return MobType.WATER;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        String color = this.randColor();
        this.entityData.define(COLOR, color);
        this.entityData.define(BORN_COLOR, color);
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(IS_DUG, false);


    }


    public boolean canDigOut() {
        return !isInPowderSnow && (level.getDayTime() > 10000 || level.getDayTime() < 5000 || level.isThundering() || level.isRaining()) && this.isDug() && !this.isMolt();
    }

    private boolean isDigTime() {
        return !level.dimensionType().hasFixedTime() && level.getDayTime() > 5000 && level.getDayTime() < 8000 && !(level.isThundering() || level.isRaining());
    }

    public boolean canDig() {
        return isDigTime() && !this.isMoving() && (this.getBlockStateOn().getBlock() instanceof GravelBlock || this.getBlockStateOn().getBlock() instanceof MudBlock || this.getBlockStateOn().is(BlockTags.DIRT) || this.getBlockStateOn().is(BlockTags.SAND)) && !this.isInWater() && !this.isDug() && !this.isMolt();
    }


    public boolean isMolt() {
        return UtilLevel.isNight(level) && !this.isDug() && !(getMoltInterval() > 0);
    }

    public String getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(String color) {
        this.entityData.set(COLOR, color);
    }

    public String getBornColor() {
        return this.entityData.get(BORN_COLOR);
    }

    public void recoverBornColor() {
        entityData.set(COLOR, getBornColor());
    }

    private String randColor() {
        int index = random.nextInt(colors.length - 1);
        return colors[index];
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("Color", this.getColor());
        pCompound.putString("BornColor", this.getBornColor());
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }


    @Override
    public void tick() {
        super.tick();
        if (this.getBlockStateOn() != preBlockState) {
            if (isDug()) this.setIsInPowderSnow(true);
            setDug(false);
            this.diggingOutAnimationState.stop();
            this.diggingAnimationState.stop();
            digTick = 0;
            setNoAi(false);
            preBlockState = this.getBlockStateOn();
        }
        if (isDug()) {
            this.setDeltaMovement(0, 0, 0);
            setNoAi(true);
        } else {

        }
        if (this.isInWater()) {
            this.playSound(SoundInit.CRAB_BUBBLE.get(), this.getSoundVolume()*0.4F, this.getVoicePitch());
        }
//        JohnnySBiologicalNotes.LOGGER.info("isDug:" + this.isDug());
        if (canDig()||digTick>0) {
            setNoAi(true);
            startDig();
        }
        if (canDigOut()||digOutTick>0) {
            setNoAi(true);
            startDigOut();
        }
        if (!this.isDug()) {
            if (this.isMoving()) {
//                    JohnnySBiologicalNotes.LOGGER.info("moving...");
                    playSound(SoundInit.CRAB_WALKING.get(), this.getSoundVolume(), this.getVoicePitch());

                if (this.level.isClientSide()) {
//                    Minecraft.getInstance().getSoundManager().play(crabWalkingSoundInstance);
                    this.restAnimationState.stop();
                    this.walkingAnimationState.startIfStopped(this.tickCount);
                }
            } else {
                if (this.level.isClientSide()) {
                    this.walkingAnimationState.stop();
                    this.restAnimationState.startIfStopped(this.tickCount);
                }
            }
        }
        if (!isMolt()) setMoltInterval(getMoltInterval() - 1);
        else {
            if (this.getMoltTime() > 0) {
                this.setMoltTime(this.getMoltTime() - 1);
                setNoAi(true);
            } else {
                setNoAi(false);
            }
        }

    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    public boolean removeWhenFarAway(double p_27492_) {
        return !this.fromBucket() && !this.hasCustomName();
    }

//    @Override
//    @Nonnull
//    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
//        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
//    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean pFromBucket) {
        this.entityData.set(FROM_BUCKET, pFromBucket);
    }

    @Override
    public void saveToBucketTag(ItemStack pStack) {
        if (this.hasCustomName()) {
            pStack.setHoverName(this.getCustomName());
        }
        Bucketable.saveDefaultDataToBucketTag(this, pStack);
        CompoundTag compoundnbt = pStack.getOrCreateTag();
        compoundnbt.putString("BucketVariantTag", this.getColor());
    }

    @Override
    public void loadFromBucketTag(CompoundTag pTag) {
        Bucketable.loadDefaultDataFromBucketTag(this, pTag);
        if (pTag.contains("BucketVariantTag", 8)) {
            this.setColor(pTag.getString("BucketVariantTag"));
        }
    }

    public boolean isDug() {
        return this.entityData.get(IS_DUG);
    }

    public void setDug(boolean dug) {
        this.entityData.set(IS_DUG, dug);
    }

    @Override
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(RegistrationInit.CRAB_BUCKET.get());
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
//        if (DATA_POSE.equals(pKey)) {
//            switch (this.getPose()) {
//                case STANDING:
//                    this.walkingAnimationState.start(this.tickCount);
////                    this.restAnimationState.start(this.tickCount);
//                    break;
//                case DIGGING:
//                    this.diggingAnimationState.start(this.tickCount);
//                    break;
//
//            }
//        }
    }

    @Override
    public SoundEvent getPickupSound() {
        return null;
    }

    public void stopAllAnimation() {
        if (this.level.isClientSide()) {
            this.diggingAnimationState.stop();
            this.walkingAnimationState.stop();
            this.restAnimationState.stop();
            this.diggingOutAnimationState.stop();
        }
    }

    private int digTick = 0;
    private BlockState preBlockState;

    public void startDig() {
        setNoAi(true);
        digTick++;
        if (digTick == 1) {
            preBlockState = getBlockStateOn();
            this.playSound(SoundInit.CRAB_TALON.get(), this.getSoundVolume(), this.getVoicePitch());
            if (this.level.isClientSide()) {
                this.diggingAnimationState.start(this.tickCount);
            }
            ;
        }
        //dig被阻止
//            if (digTick != 1 && preBlockState != this.getBlockStateOn()) {
////                this.setDug(false);
//                this.diggingOutAnimationState.stop();
//                digTick = 0;
//                setNoAi(false);
//                return;
//            }
        //dig完成
        if (digTick > UtilLevel.TIME.SECOND.getTick() * 2) {
            this.setDug(true);
            digTick = 0;
            setNoAi(false);
//                this.diggingAnimationState.stop();
        }

    }

    private int digOutTick = 0;

    public void startDigOut() {

        digOutTick++;
        if (digOutTick == 1) {
            if (this.level.isClientSide()) {
                this.diggingAnimationState.stop();
                this.diggingOutAnimationState.start(this.tickCount);

            }
            this.playSound(SoundInit.CRAB_TALON.get(), this.getSoundVolume(), this.getVoicePitch());
        } else if (digOutTick > UtilLevel.TIME.SECOND.getTick() * 2.5) {
            this.setDug(false);
            digOutTick = 0;
            if (this.level.isClientSide()) {
                this.diggingOutAnimationState.stop();
            }
        }
    }


    static class CrabRandomStrollGoal extends RandomStrollGoal {
        private final EntityCrab crabEntity;

        CrabRandomStrollGoal(EntityCrab crabEntity, double pSpeedModifier, int pInterval) {
            super(crabEntity, pSpeedModifier, pInterval);
            this.crabEntity = crabEntity;
        }

        @Override
        public void start() {
            super.start();
            crabEntity.playSound(SoundInit.CRAB_WALKING.get(), crabEntity.getSoundVolume(), crabEntity.getVoicePitch());

        }

        public boolean canUse() {
            return !crabEntity.isMolt() && !this.crabEntity.isDug() && super.canUse();
        }

        @Override
        public void tick() {
            super.tick();
            JohnnySBiologicalNotes.LOGGER.info("strolling--");
        }
    }


    class MoltGoal extends Goal {
        private final EntityCrab crabEntity;

        public MoltGoal(EntityCrab crabEntity) {
            this.crabEntity = crabEntity;
        }

        @Override
        public boolean canUse() {
            return UtilLevel.isNight(this.crabEntity.level) && isMolt();
        }

        private double x;
        private double z;

        @Override
        public void start() {
            super.start();
//            JohnnySBiologicalNotes.LOGGER.info("start molt");
//            crabEntity.setPose(Pose.DIGGING);
            this.isDropped = false;
//            crabEntity.setNoActionTime(MOLT_TIME);
            //      crabEntity.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new
            // AttributeModifier(Attributes.MOVEMENT_SPEED,0F));
        }

        @Override
        public void stop() {
            super.stop();
            crabEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100));
            crabEntity.setMoltInterval(MOLT_INTERVAL);
            crabEntity.recoverBornColor();
            crabEntity.setMoltTime(MOLT_TIME);
            crabEntity.setRecoverTime(RECOVER_TIME);
        }

        private boolean isDropped;

        private void dropShell() {
            if (!isDropped) {
                playSound(SoundInit.CRAB_DROP_SHELL.get(), crabEntity.getSoundVolume(), crabEntity.getVoicePitch());
                crabEntity.entityData.set(EntityCrab.COLOR, "BLACK");
                ItemEntity itemEntity = crabEntity.spawnAtLocation(new ItemStack(RegistrationInit.CRAB_SHELL.get(), random.nextInt(2) + 1));
                UtilItem.setDeltaMovement(itemEntity);
                crabEntity.setHealth(3F);
                if (crabEntity.random.nextInt(20) == 0) crabEntity.setHealth(0);
                isDropped = true;
            }
        }

        @Override
        public void tick() {
            super.tick();
            crabEntity.setRecoverTime(crabEntity.getRecoverTime() - 1);
//            JohnnySBiologicalNotes.LOGGER.info("Recover time:" + crabEntity.getRecoverTime());
//            JohnnySBiologicalNotes.LOGGER.info("Molt time:" + crabEntity.getMoltTime());
            if (!(crabEntity.getRecoverTime() > 0)) {
                //恢复完成
                stop();
            }

            if (crabEntity.getMoltTime() > 0) {
                //正在脱壳
//                crabEntity.setMoltTime(crabEntity.getMoltTime() - 1);
//                crabEntity.getMoveControl().setWantedPosition(crabEntity.getX(),crabEntity.getY(),crabEntity.getZ(),0);
//                crabEntity.setSpeed(0);
            } else {
                //脱壳后
                dropShell();
            }
        }
    }
//    private class CrabWalkingSoundInstance extends EntityBoundSoundInstance {
//        EntityCrab entity;
//        public CrabWalkingSoundInstance(SoundEvent pSoundEvent, SoundSource pSource, float pVolume, float pPitch, EntityCrab pEntity, long pSeed) {
//            super(pSoundEvent, pSource, pVolume, pPitch, pEntity, pSeed);
//            this.entity=pEntity;
//        }
//
//        @Override
//        public void tick() {
//            if (this.entity.isRemoved()) {
//                this.stop();
//            }
//            else if(!this.entity.isMoving()){
//                this.stop();
//            }
//            else {
//                this.x = (double)((float)this.entity.getX());
//                this.y = (double)((float)this.entity.getY());
//                this.z = (double)((float)this.entity.getZ());
//            }
//        }
//    }
}