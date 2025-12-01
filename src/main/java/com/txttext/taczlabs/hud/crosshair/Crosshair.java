package com.txttext.taczlabs.hud.crosshair;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import com.txttext.taczlabs.event.shoot.PlayerFireHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

import java.util.Map;

import static com.tacz.guns.resource.pojo.data.gun.InaccuracyType.*;
import static com.txttext.taczlabs.config.fileconfig.HudConfig.*;
import static com.txttext.taczlabs.hud.crosshair.CrosshairRenderer.*;

public class Crosshair {
    //静态常量
    private static final float lieGunSpread = 0.5f;
    private static final float sneakGunSpread = 0.7f;
    //数据
    private static float lastSpread = 0f;//保存上一tick的 spread
    public static GunData gunData;//枪械数据

    //决定要渲染的准星类型
    public static void renderCrosshair(GuiGraphics graphics, CrosshairType type, float x, float y, ClientGunIndex gunIndex, LocalPlayer player){
        //计算扩散
        float spread = getSpread(type, gunIndex, player);
        //绘制准星
        switch (type){
            case CROSSHAIR-> drawCrosshair(x, y, spread);//绘制十字准星
            case RECT-> drawRectCrosshair(x, y, spread);//绘制方形准星
            case RIGHT_ANGLE-> drawRightAngleCrosshair(x, y, spread);//绘制直角准星
            case ARC -> drawArcCrosshair(graphics, x, y, spread);
            case POINT-> drawDot(x, y);//绘制点状准星
            default-> drawCrosshair(x, y, spread);//未知情况，正常情况不会触发
        }
    }

    //准星扩散值计算
    private static float getSpread(CrosshairType type, ClientGunIndex gunIndex, LocalPlayer player) {
        //是否要基于真实散射
        boolean inaccuracy = inaccuracySpread.get();
        return inaccuracy ? getActuallyRealSpread(type, gunIndex, player) : getVisalSpread(type, player);
//        //使用散射映射表获取枪械扩散值
//        gunData = gunIndex.getGunData();
//
//        //获取准星扩散数据
//        CrosshairSpread  crosshairSpread = getCrosshairSpread(inaccuracy);
//        //float stand = crosshairSpread.spreadData().stand();
//        float move = crosshairSpread.spreadData().move();
//        float sneak = crosshairSpread.spreadData().sneak();
//        float lie = crosshairSpread.spreadData().lie();
//
//        //根据状态（潜行、趴下）决定是否缩小准星默认半径（混合影响关闭则按照下面的归一化比例来）
//        //获取玩家状态
//        InaccuracyType playerStatus = InaccuracyType.getInaccuracyType(player);
//        float status = switch (playerStatus){
//                    case SNEAK -> sneak;
//                    case LIE -> lie;
//                    default -> 1f;
//                };
//
//        //速度阈值保护。确认混合影响配置关闭。限制在 [0,1]
//        //获取玩家速度（XZ平面速度）
//        float speed = (float) player.getDeltaMovement().horizontalDistance();
//        float speedFactor = !inaccuracy ? Mth.clamp(speed, 0f, 1f) * 20 : 0;
//
//        //获取移动/站立时的实际扩散值
//        //不*2变化就太小了
//        //需要准星扩散值不为0
//        float raw = !inaccuracy && speed > 0.01f ? move*2 : 1f;//tacz的状态不可靠因此自己判断
//
////        //归一化扩散（以站立为基准）
////        float base = Math.max(stand, 0.001f);//防止除0
////        //当前状态相对于站立的比例偏移（负数变为 0，最多不超过1）
////        float normalized = Mth.clamp((raw - base) / base, 0f, 1f);
////        //结合扩散和速度影响，计算目标准星扩散
////        float targetSpread = (normalized + speedFactor) * crosshairSpread.get();//从配置获取扩散倍率
//
//        //获取准星默认半径
//        float radius = getRadius(type);
//        //结合扩散和速度影响，计算目标准星扩散
//        //baseSpread（基础扩散） = 默认准星半径 * 由潜行和趴下影响的倍率
//        float baseSpread = radius * status * raw;//基础扩散
//        //(radius * status) + speedFactor * crosshairSpread.get() + raw//如果采用加raw的算法，获取raw的时候应该获取move和stand
//        //targetSpread = 基础扩散 + 速度影响 + 开火抖动
//        float fireSpread = PlayerFireHandler.getFireSpread();
//        float targetSpread = Math.min(baseSpread + speedFactor, radius + maxSpread.get()) + fireSpread;//限制在最大扩散范围内（不限制开火扩散），加上radius是需要不受默认半径影响
//        //自然衰减 fireSpread，防止一直扩张
//        PlayerFireHandler.setFireSpread(Mth.lerp(0.15f, fireSpread, 0f));
//        //平滑靠近当前 targetSpread，不频繁重置 lastTargetSpread
//        //float tickDelta = Minecraft.getInstance().getDeltaFrameTime();//Mth.clamp(Minecraft.getInstance().getFrameTime(), 0.001f, 0.05f);//安全保护，极低帧率或者暂停菜单的场景可能让tickDelta很奇怪
//        float tickDelta = 0.025f;
//        float smoothing = animSpeed.get() / 10.f;//动画速度
//        float lerpAlpha = 1 - (float) Math.exp(-smoothing * tickDelta);
//        float spread = Mth.lerp(lerpAlpha, lastSpread, targetSpread);
//        lastSpread = spread;//更新lastSpread
//        return spread;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static float getActuallyRealSpread(CrosshairType type, ClientGunIndex gunIndex, LocalPlayer player) {
        AttachmentCacheProperty cacheProperty = IGunOperator.fromLivingEntity(player).getCacheProperty();
        if (cacheProperty == null) return getRealSpread(type, gunIndex, player);
        InaccuracyType inaccuracyType = InaccuracyType.getInaccuracyType(player);
        float inaccuracy = cacheProperty.getCache(GunProperties.INACCURACY).get(inaccuracyType);

        float radius = getRadius(type);
        return lerpAndUpdateSpread(radius * inaccuracy, radius);
    }

