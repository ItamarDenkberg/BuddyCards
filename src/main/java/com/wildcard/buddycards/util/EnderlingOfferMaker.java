package com.wildcard.buddycards.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

public class EnderlingOfferMaker {
    public static MerchantOffer createCardBuyOffer() {
        ItemStack card = new ItemStack(CardRegistry.LOADED_CARDS.get((int)(Math.random() * (CardRegistry.LOADED_CARDS.size()))).get());
        ItemStack zylex = new ItemStack(RegistryHandler.ZYLEX.get(), getZylexValueOfCard(card));
        return new MerchantOffer(card, zylex, 1, 2, 1);
    }

    public static MerchantOffer createBulkCardBuyOffer() {
        ItemStack card = new ItemStack(CardRegistry.LOADED_CARDS.get((int)(Math.random() * (CardRegistry.LOADED_CARDS.size()))).get(), 8);
        ItemStack zylex = new ItemStack(RegistryHandler.ZYLEX.get(), getZylexValueOfCard(card) * 6);
        return new MerchantOffer(card, zylex, 1, 6, 1);
    }

    public static MerchantOffer createGradedCardBuyOffer() {
        ItemStack card = new ItemStack(CardRegistry.LOADED_CARDS.get((int)(Math.random() * (CardRegistry.LOADED_CARDS.size()))).get());
        CompoundNBT nbt = new CompoundNBT();
        int i = (int) (Math.random() * 200) + 1;
        int grade;
        if (i < 20)
            grade = 1;
        else if (i < 80)
            grade = 2;
        else if (i < 140)
            grade = 3;
        else
            grade = 4;
        nbt.putInt("grade", grade);
        card.setTag(nbt);
        int val = getZylexValueOfCard(card);
        ItemStack zylex = new ItemStack(RegistryHandler.ZYLEX.get(), val);
        if(val > 64) {
            zylex = new ItemStack(RegistryHandler.ZYLEX_BLOCK.get(), (int) (val / 9 + .5));
        }
        return new MerchantOffer(card, zylex, 3, grade * 2, 1);
    }

    public static MerchantOffer createGradedCardSellOffer() {
        ItemStack card = new ItemStack(CardRegistry.LOADED_CARDS.get((int)(Math.random() * (CardRegistry.LOADED_CARDS.size()))).get());
        CompoundNBT nbt = new CompoundNBT();
        int i = (int) (Math.random() * 200) + 1;
        int grade;
        if (i < 50)
            grade = 1;
        else if (i < 105)
            grade = 2;
        else if (i < 160)
            grade = 3;
        else if (i < 200)
            grade = 4;
        else
            grade = 5;
        nbt.putInt("grade", grade);
        card.setTag(nbt);
        int val = getZylexValueOfCard(card);
        ItemStack zylex = new ItemStack(RegistryHandler.ZYLEX.get(), val);
        if(val > 64) {
            zylex = new ItemStack(RegistryHandler.ZYLEX_BLOCK.get(), (int) (val / 9 + .5));
        }
        return new MerchantOffer(zylex, card, 1, grade, 1);
    }

    public static MerchantOffer createCardTradeOffer() {
        ItemStack card = new ItemStack(CardRegistry.LOADED_CARDS.get((int)(Math.random() * (CardRegistry.LOADED_CARDS.size()))).get());
        ItemStack card2 = new ItemStack(CardRegistry.LOADED_CARDS.get((int)(Math.random() * (CardRegistry.LOADED_CARDS.size()))).get());
        CompoundNBT nbt = new CompoundNBT();
        while(Math.abs(getZylexValueOfCard(card) - getZylexValueOfCard(card2)) > 2) {
            card = new ItemStack(CardRegistry.LOADED_CARDS.get((int)(Math.random() * (CardRegistry.LOADED_CARDS.size()))).get());
            card2 = new ItemStack(CardRegistry.LOADED_CARDS.get((int)(Math.random() * (CardRegistry.LOADED_CARDS.size()))).get());
        }
        int i = (int) (Math.random() * 200) + 1;
        int grade;
        if (i < 100)
            grade = 0;
        else if (i < 125)
            grade = 1;
        else if (i < 150)
            grade = 2;
        else if (i < 175)
            grade = 3;
        else if (i < 200)
            grade = 4;
        else
            grade = 5;
        if(grade > 0) {
            nbt.putInt("grade", grade);
            card.setTag(nbt);
            card2.setTag(nbt);
        }
        return new MerchantOffer(card, card2, 1, grade * 2, 1);
    }

