package com.wildcard.buddycards.util;

import com.wildcard.buddycards.BuddyCards;
import com.wildcard.buddycards.blocks.*;
import com.wildcard.buddycards.blocks.tiles.BuddysteelVaultTile;
import com.wildcard.buddycards.blocks.tiles.CardDisplayTile;
import com.wildcard.buddycards.blocks.tiles.CardStandTile;
import com.wildcard.buddycards.client.renderer.CardDisplayTileRenderer;
import com.wildcard.buddycards.client.renderer.CardStandTileRenderer;
import com.wildcard.buddycards.client.renderer.EnderlingRenderer;
import com.wildcard.buddycards.container.BinderContainer;
import com.wildcard.buddycards.container.VaultContainer;
import com.wildcard.buddycards.effects.GradingLuckEffect;
import com.wildcard.buddycards.enchantment.EnchantmentBuddyBinding;
import com.wildcard.buddycards.enchantment.EnchantmentBuddyBoost;
import com.wildcard.buddycards.enchantment.EnchantmentExtraPage;
import com.wildcard.buddycards.entities.EnderlingEntity;
import com.wildcard.buddycards.integration.CuriosIntegration;
import com.wildcard.buddycards.integration.aquaculture.AquacultureIntegration;
import com.wildcard.buddycards.integration.fd.FarmersDelightIntegration;
import com.wildcard.buddycards.items.*;
import com.wildcard.buddycards.items.buddysteel.*;
import com.wildcard.buddycards.loot.LootInjection;
import com.wildcard.buddycards.screen.BinderScreen;
import com.wildcard.buddycards.screen.VaultScreen;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BuddyCards.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BuddyCards.MOD_ID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BuddyCards.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BuddyCards.MOD_ID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, BuddyCards.MOD_ID);
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, BuddyCards.MOD_ID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, BuddyCards.MOD_ID);
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLMS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, BuddyCards.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BuddyCards.MOD_ID);

    public static void init() {
        if (ModList.get().isLoaded("aquaculture"))
            AquacultureIntegration.init();
        if (ModList.get().isLoaded("farmersdelight"))
            FarmersDelightIntegration.init();

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENCHANTMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
        GLMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());

        if (ModList.get().isLoaded("curios"))
            CuriosIntegration.Imc();
    }

    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ScreenManager.register(BINDER_CONTAINER.get(), BinderScreen::new));
        event.enqueueWork(() -> ScreenManager.register(VAULT_CONTAINER.get(), VaultScreen::new));
        for (RegistryObject<Item> card: CardRegistry.CARDS) {
            event.enqueueWork(() -> ItemModelsProperties.register(card.get(), new ResourceLocation("grade"), (stack, world, entity) -> {
                if(stack.getTag() != null)
                    return stack.getTag().getInt("grade");
                return 0;
            }));
        }
        if (ModList.get().isLoaded("aquaculture")) {
            event.enqueueWork(() -> ItemModelsProperties.register(AquacultureIntegration.BUDDYSTEEL_FISHING_ROD.get(), new ResourceLocation("cast"), (stack, world, entity) -> {
                if(entity instanceof PlayerEntity && (entity.getItemInHand(Hand.MAIN_HAND) == stack || entity.getItemInHand(Hand.OFF_HAND) == stack) && ((PlayerEntity) entity).fishing != null)
                    return 1;
                else
                    return 0;
            }));
        }
        ClientRegistry.bindTileEntityRenderer(CARD_DISPLAY_TILE.get(), CardDisplayTileRenderer::new);
        ClientRegistry.bindTileEntityRenderer(CARD_STAND_TILE.get(), CardStandTileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ENDERLING.get(), EnderlingRenderer::new);
    }

    //Packs
    public static final RegistryObject<Item> PACK_BASE = ITEMS.register("pack.1", () -> new PackItem(1));
    public static final RegistryObject<Item> PACK_NETHER = ITEMS.register("pack.2", () -> new PackItem(2));
    public static final RegistryObject<Item> PACK_END = ITEMS.register("pack.3", () -> new PackItem(3));
    public static final RegistryObject<Item> PACK_BYG = ITEMS.register("pack.4", () -> new PackItem(4));
    public static final RegistryObject<Item> PACK_CREATE = ITEMS.register("pack.5", () -> new PackItem(5));
    public static final RegistryObject<Item> PACK_AQUACULTURE = ITEMS.register("pack.6", () -> new PackItem(6));
    public static final RegistryObject<Item> PACK_FD = ITEMS.register("pack.7", () -> new PackItem(7));
    public static final RegistryObject<Item> PACK_MYSTERY = ITEMS.register("mystery_pack", () -> new PackItem(0));

    //Containers
    public static final RegistryObject<ContainerType<BinderContainer>> BINDER_CONTAINER = CONTAINERS.register("binder",
            () -> new ContainerType<>((BinderContainer::new)));
    public static final RegistryObject<ContainerType<VaultContainer>> VAULT_CONTAINER = CONTAINERS.register("vault",
            () -> new ContainerType<>((VaultContainer::new)));

    //Binders
    public static final RegistryObject<Item> BINDER_BASE = ITEMS.register("binder.1", () -> new BinderItem(1));
    public static final RegistryObject<Item> BINDER_NETHER = ITEMS.register("binder.2", () -> new BinderItem(2));
    public static final RegistryObject<Item> BINDER_END = ITEMS.register("binder.3", () -> new BinderItem(3));
    public static final RegistryObject<Item> BINDER_BYG = ITEMS.register("binder.4", () -> new BinderItem(4));
    public static final RegistryObject<Item> BINDER_CREATE = ITEMS.register("binder.5", () -> new BinderItem(5));
    public static final RegistryObject<Item> BINDER_AQUACULTURE = ITEMS.register("binder.6", () -> new BinderItem(6));
    public static final RegistryObject<Item> BINDER_FD = ITEMS.register("binder.7", () -> new BinderItem(7));
    public static final RegistryObject<Item> CHALLENGE_BINDER = ITEMS.register("challenge_binder", ChallengeBinder::new);
    public static final RegistryObject<Item> ENDER_BINDER = ITEMS.register("ender_binder", EnderBinderItem::new);

    //Medals
    public static final RegistryObject<Item> MEDAL_BASE = ITEMS.register("medal.1", () -> new SetMedalItem(1, MedalTypes.BASE_SET));
    public static final RegistryObject<Item> MEDAL_NETHER = ITEMS.register("medal.2", () -> new SetMedalItem(2, MedalTypes.NETHER_SET));
    public static final RegistryObject<Item> MEDAL_END = ITEMS.register("medal.3", () -> new SetMedalItem(3, MedalTypes.END_SET));
    public static final RegistryObject<Item> MEDAL_BYG = ITEMS.register("medal.4", () -> new SetMedalItem(4, MedalTypes.BYG_SET));
    public static final RegistryObject<Item> MEDAL_CREATE = ITEMS.register("medal.5", () -> new SetMedalItem(5, MedalTypes.CREATE_SET));
    public static final RegistryObject<Item> MEDAL_AQUACULTURE = ITEMS.register("medal.6", () -> new SetMedalItem(6, MedalTypes.AQUACULTURE_SET));
    public static final RegistryObject<Item> MEDAL_FD = ITEMS.register("medal.7", () -> new SetMedalItem(7, MedalTypes.FD_SET));

    //Misc
    public static final RegistryObject<Item> SHREDDED_BUDDYCARD = ITEMS.register("shredded_buddycard", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> ASSORTED_GUMMIES = ITEMS.register("assorted_gummies", () -> new Item(new Item.Properties().tab(BuddyCards.TAB).food(new Food.Builder().nutrition(2).saturationMod(0.3F).fast().build())));
    public static final RegistryObject<Item> MEDAL_TOKEN = ITEMS.register("medal_token", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> GRADING_SLEEVE = ITEMS.register("grading_sleeve", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> BUDDYSTEEL_KEY = ITEMS.register("buddysteel_key", () -> new Item(new Item.Properties().tab(BuddyCards.TAB).stacksTo(1)));
    public static final RegistryObject<Item> BUDDYBEANS = ITEMS.register("buddybeans", () -> new Item(new Item.Properties().tab(BuddyCards.TAB).food(new Food.Builder().nutrition(3).saturationMod(0.3F).build())));

    //Card Display Blocks
    public static final RegistryObject<Block> OAK_CARD_DISPLAY = BLOCKS.register("oak_card_display", CardDisplayBlock::new);
    public static final RegistryObject<Block> SPRUCE_CARD_DISPLAY = BLOCKS.register("spruce_card_display", CardDisplayBlock::new);
    public static final RegistryObject<Block> BIRCH_CARD_DISPLAY = BLOCKS.register("birch_card_display", CardDisplayBlock::new);
    public static final RegistryObject<Block> JUNGLE_CARD_DISPLAY = BLOCKS.register("jungle_card_display", CardDisplayBlock::new);
    public static final RegistryObject<Block> ACACIA_CARD_DISPLAY = BLOCKS.register("acacia_card_display", CardDisplayBlock::new);
    public static final RegistryObject<Block> DARK_OAK_CARD_DISPLAY = BLOCKS.register("dark_oak_card_display", CardDisplayBlock::new);
    public static final RegistryObject<Block> CRIMSON_CARD_DISPLAY = BLOCKS.register("crimson_card_display", CardDisplayBlock::new);
    public static final RegistryObject<Block> WARPED_CARD_DISPLAY = BLOCKS.register("warped_card_display", CardDisplayBlock::new);
    public static final RegistryObject<Block> CARD_STAND = BLOCKS.register("card_stand", CardStandBlock::new);
    //Byg Card Display Blocks
    public static final RegistryObject<Block> ASPEN_CARD_DISPLAY = BLOCKS.register("aspen_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> BAOBAB_CARD_DISPLAY = BLOCKS.register("baobab_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> BLUE_ENCHANTED_CARD_DISPLAY = BLOCKS.register("blue_enchanted_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> BULBIS_CARD_DISPLAY = BLOCKS.register("bulbis_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> CHERRY_CARD_DISPLAY = BLOCKS.register("cherry_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> CIKA_CARD_DISPLAY = BLOCKS.register("cika_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> CYPRESS_CARD_DISPLAY = BLOCKS.register("cypress_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> EBONY_CARD_DISPLAY = BLOCKS.register("ebony_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> EMBUR_CARD_DISPLAY = BLOCKS.register("embur_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> ETHER_CARD_DISPLAY = BLOCKS.register("ether_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> FIR_CARD_DISPLAY = BLOCKS.register("fir_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> GLACIAL_OAK_CARD_DISPLAY = BLOCKS.register("glacial_oak_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> GREEN_ENCHANTED_CARD_DISPLAY = BLOCKS.register("green_enchanted_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> HOLLY_CARD_DISPLAY = BLOCKS.register("holly_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> JACARANDA_CARD_DISPLAY = BLOCKS.register("jacaranda_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> LAMENT_CARD_DISPLAY = BLOCKS.register("lament_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> MAHOGANY_CARD_DISPLAY = BLOCKS.register("mahogany_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> MANGROVE_CARD_DISPLAY = BLOCKS.register("mangrove_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> MAPLE_CARD_DISPLAY = BLOCKS.register("maple_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> NIGHTSHADE_CARD_DISPLAY = BLOCKS.register("nightshade_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> PALM_CARD_DISPLAY = BLOCKS.register("palm_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> PINE_CARD_DISPLAY = BLOCKS.register("pine_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> RAINBOW_EUCALYPTUS_CARD_DISPLAY = BLOCKS.register("rainbow_eucalyptus_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> REDWOOD_CARD_DISPLAY = BLOCKS.register("redwood_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> SKYRIS_CARD_DISPLAY = BLOCKS.register("skyris_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> SYTHIAN_CARD_DISPLAY = BLOCKS.register("sythian_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> WILLOW_CARD_DISPLAY = BLOCKS.register("willow_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> WITCH_HAZEL_CARD_DISPLAY = BLOCKS.register("witch_hazel_card_display", () -> new CardDisplayBlock("byg"));
    public static final RegistryObject<Block> ZELKOVA_CARD_DISPLAY = BLOCKS.register("zelkova_card_display", () -> new CardDisplayBlock("byg"));
    //Buddysteel Card Vault Blocks
    public static final RegistryObject<Block> VAULT = BLOCKS.register("buddysteel_vault.1", () -> new BuddysteelVaultBlock(1));
    public static final RegistryObject<Block> VAULT_NETHER = BLOCKS.register("buddysteel_vault.2", () -> new BuddysteelVaultBlock(2));
    public static final RegistryObject<Block> VAULT_END = BLOCKS.register("buddysteel_vault.3", () -> new BuddysteelVaultBlock(3));
    public static final RegistryObject<Block> VAULT_BYG = BLOCKS.register("buddysteel_vault.4", () -> new BuddysteelVaultBlock(4));
    public static final RegistryObject<Block> VAULT_CREATE = BLOCKS.register("buddysteel_vault.5", () -> new BuddysteelVaultBlock(5));
    public static final RegistryObject<Block> VAULT_AQUACULTURE = BLOCKS.register("buddysteel_vault.6", () -> new BuddysteelVaultBlock(6));
    public static final RegistryObject<Block> VAULT_FD = BLOCKS.register("buddysteel_vault.7", () -> new BuddysteelVaultBlock(7));

    //Card Display Items
    public static final RegistryObject<BlockItem> OAK_CARD_DISPLAY_ITEM = ITEMS.register("oak_card_display", () -> new BlockItem(OAK_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> SPRUCE_CARD_DISPLAY_ITEM = ITEMS.register("spruce_card_display", () -> new BlockItem(SPRUCE_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> BIRCH_CARD_DISPLAY_ITEM = ITEMS.register("birch_card_display", () -> new BlockItem(BIRCH_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> JUNGLE_CARD_DISPLAY_ITEM = ITEMS.register("jungle_card_display", () -> new BlockItem(JUNGLE_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> ACACIA_CARD_DISPLAY_ITEM = ITEMS.register("acacia_card_display", () -> new BlockItem(ACACIA_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> DARK_OAK_CARD_DISPLAY_ITEM = ITEMS.register("dark_oak_card_display", () -> new BlockItem(DARK_OAK_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> CRIMSON_CARD_DISPLAY_ITEM = ITEMS.register("crimson_card_display", () -> new BlockItem(CRIMSON_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> WARPED_CARD_DISPLAY_ITEM = ITEMS.register("warped_card_display", () -> new BlockItem(WARPED_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> CARD_STAND_ITEM = ITEMS.register("card_stand", () -> new BlockItem(CARD_STAND.get(), new Item.Properties().tab(BuddyCards.TAB)));
    //Byg Card Display Items
    public static final RegistryObject<BlockItem> ASPEN_CARD_DISPLAY_ITEM = ITEMS.register("aspen_card_display", () -> new BlockItem(ASPEN_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> BAOBAB_CARD_DISPLAY_ITEM = ITEMS.register("baobab_card_display", () -> new BlockItem(BAOBAB_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> BLUE_ENCHANTED_CARD_DISPLAY_ITEM = ITEMS.register("blue_enchanted_card_display", () -> new BlockItem(BLUE_ENCHANTED_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> BULBIS_CARD_DISPLAY_ITEM = ITEMS.register("bulbis_card_display", () -> new BlockItem(BULBIS_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> CHERRY_CARD_DISPLAY_ITEM = ITEMS.register("cherry_card_display", () -> new BlockItem(CHERRY_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> CIKA_CARD_DISPLAY_ITEM = ITEMS.register("cika_card_display", () -> new BlockItem(CIKA_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> CYPRESS_CARD_DISPLAY_ITEM = ITEMS.register("cypress_card_display", () -> new BlockItem(CYPRESS_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> EBONY_CARD_DISPLAY_ITEM = ITEMS.register("ebony_card_display", () -> new BlockItem(EBONY_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> EMBUR_CARD_DISPLAY_ITEM = ITEMS.register("embur_card_display", () -> new BlockItem(EMBUR_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> ETHER_CARD_DISPLAY_ITEM = ITEMS.register("ether_card_display", () -> new BlockItem(ETHER_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> FIR_CARD_DISPLAY_ITEM = ITEMS.register("fir_card_display", () -> new BlockItem(FIR_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> GLACIAL_OAK_CARD_DISPLAY_ITEM = ITEMS.register("glacial_oak_card_display", () -> new BlockItem(GLACIAL_OAK_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> GREEN_ENCHANTED_CARD_DISPLAY_ITEM = ITEMS.register("green_enchanted_card_display", () -> new BlockItem(GREEN_ENCHANTED_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> HOLLY_CARD_DISPLAY_ITEM = ITEMS.register("holly_card_display", () -> new BlockItem(HOLLY_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> JACARANDA_CARD_DISPLAY_ITEM = ITEMS.register("jacaranda_card_display", () -> new BlockItem(JACARANDA_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> LAMENT_CARD_DISPLAY_ITEM = ITEMS.register("lament_card_display", () -> new BlockItem(LAMENT_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> MAHOGANY_CARD_DISPLAY_ITEM = ITEMS.register("mahogany_card_display", () -> new BlockItem(MAHOGANY_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> MANGROVE_CARD_DISPLAY_ITEM = ITEMS.register("mangrove_card_display", () -> new BlockItem(MANGROVE_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> MAPLE_CARD_DISPLAY_ITEM = ITEMS.register("maple_card_display", () -> new BlockItem(MAPLE_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> NIGHTSHADE_CARD_DISPLAY_ITEM = ITEMS.register("nightshade_card_display", () -> new BlockItem(NIGHTSHADE_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> PALM_CARD_DISPLAY_ITEM = ITEMS.register("palm_card_display", () -> new BlockItem(PALM_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> PINE_CARD_DISPLAY_ITEM = ITEMS.register("pine_card_display", () -> new BlockItem(PINE_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> RAINBOW_EUCALYPTUS_CARD_DISPLAY_ITEM = ITEMS.register("rainbow_eucalyptus_card_display", () -> new BlockItem(RAINBOW_EUCALYPTUS_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> REDWOOD_CARD_DISPLAY_ITEM = ITEMS.register("redwood_card_display", () -> new BlockItem(REDWOOD_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> SKYRIS_CARD_DISPLAY_ITEM = ITEMS.register("skyris_card_display", () -> new BlockItem(SKYRIS_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> SYTHIAN_CARD_DISPLAY_ITEM = ITEMS.register("sythian_card_display", () -> new BlockItem(SYTHIAN_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> WILLOW_CARD_DISPLAY_ITEM = ITEMS.register("willow_card_display", () -> new BlockItem(WILLOW_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> WITCH_HAZEL_CARD_DISPLAY_ITEM = ITEMS.register("witch_hazel_card_display", () -> new BlockItem(WITCH_HAZEL_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> ZELKOVA_CARD_DISPLAY_ITEM = ITEMS.register("zelkova_card_display", () -> new BlockItem(ZELKOVA_CARD_DISPLAY.get(), new Item.Properties().tab(BuddyCards.TAB)));
    //Buddysteel Card Vault Items
    public static final RegistryObject<BlockItem> VAULT_ITEM = ITEMS.register("buddysteel_vault.1", () -> new BlockItem(VAULT.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> VAULT_NETHER_ITEM = ITEMS.register("buddysteel_vault.2", () -> new BlockItem(VAULT_NETHER.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> VAULT_END_ITEM = ITEMS.register("buddysteel_vault.3", () -> new BlockItem(VAULT_END.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> VAULT_BYG_ITEM = ITEMS.register("buddysteel_vault.4", () -> new BlockItem(VAULT_BYG.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> VAULT_CREATE_ITEM = ITEMS.register("buddysteel_vault.5", () -> new BlockItem(VAULT_CREATE.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> VAULT_AQUACULTURE_ITEM = ITEMS.register("buddysteel_vault.6", () -> new BlockItem(VAULT_AQUACULTURE.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<BlockItem> VAULT_FD_ITEM = ITEMS.register("buddysteel_vault.7", () -> new BlockItem(VAULT_FD.get(), new Item.Properties().tab(BuddyCards.TAB)));

    public static final RegistryObject<TileEntityType<CardDisplayTile>> CARD_DISPLAY_TILE = TILE_ENTITIES.register("card_display",
            () -> TileEntityType.Builder.of(CardDisplayTile::new, OAK_CARD_DISPLAY.get(), SPRUCE_CARD_DISPLAY.get(),
                    BIRCH_CARD_DISPLAY.get(), JUNGLE_CARD_DISPLAY.get(), ACACIA_CARD_DISPLAY.get(), DARK_OAK_CARD_DISPLAY.get(),
                    CRIMSON_CARD_DISPLAY.get(), WARPED_CARD_DISPLAY.get(), ASPEN_CARD_DISPLAY.get(), BAOBAB_CARD_DISPLAY.get(),
                    BLUE_ENCHANTED_CARD_DISPLAY.get(), BULBIS_CARD_DISPLAY.get(), CHERRY_CARD_DISPLAY.get(), CIKA_CARD_DISPLAY.get(),
                    CYPRESS_CARD_DISPLAY.get(), EBONY_CARD_DISPLAY.get(), EMBUR_CARD_DISPLAY.get(), ETHER_CARD_DISPLAY.get(),
                    FIR_CARD_DISPLAY.get(), GLACIAL_OAK_CARD_DISPLAY.get(), GREEN_ENCHANTED_CARD_DISPLAY.get(), HOLLY_CARD_DISPLAY.get(),
                    JACARANDA_CARD_DISPLAY.get(), LAMENT_CARD_DISPLAY.get(), MAHOGANY_CARD_DISPLAY.get(), MANGROVE_CARD_DISPLAY.get(),
                    MAPLE_CARD_DISPLAY.get(), NIGHTSHADE_CARD_DISPLAY.get(), PALM_CARD_DISPLAY.get(), PINE_CARD_DISPLAY.get(),
                    RAINBOW_EUCALYPTUS_CARD_DISPLAY.get(), REDWOOD_CARD_DISPLAY.get(), SKYRIS_CARD_DISPLAY.get(), SYTHIAN_CARD_DISPLAY.get(),
                    WILLOW_CARD_DISPLAY.get(), WITCH_HAZEL_CARD_DISPLAY.get(), ZELKOVA_CARD_DISPLAY.get()).build(null));

    public static final RegistryObject<TileEntityType<CardStandTile>> CARD_STAND_TILE = TILE_ENTITIES.register("card_stand",
            () -> TileEntityType.Builder.of(CardStandTile::new, CARD_STAND.get()).build(null));

    public static final RegistryObject<TileEntityType<BuddysteelVaultTile>> VAULT_TILE = TILE_ENTITIES.register("buddysteel_vault",
            () -> TileEntityType.Builder.of(BuddysteelVaultTile::new, VAULT.get(), VAULT_NETHER.get(), VAULT_END.get(), VAULT_BYG.get(),
                    VAULT_CREATE.get(), VAULT_AQUACULTURE.get(), VAULT_FD.get()).build(null));

    public static final RegistryObject<Enchantment> BUDDY_BINDING = ENCHANTMENTS.register("buddy_binding", EnchantmentBuddyBinding::new);
    public static final RegistryObject<Enchantment> BUDDY_BOOST = ENCHANTMENTS.register("buddy_boost", EnchantmentBuddyBoost::new);
    public static final RegistryObject<Enchantment> EXTRA_PAGE = ENCHANTMENTS.register("extra_page", EnchantmentExtraPage::new);

    //Buddysteel & Zylex
    public static final RegistryObject<Block> BUDDYSTEEL_BLOCK = BLOCKS.register("buddysteel_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<BlockItem> BUDDYSTEEL_BLOCK_ITEM = ITEMS.register("buddysteel_block", () -> new BlockItem(BUDDYSTEEL_BLOCK.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> BUDDYSTEEL_INGOT = ITEMS.register("buddysteel_ingot", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> BUDDYSTEEL_BLEND = ITEMS.register("buddysteel_blend", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> BUDDYSTEEL_NUGGET = ITEMS.register("buddysteel_nugget", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> BUDDYSTEEL_HELMET = ITEMS.register("buddysteel_helmet", () -> new BuddysteelArmorItem(BuddysteelArmorMaterial.BUDDYSTEEL, EquipmentSlotType.HEAD));
    public static final RegistryObject<Item> BUDDYSTEEL_CHESTPLATE = ITEMS.register("buddysteel_chestplate", () -> new BuddysteelArmorItem(BuddysteelArmorMaterial.BUDDYSTEEL, EquipmentSlotType.CHEST));
    public static final RegistryObject<Item> BUDDYSTEEL_LEGGINGS = ITEMS.register("buddysteel_leggings", () -> new BuddysteelArmorItem(BuddysteelArmorMaterial.BUDDYSTEEL, EquipmentSlotType.LEGS));
    public static final RegistryObject<Item> BUDDYSTEEL_BOOTS = ITEMS.register("buddysteel_boots", () -> new BuddysteelArmorItem(BuddysteelArmorMaterial.BUDDYSTEEL, EquipmentSlotType.FEET));
    public static final RegistryObject<Item> BUDDYSTEEL_SWORD = ITEMS.register("buddysteel_sword", () -> new BuddysteelSwordItem(BuddysteelItemTier.BUDDYSTEEL));
    public static final RegistryObject<Item> BUDDYSTEEL_PICKAXE = ITEMS.register("buddysteel_pickaxe", () -> new BuddysteelPickaxeItem(BuddysteelItemTier.BUDDYSTEEL));
    public static final RegistryObject<Item> BUDDYSTEEL_SHOVEL = ITEMS.register("buddysteel_shovel", () -> new BuddysteelShovelItem(BuddysteelItemTier.BUDDYSTEEL));
    public static final RegistryObject<Item> BUDDYSTEEL_AXE = ITEMS.register("buddysteel_axe", () -> new BuddysteelAxeItem(BuddysteelItemTier.BUDDYSTEEL));
    public static final RegistryObject<Item> BUDDYSTEEL_HOE = ITEMS.register("buddysteel_hoe", () -> new BuddysteelHoeItem(BuddysteelItemTier.BUDDYSTEEL));

    public static final RegistryObject<Block> PERFECT_BUDDYSTEEL_BLOCK = BLOCKS.register("perfect_buddysteel_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<BlockItem> PERFECT_BUDDYSTEEL_BLOCK_ITEM = ITEMS.register("perfect_buddysteel_block", () -> new BlockItem(PERFECT_BUDDYSTEEL_BLOCK.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_INGOT = ITEMS.register("perfect_buddysteel_ingot", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_BLEND = ITEMS.register("perfect_buddysteel_blend", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_NUGGET = ITEMS.register("perfect_buddysteel_nugget", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_HELMET = ITEMS.register("perfect_buddysteel_helmet", () -> new BuddysteelArmorItem(BuddysteelArmorMaterial.PERFECT_BUDDYSTEEL, EquipmentSlotType.HEAD));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_CHESTPLATE = ITEMS.register("perfect_buddysteel_chestplate", () -> new BuddysteelArmorItem(BuddysteelArmorMaterial.PERFECT_BUDDYSTEEL, EquipmentSlotType.CHEST));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_LEGGINGS = ITEMS.register("perfect_buddysteel_leggings", () -> new BuddysteelArmorItem(BuddysteelArmorMaterial.PERFECT_BUDDYSTEEL, EquipmentSlotType.LEGS));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_BOOTS = ITEMS.register("perfect_buddysteel_boots", () -> new BuddysteelArmorItem(BuddysteelArmorMaterial.PERFECT_BUDDYSTEEL, EquipmentSlotType.FEET));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_SWORD = ITEMS.register("perfect_buddysteel_sword", () -> new BuddysteelSwordItem(BuddysteelItemTier.PERFECT_BUDDYSTEEL));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_PICKAXE = ITEMS.register("perfect_buddysteel_pickaxe", () -> new BuddysteelPickaxeItem(BuddysteelItemTier.PERFECT_BUDDYSTEEL));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_SHOVEL = ITEMS.register("perfect_buddysteel_shovel", () -> new BuddysteelShovelItem(BuddysteelItemTier.PERFECT_BUDDYSTEEL));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_AXE = ITEMS.register("perfect_buddysteel_axe", () -> new BuddysteelAxeItem(BuddysteelItemTier.PERFECT_BUDDYSTEEL));
    public static final RegistryObject<Item> PERFECT_BUDDYSTEEL_HOE = ITEMS.register("perfect_buddysteel_hoe", () -> new BuddysteelHoeItem(BuddysteelItemTier.PERFECT_BUDDYSTEEL));

    public static final RegistryObject<Block> ZYLEX_BLOCK = BLOCKS.register("zylex_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<BlockItem> ZYLEX_BLOCK_ITEM = ITEMS.register("zylex_block", () -> new BlockItem(ZYLEX_BLOCK.get(), new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> ZYLEX = ITEMS.register("zylex", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> ZYLEX_TOKEN = ITEMS.register("zylex_token", () -> new Item(new Item.Properties().tab(BuddyCards.TAB)));
    public static final RegistryObject<Item> ZYLEX_BOOTS = ITEMS.register("zylex_boots", () -> new BuddysteelArmorItem(BuddysteelArmorMaterial.ZYLEX, EquipmentSlotType.FEET));
    public static final RegistryObject<Item> ZYLEX_BAND = ITEMS.register("zylex_band", ZylexBandItem::new);
    public static final RegistryObject<Item> ZYLEX_MEDAL = ITEMS.register("zylex_medal", () -> new MedalItem(MedalTypes.ZYLEX));

    //Effects
    public static final RegistryObject<Effect> GRADING_LUCK = EFFECTS.register("grading_luck", GradingLuckEffect::new);

    //Potions
    public static final RegistryObject<Potion> GRADING_LUCK_NORMAL = POTIONS.register("grading_luck", () -> new Potion(new EffectInstance(GRADING_LUCK.get(), 3600)));
    public static final RegistryObject<Potion> GRADING_LUCK_STRONG = POTIONS.register("grading_luck_strong", () -> new Potion(new EffectInstance(GRADING_LUCK.get(), 1800, 1)));
    public static final RegistryObject<Potion> GRADING_LUCK_LONG = POTIONS.register("grading_luck_long", () -> new Potion(new EffectInstance(GRADING_LUCK.get(), 9600)));

    public static void brewingSetup() {
        PotionBrewing.addMix(Potions.AWKWARD, BUDDYSTEEL_BLEND.get(), GRADING_LUCK_NORMAL.get());
        PotionBrewing.addMix(GRADING_LUCK_NORMAL.get(), Items.GLOWSTONE_DUST, GRADING_LUCK_STRONG.get());
        PotionBrewing.addMix(GRADING_LUCK_NORMAL.get(), Items.REDSTONE, GRADING_LUCK_LONG.get());
    }

    //GLMs
    public static RegistryObject<GlobalLootModifierSerializer<LootInjection.LootInjectionModifier>> LOOT_INJECTION = GLMS.register("loot_injection", LootInjection.LootInjectionSerializer::new);

    //Entities
    public static RegistryObject<EntityType<EnderlingEntity>> ENDERLING = ENTITIES.register("enderling",
            () -> EntityType.Builder.of(EnderlingEntity::new, EntityClassification.CREATURE).sized(.6f, 1.8f).build(new ResourceLocation(BuddyCards.MOD_ID, "enderling").toString()));

    //Eggs
    public static final RegistryObject<ModdedSpawnEggItem> ENDERLING_SPAWN_EGG = ITEMS.register("spawn_egg_enderling", () ->
            new ModdedSpawnEggItem(ENDERLING, 0x2E2744, 0x9A72CC, new Item.Properties().tab(BuddyCards.TAB)));
}
