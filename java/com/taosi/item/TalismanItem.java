package com.taosi.item;

import com.taosi.entity.ThrownTalisman;
import com.taosi.logic.TalismanEffects;
import com.taosi.logic.TalismanType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class TalismanItem extends Item {
    public final TalismanType type;
    public final int lvl;

    public TalismanItem(TalismanType type, int lvl, Properties props) {
        super(props);
        this.type = type;
        this.lvl = lvl;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (type.throwable) {
            if (!level.isClientSide) {
                ThrownTalisman e = new ThrownTalisman(level, player);
                e.setItem(stack.copyWithCount(1));
                e.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.3F, 1.0F);
                level.addFreshEntity(e);
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.5F);
            player.getCooldowns().addCooldown(this, 10);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!TalismanEffects.useSelf((ServerPlayer) player, type, lvl)) return InteractionResultHolder.fail(stack);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return lvl >= 3;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        tip.add(Component.translatable("tooltip.taosi.level", lvl).withStyle(ChatFormatting.GOLD));
        tip.add(Component.translatable("tooltip.taosi." + type.id, TalismanEffects.mult(lvl)).withStyle(ChatFormatting.GRAY));
        if (type.throwable) tip.add(Component.translatable("tooltip.taosi.placeable").withStyle(ChatFormatting.DARK_AQUA));
        if (lvl < 3) tip.add(Component.translatable("tooltip.taosi.merge").withStyle(ChatFormatting.DARK_GRAY));
    }
}