    public static MerchantOffer createPackOffer() {
        if(Math.random() > .5)
            return new MerchantOffer(new ItemStack(RegistryHandler.ZYLEX.get(), 6 + (int) (Math.random()*3)), new ItemStack(RegistryHandler.PACK_END.get()), 1, 5, 1);
        return new MerchantOffer(new ItemStack(RegistryHandler.ZYLEX.get(), 13 + (int) (Math.random()*4)), new ItemStack(RegistryHandler.PACK_MYSTERY.get()), 1, 8, 1);
    }

    public static MerchantOffer createGenericOffer() {
        int amount = (int) (Math.random() * 12) + 1;
        double random = Math.random();
        if(random > .92)
            return new MerchantOffer(new ItemStack(RegistryHandler.ZYLEX.get(), amount + 1 + (int) (Math.random()/2*amount)), new ItemStack(RegistryHandler.CARD_STAND.get(), amount/3 + 1), 3, amount, 1);
        if(random > .86)
            return new MerchantOffer(new ItemStack(RegistryHandler.ZYLEX.get(), amount + (int) (Math.random()/2*amount)), new ItemStack(RegistryHandler.GRADING_SLEEVE.get(), amount/3 + 1), 3, amount, 1);
        if(random > .8)
            return new MerchantOffer(new ItemStack(RegistryHandler.ZYLEX.get(), amount + (int) (Math.random()*2*amount)), new ItemStack(RegistryHandler.BUDDYSTEEL_NUGGET.get(), amount), 3, amount*2, 1);
        if(random > .75)
            return new MerchantOffer(new ItemStack(RegistryHandler.ZYLEX.get(), 6 + (int) (Math.random()*4)), new ItemStack(RegistryHandler.ENDER_BINDER.get()), 1, 6, 1);
        if(random > .3)
            return createBulkCardBuyOffer();
        else
            return createCardTradeOffer();
    }

    public static MerchantOffer createSpecialtyOffer(Random random) {
        double rand = Math.random() * 2;
        if(rand%1 > .8) {
            ItemStack boots = new ItemStack(RegistryHandler.ZYLEX_MEDAL.get());
            if((int) rand != 0)
                EnchantmentHelper.enchantItem(random, boots, 15, true);
            return new MerchantOffer(new ItemStack(RegistryHandler.ZYLEX_TOKEN.get(), 2 + (int) (rand * 2)), boots, 1, 32, 1);
        }
        else if(rand%1 > .45) {
            ItemStack boots = new ItemStack(RegistryHandler.ZYLEX_BOOTS.get());
            if((int) rand != 0)
                EnchantmentHelper.enchantItem(random, boots, 15, true);
            return new MerchantOffer(new ItemStack(RegistryHandler.ZYLEX_TOKEN.get(), 2 + (int) rand), boots, 1, 32, 1);
        }
        else {
            return new MerchantOffer(new ItemStack(RegistryHandler.ZYLEX_TOKEN.get(), 2 + (int) rand), new ItemStack(RegistryHandler.ZYLEX_BAND.get()), 1, 32, 1);
        }
    }

    private static int getZylexValueOfCard(ItemStack card) {
        double value = .5;
        switch(card.getRarity()) {
            case EPIC: value += 3;
            case RARE: value += 1;
            case UNCOMMON: value += .5;
        }
        if(card.hasFoil())
            value *= 2.1;
        if(card.getTag() != null && card.getTag().contains("grade")) {
            switch(card.getTag().getInt("grade")) {
                case 1: value *=.75;
                break;
                case 2: value *=1.6;
                break;
                case 3: value *= 2.3;
                break;
                case 4: value *= 5.1;
                break;
                case 5: value *= 28.8;
            }
        }
        int fval = (int) value;
        return Math.max(fval, 1);
    }
}
