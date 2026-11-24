package com.taosi.item;

import com.taosi.ModRegistry;
import com.taosi.logic.TalismanEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Kiem go dao: danh thuong 10 sat thuong, chuot phai chem set 50 sat thuong (hoi chieu 10s). */
public class PeachSwordItem extends SwordItem {
    public static final Tier TIER = new Tier() {
        public int getUses() { return 900; }
        public float getSpeed() { return 6.0F; }
        public float getAttackDamageBonus() { return 0.0F; }
        public int getLevel() { return 2; }
        public int getEnchantmentValue() { return 18; }
        public Ingredient getRepairIngredient() { return Ingredient.of(ModRegistry.PEACH_WOOD.get()); }
    };

    public PeachSwordItem(Properties props) {
        // 1 (tay khong) + 9 = 10 sat thuong
        super(TIER, 9, -2.4F, props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);

        if (level instanceof ServerLevel sl) {
            double range = 40.0;
            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 end = eye.add(look.scale(range));
            BlockHitResult br = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            Vec3 target = br.getLocation();
            AABB box = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0);
            EntityHitResult er = ProjectileUtil.getEntityHitResult(level, player, eye, end, box,
                    e -> e instanceof LivingEntity && e.isAlive() && !e.isSpectator() && e.isPickable());
            if (er != null && er.getLocation().distanceToSqr(eye) <= target.distanceToSqr(eye)) {
                target = er.getEntity().position();
            }
            TalismanEffects.strike(sl, player, target, 50.0F);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 1.0F, 1.0F);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
        player.getCooldowns().addCooldown(this, 200); // 10 giay
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
