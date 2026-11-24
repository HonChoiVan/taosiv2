package com.taosi.recipe;

import com.google.gson.JsonObject;
import com.taosi.ModRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/** Cong thuc ve bua: chi dung duoc tren ban Taosicrafting. */
public class DrawingRecipe extends ShapelessRecipe {
    public DrawingRecipe(ResourceLocation id, String group, CraftingBookCategory cat, ItemStack result, NonNullList<Ingredient> ing) {
        super(id, group, cat, result, ing);
    }

    @Override
    public RecipeType<?> getType() {
        return ModRegistry.DRAWING_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.DRAWING_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<DrawingRecipe> {
        private static DrawingRecipe wrap(ShapelessRecipe r) {
            return new DrawingRecipe(r.getId(), r.getGroup(), r.category(), r.getResultItem(RegistryAccess.EMPTY), r.getIngredients());
        }

        @Override
        public DrawingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return wrap(RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json));
        }

        @Override
        public DrawingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return wrap(RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buf));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DrawingRecipe recipe) {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buf, recipe);
        }
    }
}
