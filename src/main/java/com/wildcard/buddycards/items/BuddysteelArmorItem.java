package com.wildcard.buddycards.items;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.wildcard.buddycards.BuddyCards;
import com.wildcard.buddycards.util.RatioFinder;
import com.wildcard.buddycards.util.RegistryHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import java.util.*;

public class BuddysteelArmorItem extends ArmorItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private static final int[][] DAMAGE_REDUCTION_ARRAY = new int[][] {{1, 4, 5, 2}, {2, 5, 6, 2}, {3, 6, 8, 3}, {4, 7, 9, 4}};

    public BuddysteelArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot) {
        super(materialIn, slot, new Item.Properties().group(BuddyCards.TAB));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item." + BuddyCards.MOD_ID + ".buddysteel_gear.desc1"));
        if(stack.hasTag())
            tooltip.add(new TranslationTextComponent( "item." + BuddyCards.MOD_ID + ".buddysteel_gear.desc2")
                .appendString(String.valueOf((int)(stack.getTag().getFloat("completion") * 100)))
                .append(new TranslationTextComponent( "item." + BuddyCards.MOD_ID + ".buddysteel_gear.desc3")));
        tooltip.add(new TranslationTextComponent("item." + BuddyCards.MOD_ID + ".buddysteel_gear.desc4"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(playerIn instanceof ServerPlayerEntity) {
            CompoundNBT nbt = playerIn.getHeldItem(handIn).getTag();
            nbt.putFloat("completion", RatioFinder.getRatio((ServerPlayerEntity) playerIn));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == this.getEquipmentSlot() && stack.hasTag()) {
            multimap = LinkedHashMultimap.create();
            UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
            float ratio = stack.getTag().getFloat("completion");
            multimap.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", DAMAGE_REDUCTION_ARRAY[(int) (3 * ratio)][slot.getIndex()], AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (int) (ratio * 3), AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }
}
