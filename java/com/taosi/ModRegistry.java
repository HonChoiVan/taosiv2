package com.taosi;

import com.taosi.block.TaosiCraftingBlock;
import com.taosi.entity.ThrownTalisman;
import com.taosi.item.PeachSwordItem;
import com.taosi.item.TalismanItem;
import com.taosi.logic.TalismanType;
import com.taosi.menu.DrawingMenu;
import com.taosi.recipe.DrawingRecipe;
import com.taosi.effect.SimpleEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModRegistry {
    private static final String ID = TaosiMod.MODID;

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);

    // ---- vat pham thuong
    public static final RegistryObject<Item> PEACH_WOOD = ITEMS.register("peach_wood", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INK = ITEMS.register("taosi_ink", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PEACH_SWORD = ITEMS.register("peach_sword", () -> new PeachSwordItem(new Item.Properties()));

    // ---- ban che tao
    public static final RegistryObject<Block> TAOSI_CRAFTING = BLOCKS.register("taosicrafting",
            () -> new TaosiCraftingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Item> TAOSI_CRAFTING_ITEM = ITEMS.register("taosicrafting",
            () -> new BlockItem(TAOSI_CRAFTING.get(), new Item.Properties()));

    // ---- 10 loai bua x 3 cap
    public static final Map<String, RegistryObject<Item>> TALISMANS = new LinkedHashMap<>();
    static {
        for (TalismanType t : TalismanType.values()) {
            for (int lv = 1; lv <= 3; lv++) {
                final TalismanType ft = t;
                final int fl = lv;
                String name = "talisman_" + t.id + "_" + lv;
                TALISMANS.put(name, ITEMS.register(name, () -> new TalismanItem(ft, fl, new Item.Properties().stacksTo(16))));
            }
        }
    }

    // ---- hieu ung
    public static final RegistryObject<MobEffect> SWIFT = EFFECTS.register("swift",
            () -> new SimpleEffect(MobEffectCategory.BENEFICIAL, 0x7CFC00)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "7f2c1c10-5d2e-4c3a-9a51-100000000001", 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> SLOWED = EFFECTS.register("slowed",
            () -> new SimpleEffect(MobEffectCategory.HARMFUL, 0x5F7FA0)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "7f2c1c10-5d2e-4c3a-9a51-100000000002", -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> WEAKENED = EFFECTS.register("weakened",
            () -> new SimpleEffect(MobEffectCategory.HARMFUL, 0x8B4513));

    // ---- thuc the
    public static final RegistryObject<EntityType<ThrownTalisman>> THROWN = ENTITIES.register("thrown_talisman",
            () -> EntityType.Builder.<ThrownTalisman>of(ThrownTalisman::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("thrown_talisman"));

    // ---- menu + cong thuc ve bua
    public static final RegistryObject<MenuType<DrawingMenu>> DRAWING_MENU = MENUS.register("drawing",
            () -> IForgeMenuType.create((id, inv, buf) -> new DrawingMenu(id, inv)));
    public static final RegistryObject<RecipeType<DrawingRecipe>> DRAWING_TYPE = RECIPE_TYPES.register("drawing",
            () -> RecipeType.simple(new ResourceLocation(ID, "drawing")));
    public static final RegistryObject<RecipeSerializer<DrawingRecipe>> DRAWING_SERIALIZER = SERIALIZERS.register("drawing",
            DrawingRecipe.Serializer::new);

    // ---- tab sang tao
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.taosi"))
                    .icon(() -> new ItemStack(TALISMANS.get("talisman_explosion_1").get()))
                    .displayItems((params, out) -> ITEMS.getEntries().forEach(o -> out.accept(o.get())))
                    .build());

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        BLOCKS.register(bus);
        EFFECTS.register(bus);
        ENTITIES.register(bus);
        MENUS.register(bus);
        RECIPE_TYPES.register(bus);
        SERIALIZERS.register(bus);
        TABS.register(bus);
    }
}
