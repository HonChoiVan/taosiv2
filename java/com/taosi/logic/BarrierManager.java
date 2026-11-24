package com.taosi.logic;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/** Ket gioi 50x50: nhung sinh vat nam trong vung luc tao se khong ra khoi duoc cho den khi het gio. */
public final class BarrierManager {
    private static final double HALF = 25.0;
    private static final List<Barrier> LIST = new ArrayList<>();

    private static class Barrier {
        ResourceKey<Level> dim;
        double cx, cy, cz;
        int ticks;
        final Set<UUID> ids = new HashSet<>();
    }

    private BarrierManager() {}

    public static void create(ServerPlayer p, int ticks) {
        Barrier b = new Barrier();
        b.dim = p.level().dimension();
        b.cx = p.getX();
        b.cy = p.getY();
        b.cz = p.getZ();
        b.ticks = ticks;
        AABB box = new AABB(b.cx - HALF, b.cy - HALF, b.cz - HALF, b.cx + HALF, b.cy + HALF, b.cz + HALF);
        for (LivingEntity e : p.serverLevel().getEntitiesOfClass(LivingEntity.class, box)) b.ids.add(e.getUUID());
        LIST.add(b);
    }

    public static void clear() {
        LIST.clear();
    }

    public static void tick(MinecraftServer server) {
        if (LIST.isEmpty()) return;
        Iterator<Barrier> it = LIST.iterator();
        while (it.hasNext()) {
            Barrier b = it.next();
            ServerLevel lv = server.getLevel(b.dim);
            if (lv == null || --b.ticks <= 0) {
                it.remove();
                continue;
            }
            double m = HALF - 0.4;
            for (UUID id : b.ids) {
                Entity e = lv.getEntity(id);
                if (e == null || !e.isAlive()) continue;
                double x = Math.max(b.cx - m, Math.min(b.cx + m, e.getX()));
                double y = Math.max(b.cy - HALF, Math.min(b.cy + HALF, e.getY()));
                double z = Math.max(b.cz - m, Math.min(b.cz + m, e.getZ()));
                if (x != e.getX() || y != e.getY() || z != e.getZ()) {
                    TalismanEffects.tp(e, new Vec3(x, y, z));
                    e.setDeltaMovement(Vec3.ZERO);
                    e.hurtMarked = true;
                }
            }
            if (b.ticks % 10 == 0) {
                for (double t = -HALF; t <= HALF; t += 5.0) {
                    for (double h = 0; h <= 2; h += 2) {
                        lv.sendParticles(ParticleTypes.END_ROD, b.cx + t, b.cy + h, b.cz - HALF, 1, 0, 0, 0, 0);
                        lv.sendParticles(ParticleTypes.END_ROD, b.cx + t, b.cy + h, b.cz + HALF, 1, 0, 0, 0, 0);
                        lv.sendParticles(ParticleTypes.END_ROD, b.cx - HALF, b.cy + h, b.cz + t, 1, 0, 0, 0, 0);
                        lv.sendParticles(ParticleTypes.END_ROD, b.cx + HALF, b.cy + h, b.cz + t, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }
}
