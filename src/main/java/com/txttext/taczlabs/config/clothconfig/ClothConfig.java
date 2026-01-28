package com.txttext.taczlabs.config.clothconfig;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class ClothConfig {
    public static Screen createConfigScreen(Screen parent){
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.taczlabs.title"));
                //.setSavingRunnable(() -> {});
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        HudClothConfig.init(builder, entryBuilder);
        //FunctionClothConfig.init(builder, entryBuilder);

        return builder.build();
    }

    public static void register(){
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> createConfigScreen(parent))
        );
    }
}
