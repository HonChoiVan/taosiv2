package com.taosi.menu;

import com.taosi.ModRegistry;
import com.taosi.recipe.DrawingRecipe;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class DrawingMenu extends AbstractContainerMenu {
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    public DrawingMenu(int id, Inventory inv) {
        this(id, inv, ContainerLevelAccess.NULL);
    }

    public DrawingMenu(int id, Inventory inv, ContainerLevelAccess access) {
        super(ModRegistry.DRAWING_MENU.get(), id);
        this.access = access;
        this.player = inv.player;

        this.addSlot(new OutSlot(resultSlots, 0, 124, 35));
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 3; x++)
                this.addSlot(new Slot(craftSlots, x + y * 3, 30 + x * 18, 17 + y * 18));
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 9; x++)
                this.addSlot(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
        for (int x = 0; x < 9; x++)
            this.addSlot(new Slot(inv, x, 8 + x * 18, 142));
    }

    private class OutSlot extends Slot {
        OutSlot(Container c, int i, int x, int y) {
            super(c, i, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player pl, ItemStack stack) {
            stack.onCraftedBy(pl.level(), pl, stack.getCount());
            for (int i = 0; i < craftSlots.getContainerSize(); i++) {
                if (!craftSlots.getItem(i).isEmpty()) craftSlots.removeItem(i, 1);
            }
            this.setChanged();
        }
    }

    @Override
    public void slotsChanged(Container c) {
        access.execute((level, pos) -> update(level));
    }

    private void update(Level level) {
        if (level.isClientSide || !(player instanceof ServerPlayer sp)) return;
        ItemStack result = ItemStack.EMPTY;
        Optional<DrawingRecipe> o = level.getRecipeManager().getRecipeFor(ModRegistry.DRAWING_TYPE.get(), craftSlots, level);
        if (o.isPresent()) result = o.get().assemble(craftSlots, level.registryAccess());
        resultSlots.setItem(0, result);
        setRemoteSlot(0, result);
        sp.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, incrementStateId(), 0, result));
    }

    @Override
    public void removed(Player p) {
        super.removed(p);
        access.execute((l, pos) -> clearContainer(p, craftSlots));
    }

    @Override
    public boolean stillValid(Player p) {
        return stillValid(access, p, ModRegistry.TAOSI_CRAFTING.get());
    }

    @Override
    public ItemStack quickMoveStack(Player p, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            if (index == 0) {
                access.execute((l, pos) -> stack.getItem().onCraftedBy(stack, l, p));
                if (!this.moveItemStackTo(stack, 10, 46, true)) return ItemStack.EMPTY;
                slot.onQuickCraft(stack, copy);
            } else if (index >= 10 && index < 46) {
                if (!this.moveItemStackTo(stack, 1, 10, false)) {
                    if (index < 37) {
                        if (!this.moveItemStackTo(stack, 37, 46, false)) return ItemStack.EMPTY;
                    } else if (!this.moveItemStackTo(stack, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(stack, 10, 46, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
            if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
            slot.onTake(p, stack);
            if (index == 0) p.drop(stack, false);
        }
        return copy;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != resultSlots && super.canTakeItemForPickAll(stack, slot);
    }
}
