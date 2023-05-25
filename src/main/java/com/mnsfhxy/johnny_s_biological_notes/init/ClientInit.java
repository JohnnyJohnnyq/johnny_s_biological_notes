package com.mnsfhxy.johnny_s_biological_notes.init;

import com.mnsfhxy.johnny_s_biological_notes.JohnnySBiologicalNotes;
import com.mnsfhxy.johnny_s_biological_notes.entity.crab.ModelCrab;
import com.mnsfhxy.johnny_s_biological_notes.entity.crab.RendererCrab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JohnnySBiologicalNotes.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientInit {
    //        注册生物Layers
    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModelCrab.LAYER_LOCATION, ModelCrab::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RegistrationInit.CRAB.get(), RendererCrab::new);
    }


    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
//        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
//            return;
//        }
//        event.addSprite(PowergenRenderer.HALO);
    }

    @SubscribeEvent
    public static void onModelRegistryEvent(ModelEvent.RegisterGeometryLoaders event) {
//        event.register(GeneratorModelLoader.GENERATOR_LOADER.getPath(), new GeneratorModelLoader());
    }

    @SubscribeEvent
    public static void onKeyBindRegister(RegisterKeyMappingsEvent event) {
//        KeyBindings.init(event);
    }
    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
//        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "mana_overlay", ManaOverlay.HUD_MANA);
    }
}