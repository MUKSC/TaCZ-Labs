package com.txttext.taczlabs.hud.crosshair;

import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import com.txttext.taczlabs.event.shoot.PlayerFireHandler;
import com.txttext.taczlabs.util.DeltaTime;
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
    public static GunData GUN_DATA;//枪械数据
    private static float lastSpread = 0f;//保存上一tick的 spread
    private static final DeltaTime deltaTime = new DeltaTime();
    //private static long lastTime = System.nanoTime();

    public enum SpreadType{
        REAL,//按照真实枪械散射
        VIRTUAL,//按照虚拟散射
        SPEED//按照速度散射
    }

    //决定要渲染的准星类型
    public static void renderCrosshair(GuiGraphics graphics, CrosshairType type, float x, float y, ClientGunIndex gunIndex, LocalPlayer player){
        //计算扩散
        float spread = getSpread(type, gunIndex, player);
        //System.out.println("spread = " + spread);
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
        //选择散射类型
        //boolean inaccuracy = inaccuracySpread.get();
        return switch(spreadTypes.get()){
            case REAL -> getRealSpread(type, gunIndex, player);
            case VIRTUAL -> getVirtualSpread(type, player);
            case SPEED -> getSpeedSpread(type, player);
        };
        //return inaccuracy ?  :
    }

    private static float getRealSpread(CrosshairType type, ClientGunIndex gunIndex, LocalPlayer player){
        //使用散射映射表获取枪械扩散值
        GUN_DATA = gunIndex.getGunData();

        //获取准星扩散数据
        CrosshairSpread crosshairSpread = getCrosshairSpread(GUN_DATA);
        float move = crosshairSpread.spreadData().move();//如果枪包内数据不规范导致格式错误这三个数据可能为NAN，需健壮性检查
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
        float raw = speed > 0.01f ? 3 * move - 2 : 1f;//由(move + (move-1)* 2)得来，意思是move + move大于1的部分 *2，不*2变化就太小了

        //获取准星默认半径
        float radius = getRadius(type);
        //结合扩散和速度影响，计算目标准星扩散
        //baseSpread（基础扩散） = 默认准星半径 * 由潜行和趴下影响的倍率
        float baseSpread = radius * status * raw;//基础扩散
        return lerpAndUpdateSpread(baseSpread, radius);
    }

    //虚拟的准星扩散，不考虑真实扩散
    private static float getVirtualSpread(CrosshairType type, LocalPlayer player){
        //获取玩家状态，根据状态（潜行、趴下）决定是否缩小准星默认半径倍率
        InaccuracyType playerStatus = InaccuracyType.getInaccuracyType(player);
        float status = switch (playerStatus){
            case SNEAK -> sneakGunSpread;
            case LIE -> lieGunSpread;
            default -> 1f;
        };

        //获取玩家速度（XZ平面速度）
        float speed = (float) player.getDeltaMovement().horizontalDistance();
        float speedFactor = speed > 0.01f ? 2 : 1;
        //获取准星默认半径
        float radius = getRadius(type);

        //结合扩散和速度影响，计算目标准星扩散
        float baseSpread = radius * status * speedFactor;//baseSpread = 默认准星半径 * 由潜行和趴下影响的倍率

        return lerpAndUpdateSpread(baseSpread, radius);
    }

    //虚拟的准星扩散，不考虑真实扩散
    private static float getSpeedSpread(CrosshairType type, LocalPlayer player){
        //获取玩家状态，根据状态（潜行、趴下）决定是否缩小准星默认半径倍率
        InaccuracyType playerStatus = InaccuracyType.getInaccuracyType(player);
        float status = switch (playerStatus){
            case SNEAK -> sneakGunSpread;
            case LIE -> lieGunSpread;
            default -> 1f;
        };

        //获取玩家速度（XZ平面速度）
        float speed = (float) player.getDeltaMovement().horizontalDistance();
        float speedFactor = Mth.clamp(speed, 0f, 1f) * 80;//速度阈值保护。限制在 [0,1]
        //获取准星默认半径
        float radius = getRadius(type);

        //结合扩散和速度影响，计算目标准星扩散
        float baseSpread = radius * status + speedFactor;//baseSpread = 默认准星半径 * 由潜行和趴下影响的倍率

        return lerpAndUpdateSpread(baseSpread, radius);
    }

    //getVisalSpread()和getRealSpread()算出目标扩散值后，由此方法完成插值、更新lastSpread，衰减fireSpread
    private static float lerpAndUpdateSpread(float baseSpread, float radius){
        //获取开火叠加值
        float fireSpread = PlayerFireHandler.getFireSpread();
        //targetSpread = 基础扩散 + 速度影响 + 开火抖动
        float targetSpread = Math.min(baseSpread, radius + maxSpread.get()) + fireSpread;//限制在最大扩散范围内（不限制开火扩散），加上radius是需要不受默认半径影响

        float smoothing = animSpeed.get();

        //自然衰减 fireSpread（真实时间驱动）
        float tickDelta = deltaTime.updateTimeAndGetDeltaSec(); // 秒
        float decayAlpha = 1 - (float)Math.exp(-smoothing * tickDelta);
        PlayerFireHandler.setFireSpread(Mth.lerp(decayAlpha, fireSpread, 0f));

        //固定时间步长，保证开火扩散低帧率不跳大
        float fixedDelta = 1 / 60f;
        float lerpAlpha = 1 - (float)Math.exp(-smoothing * fixedDelta);

        //平滑靠近 targetSpread
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
//    private static CrosshairSpread getCrosshairSpread(boolean inaccuracy){
//        //gunSpread数据
//        GunSpread gunSpread = getGunSpread(GUN_DATA.getInaccuracy());
//        float base = gunSpread.spreadData().stand();
//        float move = gunSpread.spreadData().move();
//        float sneak = gunSpread.spreadData().sneak();
//        float lie = gunSpread.spreadData().lie();
//
//        return new CrosshairSpread(
//                new SpreadData(
//                        //以站立为基准归一化各状态扩散，基准为1
//                        Math.max(base, 0.001f),//防止除0,
//                        move / base,
//                        inaccuracy ? sneakGunSpread : sneak / base,//是否开启按照扩散值
//                        inaccuracy ? lieGunSpread : lie / base
//                )
//        );
//    }

    //将枪械扩散值归一化为准星扩散值，这个版本的区别是不需要inaccuracy（因为现在的getVisalSpread()暂时不需要一个弄复杂的Factor，直接取这两个常量就完了
    private static CrosshairSpread getCrosshairSpread(GunData data){
        //gunSpread数据
        GunSpread gunSpread = getGunSpread(data.getInaccuracy());
        float base = gunSpread.spreadData().stand();
        float move = gunSpread.spreadData().move();
        float sneak = gunSpread.spreadData().sneak();
        float lie = gunSpread.spreadData().lie();

        //以站立为基准归一化各状态扩散，基准为1

        //防止除0导致NaN特写一个if
        if(base != 0){
            return new CrosshairSpread(new SpreadData(base, move / base, sneak / base, lie / base));
        }
        else{
            return new CrosshairSpread(new SpreadData(1 , 1, 1, 1));
        }

    }
}
