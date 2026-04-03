/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.client.animation.ClientAnimationHandler
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler$AnimInfo
 *  com.leviathanstudio.craftstudio.common.animation.IAnimated
 *  com.leviathanstudio.craftstudio.common.animation.InfoChannel
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.client.animation.ClientAnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.leviathanstudio.craftstudio.common.animation.InfoChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.pathing.PathFinder;
import net.tangotek.tektopia.pathing.PathNavigateVillager2;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;
import net.tangotek.tektopia.tickjob.TickJobQueue;

public abstract class EntityVillageNavigator
extends EntityCreature
implements IAnimated {
    private static final DataParameter<String> ANIM_NAME = EntityDataManager.func_187226_a(EntityVillageNavigator.class, (DataSerializer)DataSerializers.field_187194_d);
    protected Village village;
    protected String curAnim = "";
    protected List<AnimationTrigger> animTriggers = new ArrayList<AnimationTrigger>();
    protected final TickJobQueue jobs;
    private int idleTicks;
    private boolean isWalking;
    private final int rolesMask;
    private int aiTick = 0;
    private boolean aiReset = false;
    private boolean storagePriority = false;
    private boolean triggeredAnimationRunning = false;
    private String aiDebugTask = "none";
    private String aiDebugReason = "";
    private BlockPos aiDebugTarget = null;

    public EntityVillageNavigator(World worldIn, int rolesMask) {
        super(worldIn);
        this.rolesMask = rolesMask;
        this.jobs = new TickJobQueue();
        if (worldIn.field_72995_K) {
            this.setupClientJobs();
        } else {
            this.setupServerJobs();
        }
    }

    protected PathNavigate func_175447_b(World worldIn) {
        PathNavigateVillager2 nav = new PathNavigateVillager2((EntityLiving)this, this.field_70170_p, this.getCanUseDoors());
        return nav;
    }

    public void onAddedToWorld() {
        if (!this.field_70170_p.field_72995_K && !this.field_70170_p.field_72995_K) {
            this.debugOut("onAddedToWorld " + this.func_70661_as());
        }
    }

    protected void func_110147_ax() {
        super.func_110147_ax();
        this.field_70180_af.func_187227_b(ANIM_NAME, "");
    }

    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_187214_a(ANIM_NAME, "");
    }

    private void updateClientAnim(String newAnim) {
        ClientAnimationHandler clientAnim = (ClientAnimationHandler)this.getAnimationHandler();
        Set<String> animKeys = clientAnim.getAnimChannels().keySet();
        animKeys.forEach(a -> clientAnim.stopAnimation(a, (IAnimated)this));
        if (!newAnim.isEmpty()) {
            clientAnim.startAnimation("tektopia", newAnim, (IAnimated)this);
        }
    }

    public void func_184206_a(DataParameter<?> key) {
        super.func_184206_a(key);
        if (this.isWorldRemote() && ANIM_NAME.equals(key)) {
            this.updateClientAnim((String)this.field_70180_af.func_187225_a(ANIM_NAME));
        }
    }

    protected static void setupAnimations(AnimationHandler animHandler, String modelName) {
    }

    public boolean isAITick() {
        if (this.aiTick < 0) {
            this.aiReset = true;
            return true;
        }
        return false;
    }

    public boolean isStoragePriority() {
        return this.storagePriority;
    }

    public void setStoragePriority() {
        this.storagePriority = true;
    }

    public void setAIDebug(String task, String reason, BlockPos target) {
        this.aiDebugTask = task == null || task.isEmpty() ? "none" : task;
        this.aiDebugReason = reason == null ? "" : reason;
        this.aiDebugTarget = target;
    }

    public String getAIDebugTask() {
        return this.aiDebugTask;
    }

    public String getAIDebugReason() {
        return this.aiDebugReason;
    }

    public BlockPos getAIDebugTarget() {
        return this.aiDebugTarget;
    }

    public String getServerAnimationName() {
        return (String)this.field_70180_af.func_187225_a(ANIM_NAME);
    }

    protected void setupClientJobs() {
        this.jobs.clear();
    }

    protected void setupServerJobs() {
        this.jobs.clear();
        this.addJob(new TickJob(50, 100, true, () -> {
            boolean inVillageGraph;
            BlockPos blockPos = new BlockPos((Entity)this);
            VillageManager vm = VillageManager.get(this.field_70170_p);
            Village nearVillage = vm.getNearestVillage(blockPos, 360);
            if (nearVillage != this.getVillage()) {
                this.detachVillage();
            }
            boolean bl = inVillageGraph = nearVillage != null && nearVillage.getPathingGraph().getNearbyBaseNode(this.func_174791_d(), this.field_70130_N, this.field_70131_O, this.field_70130_N) != null;
            if (!this.hasVillage()) {
                if (inVillageGraph) {
                    this.attachToVillage(nearVillage);
                }
            } else if (!inVillageGraph) {
                this.detachVillage();
            }
            if (!this.hasVillage()) {
                this.func_110177_bN();
            } else {
                this.func_175449_a(this.village.getOrigin(), -1);
            }
        }));
    }

    public void addJob(TickJob job) {
        this.jobs.addJob(job);
    }

    public boolean isRole(VillagerRole role) {
        return (this.rolesMask & role.value) > 0;
    }

    protected boolean getCanUseDoors() {
        return true;
    }

    public boolean canNavigate() {
        return true;
    }

    protected void attachToVillage(Village v) {
        this.village = v;
        this.debugOut("Attaching to village");
    }

    protected void detachVillage() {
        this.village = null;
    }

    public void debugOut(String text) {
        if (this.hasVillage()) {
            this.getVillage().debugOut(((Object)((Object)this)).getClass().getSimpleName() + "|" + this.func_145748_c_().func_150254_d() + "|" + this.func_145782_y() + " " + (text.charAt(0) == ' ' ? text : " " + text));
        }
    }

    public void playSound(SoundEvent soundEvent) {
        this.func_184185_a(soundEvent, this.func_70681_au().nextFloat() * 0.4f + 0.8f, this.func_70681_au().nextFloat() * 0.4f + 0.8f);
    }

    public void startMovement() {
    }

    public void updateMovement(boolean arrived) {
    }

    public void resetMovement() {
        this.field_70699_by.func_75499_g();
    }

    public void func_70636_d() {
        VillageStructure struct;
        if (this.storagePriority && this.isAITick() && this.hasVillage() && ((struct = this.village.getNearestStructure(VillageStructureType.STORAGE, this.func_180425_c())) == null || !struct.isBlockNear(this.func_180425_c(), 3.0))) {
            this.storagePriority = false;
        }
        super.func_70636_d();
        if (!this.isWorldRemote()) {
            --this.aiTick;
            if (this.aiReset) {
                this.aiTick = this.func_70681_au().nextInt(10) + 15;
                this.aiReset = false;
            }
        }
        if (this.isWorldRemote()) {
            this.idleTicks = this.field_70142_S == this.field_70165_t && this.field_70136_U == this.field_70161_v && this.field_70137_T == this.field_70163_u ? ++this.idleTicks : 0;
            boolean bl = this.isWalking = this.idleTicks <= 1;
            if (this.isWalking && this.field_70122_E && !this.triggeredAnimationRunning) {
                this.startWalking();
            } else {
                this.stopWalking();
            }
            this.getAnimationHandler().animationsUpdate((IAnimated)this);
            this.checkAnimationTriggers();
        }
        this.jobs.tick();
    }

    protected boolean isWalking() {
        return this.isWalking;
    }

    @SideOnly(value=Side.CLIENT)
    protected void startWalking() {
    }

    @SideOnly(value=Side.CLIENT)
    protected void stopWalking() {
    }

    protected void playClientAnimation(String anim) {
        if (!this.getAnimationHandler().isAnimationActive("tektopia", anim, (IAnimated)this)) {
            this.getAnimationHandler().startAnimation("tektopia", anim, (IAnimated)this);
        }
    }

    public void stopClientAnimation(String anim) {
        if (this.getAnimationHandler().isAnimationActive("tektopia", anim, (IAnimated)this)) {
            this.getAnimationHandler().stopAnimation("tektopia", anim, (IAnimated)this);
        }
    }

    public SoundCategory func_184176_by() {
        return SoundCategory.NEUTRAL;
    }

    public void stopServerAnimation(String anim) {
        this.field_70180_af.func_187227_b(ANIM_NAME, "");
    }

    public void playServerAnimation(String anim) {
        this.field_70180_af.func_187227_b(ANIM_NAME, anim);
    }

    public boolean isPlayingAnimation(String anim) {
        return anim == this.field_70180_af.func_187225_a(ANIM_NAME);
    }

    protected void checkAnimationTriggers() {
        ClientAnimationHandler clientAnim = (ClientAnimationHandler)this.getAnimationHandler();
        @SuppressWarnings(value={"unchecked"})
        Map<InfoChannel, AnimationHandler.AnimInfo> animInfoMap = (Map<InfoChannel, AnimationHandler.AnimInfo>)clientAnim.getCurrentAnimInfo().get(this);
        this.triggeredAnimationRunning = false;
        if (animInfoMap != null) {
            for (Map.Entry<InfoChannel, AnimationHandler.AnimInfo> animInfo : animInfoMap.entrySet()) {
                for (AnimationTrigger trigger : this.animTriggers) {
                    if (!trigger.name.equals(((InfoChannel)animInfo.getKey()).name)) continue;
                    this.triggeredAnimationRunning = true;
                    trigger.update(((AnimationHandler.AnimInfo)animInfo.getValue()).currentFrame);
                }
            }
        }
    }

    public void addAnimationTriggerRange(String name, int startFrame, int endFrame, Runnable callback) {
        if (this.field_70170_p.field_72995_K) {
            for (int i = startFrame; i <= endFrame; ++i) {
                this.addAnimationTrigger(name, i, callback);
            }
        }
    }

    public void addAnimationTrigger(String name, int keyFrame, Runnable callback) {
        if (this.field_70170_p.field_72995_K) {
            this.animTriggers.add(new AnimationTrigger(name, keyFrame, callback));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void func_180426_a(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        super.func_180426_a(x, y, z, yaw, pitch, 1, teleport);
    }

    public void faceLocation(double x, double z, float maxYawChange) {
        double dx = x - this.field_70165_t;
        double dz = z - this.field_70161_v;
        float f = (float)(MathHelper.func_181159_b((double)dz, (double)dx) * 57.29577951308232) - 90.0f;
        this.field_70177_z = this.updateRotation(this.field_70177_z, f, maxYawChange);
    }

    private float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.func_76142_g((float)(targetAngle - angle));
        return angle + f;
    }

    public boolean hasVillage() {
        return this.village != null && this.village.isLoaded();
    }

    public Village getVillage() {
        if (this.hasVillage()) {
            return this.village;
        }
        return null;
    }

    public float func_70689_ay() {
        return 0.35f;
    }

    public int getDimension() {
        return this.field_71093_bK;
    }

    public double getX() {
        return this.field_70165_t;
    }

    public double getY() {
        return this.field_70163_u;
    }

    public double getZ() {
        return this.field_70161_v;
    }

    public boolean isWorldRemote() {
        return this.field_70170_p.field_72995_K;
    }

    public PathFinder getPathFinder() {
        return ((PathNavigateVillager2)this.func_70661_as()).getVillagerPathFinder();
    }

    public abstract AnimationHandler getAnimationHandler();

    private class AnimationTrigger {
        protected String name;
        protected int keyFrame;
        protected Runnable callback;
        protected boolean triggered = false;
        private float lastFrame = 0.0f;

        public AnimationTrigger(String name, int keyFrame, Runnable callback) {
            this.name = name;
            this.keyFrame = keyFrame;
            this.callback = callback;
        }

        public void update(float curFrame) {
            if (curFrame < this.lastFrame) {
                this.triggered = false;
            }
            if (curFrame >= (float)this.keyFrame && !this.triggered) {
                this.triggered = true;
                this.callback.run();
            }
            this.lastFrame = curFrame;
        }
    }
}

