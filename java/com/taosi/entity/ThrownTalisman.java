package com.taosi.entity;

import com.taosi.ModRegistry;
import com.taosi.item.TalismanItem;
import com.taosi.logic.TalismanEffects;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class ThrownTalisman extends ThrowableItemProjectile implements ItemSupplier {
    public ThrownTalisman(EntityType<? extends ThrownTalisman> type, Level level) {
        super(type, level);
    }

    public ThrownTalisman(Level level, LivingEntity shooter) {
        super(ModRegistry.THROWN.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.PAPER;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide && level() instanceof ServerLevel sl && getItem().getItem() instanceof TalismanItem t) {
            LivingEntity owner = getOwner() instanceof LivingEntity o ? o : null;
            TalismanEffects.activate(sl, owner, result.getLocation(), t.type, t.lvl);
            discard();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
