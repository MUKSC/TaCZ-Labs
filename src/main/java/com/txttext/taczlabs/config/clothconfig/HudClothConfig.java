package com.txttext.taczlabs.config.clothconfig;

import com.txttext.taczlabs.config.fileconfig.HudConfig;
import com.txttext.taczlabs.hud.crosshair.Crosshair;
import com.txttext.taczlabs.hud.crosshair.CrosshairType;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class HudClothConfig {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void init(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory hud = builder.getOrCreateCategory(Component.translatable("config.taczlabs.hud"));
        /*是否启用TL准星*/
        hud.addEntry(entryBuilder.startBooleanToggle(
                Component.translatable("config.taczlabs.hud.enable_tl_crosshair"),
                        HudConfig.ENABLE_TL_CROSSHAIR.get()
                )
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.taczlabs.hud.enable_tl_crosshair.desc"))
                .setSaveConsumer(HudConfig.ENABLE_TL_CROSSHAIR::set)//Recommended: Called when user save the config
                .build()
        );
        //全局设置

        /*视觉效果*/
        List<AbstractConfigListEntry<?>> dynamicAndShadow = new ArrayList<>();

        //ARGB滑块
        dynamicAndShadow.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_alpha"), HudConfig.A.get(), 0, 255)
                .setSaveConsumer(val -> {
                    HudConfig.A.set(val);
                    updateColorFromARGB();
                }).build());
        dynamicAndShadow.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_red"), HudConfig.R.get(), 0, 255)
                .setSaveConsumer(val -> {
                    HudConfig.R.set(val);
                    updateColorFromARGB();
                }).build());
        dynamicAndShadow.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_green"), HudConfig.G.get(), 0, 255)
                .setSaveConsumer(val -> {
                    HudConfig.G.set(val);
                    updateColorFromARGB();
                }).build());
        dynamicAndShadow.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_blue"), HudConfig.B.get(), 0, 255)
                .setSaveConsumer(val -> {
                    HudConfig.B.set(val);
                    updateColorFromARGB();
                }).build());
        //阴影不透明度
        dynamicAndShadow.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.shadow_alpha"),
                        HudConfig.shadowAlpha.get(), 0, 255)
                .setTooltip(Component.translatable("config.taczlabs.hud.shadow_alpha.desc"))
                .setDefaultValue(128)
                .setSaveConsumer(HudConfig.shadowAlpha::set)
                .build());
        //阴影偏移
        dynamicAndShadow.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.shadow_offset"),
                        HudConfig.shadowOffset.get(), 0, 3)
                .setTooltip(Component.translatable("config.taczlabs.hud.shadow_offset.desc"))
                .setDefaultValue(1)
                .setSaveConsumer(HudConfig.shadowOffset::set)
                .build());
        //严格按照散射值的准星扩散
//        dynamicAndShadow.add(entryBuilder.startBooleanToggle(
//                                Component.translatable("config.taczlabs.hud.inaccuracy_spread"),
//                                HudConfig.inaccuracySpread.get()
//                        )
//                        .setDefaultValue(true)
//                        .setTooltip(Component.translatable("config.taczlabs.hud.inaccuracy_spread.desc"))
//                        .setSaveConsumer(HudConfig.inaccuracySpread::set)//Recommended: Called when user save the config
//                        .build()
//        );
        dynamicAndShadow.add(entryBuilder.startEnumSelector(
                        Component.translatable("config.taczlabs.hud.spread_type"),
                        Crosshair.SpreadType.class,
                        HudConfig.spreadTypes.get()
                  )
                        .setDefaultValue(HudConfig.spreadTypes.getDefault())
                        .setTooltip(Component.translatable("config.taczlabs.hud.spread_type.desc"))
                        .setEnumNameProvider(value -> Component.translatable("config.taczlabs.hud.spread_type." + value.name().toLowerCase()))
                        .setSaveConsumer(HudConfig.spreadTypes::set)//Recommended: Called when user save the config
                        .build()
        );
        //移速影响最大值
