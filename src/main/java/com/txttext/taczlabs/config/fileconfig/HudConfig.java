package com.txttext.taczlabs.config.fileconfig;

import com.txttext.taczlabs.hud.crosshair.Crosshair;
import com.txttext.taczlabs.hud.crosshair.CrosshairType;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/**客户端的 HUD 配置*/
public class HudConfig {
    public static ForgeConfigSpec.BooleanValue ENABLE_TL_CROSSHAIR;//是否启用本模组的准星

    //public static ForgeConfigSpec.BooleanValue inaccuracySpread;//严格按照真实扩散值作为准星扩散
    public static ForgeConfigSpec.EnumValue<Crosshair.SpreadType> spreadTypes;
    /*全局设置*/
    public static ForgeConfigSpec.IntValue color;//准星颜色值（RGBA HEX）
    public static ForgeConfigSpec.IntValue R;//准星颜色值（RGBA）
    public static ForgeConfigSpec.IntValue G;//准星颜色值（RGBA）
    public static ForgeConfigSpec.IntValue B;//准星颜色值（RGBA）
    public static ForgeConfigSpec.IntValue A;//准星颜色值（RGBA）
    //
    public static ForgeConfigSpec.IntValue shadowAlpha;//阴影不透明度
    public static ForgeConfigSpec.IntValue shadowOffset;//阴影偏移量
    public static ForgeConfigSpec.IntValue maxSpread;//最大准星扩散半径
    public static ForgeConfigSpec.IntValue animSpeed;//动画速度
    public static ForgeConfigSpec.IntValue shootingSpread;//开火扩散
    //
    public static ForgeConfigSpec.EnumValue<CrosshairType> pistolCrosshair;//手枪准星
    public static ForgeConfigSpec.EnumValue<CrosshairType> smgCrosshair;//冲锋枪准星
    public static ForgeConfigSpec.EnumValue<CrosshairType> rifleCrosshair;//步枪准星
    public static ForgeConfigSpec.EnumValue<CrosshairType> mgCrosshair;//机枪准星
    public static ForgeConfigSpec.EnumValue<CrosshairType> shotgunCrosshair;//霰弹枪准星
    public static ForgeConfigSpec.EnumValue<CrosshairType> sniperCrosshair;//狙击枪准星
    public static ForgeConfigSpec.EnumValue<CrosshairType> rpgCrosshair;//重武器准星
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> customCrosshairs;//自定义名单（需要 GunID）

    /*局部设置*/
    public static ForgeConfigSpec.IntValue crosshairRadius;//准星基础半径（十字准星）
    public static ForgeConfigSpec.IntValue crosshairLength;//准星长度（十字准星）
    public static ForgeConfigSpec.IntValue crosshairWidth;//准星宽度（十字准星）
    public static ForgeConfigSpec.IntValue rectCrosshairRadius;//准星基础半径（方形准星）
    public static ForgeConfigSpec.IntValue rectCrosshairLength;//准星长度（方形准星）
    public static ForgeConfigSpec.IntValue rectCrosshairWidth;//准星宽度（方形准星）
    public static ForgeConfigSpec.IntValue rightAngleCrosshairRadius;//准星基础半径（直角准星）
//    public static ForgeConfigSpec.IntValue rightAngleCrosshairLength;//准星长度（直角准星）
    public static ForgeConfigSpec.IntValue rightAngleCrosshairWidth;//准星宽度（直角准星）



    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("HUD");

        /*是否启用本模组的准星*/
        ENABLE_TL_CROSSHAIR = builder
                .comment("New Crosshair of TaCZ:Labs.")
                .define("Enable TaCZ:Labs Crosshair", true);

        /*全局设置*/
        builder.push("Global Settings");
        /*准星颜色值（RGBA）*/
        color = builder
                .comment("Crosshair RGBA HEX color, format: 0xAARRGGBB.")
                .defineInRange("Crosshair Color", 0xE6FFFFCC, Integer.MIN_VALUE, Integer.MAX_VALUE);
        A = builder.defineInRange("A", 0xE6, 0, 255);
        R = builder.defineInRange("R", 0xFF, 0, 255);
        G = builder.defineInRange("G", 0xFF, 0, 255);
        B = builder.defineInRange("B", 0xCC, 0, 255);