    private static float getRealSpread(CrosshairType type, ClientGunIndex gunIndex, LocalPlayer player){
        //使用散射映射表获取枪械扩散值
        gunData = gunIndex.getGunData();

        //获取准星扩散数据
        CrosshairSpread crosshairSpread = getCrosshairSpread();
        float move = crosshairSpread.spreadData().move();
        float sneak = crosshairSpread.spreadData().sneak();
        float lie = crosshairSpread.spreadData().lie();

        //获取玩家状态，根据状态（潜行、趴下）决定是否缩小准星默认半径倍率
        InaccuracyType playerStatus = InaccuracyType.getInaccuracyType(player);
        float status = switch (playerStatus){
            case SNEAK -> sneak;
            case LIE -> lie;
            default -> 1f;
        };

        //获取玩家速度（XZ平面速度）
        float speed = (float) player.getDeltaMovement().horizontalDistance();
        //获取移动/站立时的实际扩散值（tacz的状态不可靠因此自己判断）
        float raw = speed > 0.01f ? move*2 : 1f;//不*2变化就太小了

        //获取准星默认半径
        float radius = getRadius(type);
        //结合扩散和速度影响，计算目标准星扩散
        //baseSpread（基础扩散） = 默认准星半径 * 由潜行和趴下影响的倍率
        float baseSpread = radius * status * raw;//基础扩散
        return lerpAndUpdateSpread(baseSpread, radius);
    }

    //虚拟的准星扩散，不考虑真实扩散
    private static float getVisalSpread(CrosshairType type, LocalPlayer player){
        //获取玩家状态，根据状态（潜行、趴下）决定是否缩小准星默认半径倍率
        InaccuracyType playerStatus = InaccuracyType.getInaccuracyType(player);
        float status = switch (playerStatus){
            case SNEAK -> sneakGunSpread;
            case LIE -> lieGunSpread;
            default -> 1f;
        };

        //获取玩家速度（XZ平面速度）
        float speed = (float) player.getDeltaMovement().horizontalDistance();
        float speedFactor = Mth.clamp(speed, 0f, 1f) * 50;//速度阈值保护。限制在 [0,1]
        //获取准星默认半径
        float radius = getRadius(type);

        //结合扩散和速度影响，计算目标准星扩散
        float baseSpread = radius * status;//baseSpread = 默认准星半径 * 由潜行和趴下影响的倍率
        return lerpAndUpdateSpread(baseSpread + speedFactor, radius);
    }

