package com.taosi.logic;

import com.taosi.ModRegistry;
import com.taosi.item.TalismanItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class TalismanEffects {
    public static final String OWNER_TAG = "TaosiOwner";
    public static final String SUMMON_TAG = "TaosiSummoner";

    private TalismanEffects() {}

    public static int mult(int lvl) {
        return 1 << (lvl - 1);
    }

    // ---------------------------------------------------------------- bua nem / bua dat
    public static void activate(ServerLevel level, @Nullable LivingEntity owner, Vec3 pos, TalismanType type, int lvl) {
        switch (type) {
            case EXPLOSION -> explode(level, owner, pos, lvl);
            case LIGHTNING -> strike(level, owner, pos, 25.0F * mult(lvl));
            case SLOW -> debuff(level, owner, pos, lvl, true);
            case WEAKEN -> debuff(level, owner, pos, lvl, false);
            case SUMMON -> summon(level, owner, pos, lvl);
            default -> {}
        }
    }

    private static void explode(ServerLevel level, @Nullable LivingEntity owner, Vec3 pos, int lvl) {
        float dmg = 20.0F * mult(lvl); // 20 / 40 / 80
        double r = 3 + lvl;
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 4.0F, 1.0F);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos).inflate(r),
                x -> x != owner && x.isAlive() && x.distanceToSqr(pos) <= r * r)) {
            e.hurt(level.damageSources().explosion(null, owner), dmg);
        }
    }

    public static void strike(ServerLevel level, @Nullable LivingEntity owner, Vec3 pos, float dmg) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt == null) return;
        bolt.moveTo(pos.x, pos.y, pos.z);
        bolt.setDamage(dmg);
        if (owner instanceof ServerPlayer sp) bolt.setCause(sp);
        level.addFreshEntity(bolt);
    }

    private static void debuff(ServerLevel level, @Nullable LivingEntity owner, Vec3 pos, int lvl, boolean slow) {
        int dur = 300 * mult(lvl); // 15s / 30s / 60s
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos).inflate(6.0),
                x -> x != owner && x.isAlive())) {
            e.addEffect(new MobEffectInstance(slow ? ModRegistry.SLOWED.get() : ModRegistry.WEAKENED.get(), dur, 0));
        }
        level.sendParticles(slow ? ParticleTypes.ITEM_SNOWBALL : ParticleTypes.SMOKE, pos.x, pos.y + 0.5, pos.z, 40, 2.5, 0.5, 2.5, 0.02);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static void summon(ServerLevel level, @Nullable LivingEntity owner, Vec3 pos, int lvl) {
        int n = 2 * mult(lvl); // 2 / 4 / 8 moi loai
        UUID id = owner != null ? owner.getUUID() : UUID.randomUUID();
        for (int i = 0; i < n * 2; i++) {
            EntityType<? extends Mob> t;
            if (i % 2 == 0) t = EntityType.ZOMBIE;
            else t = EntityType.SKELETON;
            Mob m = t.create(level);
            if (m == null) continue;
            double x = pos.x + (level.random.nextDouble() - 0.5) * 5.0;
            double z = pos.z + (level.random.nextDouble() - 0.5) * 5.0;
            m.moveTo(x, pos.y + 0.2, z, level.random.nextFloat() * 360.0F, 0.0F);
            m.finalizeSpawn(level, level.getCurrentDifficultyAt(m.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            m.getPersistentData().putUUID(SUMMON_TAG, id);
            m.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1400, 0, false, false));
            m.setPersistenceRequired();
            level.addFreshEntity(m);
            level.sendParticles(ParticleTypes.SOUL, x, pos.y + 0.5, z, 6, 0.2, 0.4, 0.2, 0.02);
        }
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    // ---------------------------------------------------------------- bua dung len ban than
    public static boolean useSelf(ServerPlayer p, TalismanType type, int lvl) {
        ServerLevel level = p.serverLevel();
        switch (type) {
            case SPEED:
                // hieu ung rieng: +100% toc do chay
                p.addEffect(new MobEffectInstance(ModRegistry.SWIFT.get(), 600 * mult(lvl), 0, false, true, true));
                level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.6F, 1.5F);
                return true;
            case INVISIBLE:
                p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 600 * mult(lvl), 0, false, false, true));
                level.sendParticles(ParticleTypes.CLOUD, p.getX(), p.getY() + 1, p.getZ(), 20, 0.3, 0.6, 0.3, 0.02);
                return true;
            case BARRIER:
                BarrierManager.create(p, 600 * mult(lvl)); // 30s / 60s / 120s
                level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
                return true;
            case TELEPORT:
                return teleport(p, level, lvl);
            case SWAP:
                return swap(p, level, lvl);
            default:
                return false;
        }
    }

    public static void tp(Entity e, Vec3 p) {
        if (e instanceof ServerPlayer sp) {
            sp.connection.teleport(p.x, p.y, p.z, sp.getYRot(), sp.getXRot());
        } else {
            e.moveTo(p.x, p.y, p.z, e.getYRot(), e.getXRot());
        }
        e.fallDistance = 0;
    }

    private static boolean teleport(ServerPlayer p, ServerLevel level, int lvl) {
        double range = 20.0 * mult(lvl); // 20 / 40 / 80 khoi
        Vec3 eye = p.getEyePosition();
        Vec3 look = p.getLookAngle();
        Vec3 end = eye.add(look.scale(range));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p));
        Vec3 dest = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation().subtract(look.scale(0.6));
        dest = dest.subtract(0, p.getEyeHeight(), 0);
        for (int i = 0; i < 12; i++) {
            if (level.noCollision(p, p.getBoundingBox().move(dest.subtract(p.position())))) break;
            dest = dest.add(0, 0.5, 0);
        }
        level.sendParticles(ParticleTypes.PORTAL, p.getX(), p.getY() + 1, p.getZ(), 30, 0.3, 0.6, 0.3, 0.3);
        tp(p, dest);
        level.sendParticles(ParticleTypes.PORTAL, dest.x, dest.y + 1, dest.z, 30, 0.3, 0.6, 0.3, 0.3);
        level.playSound(null, dest.x, dest.y, dest.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }

    private static boolean swap(ServerPlayer p, ServerLevel level, int lvl) {
        double range = 20.0 * mult(lvl);
        Vec3 eye = p.getEyePosition();
        Vec3 look = p.getLookAngle();
        Vec3 end = eye.add(look.scale(range));
        AABB box = p.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0);
        EntityHitResult r = ProjectileUtil.getEntityHitResult(level, p, eye, end, box,
                e -> e instanceof LivingEntity && e.isAlive() && !e.isSpectator() && e.isPickable());
        if (r == null) {
            p.displayClientMessage(Component.translatable("message.taosi.no_target"), true);
            return false;
        }
        Entity target = r.getEntity();
        Vec3 a = p.position();
        Vec3 b = target.position();
        tp(p, b);
        tp(target, a);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, a.x, a.y + 1, a.z, 30, 0.3, 0.6, 0.3, 0.2);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, b.x, b.y + 1, b.z, 30, 0.3, 0.6, 0.3, 0.2);
        level.playSound(null, b.x, b.y, b.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.2F);
        return true;
    }

    // ---------------------------------------------------------------- phim K / L
    public static void placeHeld(ServerPlayer p) {
        ServerLevel level = p.serverLevel();
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack s = p.getItemInHand(hand);
            if (s.getItem() instanceof TalismanItem t && t.type.throwable) {
                ItemEntity e = new ItemEntity(level, p.getX(), p.getY() + 0.1, p.getZ(), s.copyWithCount(1));
                e.setNoPickUp();
                e.setUnlimitedLifetime();
                e.setInvulnerable(true);
                e.setDeltaMovement(Vec3.ZERO);
                e.getPersistentData().putUUID(OWNER_TAG, p.getUUID());
                level.addFreshEntity(e);
                if (!p.getAbilities().instabuild) s.shrink(1);
                level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 0.8F);
                return;
            }
        }
        p.displayClientMessage(Component.translatable("message.taosi.hold_talisman"), true);
    }

    public static void detonateAll(ServerPlayer p) {
        ServerLevel level = p.serverLevel();
        List<ItemEntity> list = new ArrayList<>();
        for (Entity e : level.getAllEntities()) {
            if (e instanceof ItemEntity ie && ie.getPersistentData().hasUUID(OWNER_TAG)
                    && ie.getPersistentData().getUUID(OWNER_TAG).equals(p.getUUID())) {
                list.add(ie);
            }
        }
        if (list.isEmpty()) {
            p.displayClientMessage(Component.translatable("message.taosi.none_placed"), true);
            return;
        }
        for (ItemEntity ie : list) {
            Vec3 pos = ie.position();
            ItemStack s = ie.getItem();
            ie.discard();
            if (s.getItem() instanceof TalismanItem t) activate(level, p, pos, t.type, t.lvl);
        }
    }
}
