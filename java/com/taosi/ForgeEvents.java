package com.taosi;

import com.taosi.logic.BarrierManager;
import com.taosi.logic.TalismanEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber(modid = TaosiMod.MODID)
public class ForgeEvents {

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        MinecraftServer s = ServerLifecycleHooks.getCurrentServer();
        if (s != null) BarrierManager.tick(s);
    }

    @SubscribeEvent
    public static void serverStop(ServerStoppingEvent e) {
        BarrierManager.clear();
    }

    /** Bua suy yeu: ke dich gay it hon 30% sat thuong. */
    @SubscribeEvent
    public static void hurt(LivingHurtEvent e) {
        if (e.getSource().getEntity() instanceof LivingEntity attacker && attacker.hasEffect(ModRegistry.WEAKENED.get())) {
            e.setAmount(e.getAmount() * 0.7F);
        }
    }

    /** Quai trieu hoi chi tan cong quai vat khac, khong danh nguoi choi / dan lang / quai cung phe. */
    @SubscribeEvent
    public static void changeTarget(LivingChangeTargetEvent e) {
        LivingEntity self = e.getEntity();
        LivingEntity t = e.getNewTarget();
        if (t == null || !self.getPersistentData().contains(TalismanEffects.SUMMON_TAG)) return;
        if (!(t instanceof Enemy) || t.getPersistentData().contains(TalismanEffects.SUMMON_TAG)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent e) {
        LivingEntity le = e.getEntity();
        if (le.level().isClientSide || !(le instanceof Mob)) return;
        if (!le.getPersistentData().contains(TalismanEffects.SUMMON_TAG)) return;
        Mob m = (Mob) le;
        if (m.tickCount > 1200) { // ton tai 60 giay
            m.discard();
            return;
        }
        if (m.tickCount % 10 != 0) return;
        LivingEntity cur = m.getTarget();
        if (cur != null && cur.isAlive()) return;
        List<Mob> foes = m.level().getEntitiesOfClass(Mob.class, m.getBoundingBox().inflate(16.0),
                x -> x != m && x instanceof Enemy && x.isAlive() && !x.getPersistentData().contains(TalismanEffects.SUMMON_TAG));
        foes.stream().min(Comparator.comparingDouble((Mob x) -> m.distanceToSqr(x))).ifPresent(m::setTarget);
    }
}