        //阴影不透明度
        shadowAlpha = builder
                .comment("Shadow alpha of crosshair, range 0 ~ 255, default 128.")
                .defineInRange("Shadow Alpha", 128, 0, 255);
        /*准星阴影偏移量*/
        shadowOffset = builder
                .comment("Shadow offset of crosshair, range 1 ~ 6, default 1 .")
                .defineInRange("Shadow Offset", 1, 0, 3);
        builder.pop();
        //扩散类型
        spreadTypes = builder
                .comment("Real: Follows the actual firearm spread\nVirtual: moving*2, sneaking*0.7, lying*0.5\nSpeed: Moving based on player speed, sneaking*0.7, lying*0.5")
                .defineEnum("Spread Rules", Crosshair.SpreadType.REAL);
//        //移速影响最大值，准星受速度影响扩散的最大值
//        speedSpread = builder
//                .comment("Maximum value of collimator spread affected by velocity, range 0 ~ 100, default 100 .")
//                .defineInRange("Max Speed Spread", 100, 0, 100);
        /*最大扩散半径*/
        maxSpread = builder
                .comment("Max spread radius, range 0 ~ 50, default 30.")
                .defineInRange("Crosshair Max Spread", 30, 0, 50);
        //准星动画速度
        animSpeed = builder
                .comment("Speed of crosshair anim. range 10 ~ 30, default 20.")
                .defineInRange("Anim Speed", 20, 10, 30);
        //开火抖动
        shootingSpread = builder
                .comment("Magnitude of collimation spread according to the recoil at the time of firing, range 0 ~ 5, default 2.")
                .defineInRange("Shooting Judder", 5, 0, 10);
        builder.pop();
        /*局部设置*/
        builder.push("Local Settings");
        //准星类型
        builder.push("Crosshair Type");
        pistolCrosshair = builder.comment("Crosshair of Pistol").defineEnum("Pistol", CrosshairType.RIGHT_ANGLE);//手枪
        smgCrosshair = builder.comment("Crosshair of Sub-Machine Gun").defineEnum("Sub-Machine Gun", CrosshairType.CROSSHAIR);//冲锋枪
        rifleCrosshair = builder.comment("Crosshair of Rifle").defineEnum("Rifle", CrosshairType.CROSSHAIR);//步枪
        mgCrosshair = builder.comment("Crosshair of Machine Gun").defineEnum("Machine Gun", CrosshairType.CROSSHAIR);//机枪
        sniperCrosshair = builder.comment("Crosshair of Sniper").defineEnum("Sniper", CrosshairType.CROSSHAIR);//狙击枪
        shotgunCrosshair = builder.comment("Crosshair of Shotgun").defineEnum("Shotgun", CrosshairType.RECT);//霰弹枪
        rpgCrosshair = builder.comment("Crosshair of Heavy Weapon").defineEnum("Heavy Weapon", CrosshairType.RECT);//重武器
        //自定义名单
//        customCrosshairs = builder
//                .comment("""
//            Custom list of gun and crosshair.
//            Format: "GunID=CrosshairType"
//            Example: "tacz:example=CROSSHAIR"
//            """)
//                .defineListAllowEmpty(
//                        List.of("custom_crosshairs"),
//                        List.of("tacz:example=CROSSHAIR"),
//                        obj -> obj instanceof String && ((String) obj).contains("=")
//                );
        builder.pop();

        builder.push("Crosshair Properties");
        //十字准星半径
        crosshairRadius = builder
                .comment("Radius of crosshair, range 0 ~ 10, default 5 .")
                .defineInRange("Crosshair Radius", 5, 0, 10);
        //十字准星长度
        crosshairLength = builder
                .comment("Length of crosshair, range 1 ~ 20, default 6 .")
                .defineInRange("Crosshair Length", 6, 1, 20);
        //十字准星粗细
        crosshairWidth = builder
                .comment("Width of crosshair, range 1 ~ 10, default 2 .")
                .defineInRange("Crosshair Width", 1, 1, 10);
        //方形准星半径
        rectCrosshairRadius = builder
                .comment("Radius of rect crosshair, range 0 ~ 20, default 12 .")
                .defineInRange("Rect Crosshair Radius", 10, 0, 20);
        //方形准星长度
        rectCrosshairLength = builder
                .comment("Length of rect crosshair, range 1 ~ 20, default 6 .")
                .defineInRange("Rect Crosshair Length", 6, 1, 20);
        //方形准星宽度
        rectCrosshairWidth = builder
                .comment("Width of rect crosshair, range 1 ~ 10, default 1 .")
                .defineInRange("Rect Crosshair Width", 1, 1, 10);
        //直角准星半径
        rightAngleCrosshairRadius = builder
                .comment("Radius of right angle crosshair, range 0 ~ 10, default 4 .")
                .defineInRange("Right Angle Crosshair Radius", 4, 0, 10);
//        //直角准星长度
//        rightAngleCrosshairLength = builder
//                .comment("Length of right angle crosshair, range 1 ~ 30, default 12 .")
//                .defineInRange("Right Angle Crosshair Length", 12, 1, 30);
        //直角准星宽度
        rightAngleCrosshairWidth = builder
                .comment("Width of right angle crosshair, range 1 ~ 5, default 1 .")
                .defineInRange("Right Angle Crosshair Width", 1, 1, 5);
        builder.pop();//退出Crosshair Properties
        builder.pop();//退出HUD
    }
}