    //getVisalSpread()和getRealSpread()算出目标扩散值后，由此方法完成插值、更新lastSpread，衰减fireSpread
    private static float lerpAndUpdateSpread(float baseSpread, float radius){
        //targetSpread = 基础扩散 + 速度影响 + 开火抖动
        float fireSpread = PlayerFireHandler.getFireSpread();
        float targetSpread = Math.min(baseSpread, radius + maxSpread.get()) + fireSpread;//限制在最大扩散范围内（不限制开火扩散），加上radius是需要不受默认半径影响
        //自然衰减 fireSpread
        PlayerFireHandler.setFireSpread(Mth.lerp(0.15f, fireSpread, 0f));
        //平滑靠近 targetSpread
        float smoothing = animSpeed.get() / 10.f;//动画速度
        float lerpAlpha = 1 - (float) Math.exp(-smoothing * 0.05f);
        float spread = Mth.lerp(lerpAlpha, lastSpread, targetSpread);
        lastSpread = spread;//更新lastSpread
        return spread;
    }

    //根据准星类型获取配置中的默认半径
    private static float getRadius(CrosshairType type){
        return switch (type) {
            case CROSSHAIR -> (float) crosshairRadius.get();//十字准星
            case RECT -> (float) rectCrosshairRadius.get();//方形准星
            case RIGHT_ANGLE -> (float) rightAngleCrosshairRadius.get();//直角准星
            default -> (float) crosshairRadius.get();//点状准星和未知情况
        };
    }

    //获取枪械扩散值
    private static GunSpread getGunSpread(Map<InaccuracyType, Float> map){
        float stand = map.getOrDefault(STAND, 1.0f);
        return new GunSpread(
                new SpreadData(
                        stand,
                        map.getOrDefault(MOVE, stand),//默认使用stand避免空值//move
                        map.getOrDefault(SNEAK, stand),//sneak
                        map.getOrDefault(LIE, stand)//lie
                )
        );
    }

    //将枪械扩散值归一化为准星扩散值
    private static CrosshairSpread getCrosshairSpread(boolean inaccuracy){
        //gunSpread数据
        GunSpread gunSpread = getGunSpread(gunData.getInaccuracy());
        float base = gunSpread.spreadData().stand();
        float move = gunSpread.spreadData().move();
        float sneak = gunSpread.spreadData().sneak();
        float lie = gunSpread.spreadData().lie();

        return new CrosshairSpread(
                new SpreadData(
                        //以站立为基准归一化各状态扩散，基准为1
                        Math.max(base, 0.001f),//防止除0,
                        move / base,
                        inaccuracy ? sneakGunSpread : sneak / base,//是否开启按照扩散值
                        inaccuracy ? lieGunSpread : lie / base
                )
        );
    }

    //将枪械扩散值归一化为准星扩散值，这个版本的区别是不需要inaccuracy（因为现在的getVisalSpread()暂时不需要一个弄复杂的Factor，直接取这两个常量就完了
    private static CrosshairSpread getCrosshairSpread(){
        //gunSpread数据
        GunSpread gunSpread = getGunSpread(gunData.getInaccuracy());
        float base = gunSpread.spreadData().stand();
        float move = gunSpread.spreadData().move();
        float sneak = gunSpread.spreadData().sneak();
        float lie = gunSpread.spreadData().lie();

        return new CrosshairSpread(
                new SpreadData(
                        //以站立为基准归一化各状态扩散，基准为1
                        Math.max(base, 0.001f),//防止除0,
                        move / base,
                        sneak / base,//是否开启按照扩散值
                        lie / base
                )
        );
    }
}