//        dynamicAndShadow.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.speed_spread"),
//                        HudConfig.speedSpread.get(), 0, 100)
//                .setTooltip(Component.translatable("config.taczlabs.hud.speed_spread.desc"))
//                .setDefaultValue(100)
//                .setSaveConsumer(HudConfig.speedSpread::set)
//                .build());
        //扩散幅度
        dynamicAndShadow.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.max_spread"),
                        HudConfig.maxSpread.get(), 0, 50)
                .setTooltip(Component.translatable("config.taczlabs.hud.max_spread.desc"))
                .setDefaultValue(30)
                .setSaveConsumer(HudConfig.maxSpread::set)
                .build());
        //动画速度
        dynamicAndShadow.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.anim_speed"),
                        HudConfig.animSpeed.get(), 10 , 30)
                .setDefaultValue(20)
                .setTooltip(Component.translatable("config.taczlabs.hud.anim_speed.desc"))
                .setSaveConsumer(HudConfig.animSpeed::set)
                .build()
        );
        //开火抖动
        dynamicAndShadow.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.shooting_spread"),
                        HudConfig.shootingSpread.get(), 0 , 10)
                .setDefaultValue(5)
                .setTooltip(Component.translatable("config.taczlabs.hud.shooting_spread.desc"))
                .setSaveConsumer(HudConfig.shootingSpread::set)
                .build()
        );
        hud.addEntry(entryBuilder.startSubCategory(Component.translatable("config.taczlabs.hud.dynamic_and_shadow"),
                        (List<AbstractConfigListEntry>)(List<?>) dynamicAndShadow
                )
                .setTooltip(Component.translatable("config.taczlabs.hud.dynamic_and_shadow.desc"))
                .setExpanded(true)//默认展开
                .build());

        //准星类型（配置不同枪械使用的准星）
        //List<AbstractConfigListEntry<?>> alphaColor = new ArrayList<>();
        List<AbstractConfigListEntry<?>> crosshairType = new ArrayList<>();
        //手枪准星
        crosshairType.add(entryBuilder.startEnumSelector(
                                Component.translatable("config.taczlabs.hud.gun.crosshair.pistol"),
                                CrosshairType.class,
                                HudConfig.pistolCrosshair.get()
                        )
                        .setDefaultValue(HudConfig.pistolCrosshair.getDefault())//获取HudConfig的默认值（TL_CROSSHAIR）
                        .setEnumNameProvider(value -> Component.translatable("config.taczlabs.hud.crosshair." + value.name().toLowerCase()))
                        .setSaveConsumer(HudConfig.pistolCrosshair::set)
                        .build()
        );
        //冲锋枪准星
        crosshairType.add(entryBuilder.startEnumSelector(
                                Component.translatable("config.taczlabs.hud.gun.crosshair.smg"),
                                CrosshairType.class,
                                HudConfig.smgCrosshair.get()
                        )
                        .setDefaultValue(HudConfig.smgCrosshair.getDefault())//获取HudConfig的默认值（TL_CROSSHAIR）
                        .setEnumNameProvider(value -> Component.translatable("config.taczlabs.hud.crosshair." + value.name().toLowerCase()))
                        .setSaveConsumer(HudConfig.smgCrosshair::set)
                        .build()
        );
        //步枪准星
        crosshairType.add(entryBuilder.startEnumSelector(
                                Component.translatable("config.taczlabs.hud.gun.crosshair.rifle"),
                                CrosshairType.class,
                                HudConfig.rifleCrosshair.get()
                        )
                        .setDefaultValue(HudConfig.rifleCrosshair.getDefault())
                        .setEnumNameProvider(value -> Component.translatable("config.taczlabs.hud.crosshair." + value.name().toLowerCase()))
                        .setSaveConsumer(HudConfig.rifleCrosshair::set)
                        .build()
        );
        //机枪准星
        crosshairType.add(entryBuilder.startEnumSelector(
                                Component.translatable("config.taczlabs.hud.gun.crosshair.mg"),
                                CrosshairType.class,
                                HudConfig.mgCrosshair.get()
                        )
                        .setDefaultValue(HudConfig.mgCrosshair.getDefault())
                        .setEnumNameProvider(value -> Component.translatable("config.taczlabs.hud.crosshair." + value.name().toLowerCase()))
                        .setSaveConsumer(HudConfig.mgCrosshair::set)
                        .build()
        );
        //狙击枪准星
        crosshairType.add(entryBuilder.startEnumSelector(
                                Component.translatable("config.taczlabs.hud.gun.crosshair.sniper"),
                                CrosshairType.class,
                                HudConfig.sniperCrosshair.get()
                        )
                        .setDefaultValue(HudConfig.sniperCrosshair.getDefault())
                        .setEnumNameProvider(value -> Component.translatable("config.taczlabs.hud.crosshair." + value.name().toLowerCase()))
                        .setSaveConsumer(HudConfig.sniperCrosshair::set)
                        .build()
        );
        //霰弹枪准星
        crosshairType.add(entryBuilder.startEnumSelector(
                                Component.translatable("config.taczlabs.hud.gun.crosshair.shotgun"),
                                CrosshairType.class,
                                HudConfig.shotgunCrosshair.get()
                        )
                        .setDefaultValue(HudConfig.shotgunCrosshair.getDefault())
                        .setEnumNameProvider(value -> Component.translatable("config.taczlabs.hud.crosshair." + value.name().toLowerCase()))
                        .setSaveConsumer(HudConfig.shotgunCrosshair::set)
                        .build()
        );
        //重武器准星
        crosshairType.add(entryBuilder.startEnumSelector(
                                Component.translatable("config.taczlabs.hud.gun.crosshair.rpg"),
                                CrosshairType.class,
                                HudConfig.rpgCrosshair.get()
                        )
                        .setDefaultValue(HudConfig.rpgCrosshair.getDefault())
                        .setEnumNameProvider(value -> Component.translatable("config.taczlabs.hud.crosshair." + value.name().toLowerCase()))
                        .setSaveConsumer(HudConfig.rpgCrosshair::set)
                        .build()
        );
        //自定义准星列表
