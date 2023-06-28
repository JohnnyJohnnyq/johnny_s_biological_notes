package com.mnsfhxy.johnny_s_biological_notes.init;

import com.mnsfhxy.johnny_s_biological_notes.JohnnySBiologicalNotes;
import com.mnsfhxy.johnny_s_biological_notes.entity.crab.EntityCrab;
//import com.mnsfhxy.johnny_s_biological_notes.entity.crab.BRendererCrab;
import com.mnsfhxy.johnny_s_biological_notes.entity.crab.ModelCrab;
import com.mnsfhxy.johnny_s_biological_notes.entity.crab.RendererCrab;
import com.mnsfhxy.johnny_s_biological_notes.entity.drifter.EntityDrifter;
import com.mnsfhxy.johnny_s_biological_notes.entity.peeper.EntityPeeper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

//注意！在使用了 @Mod.EventBusSubscriber 的类里，所有方法都必须有 public 和 static 修饰符。
@Mod.EventBusSubscriber(modid = JohnnySBiologicalNotes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModInit {
    public static final String TAB_NAME = "JohnnySBiologicalNotes";
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.DIAMOND);
        }

        @Override
        public String getRecipeFolderName() {
            return JohnnySBiologicalNotes.MODID;
        }
    };


    public static void init() {
//                IEventBus bus = MinecraftForge.EVENT_BUS;

    }

    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(RegistrationInit.CRAB.get(), EntityCrab.prepareAttributes().build());
        event.put(RegistrationInit.DRIFTER.get(), EntityDrifter.prepareAttributes().build());
        event.put(RegistrationInit.PEEPER.get(), EntityPeeper.prepareAttributes().build());


    }


    @SubscribeEvent
    public static void onSpawnPlacementRegisterEvent(SpawnPlacementRegisterEvent event) {
//        event.register(RegistrationInit.CRAB.get(),
//                SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                EntityCrab::checkCrabSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
//        event.register(RegistrationInit.PEEPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG,
//                EntityPeeper::checkPeeperSpawnRule,SpawnPlacementRegisterEvent.Operation.REPLACE);

    }


    @SubscribeEvent
    public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(PotionsInit::initBrewing);
        event.enqueueWork(EntityPeeper::init);
        event.enqueueWork(EntityCrab::init);

    }


}