//        List<String> configList = new ArrayList<>(HudConfig.customCrosshairs.get());
//        crosshairType.add(entryBuilder.)
        //注册折叠选项
        hud.addEntry(entryBuilder.startSubCategory(Component.translatable("config.taczlabs.hud.crosshair.crosshair_type"),
                        (List<AbstractConfigListEntry>)(List<?>)crosshairType
                )
                .setTooltip(Component.translatable("config.taczlabs.hud.crosshair.crosshair_type.desc"))
                .setExpanded(true)//默认展开
                .build());

        //局部设置

        //各准星设置
        List<AbstractConfigListEntry<?>> eachCrosshairProperties = new ArrayList<>();
        //十字准星设置
        List<AbstractConfigListEntry<?>> crosshairProperties = new ArrayList<>();
        crosshairProperties.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_radius"),
                        (HudConfig.crosshairRadius.get()), 0, 10)
                .setDefaultValue(5)
                .setSaveConsumer(HudConfig.crosshairRadius::set)
                .build());
        crosshairProperties.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_length"),
                        (HudConfig.crosshairLength.get()), 1, 20)
                .setDefaultValue(6)
                .setSaveConsumer(HudConfig.crosshairLength::set)
                .build());
        crosshairProperties.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_width"),
                        (HudConfig.crosshairWidth.get()), 1, 10)
                .setDefaultValue(1)
                .setSaveConsumer(HudConfig.crosshairWidth::set)
                .build());
        eachCrosshairProperties.add(entryBuilder.startSubCategory(Component.translatable("config.taczlabs.hud.crosshair.crosshair"),
                        (List<AbstractConfigListEntry>)(List<?>) crosshairProperties).setExpanded(true).build());
        //方形准星设置
        List<AbstractConfigListEntry<?>> rectCrosshairProperties = new ArrayList<>();
        rectCrosshairProperties.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_radius"),
                        (HudConfig.rectCrosshairRadius.get()), 0, 20)
                .setDefaultValue(10)
                .setSaveConsumer(HudConfig.rectCrosshairRadius::set)
                .build());
        rectCrosshairProperties.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_length"),
                        (HudConfig.rectCrosshairLength.get()), 1, 20)
                .setDefaultValue(6)
                .setSaveConsumer(HudConfig.rectCrosshairLength::set)
                .build());
        rectCrosshairProperties.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_width"),
                        (HudConfig.rectCrosshairWidth.get()), 1, 10)
                .setDefaultValue(1)
                .setSaveConsumer(HudConfig.rectCrosshairWidth::set)
                .build());
        eachCrosshairProperties.add(entryBuilder.startSubCategory(Component.translatable("config.taczlabs.hud.crosshair.rect"),
                        (List<AbstractConfigListEntry>)(List<?>) rectCrosshairProperties).setExpanded(true).build());
        //直角准星设置
        List<AbstractConfigListEntry<?>> rightAngleCrosshairProperties = new ArrayList<>();
        rightAngleCrosshairProperties.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_radius"),
                        HudConfig.rightAngleCrosshairRadius.get(), 0, 10)
                .setDefaultValue(4)
                .setSaveConsumer(HudConfig.rightAngleCrosshairRadius::set)
                .build());
//        rightAngleCrosshairProperties.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_length"),
//                        (HudConfig.rightAngleCrosshairLength.get()), 1, 30)
//                .setDefaultValue(12)
//                .setSaveConsumer(HudConfig.rightAngleCrosshairLength::set)
//                .build());
        rightAngleCrosshairProperties.add(entryBuilder.startIntSlider(Component.translatable("config.taczlabs.hud.crosshair_width"),
                        HudConfig.rightAngleCrosshairWidth.get(), 1, 5)
                .setDefaultValue(1)
                .setSaveConsumer(HudConfig.rightAngleCrosshairWidth::set)
                .build());
        eachCrosshairProperties.add(entryBuilder.startSubCategory(Component.translatable("config.taczlabs.hud.crosshair.right_angle"),
                        (List<AbstractConfigListEntry>)(List<?>) rightAngleCrosshairProperties).setExpanded(true).build());
        //回到第一层嵌套
        hud.addEntry(entryBuilder.startSubCategory(Component.translatable("config.taczlabs.hud.crosshair.each_crosshair_prop"),
                        (List<AbstractConfigListEntry>)(List<?>) eachCrosshairProperties)
                .setTooltip(Component.translatable("config.taczlabs.hud.crosshair.each_crosshair_prop.desc"))
                .setExpanded(true).build());
    }

    //从ARGB更新color整体
    private static void updateColorFromARGB() {
        int argb = (HudConfig.A.get() & 0xFF) << 24 |
                (HudConfig.R.get() & 0xFF) << 16 |
                (HudConfig.G.get() & 0xFF) << 8  |
                (HudConfig.B.get() & 0xFF);
        HudConfig.color.set(argb);
    }
    private static void addListSlider(
            ConfigEntryBuilder entryBuilder,
            List<AbstractConfigListEntry<?>> list,
            String name,
            String toolTip,
            ForgeConfigSpec.DoubleValue value,
            int defaultValue,
            int min,
            int max
    ){
        list.add(entryBuilder.startIntSlider(Component.translatable(name), (int)(value.get() * 10), min, max)
                .setTooltip(Component.translatable(toolTip))
                .setDefaultValue(defaultValue)
                .setSaveConsumer(val -> value.set(( (double)val)/10D)
                ).build());

    }
}

